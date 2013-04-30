package printer;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class PrinterImpl extends AbstractPrinter {
	
	ConcurrentMap<String, Integer> lossCounts; // { clientName, lossCount }
	
	public PrinterImpl() {
		super();
		lossCounts = new ConcurrentHashMap<String, Integer>();
	}

	@Override
	public boolean isAvailable() {
		return this.available;
	}

	@Override
	public synchronized void requestToPrint(Client c, File f) throws IllegalPrintStateException {
		if (!isAvailable() || !isFair(c))
			throw new IllegalPrintStateException();
		
		// set variables that allow print() to be called
		client = c;
		file = f;
		available = false;
		// also reset the loss count to 0
		lossCounts.replace(client.getName(), 0);
	}

	@Override
	public synchronized void requestToPrintNext(Client c) throws IllegalPrintStateException {
		if (isAvailable() && isFair(c)) 
			throw new IllegalPrintStateException();
		
		// init lossCount[client] if required
		lossCounts.putIfAbsent(c.getName(), 0);
		// increase the loss count
		lossCounts.replace(c.getName(), lossCounts.get(c.getName()) + 1);
		// wait
		try {
			while (!(isAvailable() && isFair(c)))
				wait();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean isFair(Client c) {
		// init lossCount[client] if required
		lossCounts.putIfAbsent(c.getName(), 0);
		
		// determine if you have maxLossCount
		int maxLossCount = 0;
		for (Map.Entry<String, Integer> lossCount : lossCounts.entrySet()) {
			if (lossCount.getValue() > maxLossCount) {
				maxLossCount = lossCount.getValue();
			}
		}
		if (maxLossCount == lossCounts.get(c.getName())) {
			return true;
		}
		return false;
	}

	@Override
	protected void print() throws IllegalPrintStateException {
		if (isAvailable())
			throw new IllegalPrintStateException();
		
		// call printInternal() and reset available to true
		// then notify other clients to allow them to proceed
		synchronized (this) {
			printInternal();
			available = true;
			notifyAll();
		}
	}
}

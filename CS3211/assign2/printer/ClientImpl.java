package printer;

import java.io.File;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


public class ClientImpl extends AbstractClient {

	public ClientImpl(String name, Printer p, List<File> queue) {
		super(name, p, queue);
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	protected void tryToPrint() {
		File file = null;
		while (true) {
			try {
				// grab a file if required
				if (file == null) {
					synchronized (queue) {
						if (queue.isEmpty()) break;
						file = queue.remove(0);
					}
				}
				
				// try to print or go into "queue" (aka wait)
				synchronized (printer) {
					if (printer.isAvailable() && printer.isFair(this)) {
						// print ... then reset file to null, 
						// triggering the getting on the next file if available
						printer.requestToPrint(this, file);
						file = null;
					} else {
						printer.requestToPrintNext(this);
					}
				}
			} catch (IllegalPrintStateException e) {
				e.printStackTrace();
			}
		}
	}

}

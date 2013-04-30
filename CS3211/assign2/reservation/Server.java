package reservation;

import java.util.AbstractMap.SimpleEntry;
import java.util.TreeMap;
import java.util.concurrent.BlockingQueue;

public class Server implements Runnable {
	
	protected TreeMap<Integer, Boolean> reservations;
	protected BlockingQueue<Request> requests;
	protected TreeMap<Integer, BlockingQueue<Response>> responses;
	
	public Server(BlockingQueue<Request> requests) {
		this.requests = requests;
		this.responses = new TreeMap<Integer, BlockingQueue<Response>>();
		
		// initialize random data for reservations
		reservations = new TreeMap<Integer, Boolean>();
		for (int i = 0; i < 20; i++) {
			reservations.put(i, (i < 5 ? true : false)); // 0 - 4 booked
		}
	}
	
	public void connect(int terminalId, BlockingQueue<Response> responseQueue) {
		this.responses.put(terminalId, responseQueue);
	}

	@Override
	public void run() {
		try {
			while (true) {
				Request req = requests.take();
				switch (req.function) {
				case "getReservationStatus":
					responses.get(req.terminalId).put(new Response(req.function, this.getReservationStatus()));
					break;
				case "book":
					int seatId = (int) req.args;
					SimpleEntry<Integer, Boolean> entry = new SimpleEntry<Integer, Boolean>(seatId, book(seatId));
					responses.get(req.terminalId).put(new Response(req.function, entry));
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	// returns a copy of reservations
	// here its OK to be not synchronized as its a read only thing
	public TreeMap<Integer, Boolean> getReservationStatus() {
		TreeMap<Integer, Boolean> reservationsCopy = new TreeMap<Integer, Boolean>();
		reservationsCopy.putAll(reservations);
		return reservationsCopy;
	}
	
	// book() is "synchronized" thus only 1 thread can modify it at a time
	// a check is done before the actual booking (modification of reservations)
	// thus ensuring the seat is not booked by any other in the process
	public synchronized boolean book(int seat) {
		if (reservations.get(seat) == false) {
			reservations.put(seat, true);
			return true;
		}
		return false;
	}

}

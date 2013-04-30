package reservation;

import java.util.*;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.*;
import java.util.concurrent.*;

public class Terminal implements Runnable {
	
	protected Server server;
	protected int terminalId;
	protected BlockingQueue<Request> requests;
	protected BlockingQueue<Response> responses;

	public Terminal(int terminalId, BlockingQueue<Request> requests, BlockingQueue<Response> responses, Server server) {
		this.terminalId = terminalId;
		this.requests = requests;
		this.responses = responses;
		this.server = server;
		this.server.connect(this.terminalId, this.responses);
	}

	@SuppressWarnings("unchecked")
	@Override
	public void run() {
		try {
			// "simulation" - send requests to server
			requests.put(new Request(this.terminalId, "getReservationStatus", null));
			requests.put(new Request(this.terminalId, "book", 10));
			requests.put(new Request(this.terminalId, "getReservationStatus", null));
			requests.put(new Request(this.terminalId, "book", 11));
			requests.put(new Request(this.terminalId, "getReservationStatus", null));
			requests.put(new Request(this.terminalId, "book", 12));
			requests.put(new Request(this.terminalId, "getReservationStatus", null));
			// end simulation code
			
			while (true) {
				Response res = responses.take();
				switch (res.function) {
				case "getReservationStatus":
					TreeMap<Integer, Boolean> reservations = (TreeMap<Integer, Boolean>) res.data;
					synchronized (System.out) {
						System.out.println("getReservationStatus: Terminal #" + this.terminalId);
						for (Entry<Integer, Boolean> reservation : reservations.entrySet()) {
							System.out.println(" - " + reservation.getKey() + " \t " + (reservation.getValue() ? "Booked" : "Available"));
						}
					}
					break;
				case "book":
					SimpleEntry<Integer, Boolean> data = (SimpleEntry<Integer, Boolean>) res.data;
					synchronized (System.out) {
						System.out.println(terminalId + ": Booking seat " + data.getKey() + " " + (data.getValue() ? "Successful" : "Unsucessful"));
					}
					break;
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	
	
}

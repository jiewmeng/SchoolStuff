/**
 * In this simulation, Terminals (clients) will put requests into a request queue of the server. 
 * This Request will contain their terminalId (so Server will know who to reply to), the function
 * to call on server and any arguments. They will then wait for the Server to respond by putting 
 * a Response (function called, data returned) into the Terminal's response queue. 
 * 
 * When the Server sees Requests in its queue, it will process them and respond to the appropriate
 * Terminal. Similarly when Terminals see Responses in its queue, it will process them and output
 * to the user the results. 
 * 
 * The queues are implemented using `ArrayBlockingQueue` and the waiting for an item in the queue
 * is done by its blocking `take()`. 
 * 
 * In some sense, this is something like a 2 way Producer/Consumer problem. The server consumes 
 * Requests and produces Responses. The terminals does the reverse. 
 * 
 * The main part making it impossible for double booking is `Server.book(seat)` is synchronized. 
 * If 2 terminals tries to `book(1)`. Only 1 can enter the function, and successfully book the 
 * seat. The next one will be unsuccessful because of the check if the seat was booked. 
 * 
 * For the purposes of this simulation, the requests are hard-coded in `Terminal.run()`, before
 * the `while (true)`
 */

package reservation;

import java.util.concurrent.*;

public class ReservationsMain {
	
	public static void main(String[] args) {
		BlockingQueue<Request> requests = new ArrayBlockingQueue<Request>(10);
		Server server = new Server(requests);
		Terminal t1 = new Terminal(1, requests, new ArrayBlockingQueue<Response>(10), server);
		Terminal t2 = new Terminal(2, requests, new ArrayBlockingQueue<Response>(10), server);
		Terminal t3 = new Terminal(3, requests, new ArrayBlockingQueue<Response>(10), server);
		
		new Thread(server).start();
		new Thread(t1).start();
		new Thread(t2).start();
		new Thread(t3).start();
	}
	
}

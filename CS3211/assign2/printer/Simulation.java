package printer;

import java.io.File;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class Simulation {

  public static volatile boolean isReady = false;

  public static void main(String[] args) throws URISyntaxException {
    Printer p = new PrinterImpl();
    new Thread(p).start();

    List<File> queue = new ArrayList<File>();    
    queue.add(new File(Simulation.class.getResource("text1").toURI()));
    queue.add(new File(Simulation.class.getResource("text2").toURI()));
    queue.add(new File(Simulation.class.getResource("text3").toURI()));

    List<Client> clientList = new ArrayList<Client>();
    for (int i = 1; i <= 3; i++) {
      Client c = new ClientImpl("Client " + i, p, queue);
      clientList.add(c);
    }

    for (Client c : clientList) {
      new Thread(c).start();
    }

    isReady = true;
  }

}

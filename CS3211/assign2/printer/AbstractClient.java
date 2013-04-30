package printer;

import java.io.File;
import java.util.List;

public abstract class AbstractClient implements Client {

  protected String name;
  protected Printer printer;
  protected List<File> queue;

  public AbstractClient(String name, Printer p, List<File> queue) {
    this.name = name;
    this.printer = p;
    this.queue = queue;
  }

  @Override
  public void run() {
    // wait until all the clients are ready to run.
    while (!Simulation.isReady) {
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {
      }
    }

    tryToPrint();
  }

  /**
   * Tries to request the printer to print the contents of the files in the
   * queue.
   * 
   * Hint: call either printer.requestToPrint or printer.requestToPrintNext
   * depending on the availability of the printer and the fairness of the
   * scheduling.
   */
  protected abstract void tryToPrint();
}

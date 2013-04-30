package printer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public abstract class AbstractPrinter implements Printer {

  protected boolean available = true;

  protected Client client;
  protected File file;

  @Override
  public void run() {
    while (true) {
      if (!isAvailable()) {
        // there is a print job to process.
        try {
          print();
        } catch (IllegalPrintStateException e) {
          System.err.println(e);
        }
      } else {
        // there is no job to process; sleep for a while.
        try {
          Thread.sleep(1);
        } catch (InterruptedException e) {
          System.err.println(e);
        }
      }
    }
  }

  /**
   * This internal method processes the latest print request.
   * 
   * @throws IllegalPrintStateException
   *           if available is true; that is, this method should not be called
   *           if there is no job to process.
   */
  protected abstract void print() throws IllegalPrintStateException;

  /**
   * This is a helper method that prints out the contents of a file to the
   * console. 
   */
  protected void printInternal() {
    synchronized (System.out) {
      System.out.println("*************************************");
      System.out.println("** " + client.getName());
      System.out.println("*************************************");
    }

    try {
      String line;
      BufferedReader br = new BufferedReader(new FileReader(file));
      while ((line = br.readLine()) != null) {
        System.out.println(line);
        Thread.sleep(100);
      }
    } catch (Exception e) {
      System.err.println(e);
    }
  }
}

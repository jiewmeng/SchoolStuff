package printer;

import java.io.File;

public interface Printer extends Runnable {

  /**
   * Returns true if this printer is not occupied by any client.
   * 
   * @return true if this printer is not occupied by any client.
   */
  public boolean isAvailable();

  /**
   * Requests to print to the console the contents of the specified file f
   * belonging to the specified client c. This method should be called only if
   * the printer is available (i.e., isAvailable() returns true) and it is fair
   * to allocate the printer to the specified client c (i.e., isFair(c) returns
   * true). Otherwise, an IllegalPrintStateException must be thrown.
   * 
   * @param c
   *          the client who asks to use the printer.
   * @param f
   *          the file whose contents should be printed out to the console.
   * @throws IllegalPrintStateException
   *           if isAvailable() or isFair(c) returns false.
   */
  public void requestToPrint(Client c, File f) throws IllegalPrintStateException;

  /**
   * Requests to handle a print job of the specified client c in the future.
   * This method is intended to be called by a client who tries to use the
   * printer when the printer is already occupied by the other client. In
   * addition, even if the printer is available, this method should also be
   * called if it is unfair to allocate the printer to the specified client c.
   * For example, if the specified client already used the printer previously
   * while the other clients are waiting for the printer, it is unfair to
   * allocate the printer to the same client again.
   * 
   * By allowing a client to call this method, the printer can be informed about
   * which clients want to use the printer. This information can be used to
   * enforce fair scheduling between multiple clients.
   * 
   * @param c
   *          the client who asks to use the printer in the future.
   * @throws IllegalPrintStateException
   *           if both isAvailable() and isFair(c) return true.
   */
  public void requestToPrintNext(Client c) throws IllegalPrintStateException;

  /**
   * Returns true if it is fair to allocate the printer to the specified client.
   * It is unfair to allocate the printer to a client c if there is another
   * client other than the specified client c that has been waiting for its turn
   * longer than c does.
   * 
   * @param c
   *          a client for which the fairness is asked.
   * @return true if it is fair to allocate the printer to c.
   */
  public boolean isFair(Client c);

}

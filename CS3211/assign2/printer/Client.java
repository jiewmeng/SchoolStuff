package printer;

public interface Client extends Runnable {

  /**
   * Returns the name of this client
   * 
   * @return the name of this client
   */
  public String getName();

}

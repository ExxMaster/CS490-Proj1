import java.net.*;
import java.rmi.*;

public class RMIChatServer {
  public static void main(String args[]) {
    try {
      RMIImplementation RMIImplementation = new RMIImplementation();
      Naming.rebind("RMIChatServer", RMIImplementation);
      System.out.println("Server: The sum is: " + RMIInterface.add(50.0,50.0));
    }
    catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}

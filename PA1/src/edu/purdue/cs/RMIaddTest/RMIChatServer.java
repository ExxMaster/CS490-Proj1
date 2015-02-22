import java.net.*;
import java.rmi.*;

public class RMIChatServer {
  public static void main(String args[]) {
    try {
      RMIImplementation RMIImplementation = new RMIImplementation();
      Naming.rebind("RMIChatServer", RMIImplementation);
    }
    catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}

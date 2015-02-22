import java.net.*;
import java.rmi.*;

public class RMIChatServer {
  public static void main(String args[]) {
    try {
      RMIImplementation impl = new RMIImplementation();
      Naming.rebind("RMIChatServer", impl);
    }
    catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}

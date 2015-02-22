import java.rmi.*;
import java.rmi.server.*;

public class RMIImplementation extends UnicastRemoteObject
  implements RMIInterface {

  public RMIImplementation() throws RemoteException {
  }
  public double add(double d1, double d2) throws RemoteException {
    return d1 + d2;
  }
}

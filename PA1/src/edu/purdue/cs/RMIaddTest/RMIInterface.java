import java.rmi.*;

public interface RMIInterface extends Remote {
  double add(double d1, double d2) throws RemoteException;
}

import java.rmi.*;

public interface RMIInterface extends Remote {
  double add(double d1, double d2) throws RemoteException;
  public boolean addToGroup(String s) throws RemoteException;
  public void prompt(String s) throws RemoteException;
  public boolean removeFromGroup(String s) throws RemoteException;
  public String getGroup() throws RemoteException;
}

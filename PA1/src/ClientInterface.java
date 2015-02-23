
import java.rmi.Remote;
import java.rmi.RemoteException;

public interface ClientInterface extends Remote {
	public void putMessage(String m) throws RemoteException;
}

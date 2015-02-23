
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ClientImplementation extends UnicastRemoteObject implements
		ClientInterface {
		
	public ClientImplementation() throws RemoteException {
		super();
	}

	@Override
	public void putMessage(String m) throws RemoteException {
		System.out.println(m);
	}

}

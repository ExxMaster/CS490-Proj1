import java.rmi.*;
import java.rmi.server.*;

public class RMIImplementation extends UnicastRemoteObject
  implements RMIInterface {
	RMIChatServer server;
  public RMIImplementation() throws RemoteException {
  	 	server =  new RMIChatServer();
  }
  public double add(double d1, double d2) throws RemoteException {
    return d1 + d2;
  }
  
  public boolean addToGroup(String s) {
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        System.out.println(sa[0]);
        System.out.println(server.group.size());
        for(int i = 0; i < server.group.size(); i++) {
            if(server.group.get(i).contains(sa[0])) {
                return false;
            }
        }
        server.group.add(s);
        return true;
    }
    
    public boolean removeFromGroup(String s) {
        return server.group.remove(s);
    }
    
    public void prompt(String m){
    	System.out.println(m);
    	Long l = System.currentTimeMillis();
		m = m.substring(m.indexOf('<'));
		if(server.heart_beat.put(m, l) == null) {
		 	 System.out.println("updating failed");
		}
		System.out.printf("update time to %s\n", l.toString());
    }
    
    public String getGroup(){
	    return server.group.toString();
    }
}

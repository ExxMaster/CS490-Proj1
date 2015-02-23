import java.util.*;
import java.util.concurrent.*;
import java.net.*;
import java.rmi.*;


public class RMIChatServer {
  
    public static final int THREAD_POOL_CAPACITY = 10;
    public static ArrayList<String> group;
    public static ConcurrentHashMap<String, Long> heart_beat;
    public ThreadPoolExecutor executor;
  
  	public RMIChatServer(){
  		group = new ArrayList<String>();
        heart_beat = new ConcurrentHashMap<String, Long>();
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
  	}
  public static void main(String args[]) {
    try {
      RMIImplementation impl = new RMIImplementation();
      Naming.rebind("RMIChatServer", impl);
      //System.out.println("Server: The sum is: " + impl.add(50, 49));
      while(true){
		 // System.out.println("Checking Heartbeat");
		  try {
		  		Thread.sleep(1000);
		 } catch (InterruptedException e) {
		  e.printStackTrace();
		 }
		 Long time = System.currentTimeMillis();
		 for(int i = 0; i < group.size(); i++) {
		  String id = group.get(i);
		  Long t = heart_beat.get(id);
		  if(time - t > 10000) {
		   heart_beat.remove(id);
		   impl.removeFromGroup(id);
		   System.out.printf("%s removed\n time stap:\t%s\n time now:\t%s\n", id, t.toString(), time.toString());
		  }
		 }
	  }
    }
    
    catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}

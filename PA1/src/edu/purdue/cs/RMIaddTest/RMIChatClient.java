import java.rmi.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RMIChatClient {
	private String _name, _ip;
    private int _port;
    public static RMIInterface intf = null;
    public static int heartbeat_rate = 5;
    public static int THREAD_POOL_CAPACITY = 10;
    ThreadPoolExecutor executor;
    
    
	 private RMIChatClient() {
		 
		this._ip = "127.0.0.1";//s.getLocalAddress().toString().substring(1);
		this._port = 33550;//serverSocket.getLocalPort();
		this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
	}
	
	
	
	 private boolean register() throws RemoteException{
        //get the name
        Scanner sc = new Scanner(System.in);
        System.out.print("Please Enter Your Name: ");
        this._name = sc.nextLine();
         
        String m = "<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
        //System.out.println(m);
   		boolean reg = false;
   		/*try{*/
   			reg = intf.addToGroup(m);
   		/*}catch(Exception e){
   			System.out.println("addToGroup failed");
   		} */
        return(reg);
    }
    
//--------------------------------------------------------------------------------    
    

    private void sendHeartbeat() {
        this.executor.execute(new Runnable() {

         RMIChatClient c;
         
   @Override
   public void run() {
    try {
              while(true) {
                  String m = "heartbeat<" + c._name + ", " + c._ip + ", " +  c._port + ">" + "\n";
                  //System.out.println("Message sent to the server : " + m);
                  intf.prompt(m);
                  Thread.sleep(heartbeat_rate * 1000);
              }
          } catch (Exception e) {
              e.printStackTrace();
          }
   }
   
   public Runnable init(RMIChatClient cc) {
    this.c = cc;
    return this;
   }
         
        }.init(this));
    }
    


	/*private void sendHeartbeat() throws RemoteException {
		while(true) {
			  String m = "heartbeat<"+">" + "\n";
			  System.out.println("Message sent to the server : " + m);
			  boolean b = intf.prompt(m);
			  //Thread.sleep(heartbeat_rate * 1000);
		}
	}*/
//--------------------------------------------------------------------------------






  public static void main(String args[]) {
    try {
      String addServerURL = "rmi://" + args[0] + "/RMIChatServer";
      intf = (RMIInterface) Naming.lookup(addServerURL);
    
    
    RMIChatClient cc = new RMIChatClient();
	while(true) {
        if(cc.register()) break;
    }
      /*System.out.println("The first number is: " + args[1]);
      double d1 = Double.valueOf(args[1]).doubleValue();
      System.out.println("The second number is: " + args[2]);

      double d2 = Double.valueOf(args[2]).doubleValue();
      System.out.println("The sum is: " + intf.add(d1, d2));
    */
    cc.sendHeartbeat();
    System.out.println("done");
    while(true){
    	System.out.print(">");
    	Scanner sc = new Scanner(System.in);
    	String msg = sc.nextLine();
    	//System.out.println(msg);
    	if(msg.equals("get")){
    		String g = intf.getGroup();
    		System.out.println(g);
    	}
    	if(msg.equals("exit")) break;
    }
    }catch(Exception e) {
      System.out.println("Exception: " + e);
    }
  }
}


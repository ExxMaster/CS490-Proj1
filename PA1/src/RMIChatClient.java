
import java.rmi.*;
import java.util.*;
import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RMIChatClient {
	private String _name, _ip;
    private int _port;
    public RMIInterface intf = null;
    public static int heartbeat_rate = 5;
    public static int THREAD_POOL_CAPACITY = 10;
    ThreadPoolExecutor executor;
    
    
	 public RMIChatClient() {
	 	try{
			this._ip = InetAddress.getLocalHost().getHostAddress();
			System.out.println(this._ip);
			//s.getLocalAddress().toString().substring(1);
		}catch (Exception e) {
			System.out.println("ip address failure");
		}
		//serverSocket.getLocalPort();
		this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
	}
	
	
	
	 private boolean register() throws RemoteException{
        //get the name
        Scanner sc = new Scanner(System.in);
        System.out.print("Please Enter Your Name: ");
        this._name = sc.nextLine();
         
        String m = "<" + this._name + ", " + this._ip + ">" + "\n";
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
                  String m = "heartbeat<" + c._name + ", " + c._ip + ">" + "\n";
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
    
    public void chat(String msg) throws Exception {
    	String[] sp = msg.split(" ");
		String add = "rmi://" + sp[2] + "/" + sp[1];
		ClientInterface ci = (ClientInterface)Naming.lookup(add);
		Scanner sc = new Scanner(System.in);
		while(true) {
			String m = sc.nextLine();
			if(m.equals("exit")) {
				return;
			} else {
				ci.putMessage(m + " (" + "From " + this._name + " " + this._ip + ")");
			}
		}	
    }
			
	@SuppressWarnings("resource")
	public static void main(String args[]) {
		try {
			RMIChatClient cc = new RMIChatClient();
			Scanner sc = new Scanner(System.in);
			String addServerURL = "rmi://" + args[0] + "/RMIChatServer";
			cc.intf = (RMIInterface) Naming.lookup(addServerURL);

			while(true) {
				if(cc.register()) break;
			}

			ClientImplementation impl = new ClientImplementation();
			Naming.rebind(cc._name, impl);
			cc.sendHeartbeat();
			System.out.println("registered");
			while(true){
				System.out.print(">");
				String msg = sc.nextLine();
				if(msg.equals("get")){
					String g = cc.intf.getGroup();
					System.out.println(g);
				} else if(msg.contains("chat")) {
					cc.chat(msg);
				} else if(msg.equals("exit")) {
					System.exit(0);
				} else {
					System.out.println("Command not found");
					System.out.println("If you want chat, please initialize it first");
				}
			}
		}catch(Exception e) {
			System.out.println("Exception: " + e);
		}
	}
}



import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class SingleThreadedChatServer implements Runnable {
    
    //Constant Variable
    private static int PORT = 1222;
    private static final int THREAD_POOL_CAPACITY = 10;
    
    private ServerSocket serverSocket;
    private ArrayList<String> group;
    private ArrayList<Socket> clients;
    private ConcurrentHashMap<String, Long> heart_beat;
    private ThreadPoolExecutor executor;
    
    public SingleThreadedChatServer() {
        
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        
        //create the server socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        group = new ArrayList<String>();
        clients = new ArrayList<Socket>();
        heart_beat = new ConcurrentHashMap<String, Long>();
    }
    
    @Override
    public void run() {
        try {
            while(true) {
              for(int i = 0; i < clients.size(); i++) {
                Socket temp = clients.get(i);
                BufferedReader br = new BufferedReader(new InputStreamReader(temp.getInputStream()));
                if(br.ready()) {
                	String m = br.readLine();
                	if(m.contains("register")) {
                        m = m.substring(m.indexOf('<'));
                        this.modifyGroup(m, "a", temp);
                        this.heart_beat.put(m, System.currentTimeMillis());
                    } else if(m.equals("get")) {
                        ObjectOutputStream oos = new ObjectOutputStream(temp.getOutputStream());
                        oos.writeObject(this.group);
                        oos.flush();
                    } else if(m.contains("heartbeat")) {
                    	Long l = System.currentTimeMillis();
                    	m = m.substring(m.indexOf('<'));
                        if(this.heart_beat.put(m, l) == null) {
                        	System.out.println("updating failed");
                        }
                        System.out.printf("update time to %s\n", l.toString());
                    }
                }
              }
              Thread.sleep(1000);
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }
    }
    
    private synchronized void modifyClients(Socket s, String flag) {
    	if(flag.equals("a")) {
    		this.clients.add(s);
    	} else if(flag.equals("r")) {
    		this.clients.remove(s);
    	}
    }
    
    private synchronized boolean modifyGroup(String s, String flag, Socket c) throws IOException {
    	if(flag.equals("a")) {
    		return this.addToGroup(s, c);
    	} else if(flag.equals("r")) {
    		return this.group.remove(s);
    	}
    	return false;
    }
    
    private synchronized boolean addToGroup(String s, Socket c) throws IOException {
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        
        BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(c.getOutputStream()));
        
        for(int i = 0; i < this.group.size(); i++) {
            if(this.group.get(i).contains(sa[0])) {
                try {             
                    bw.write("Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        this.group.add(s);
        
        try {            
            bw.write("Success\n");
            bw.flush();
        } catch (IOException e) {
            System.out.println("error: sending success");
            e.printStackTrace();
        }
        
        return true;
    }
    
    public void checkingHeartbeat() {
    	this.executor.execute(new Runnable() {

			private SingleThreadedChatServer ss;

			@Override
			public void run() {
				
				while(true) {
					System.out.println("Checking Heartbeat");
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
							try {
								this.ss.modifyGroup(id, "r", null);
							} catch (IOException e) {
								e.printStackTrace();
							}
							this.ss.modifyClients(this.ss.clients.get(i), "r");
							System.out.printf("%s removed\n time stap:\t%s\n time now:\t%s\n", id, t.toString(), time.toString());
						}
					}
				}
			}
			
			public Runnable init(SingleThreadedChatServer s) {
				this.ss = s;
				return this;
			}
    		
    	}.init(this));
    }
    
    public void startServer() {
    this.checkingHeartbeat();
     this.executor.execute(this);
     while (true) {
            try {
                Socket _client = this.serverSocket.accept();
                System.out.println("One client connected");
                this.modifyClients(_client, "a");
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }
    }
    
    public static void main(String[] args) {
	if(args.length != 1){
            System.err.println("need port number");
            System.exit(1);
        }
        int PORT = Integer.parseInt(args[0]);        
	System.out.println("Start!");
        SingleThreadedChatServer ss = new SingleThreadedChatServer();
        ss.startServer();        
    }
}

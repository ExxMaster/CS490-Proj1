import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class STCSG implements Runnable {
    
    //Constant Variable
    private static final int PORT = 1222;  //TODO
    private static final int THREAD_POOL_CAPACITY = 10;
    
    private ServerSocket serverSocket;
    private static ArrayList<String> group;
    private static ArrayList<Socket> clients;
    private static ConcurrentHashMap<String, Long> heart_beat;
    private ThreadPoolExecutor executor;
    
    //child server attributes
    private Socket _client;
    private static BufferedWriter bw;
    private String name;
    
    public STCSG() {
        
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
    
    public STCSG(Socket client) {
        this._client = client;
        try {
            this.bw = new BufferedWriter(new OutputStreamWriter(this._client.getOutputStream()));
        } catch (Exception e) {
            System.out.println("init failed");
        }
    }
    
    private synchronized boolean addToGroup(String s) {
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        System.out.println(sa[0]);
        for(int i = 0; i < STCSG.group.size(); i++) {
            if(STCSG.group.get(i).contains(sa[0])) {
                try {             
                    bw.write("Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        STCSG.group.add(s);
        
        try {            
            bw.write("Success\n");
            bw.flush();
        } catch (IOException e) {
            System.out.println("error: sending success");
            e.printStackTrace();
        }
        
        return true;
    }
    
    private synchronized static boolean removeFromGroup(String s) {
        return STCSG.group.remove(s);
    }
   /* 
    public void register() {
      this.executor.execute(new Runnable() {
        
        @Override
        public void run() {
          while(true) {
            Socket _client = this.serverSocket.accept();
            clients.add(_client);
          }
        }
        
      });
    }*/
    
    public void checkingHeartbeat() {
     this.executor.execute(new Runnable() {

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
       heart_beat.remove(id);
       removeFromGroup(id);
       System.out.printf("%s removed\n time stap:\t%s\n time now:\t%s\n", id, t.toString(), time.toString());
      }
     }
    }
   }
      
     });
    }
    
    @Override
    public void run() {
        BufferedReader br;
        try {
            while(true) {
                System.out.println("in the main thread WHILE"); //DONOT REMOVE THIS
              for(int i = 0; i < clients.size(); i++) {
                Socket temp = clients.get(i);
                System.out.println("********************in the main thread FOR and \ti="+i);  //DONOT REMOVE THIS
                br = new BufferedReader(new InputStreamReader(temp.getInputStream()));
                String m;
                m = br.readLine();
                System.out.println(m);
                if(m.contains("register")) {
                  m = m.substring(m.indexOf('<'));
                  //add to Group
                  this.addToGroup(m);
                  this.name = m;
                  STCSG.heart_beat.put(m, System.currentTimeMillis());
                } else if(m.equals("get")) {
                  ObjectOutputStream oos = new ObjectOutputStream(temp.getOutputStream());
                  oos.writeObject(STCSG.group);
                  oos.flush();
                  
                  //TODO I THINK THE PROBLEM LIES HERE. GET IS ACTING WEIRD.
                } else if(m.contains("heartbeat")) {
                  Long l = System.currentTimeMillis();
                  m = m.substring(m.indexOf('<'));
                  if(STCSG.heart_beat.put(m, l) == null) {
                    System.out.println("updating failed");
                  }
                  System.out.printf("update time to %s\n", l.toString());
                }
              }
            }
        } catch (Exception e) {
            System.out.printf("Connection to %s lost\n", this.name);
            Thread.yield();
        }
    }
    
    public void startServer() {
     this.checkingHeartbeat();
     this.executor.execute(this);
        while (true) {
            try {
                Socket _client = this.serverSocket.accept();
                STCSG ms = new STCSG(_client);
                STCSG.clients.add(ms._client);
                try {            
                  STCSG.bw.write("Success\n");
                  STCSG.bw.flush();
                } catch (IOException e) {
                  System.out.println("error: sending success");
                  e.printStackTrace();
                }

            } catch (IOException e) {
                e.printStackTrace();
                break;
            }
        }
    }
    
    public static void main(String[] args) {
        System.out.println("Start!");
        STCSG ms = new STCSG();
        ms.startServer();        
    }
}

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

public class STCS extends Thread {//implements Runnable {
    
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
    private Socket _client2;
    BufferedWriter bw;
    private String name;
    
    public STCS() {
        
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        
        //create the server socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        group = new ArrayList<String>();
        clients =  new ArrayList<Socket>();
        heart_beat = new ConcurrentHashMap<String, Long>();
    }
    
    public STCS(Socket client) {
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
        for(int i = 0; i < STCS.group.size(); i++) {
            if(STCS.group.get(i).contains(sa[0])) {
                try {             
                    bw.write("Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        STCS.group.add(s);
        
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
        return STCS.group.remove(s);
    }
    //@Override
    public void run(){
      //listen for msg
      //check msg = heartbeat or get
      //execute accordingly
      //check if heartbeat is greater than time
      BufferedReader br;
      try {
        br = new BufferedReader(new InputStreamReader(_client.getInputStream()));
        while(true) {
          String m;
          m = br.readLine();
          System.out.println(m);
          if(m.equals("get")) {
            ObjectOutputStream oos = new ObjectOutputStream(this._client.getOutputStream());
            oos.writeObject(STCS.group);
            oos.flush();
          } else if(m.contains("heartbeat")) {
            Long l = System.currentTimeMillis();
            m = m.substring(m.indexOf('<'));
            if(STCS.heart_beat.put(m, l) == null) {
              System.out.println("updating failed");
            }
            System.out.printf("update time to %s\n", l.toString());
          }
          //Checking if hearbeat is still valid.
          System.out.println("Checking Heartbeat");
          /*try {
            Thread.sleep(1000);
          } catch (InterruptedException e) {
            e.printStackTrace();
          }*/
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
      } catch (Exception e) {
        System.out.printf("Connection to %s lost\n", this.name);
        Thread.yield();
      }
    }
    
    public void register() {
      while(true){
        //accept
        //STCS s = new STCS()l
        //if(group==Null) Thread lol = new Thread(s)
        //lol.heartbeat
        //start hb thread
        //addtogroup
        try {
          Socket _client = this.serverSocket.accept();
          //STCS ms = new STCS(_client);
          clients.add(_client);
        } catch (IOException e) {
          e.printStackTrace();
        }
        if(STCS.group.size()==0){
          //start hb thread
          //STCS hb = new STCS();
          // hb.start();
        }
        for(int i = 0; i < clients.size(); i++) {
          Socket temp = clients.get(i);
          BufferedReader br;
          try {
            br = new BufferedReader(new InputStreamReader(temp.getInputStream()));
            String m;
            m = br.readLine();
            System.out.println(m);
            /*if(().equals(null)){
              continue;//break;
            }*/
            if(m.contains("register")) {
              m = m.substring(m.indexOf('<'));
              //add to Group
              System.out.println("hi1");
              this.addToGroup(m);
              System.out.println("hi2");
              this.name = m;
              System.out.println("hi3");
              STCS.heart_beat.put(m, System.currentTimeMillis());
            } else if(m.equals("get")) {
              ObjectOutputStream oos = new ObjectOutputStream(this._client.getOutputStream());
              oos.writeObject(STCS.group);
              oos.flush();
            } else if(m.contains("heartbeat")) {
              Long l = System.currentTimeMillis();
              m = m.substring(m.indexOf('<'));
              if(STCS.heart_beat.put(m, l) == null) {
                System.out.println("updating failed");
              }
              System.out.printf("update time to %s\n", l.toString());
            }
          } catch (Exception e) {
            System.out.printf("Connection to %s lost\n", this.name);
            Thread.yield();
          }
        }
      }
    }
    
    public static void main(String[] args) {
        System.out.println("Start!");
        STCS ms = new STCS();
        ms.register();
    }
}
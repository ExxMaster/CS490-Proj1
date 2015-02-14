package edu.purdue.cs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultithreadedChatServer implements Runnable {
    
    //Constant Variable
    private static final int PORT = 1222;
    private static final int THREAD_POOL_CAPACITY = 10;
    
    private ServerSocket serverSocket;
    private static ArrayList<String> group;
    private static ConcurrentHashMap<String, Long> heart_beat;
    private ThreadPoolExecutor executor;
    
    //child server attributes
    private Socket _client;
    BufferedWriter bw;
    private String name;
    
    public MultithreadedChatServer() {
        
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        
        //create the server socket
        try {
            serverSocket = new ServerSocket(PORT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        group = new ArrayList<String>();
        heart_beat = new ConcurrentHashMap<String, Long>();
    }
    
    public MultithreadedChatServer(Socket client) {
        this._client = client;
        try {
            this.bw = new BufferedWriter(new OutputStreamWriter(this._client.getOutputStream()));
        } catch (Exception e) {
            System.out.println("init failed");
        }
    }
    
    public void startServer() {
    	this.checkingHeartbeat();
        while (true) {
            try {
                Socket _client = this.serverSocket.accept();
                MultithreadedChatServer ms = new MultithreadedChatServer(_client);
                this.executor.execute(ms);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    private synchronized boolean addToGroup(String s) {
        String [] sa = s.split(",");
        sa[0] = sa[0].substring(1);
        System.out.println(sa[0]);
        for(int i = 0; i < MultithreadedChatServer.group.size(); i++) {
            if(MultithreadedChatServer.group.get(i).contains(sa[0])) {
                try {             
                    bw.write("Failed\n");
                    bw.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return false;
            }
        }
        MultithreadedChatServer.group.add(s);
        
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
        return MultithreadedChatServer.group.remove(s);
    }
    
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
					Set<Entry<String, Long>> s = MultithreadedChatServer.heart_beat.entrySet();
					Iterator<Entry<String, Long>> i = s.iterator();
					if(i.hasNext()) {
						Entry<String, Long> entry = i.next();
						if(time - entry.getValue() > 10000) {
							MultithreadedChatServer.heart_beat.remove(entry.getKey());
							System.out.printf("client %s removed\n", entry.getKey());
							MultithreadedChatServer.removeFromGroup(entry.getKey());
						}
					}
				}
			}
    		
    	});
    }
    
    public static void main(String[] args) {
        System.out.println("Start!");
        MultithreadedChatServer ms = new MultithreadedChatServer();
        ms.startServer();
    }
    
    @Override
    public void run() {
        BufferedReader br;
        try {
            br = new BufferedReader(new InputStreamReader(_client.getInputStream()));
            while(true) {
                String m;
                m = br.readLine();
                System.out.println(m);
                if(m.contains("register")) {
                    m = m.substring(m.indexOf('<'));
                    //add to Group
                    this.addToGroup(m);
                    this.name = m;
                    MultithreadedChatServer.heart_beat.put(m, System.currentTimeMillis());
                } else if(m.equals("get")) {
                    ObjectOutputStream oos = new ObjectOutputStream(this._client.getOutputStream());
                    oos.writeObject(MultithreadedChatServer.group);
                    oos.flush();
                } else if(m.contains("heartbeat")) {
                    MultithreadedChatServer.heart_beat.put(m, System.currentTimeMillis());
                }
            }
        } catch (Exception e) {
            System.out.printf("Connection to %s lost\n", this.name);
            Thread.yield();
        }
    }
}
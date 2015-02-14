package edu.purdue.cs;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

class ChatClient implements Runnable{
    
    private String _name, _ip;
    private int _port;
    private Socket s;
    private ThreadPoolExecutor executor;
    
    //IO buffer
    BufferedWriter bw;
    BufferedReader br;
    
    //constant variable
    private static final int heartbeat_rate = 5;
    private static final String serverAddress = "localhost";
    private static final int portNumber = 1222;
    private static final int THREAD_POOL_CAPACITY = 10;
    
    private ChatClient() {
        
        try{
            s = new Socket(serverAddress, portNumber);
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }catch (IOException e) {
            System.out.println("Cannection to server failed");
            System.exit(1);
        }
        
        this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
        this._ip = s.getLocalAddress().toString().substring(1);
        this._port = s.getLocalPort();
        
    }
    
    //register client
    private boolean register() {
        //get the name
		Scanner sc = new Scanner(System.in);
		System.out.print("Please Enter Your Name: ");
		this._name = sc.nextLine();
		
		String m = "register<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
		//System.out.println(m);
		
		this.sendMessage(m);
		//System.out.println("Message sent to the server : " + m);

		m = this.readMessage();
		//System.out.println(m);
		
		return(m.equals("Success"));
    }
    
    //send heart beat every heartbeat_rate seconds
    private void sendHeartbeat() {
        this.executor.execute(this);
    }
    
    //write String s to the Socket
    private boolean sendMessage(String s) {
    	try {
			this.bw.write(s);
			this.bw.flush();
		} catch (IOException e) {
			return false;
		}
    	return true;
    }
    
    //read message from the socket
    private String readMessage() {
    	String s;
    	try {
			s = this.br.readLine();
		} catch (IOException e) {
			return null;
		}
    	return s;
    }

	@Override
	public void run() {
		try {
            while(true) {
                String m = "heartbeat<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
                this.sendMessage(m);
                //System.out.println("Message sent to the server : " + m);
                Thread.sleep(heartbeat_rate * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
	}
	
	private void prompt() {
		Scanner sc = new Scanner(System.in);
		while(true) {
			try {
				System.out.print("> ");
				String command = sc.nextLine();
				if(command.equals("exit")) {
					sc.close();
					System.exit(1);
				} else if(command.equals("get")) {
					this.get();
				} else if(command.contains("chat")) {
					this.chat(command);
				} else {
					System.out.println("Command not found");
				}
			} catch(Exception e) {
				System.out.println("exit");
				System.exit(1);
				
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	private ArrayList<String> get() {
		try {
			String m = "get\n";
			this.sendMessage(m);
			ObjectInputStream ois = new ObjectInputStream(this.s.getInputStream());
			ArrayList<String> a = (ArrayList<String>) ois.readObject();
			System.out.println(a.toString());
		} catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void chat(String s) {
		this.executor.execute(new Runnable() {

			@Override
			public void run() {
				
			}
			
		});
	}
    
    public static void main(String[] args) throws Exception {
        ChatClient cc = new ChatClient();
        
        while(true) {
            if(cc.register()) break;
        }
        
        cc.sendHeartbeat();
        cc.prompt();
    }
}
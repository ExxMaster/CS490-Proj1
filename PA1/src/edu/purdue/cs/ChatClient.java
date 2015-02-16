//package edu.purdue.cs;

import java.io.*;
import java.net.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
class ChatClient implements Runnable{
    
    private String _name, _ip;
    private int _port;
    
    //Connection to server
    private Socket s;
    
    //ThreadPool
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
    @SuppressWarnings("resource")
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
    
    @SuppressWarnings("unchecked")
    private ArrayList<String> chat(String message) {
    	String name1 = "";
		String msg1 = "";
		String user = "";
		System.out.println("here in chat");
		
		/*
		Socket ch;
		try{
            ch = new Socket(serverAddress, portNumber);
            chbw = new BufferedWriter(new OutputStreamWriter(ch.getOutputStream()));
           // chbr = new BufferedReader(new InputStreamReader(ch.getInputStream()));
        }catch (IOException e) {
            System.out.println("Cannection to server failed");
            System.exit(1);
        }*/
        
        
        
    	try{
    		//String mydata = "some string with 'the data i want' inside";
			//Pattern pattern = Pattern.compile("[A-Za-z0-9.,?!$#@\-\=\+\%\* ]+");
			Pattern pattern = Pattern.compile("'(.*?)'");
			Matcher matcher = pattern.matcher(message);
			
			if (matcher.find())
			{
				System.out.println(matcher.group(1));
				name1 = matcher.group(1);
			}
			if (matcher.find())
			{
				System.out.println(matcher.group(1));
				msg1 = matcher.group(1);
			}
			
			this.sendMessage("chat\n");
    		ObjectInputStream ois = new ObjectInputStream(this.s.getInputStream());
            ArrayList<String> a = (ArrayList<String>) ois.readObject();
            //System.out.println(a.toString());
            //System.out.println("hdhasdgashdghasdgsah" + a.size());
            for(int i = 0; i < a.size(); i++) {
				user = a.get(i).toString();
				System.out.println("user: "+user);
				if(user.contains(name1) && name1!=null && msg1!=null){
					//TODO parse user to get ip and port
					// create socket to bufferwrite to port
					// bw.write msg1
					//this.sendMessage(msg1);
				}			
			}
            
    		
    	} catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    private void recieve(){
    	String user = "";
    	//while(true){
			//ObjectInputStream ois = new ObjectInputStream(this.s.getInputStream());
		  //  ArrayList<String> a = (ArrayList<String>) ois.readObject();
			//for(int i = 0; i < a.size(); i++) {
				//user = a.get(i).toString();
				//TODO parse user to get ip and port
				// make socket to buffer read from port
				// String msg = br.readLine();
				// System.out.println(msg);
			//}
		//}
    }
    
    public static void main(String[] args) throws Exception {
        ChatClient cc = new ChatClient();
        
        while(true) {
            if(cc.register()) break;
        }
       // Thread t = new Thread();
        //t.recieve();
        cc.sendHeartbeat();
        cc.prompt();
    }
}

package edu.purdue.cs;

import java.io.*;
import java.net.*;
import java.util.*;

class ChatClient {
    
    private String _name, _ip;
    private int _port;
    private Socket s;
    
    //IO buffer
    BufferedWriter bw;
    BufferedReader br;
    
    //constant variable
    private static final int heartbeat_rate = 5;
    private static final String serverAddress = "localhost";
    private static final int portNumber = 1222;
    
    private ChatClient() {
        
        try{
            s = new Socket(serverAddress, portNumber);
            bw = new BufferedWriter(new OutputStreamWriter(s.getOutputStream()));
            br = new BufferedReader(new InputStreamReader(s.getInputStream()));
        }catch (IOException e) {
            System.out.println("Cannection to server failed");
            System.exit(1);
        }
        
        
        this._ip = s.getLocalAddress().toString().substring(1);
        this._port = s.getLocalPort();
        
    }
    
    //register client
    private boolean register() {
        //get the name
		Scanner sc = new Scanner(System.in);
		System.out.print("Please Enter Your Name: ");
		this._name = sc.nextLine();
		sc.close();
		
		String m = "register<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
		System.out.println(m);
		
		this.sendMessage(m);
		System.out.println("Message sent to the server : " + m);
		
		if(s.isConnected()) {
			System.out.println("alive");
		}
		m = this.readMessage();
		System.out.println("sdadasd");
		System.out.println(m);
		
		return(m.equals("Success"));
    }
    
    //send heart beat every heartbeat_rate seconds
    private void sendHeartbeat() {
        try {
            while(true) {
                if(this.s.isConnected()) {
                    System.out.println("Still Alive");
                } else {
                    System.out.println("Dead Already");
                }
                
                String m = "heartbeat<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
                System.out.println(m);
                this.sendMessage(m);
                System.out.println("Message sent to the server : " + m);
                Thread.sleep(heartbeat_rate * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
    
    public static void main(String[] args) throws Exception {
        ChatClient cc = new ChatClient();
        
        while(true) {
            if(cc.register()) break;
        }
        
        cc.sendHeartbeat();
    }
    
}
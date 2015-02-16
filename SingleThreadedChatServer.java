//package edu.purdue.cs;

import java.io.*;
import java.net.*;
import java.util.*;


	
class Client{
	public static String name;
	public static String ip;
	public static int port;
	
	public Client(String name, String ip, int port){
		this.name = name;
		this.ip = ip;
		this.port = port;
	}
}

class SingleThreadedChatServer{

	public static ArrayList<Client> user = new ArrayList<Client>();
	public static int portNumber;
	public static int numofusers = 0;
	public static void main(String[] args) throws Exception{
		if(args.length<1){
			System.out.println("Must enter port number");
			return;
		}
		portNumber = Integer.parseInt(args[0]);
		Register r = new Register();
		r.start();
		//b.start();
		//Heartbeat h = new Heartbeat();
		//h.start();
		
	}
}

class Register extends Thread {
	ServerSocket serverSocket = null;	
 	private static Socket sock;
	private String _name, _ip;
	private int _port;
	public void run(){
	
    	//String address = "localhost";
    	
    	int port = SingleThreadedChatServer.portNumber;
    	try{
	    	serverSocket = new ServerSocket(port);	
    	}catch (IOException e) {
            System.out.println("ServerSocket failed");
            System.exit(1);
        }
    	
		
		try{
			while(true){
			System.out.println("waiting for user");
				sock = serverSocket.accept();	
			System.out.println("accepted user");
				InputStream is = sock.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				_name = br.readLine();
				System.out.println("name: "+_name);
				//parse name
				_name="David";
				_ip = "123.4566.789";
				_port = 12345;
				Client c = new Client(_name, _ip, _port);
				if(!SingleThreadedChatServer.user.contains(c)){
					SingleThreadedChatServer.user.add(c);
	   				SingleThreadedChatServer.numofusers++;
					Heartbeat h = new Heartbeat();
					h.start();
				}else{
					System.out.println("User already in G");
				}
			}
	   	}catch(IOException e){
	   		System.exit(1);
	   	}

 	}
}




/*
class Broadcast extends Thread {
 public String Register(String s){
  //if s is in G return false else return true
 }
 public void run(){
  System.out.println("Register is running");
  
 }
}
*/

class Heartbeat extends Thread {
 	ServerSocket hbserv = null;
	Socket hbsock;
	int heartbeat_rate = 5;
	public void run(){
		System.out.println("Heartbeat is running");
		try{
			hbserv = new ServerSocket(1111);
		}catch(IOException e){
			System.out.println("Heartbeat failer");
			System.exit(1);
		}
		try{
			while(true){
				hbsock = hbserv.accept();
				InputStream is = hbsock.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader br = new BufferedReader(isr);
				String _msg = br.readLine();
				//PrintWrite pw = 
				if(_msg.equals("get")){
					int i=0;
					while(i<SingleThreadedChatServer.numofusers){
						Client temp = SingleThreadedChatServer.user.get(i);
						System.out.println("name: "+temp.name);
					}
				}else{
				//parse name
				//Client c = new Client(_name, _ip, _port);
				//check heartbeat rate
				//if(heartbeat_rate< c.heartbeat){
				//	SingleThreadedChatServer.user.remove(c);
				//	SingleThreadedChatServer.numofusers--;
				//}else{
					//c is alive
				//}
				}
			}


		}catch(IOException e){
			System.out.println("Heartbeat disc");
		}
	
	}
}



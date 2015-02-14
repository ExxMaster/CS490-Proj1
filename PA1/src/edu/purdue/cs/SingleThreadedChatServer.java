package edu.purdue.cs;

import java.io.*;
import java.net.*;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.concurrent.ConcurrentHashMap;

class SingleThreadedChatServer implements Runnable {
	
    private static final int portNumber = 6666;
    
	private static ConcurrentHashMap<String, Socket> _clients;
	public static ConcurrentHashMap<String, Long> group;
	private static Calendar calendar;
	private static ServerSocket serverSocket;
	
	public SingleThreadedChatServer() {
		_clients = new ConcurrentHashMap<String, Socket>();
		group = new ConcurrentHashMap<String, Long>();
		calendar = Calendar.getInstance();
		
		try {
			serverSocket = new ServerSocket(portNumber);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
    
    @SuppressWarnings("unused")
	public static void main(String[] args) {
        
    	SingleThreadedChatServer ss = new SingleThreadedChatServer();
    	
    	
    	
        try {
            while(true) {
                Socket _client = SingleThreadedChatServer.serverSocket.accept();
                InputStream is = _client.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String number = br.readLine();
                
                SingleThreadedChatServer._clients.put(number, _client);
                SingleThreadedChatServer.group.put(number, SingleThreadedChatServer.calendar.getTimeInMillis());
                
                System.out.println("Message received from client is "+number);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



	@Override
	public void run() {
		while(true) {
			Enumeration<Socket> e = SingleThreadedChatServer._clients.elements();
			while(e.hasMoreElements()) {
				try {
					BufferedReader br = new BufferedReader(new InputStreamReader(e.nextElement().getInputStream()));
					
					//not robust enough
					if(br.ready()) {
						br.readLine();
						SingleThreadedChatServer.processMessage();
					}
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	//TODO: processing message
	private static void processMessage() {
		
	}
}
package edu.purdue.cs;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.LinkedList;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class MultithreadedChatServer implements Runnable {
	
	//Constant Variable
	private static final int PORT = 1222;
	private static final int THREAD_POOL_CAPACITY = 10;
	
	private ServerSocket serverSocket;
	public static LinkedList<String> group;
	private ThreadPoolExecutor executor;
	
	private Socket _client;
	OutputStream os;
	OutputStreamWriter osw;
	BufferedWriter bw;
	
	public MultithreadedChatServer() {
		
		this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(THREAD_POOL_CAPACITY);
		
		//create the server socket
		try {
			serverSocket = new ServerSocket(PORT);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		group = new LinkedList<String>();
	}
	
	public MultithreadedChatServer(Socket client) {
		this._client = client;
		try {
			this.os = this._client.getOutputStream();
			this.osw = new OutputStreamWriter(os);
	        this.bw = new BufferedWriter(osw);
		} catch (Exception e) {
			System.out.println("init failed");
		}
	}
	
	public void startServer() {
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
            if(_client.isConnected()) {
            	System.out.println("still alive");
            }
            bw.flush();
            System.out.println("Message sent");
		} catch (IOException e) {
			System.out.println("error: sending success");
			e.printStackTrace();
		}
		
		return true;
	}
	
	public static void main(String[] args) {
		System.out.println("Start!");
		MultithreadedChatServer ms = new MultithreadedChatServer();
		ms.startServer();
	}

	@Override
	public void run() {
		InputStream is;
		InputStreamReader isr;
		BufferedReader br;
		try {
			is = _client.getInputStream();
			isr = new InputStreamReader(is);
			br = new BufferedReader(isr);
			while(true) {
				String m;
				m = br.readLine();
				System.out.println(m);
				if(m.contains("register")) {
					m = m.substring(m.indexOf('<'));
					//add to Group
					if(this.addToGroup(m)) {
						System.out.println("successfully added");
					} else {
						System.out.println("failed");
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
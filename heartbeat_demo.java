package edu.purdue.cs;

import java.io.*;
import java.net.*;

class SingleThreadedChatServer{
    
	private static Socket _client;
    private static final int portNumber = 6666;
    
    public static void main(String[] args) {
        
        try {
            @SuppressWarnings("resource")
			ServerSocket serverSocket = new ServerSocket(portNumber);
            
            _client = serverSocket.accept();
            while(true) {
            	System.out.println("entering");
                InputStream is = _client.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String number = br.readLine();
                System.out.println("Message received from client is "+number);
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                _client.close();
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }
}
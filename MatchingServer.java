//MatchingServer


import java.io.*;
import java.net.*;
import java.util.*;
//import java.util.Scanner;


class MatchingServer{
    
    //TODO create a thread pool to track active games and manage joining the threads before server closes.
    //TODO create a scanner to listen for an exit command for the server to exit gracefully and join all threads.
    
    public static void main(String[] args) throws IOException{
        //argument 1 should be the port number
        //check if number of argument is valid
        if(args.length != 1){
            System.err.println("arguments failed\n");
            System.exit(1);
        }
        int portNumber = Integer.parseInt(args[0]);
        
        //set up server controller
        
        try{
            //set up the server and client sockets
            ServerSocket serverSocket = new ServerSocket(portNumber);
            
            
            while(System.in.available() == 0){
                //Server waits for two clients to connect to it
                System.out.println("waiting for clients\n");
                Socket clientSocketA = serverSocket.accept();
                System.out.println("connected to client A\n");
                Socket clientSocketB = serverSocket.accept();
                System.out.println("connected to client B\n");
                //Set up a game server on a new thread
                GameServer gameServer = new GameServer(clientSocketA, clientSocketB);
                Thread gameThread = new Thread(gameServer);
                System.out.println("starting GameServer in new thread\n");
                gameThread.start();
            }
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("socket failure:\n" + e.getMessage() + "\n");
            System.exit(1);
        }
        return;
    }
}

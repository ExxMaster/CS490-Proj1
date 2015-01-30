
import java.io.*;
import java.net.*;
import java.util.*;
class SingleThreadedChatServer{
  
  public static void main(String[] args){
    
    if(args.length != 1){
      System.err.println("arguments failed\n");
      System.exit(1);
    }
    int portNumber = Integer.parseInt(args[0]);
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
        ChatClient chatclient = new ChatClient(clientSocketA, clientSocketB);
        Thread chatthread = new Thread(chatclient);
        System.out.println("starting GameServer in new thread\n");
        chatthread.start();
      }
      serverSocket.close();
    }
    catch (IOException e) {
            System.err.println("socket failure:\n" + e.getMessage() + "\n");
            System.exit(1);
    }
    return;
  }
}
/*
while ( true ) {
accept connection from client c ;
receive message m from client c ;
if ( m == register < name , ip , port >)
// process registration , and send an a c k n o w l e d g e m e n t / failure to c
else if (( m == heartbeat < name >)
if ( previous heartbeat < name > was received less than
hear tbeat_ra te seconds ago )
// keep client c alive
else
// remove client c from G
else if ( m == get )
// send G to client c
else
// invalid message from client c , server closes c o n n e c t i o n ;
}*/

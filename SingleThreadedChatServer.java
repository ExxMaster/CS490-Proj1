
import java.io.*;
import java.net.*;
import java.util.*;

class SingleThreadedChatServer{
  
 public static void main(String[] args) throws IOException {
  
  int portNumber = 5555;
  
  String[] G;
  
  //set up the server sockets
  ServerSocket serverSocket = new ServerSocket(portNumber);
  
  try {
   while( true ){
     System.out.println("Listening for clients");
     //accept connection from client c
     Socket client = serverSocket.accept();
     System.out.println("Client found!");
    try {
     //receive message m from client c
      BufferedReader input = new BufferedReader(new InputStreamReader(client.getInputStream()));
        String answer = input.readLine();
        System.out.printf("%s\n", answer);
    } finally {
     client.close();
    }
   }
  } finally {
   serverSocket.close();
  }
 }
}

/*
while ( true ) {
  accept connection from client c ;
  receive message m from client c ;
  if ( m == register < name , ip , port >){
    // process registration , and send an a c k n o w l e d g e m e n t / failure to c
  }else if (( m == heartbeat < name >){
    if ( previous heartbeat < name > was received less than hear tbeat_ra te seconds ago ){
      // keep client c alive
    }else{
   // remove client c from G
   }
  }else if ( m == get ){
    // send G to client c
  }else{
    // invalid message from client c , server closes c o n n e c t i o n ;
  }
}*/

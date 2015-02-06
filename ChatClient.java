import java.io.*;
import java.net.*;
import java.util.*;
//http://stackoverflow.com/questions/1436884/multithreaded-chat-server-in-java
class ChatClient {
  
  static int heartbeat_rate = 5;
    
  public static void main(String[] args) throws IOException {

    Scanner sc = new Scanner(System.in);
    System.out.print("Please Enter Your Name: ");
    String _name = sc.nextLine();
    String heartbeat = "anything";
    String serverAddress = "data.cs.purdue.edu";
    Socket s = null;
    BufferedReader input = null;
    PrintWriter out = null;
    try{
        s = new Socket(serverAddress, 5555);
        input = new BufferedReader(new InputStreamReader(s.getInputStream()));
        out = new PrintWriter(s.getOutputStream(), true);
    }catch (IOException e) {
      System.out.println("Cannection to server failed");
      System.exit(1);
    }
    //output to server
    out.printf("<%s, %s, %d>\n", _name, s.getLocalAddress().toString(), s.getLocalPort());
  }
}

import java.io.*;
import java.net.*;
import java.util.*;

class ChatClient {
  
  static int heartbeat_rate = 5;
    
  public static void main(String[] args) throws IOException {

    Scanner sc = new Scanner(System.in);
    System.out.print("Please Enter Your Name: ");
    String _name = sc.nextLine();
    
    String serverAddress = "moore01.cs.purdue.edu";
    Socket s = new Socket(serverAddress, 5555);
    PrintWriter out = new PrintWriter(s.getOutputStream(), true);
    //output to server
    out.printf("<%s, %s, %d>\n", _name, s.getLocalAddress().toString(), s.getLocalPort());
    
  }
}

import java.io.*;
import java.net.*;
import java.util.*;

class ChatClient {
    
    private String _name, _ip;
    private int _port;
    private Socket s;
    
    //constant variable
    private static final int heartbeat_rate = 5;
    private static final String serverAddress = "localhost";
    private static int portNumber = 0;

    
    private ChatClient() {
        //get the name
        Scanner sc = new Scanner(System.in);
        System.out.print("Please Enter Your Name: ");
        this._name = sc.nextLine();
        sc.close();
        
        try{
            s = new Socket(serverAddress, portNumber);
        }catch (IOException e) {
            System.out.println("Connection to server failed");
            System.exit(1);
        }
        
        this._ip = s.getLocalAddress().toString().substring(1);
        this._port = s.getLocalPort();
        
    }
    
    //register client
    private boolean register() {
        try {
            //Send the register message to the server
            OutputStream os = this.s.getOutputStream();
            OutputStreamWriter osw = new OutputStreamWriter(os);
            BufferedWriter bw = new BufferedWriter(osw);
            
            String m = "register<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
            System.out.println(m);
            
            bw.write(m);
            bw.flush();
            System.out.println("Message sent to the server : " + m);
            
        } catch (IOException e) {
            System.out.println("error: register");
        }
        return true;
    }
    
    private void sendHeartbeat() {
        try {
            while(true) {
                if(this.s.isConnected()) {
                    System.out.println("Still Alive");
                } else {
                    System.out.println("Dead Already");
                }
                OutputStream os = this.s.getOutputStream();
                OutputStreamWriter osw = new OutputStreamWriter(os);
                BufferedWriter bw = new BufferedWriter(osw);
                
                String m = "heartbeat<" + this._name + ", " + this._ip + ", " +  this._port + ">" + "\n";
                System.out.println(m);
                bw.write(m);
                bw.flush();
                System.out.println("Message sent to the server : " + m);
                Thread.sleep(heartbeat_rate * 1000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void main(String[] args) throws Exception {
        portNumber = Integer.parseInt(args[0]);
        ChatClient cc = new ChatClient();
        cc.register();
        cc.sendHeartbeat();
    }
    
}
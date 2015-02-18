# CS490-Proj1

For Server:

Must have a ArrayList<String> to hold registration message for clients

If recieve "register<$name, $ip, $port>", put <$name, $ip, $port> in ArrayList<String> if the $name is not already used by some other clients
If registration process finished, send "Success\n" back to client. If not send "Failed\n".

If recieve "get" send ArrayList<String> to the client

If recieve "heartbeat<$name, $ip, $port>", update the heart beat table.

that is all the interaction between client and server, if anything is unclear, check out the public void run() in the MutiThreadedChatServer.

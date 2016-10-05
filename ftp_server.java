import java.io.* ;
import java.net.* ;
import java.util.* ;

public class ftp_server {
	private static final int BUFSIZE = 256;   // Size of receive buffer
    private static final int CMDLENGTH = 4;   // String length of commands
	
	public static void main(String argv[]) throws Exception {
		int controlPort = 2264;
		int dataPort = 2265;
		int recvMsgSize;   // Size of received message
		byte[] byteBuffer = new byte[BUFSIZE];  // Receive buffer
		
		// Establish the listen socket.
		ServerSocket controlListen = new ServerSocket(controlPort);
		
		// Process HTTP service requests in an infinite loop.
		while (true) {
			// Listen for a TCP connection request.
			Socket controlConnection = controlListen.accept();
			
			InputStream inFromClient_Control = controlConnection.getInputStream();
			OutputStream outToClient_Control = controlConnection.getOutputStream();
			
			
			System.out.println("Client connected to Server: CONTROLLER");
			System.out.println("");
			
			// Receive until client closes connection, indicated by -1 return
			while ((recvMsgSize = inFromClient_Control.read(byteBuffer)) != -1) {
				// create cmdReceived
				String cmdReceived = new String(byteBuffer,0,CMDLENGTH);
                String secondArg = new String(byteBuffer,CMDLENGTH,recvMsgSize);
				
                /*
                 *  NOTE: The secondArg does not flush. i.e. 
                 *      1. stor file_1.txt
                 *      2. list
                 *
                 *      The second arg is still going to be file_1.txt.
                 *      This is because it is left over from before.
                 *
                 */
                
				// open dataConnection socket
				Socket dataConnection = new Socket(controlConnection.getInetAddress(), dataPort);
				System.out.println("Connected to client...DATA LINE");
				
				System.out.println("Message Size: " + recvMsgSize);
				System.out.println("cmd Received: " + cmdReceived.toLowerCase());
                System.out.println("Second Arg Received: " + secondArg);
				
				// based on cmdReceived do something
				if (cmdReceived.toLowerCase().equals("list")) {
					
					System.out.println("Running " + cmdReceived);
					
				} else if (cmdReceived.toLowerCase().equals("retr")) {
					
					System.out.println("Running " + cmdReceived);
					
				} else if (cmdReceived.toLowerCase().equals("stor")) {
					
					System.out.println("Running " + cmdReceived);
					
				} else if (cmdReceived.toLowerCase().equals("quit")) {
					
					System.out.println("Running " + cmdReceived);
					
					dataConnection.close();
					System.out.println("Server DATA LINE Closed");
					
					controlConnection.close();
					System.out.println("Server CONTROLLER Closed");
					
					System.exit(0);
				}
				
				dataConnection.close();
				System.out.println("Server DATA LINE Closed");
				System.out.println("");
			}
			
			// Construct an object to process the HTTP request message.
			//HttpRequest request = new HttpRequest(connection);
			
			// Create a new thread to process the request.
			//Thread thread = new Thread(request);
			
			// Start the thread.
			//thread.start();
		}
	}
}




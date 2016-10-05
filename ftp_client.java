import java.io.* ;
import java.net.* ;
import java.util.* ;

public class ftp_client {
	private static final int BUFSIZE = 256;   // Size of receive buffer
    
	public static void main(String[] args) throws IOException {
		int controlPort = 2264;
		int dataPort = 2265;
		String server = args[0];	   // Server name or IP address
		String cmd = new String("");
		Scanner console = new Scanner(System.in);
		boolean timeToQuit = false;
        byte[] byteBuffer = new byte[BUFSIZE];  // sender buffer
		
		if (args.length != 1)  // Test for correct # of args
			throw new IllegalArgumentException("Parameter(s): <Server>");
		
		// Create socket that is connected to server on specified port
		Socket controlConnection = new Socket(server, controlPort);
		System.out.println("Connected to server...CONTROLLER");
		System.out.println("");
		
		InputStream inFromServer_Control = controlConnection.getInputStream();
		OutputStream outToServer_Control = controlConnection.getOutputStream();
		
		System.out.print("cmd: ");
		cmd = console.nextLine();
		
		// Establish the listen socket.
		ServerSocket dataListen = new ServerSocket(dataPort);
		
		while (!timeToQuit) {
			
            outToServer_Control.write(cmd.getBytes());
            
			// Listen for a TCP connection request.
			Socket dataConnection = dataListen.accept();	// This socket goes with ServerSocket
			
			InputStream inFromServer_Data = dataConnection.getInputStream();
			OutputStream outToServer_Data = dataConnection.getOutputStream();
			
			System.out.println("Connected to server...DATA LINE");
			
			// close dataConnection
			dataConnection.close();
			System.out.println("Client DATA LINE Closed");
			System.out.println("");
			
			// if quiting, then indicate so; else, get next command.
			if (cmd.toLowerCase().equals("quit")) {
				timeToQuit = true;
			} else {
				System.out.print("cmd: ");
				cmd = console.next();
			}
		}
		
		/*
		InputStream in = socket.getInputStream();
		OutputStream out = socket.getOutputStream();
		
		out.write(byteBuffer);  // Send the encoded string to the server
		
		// Receive the same string back from the server
		int totalBytesRcvd = 0;  // Total bytes received so far
		int bytesRcvd;		   // Bytes received in last read
		while (totalBytesRcvd < byteBuffer.length) {
			if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd,
									 byteBuffer.length - totalBytesRcvd)) == -1)
				throw new SocketException("Connection close prematurely");
			totalBytesRcvd += bytesRcvd;
		}
		
		System.out.println("Received: " + new String(byteBuffer));
		*/
		controlConnection.close();  // Close the socket and its streams
		System.out.println("Client CONTROLLER Closed");
	}
}

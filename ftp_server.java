import java.io.* ;
import java.net.* ;
import java.util.* ;

public class ftp_server {
	public static void main(String argv[]) throws Exception {
		int controlPort = 2264;
		
		// Establish the listen socket.
		ServerSocket controlListen = new ServerSocket(controlPort);
		
		// Listen for a TCP connection request.
		Socket controlConnection = controlListen.accept();
        
        // Construct an object to process the command request
        CmdRequest request = new CmdRequest(controlConnection);
        
		// Create a new thread to process the request.
		Thread thread = new Thread(request);
		
		// Start the thread.
		thread.start();
	}
}




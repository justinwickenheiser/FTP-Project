import java.io.* ;
import java.net.* ;
import java.util.* ;

public class ftp_client {
	private static final int BUFSIZE = 256;   // Size of receive buffer
	private static final int CMDLENGTH = 4;   // String length of commands
    
	public static void main(String[] args) throws IOException {
		int controlPort = 2264;
		int dataPort = 2265;
		String server = args[0];	   // Server name or IP address
		String cmd = new String("");
		Scanner console = new Scanner(System.in);
		boolean timeToQuit = false;
        int recvMsgSize;   // Size of received message
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
			
			// write cmd over CONTROL line 
	        outToServer_Control.write(cmd.getBytes());
            
			// Listen for a TCP connection request.
			Socket dataConnection = dataListen.accept();	// This socket goes with ServerSocket
			
			InputStream inFromServer_Data = dataConnection.getInputStream();
			OutputStream outToServer_Data = dataConnection.getOutputStream();
			
			System.out.println("Connected to server...DATA LINE");

			if (cmd.toLowerCase().indexOf("list") != -1) {
				// Receive until server closes connection, indicated by -1 return
				while ((recvMsgSize = inFromServer_Data.read(byteBuffer)) != -1) {
					System.out.println(new String(byteBuffer, 0, recvMsgSize));
				}
			} else if (cmd.toLowerCase().indexOf("retr") != -1) {

				String fileRequestedString = new String(cmd.getBytes(), CMDLENGTH+1, cmd.length()-CMDLENGTH-1);

				// Receive until server closes connection, indicated by -1 return
				while ((recvMsgSize = inFromServer_Data.read(byteBuffer)) != -1) {
					try {
						File fileRequested = new File(fileRequestedString);
						FileOutputStream fileOutputStream = new FileOutputStream(fileRequested);
						fileOutputStream.write(byteBuffer);
						fileOutputStream.close();
					} catch (Exception e) {
						System.out.println("Error trying to write file.");
					}
				}
			} else if (cmd.toLowerCase().indexOf("stor") != -1) {
				String fileSendingString = new String(cmd.getBytes(), CMDLENGTH+1, cmd.length()-CMDLENGTH-1);
				File fileToSend = new File(fileSendingString);

				// if File exists write file, otherwise write error message
				if (fileToSend.exists()) {
					try {
						// declare variables for converting file to byte[]
						FileInputStream fileInputStream = new FileInputStream(fileToSend);
						byte[] fileByteArray = new byte[(int) fileToSend.length()];

						// convert file
						fileInputStream.read(fileByteArray);
						fileInputStream.close();

						// write to client over DATA line
						outToServer_Data.write( fileByteArray );
					} catch (Exception e) {
						e.printStackTrace();
					}

					// wait for server to respond
					while ((recvMsgSize = inFromServer_Data.read(byteBuffer)) != -1) {
						System.out.println(new String(byteBuffer,0,recvMsgSize));
					}
				} else {
					System.out.println("File does not exist.");
				}
			}

			// if cmd is NOT stor, close data connection, otherwise it is handled in stor
/*			if (cmd.toLowerCase().indexOf("stor") == -1) {
				// close dataConnection
				dataConnection.close();
				System.out.println("Client DATA LINE Closed");
				System.out.println("");
			}
*/
			// close dataConnection
			dataConnection.close();
			System.out.println("Client DATA LINE Closed");
			System.out.println("");

			// if quiting, then indicate so; else, get next command.
			if (cmd.toLowerCase().equals("quit")) {
				timeToQuit = true;
			} else {
				System.out.print("cmd: ");
				cmd = console.nextLine();
			}
		}
		
		controlConnection.close();  // Close the socket and its streams
		System.out.println("Client CONTROLLER Closed");
	}
}

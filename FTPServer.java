import java.io.*;
import java.net.*;
import java.util.*;


public class FTPServer {

	public FTPServer(int listenPort) throws IOException {
		// Establish the listen socket.
		ServerSocket serverListenSocket = new ServerSocket(listenPort);
		ServerSocket dataListenSocket = new ServerSocket(2265);
		while (true) {
			// Listen for a TCP connection request.
			Socket clientSocket = serverListenSocket.accept();

			HandlerFTP handlerForClient = new HandlerFTP(clientSocket, dataListenSocket);
			handlerForClient.start();
		}
	}

	public static void main (String args[]) throws IOException { 
		if (args.length != 1) {
			throw new RuntimeException ("Syntax: java FTPServer <listen_port>\nSuggested port: 2264"); 
		}
		new FTPServer(Integer.parseInt (args[0])); 
	}
	
}
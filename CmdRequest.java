import java.io.* ;
import java.net.* ;
import java.util.* ;

final class CmdRequest implements Runnable {
    private static final int BUFSIZE = 256;   // Size of receive buffer
    private static final int CMDLENGTH = 4;   // String length of commands
    
    final int dataPort = 2265;
    int recvMsgSize;   // Size of received message
    byte[] byteBuffer = new byte[BUFSIZE];  // Receive buffer
    Socket controlConnection;
    
    // Constructor
    public CmdRequest(Socket socket) throws Exception {
        this.controlConnection = socket;
    }
    
    // Implement the run() method of the Runnable interface.
    public void run() {
        try {
            processRequest();
        } catch (Exception e) {
            System.out.println(e);
        }
    }
    
    private void processRequest() throws Exception {
        InputStream inFromClient_Control = controlConnection.getInputStream();
        OutputStream outToClient_Control = controlConnection.getOutputStream();
        
        
        System.out.println("Client connected to Server: CONTROLLER");
        System.out.println("");
        
        // Receive until client closes connection, indicated by -1 return
        while ((recvMsgSize = inFromClient_Control.read(byteBuffer)) != -1) {
            // create cmdReceived
            String cmdReceived = new String(byteBuffer,0,CMDLENGTH);
            String secondArg = new String(byteBuffer,CMDLENGTH+1,recvMsgSize);
            
            // open dataConnection socket
            Socket dataConnection = new Socket(controlConnection.getInetAddress(), dataPort);
            System.out.println("Connected to client...DATA LINE");
            
            InputStream inFromClient_Data = dataConnection.getInputStream();
            OutputStream outToClient_Data = dataConnection.getOutputStream();
            
            // based on cmdReceived do something
            if (cmdReceived.toLowerCase().equals("list")) {
                
                System.out.println("Running " + cmdReceived);
                File directory = new File(".");
                File[] listOfFiles = directory.listFiles();
                String fileName = new String("");
                // Loop through the number of files and write each one
                for (int i = 0; i < listOfFiles.length; i++) {
                    // get File name
                    if (listOfFiles[i].isFile()) {
                        fileName = listOfFiles[i].getName();
                    } else if (listOfFiles[i].isDirectory()) {
                        fileName = listOfFiles[i].getName() + "/";
                    }
                    
                    // write to client over DATA line
                    outToClient_Data.write( fileName.getBytes() );
                }
                
            } else if (cmdReceived.toLowerCase().equals("retr")) {
                
                System.out.println("Running " + cmdReceived);
                File fileToRetr = new File(secondArg);
                
                // if File exists write file, otherwise write error message
                if (fileToRetr.exists()) {
                    try {
                        // declare variables for converting file to byte[]
                        FileInputStream fileInputStream = new FileInputStream(fileToRetr);
                        byte[] fileByteArray = new byte[(int) fileToRetr.length()];
                        
                        // convert file
                        fileInputStream.read(fileByteArray);
                        fileInputStream.close();
                        
                        // write to client over DATA line
                        outToClient_Data.write( fileByteArray );
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    // write error message to client over DATA line
                    String errMsg = new String("File does not exist.");
                    outToClient_Data.write( errMsg.getBytes() );
                }
                
            } else if (cmdReceived.toLowerCase().equals("stor")) {
                
                System.out.println("Running " + cmdReceived);
                
                while ((recvMsgSize = inFromClient_Data.read(byteBuffer)) != -1) {
                    try {
                        File fileToStore = new File(secondArg);
                        FileOutputStream fileOutputStream = new FileOutputStream(fileToStore);
                        fileOutputStream.write(byteBuffer);
                        fileOutputStream.close();
                    } catch (Exception e) {
                        String storMsg = new String("Message failed to store.");
                        System.out.println(storMsg);
                    }
                }
                
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
    }
}
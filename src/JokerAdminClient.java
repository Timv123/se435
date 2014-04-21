import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;



public class JokerAdminClient {
	public static void main(String args[]) {
		String serverName;      
		System.out.println(args.length);
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];
		System.out.println("AdminClient version 0.0.1");
		System.out.println("Using server: " + serverName + ", Port: 2565");
		
		//While user's input is NOT "quit" send off to get a remoteAddress
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String mode;
			do {
				System.out.print("Enter a mode: Jokes, Proverb or Admin, (quit) to end: ");
				System.out.flush();
				mode = in.readLine();
				if (mode.indexOf("quit") < 0)
					sendModeToServer(mode, serverName);
			} while (mode.indexOf("quit") < 0); // quitSmoking.com?
												// System.out.println
												// ("Cancelled by user request.");
		} catch (IOException x) {
			x.printStackTrace();
		}
	}

	static String toText(byte ip[]) { /* Make portable for 128 bit format */
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0)
			result.append(".");
			result.append(0xff & ip[i]);
		}
		return result.toString();
	}

	// Method take parameters and creates a port for client/server communication
	static void sendModeToServer(String mode, String serverName) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
        
        
		try {
			/* Open our connection to server port, choose your own port number.. */
			sock = new Socket(serverName, 2565);
			// Create filter I/O streams for the socket:
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			
			// Send machine name or IP address to server:
			toServer.println(mode); 

			// Execute the stream
			toServer.flush();
		
			
			// Read two or three lines of response from the server,
			// and block while synchronously waiting:
			for (int i = 1; i <= 3; i++) {
				textFromServer = fromServer.readLine();
				if (textFromServer != null)
					System.out.println(textFromServer);
			}
			sock.close();
		} catch (IOException x) {
			System.out.println("Socket error.");
			x.printStackTrace();
		}
	}


		

}

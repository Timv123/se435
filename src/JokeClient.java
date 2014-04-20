import java.io.*; 
import java.net.Socket;
import java.util.UUID;

public class JokeClient {
	public static void main(String args[]) {
		String serverName;
		UUID uuid = UUID.randomUUID();
        String uid = uuid.toString();
        
		System.out.println(args.length);
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];
		System.out.println("JokeServer version 0.0.1");
		System.out.println("Using server: " + serverName + ", Port: 1800");
		
		//While user's input is NOT "quit" send off to get a remoteAddress
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String name;
			do {
				System.out.print("Enter a name to get jokes or proverb, (quit) to end: ");
				System.out.flush();
				name = in.readLine();
				if (name.indexOf("quit") < 0)
					getJokesFromJserver(name, serverName, uuid);
			} while (name.indexOf("quit") < 0); // quitSmoking.com?
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
	static void getJokesFromJserver(String name, String serverName,UUID uuid) {
		Socket sock;
		BufferedReader fromServer;
		PrintStream toServer;
		String textFromServer;
        
        
		try {
			/* Open our connection to server port, choose your own port number.. */
			sock = new Socket(serverName, 1800);
			// Create filter I/O streams for the socket:
			fromServer = new BufferedReader(new InputStreamReader(sock.getInputStream()));
			toServer = new PrintStream(sock.getOutputStream());
			
			// Send machine name or IP address to server:
			toServer.println(uuid); 

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

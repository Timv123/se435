import java.io.*; 
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class JokeClient {
	public static void main(String args[]) {
		String serverName;
		UUID uuid = UUID.randomUUID();
       
		System.out.println(args.length);
		if (args.length < 1)
			serverName = "localhost";
		else
			serverName = args[0];
		System.out.println("JokeServer version 0.0.1");
		System.out.println("Using server: " + serverName + ", Port: 1800");
		
		//While user's input is NOT "quit" send off to get a jokes
		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		try {
			String name;

			System.out.print("Enter a name to get jokes or proverb, (quit) to end: ");
			name = in.readLine();
			getJokesFromJserver(name, serverName, uuid);
				
			while (name.indexOf("quit") < 0) {

				Scanner scanner = new Scanner(System.in);
		        System.out.print("Press ENTER for next joke/proverb:\t");
		        String name2 = scanner.nextLine();
		        getJokesFromJserver(name2, serverName, uuid);
			}
		} catch (IOException x) {
			x.printStackTrace();
		}
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
			toServer.flush();
			toServer.println(name); 

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

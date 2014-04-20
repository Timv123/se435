
import java.io.*; // Get the
import java.net.*; // Get the
import java.util.HashMap;
import java.util.UUID;

// Class definition
// Class member, socket, local to Worker.
class Worker extends Thread {
	Socket sock;

	Worker(Socket s) {
		sock = s;
	} // Constructor, assign args to local sock

	public void run(){
		// Get I/O streams in/out from the socket:
		PrintStream out = null; //stream to sock
		BufferedReader in = null;//read from sock
		String uuid = null;
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); //Taking input from socket
			out = new PrintStream(sock.getOutputStream()); 
			
			// Note that this branch might not execute when expected: 
			if (JokerServer.controlSwitch != true){
			System.out.println("Listener is now shutting down as per client request.");
			out.println("Server is now shutting down. Goodbye!"); 
			}
			
			else try {
			String name;
			name = in.readLine ();
			if(validateUUID(name)!=true){	
				uuid = in.readLine();
			}
			//listening to socket for "shutdown" if greater than -1, otherwise call printRemoteAd..() to print InetAddress address.
			if (name.indexOf("shutdown") > -1){
				JokerServer.controlSwitch = false;
				System.out.println("Worker has captured a shutdown request."); 
				out.println("Shutdown request has been noted by worker."); 
				out.println("Please send final shutdown request to listener.");
				sock.close(); 
			} else{
				System.out.println("Name for jokes is: " + name );
				printJokesOrProverbs(name, out, uuid);
			}
		} catch (IOException x) { System.out.println("Server read error"); x.printStackTrace ();}
		 sock.close();
		// close this connection, but not the server; 
		 } catch (IOException ioe) {System.out.println(ioe);}
	}

	// Print the jokes or proverbs
	static void printJokesOrProverbs (String name, PrintStream out, String uuid) { 
		try {	
		out.println("Looking up " + uuid);
		//Get the i.p address of the name parameter
		InetAddress machine = InetAddress.getByName (name); 
		out.println("Host name : " + machine.getHostName ()); 
		out.println("Host IP : " + toText (machine.getAddress ()));
	} catch(UnknownHostException ex) {
		out.println ("Failed in atempt to look up " + name);}
		
	}
	static boolean validateUUID (String uuid){
		
		if(uuid == null) return false;
		try{
			UUID uidString = UUID.fromString(uuid);
			String toStringUUID = uidString.toString();
        return toStringUUID.equals(uuid);
		}catch(IllegalArgumentException e){
		return false;
		}
		
	}
	
//	static String getJokes(String name, String uuid){
//		
//		HashMap<String,Integer> jokes = new HashMap <String,Integer>();
//		
//		
//		jokes.put("A", 1);  
//		jokes.put("B", 2);
//		jokes.put("C", 3);
//		jokes.put("D", 4);
//		jokes.put("E", 5);
//		
//		return jokes;
//		
//	}
//	static String getProverb(String name){
//		HashMap<String,Integer> proverbs = new HashMap <String,Integer>();
//
//		proverbs.put("A", 1);
//		proverbs.put("B", 2);
//		proverbs.put("C", 3);
//		proverbs.put("D", 4);
//		proverbs.put("E", 5);
//	}

	// Not interesting to us:
	static String toText(byte ip[]) { /* Make portable for 128 bit format */
		StringBuffer result = new StringBuffer();
		for (int i = 0; i < ip.length; ++i) {
			if (i > 0)
				result.append(".");
			result.append(0xff & ip[i]);
		}
		return result.toString();
	}
	}

	public class JokerServer {
		public static boolean controlSwitch = true;

		public static void main(String a[]) throws IOException {
			int q_len = 6; /* Number of requests for OpSys to queue */
			int port = 1800; // static port
			Socket sock;
			ServerSocket servsock = new ServerSocket(port, q_len); //create serverSocket 
			System.out.println(" Listening to serve jokes or proverb at port .\n" + port);
			
			//Continue to listen to server socket and accept it
			while (controlSwitch) {
				sock = servsock.accept(); // wait for the next client connection
				if (controlSwitch)
					new Worker(sock).start(); // Spawn worker to handle it //
				
			}
		}
	}

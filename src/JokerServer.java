
import java.io.*; // Get the
import java.net.*; // Get the
import java.util.HashMap;
import java.util.Random;
import java.util.UUID;

class AdminWorker extends Thread {
	Socket sock;	
	AdminWorker(Socket s) {
		sock = s;
	} // Constructor, assign args to local sock
	public void run(){
		// Get I/O streams in/out from the socket:
		PrintStream out = null; //stream to sock
		BufferedReader in = null;//read from sock	
	try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); //Taking input from socket
			out = new PrintStream(sock.getOutputStream()); 		
			// Note that this branch might not execute when expected: 
			if (AdminLooper.adminControlSwitch != true){
			System.out.println("Listener is now shutting down as per client request.");
			out.println("Server is now shutting down. Goodbye!"); 
			}		
			else try {			
			String modeEntered = in.readLine ();		
			//listening to socket for "shutdown" if greater than -1, otherwise call printRemoteAd..() to print InetAddress address.
			if (modeEntered.indexOf("shutdown") > -1){
				AdminLooper.adminControlSwitch = false;
				System.out.println("AdminWorker has captured a shutdown request."); 
				out.println("Shutdown request has been noted by worker."); 
				out.println("Please send final shutdown request to listener.");
				sock.close(); 
			}
			else{
				JokerServer.adminInputMode = modeEntered;					
			}
		} catch (IOException x) { System.out.println("Server read error"); x.printStackTrace ();}
		 sock.close();
		// close this connection, but not the server; 
		 } catch (IOException ioe) {System.out.println(ioe);}
	}
	
}
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
		
		try {
			in = new BufferedReader(new InputStreamReader(sock.getInputStream())); //Taking input from socket
			out = new PrintStream(sock.getOutputStream()); 
			
			// Note that this branch might not execute when expected: 
			if (JokerServer.controlSwitch != true){
			System.out.println("Listener is now shutting down as per client request.");
			out.println("Server is now shutting down. Goodbye!"); 
			}
			
			else try {
			String uuid= in.readLine();
			String name = in.readLine ();			
			//listening to socket for "shutdown" if greater than -1, otherwise call printRemoteAd..() to print InetAddress address.
			if (name.indexOf("shutdown") > -1){
				JokerServer.controlSwitch = false;
				System.out.println("Worker has captured a shutdown request."); 
				out.println("Shutdown request has been noted by worker."); 
				out.println("Please send final shutdown request to listener.");
				sock.close(); 
			} else{
				
				if(validateUUID(uuid)==true){	
					System.out.println("Name of UUID: " + uuid );						
				}
				System.out.println("mode is : " + JokerServer.adminInputMode);	
				System.out.println("Name for jokes is: " + name );		
				printJokesOrProverbs(name, out , uuid);
			}
		} catch (IOException x) { System.out.println("Server read error"); x.printStackTrace ();}
		 sock.close();
		// close this connection, but not the server; 
		 } catch (IOException ioe) {System.out.println(ioe);}
	}
	// Print the jokes or proverbs
	static void printJokesOrProverbs (String name, PrintStream out ,String uuid) { 	
		out.println("Looking up ");
		//Get jokes	
		String joke = getJokes(name, uuid);
		out.println("Joke: " + joke); 	
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
	static String getJokes(String name, String uuid){		
//		HashMap<String,Integer> jokes = new HashMap <String,Integer>();
//		jokes.put("A", 1);  
//		jokes.put("B", 2);
//		jokes.put("C", 3);
//		jokes.put("D", 4);
//		jokes.put("E", 5);		
		return name;	
	}
	static boolean validateJoke(String joke, String uuid){		
		return true;
	}
//	static String getProverb(String name){
//		HashMap<String,Integer> proverbs = new HashMap <String,Integer>();
//		proverbs.put("A", 1);
//		proverbs.put("B", 2);
//		proverbs.put("C", 3);
//		proverbs.put("D", 4);
//		proverbs.put("E", 5);
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
	class AdminLooper implements Runnable {
		  public static boolean adminControlSwitch = true;

		  public void run(){ // RUNning the Admin listen loop
			  
		    System.out.println("In the admin looper thread");	    
		    int q_len = 6; /* Number of requests for OpSys to queue */
		    int port = 2565;  // We are listening at a different port for Admin clients
		    Socket sock;	  
		    try{
		      ServerSocket servsock = new ServerSocket(port, q_len);
		      while (adminControlSwitch) {
				// wait for the next ADMIN client connection:
				sock = servsock.accept();
				new AdminWorker (sock).start(); 
			      }
			    }catch (IOException ioe) {System.out.println(ioe);}
		  }
	}
	public class JokerServer {
		public static boolean controlSwitch = true;
		public static String adminInputMode;
		public static void main(String a[]) throws IOException {
		    int q_len = 6; /* Number of requests for OpSys to queue */
		    int port = 1800;
		    Socket sock;
		    
		    AdminLooper AL = new AdminLooper(); // create a DIFFERENT thread
		    Thread t = new Thread(AL);
		    t.start();  // ...and start it, waiting for administration input	
		    
		    ServerSocket servsock = new ServerSocket(port, q_len);	        
		    System.out.println("Joke server starting up.\n");
		    while (controlSwitch) {
		      // wait for the next client connection:
		      sock = servsock.accept();
		      new Worker (sock).start();
		    }
		  }
	
	}
	
	

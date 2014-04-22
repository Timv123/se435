
import java.io.*; // Get the
import java.net.*; // Get the
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
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
			String jokesTypes =null;
			
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
				
				//check for mode then generate jokes/proverb 
				if(JokerServer.adminInputMode.equals("Jokes")){
					String jokes = getJokes(uuid);
					printJokesOrProverbs(name , out, uuid, jokes);	
				}
				else if (JokerServer.adminInputMode.equals("Proverbs")){
					LinkedList<String> proverb = getProverb();
					String proverbStr = proverb.pop();
					printJokesOrProverbs(name , out, uuid, proverbStr);	
					
				}
				
				System.out.println("mode is : " + JokerServer.adminInputMode);	
				System.out.println("Name for jokes is: " + name );	
				
				
				printJokesOrProverbs(name, out , uuid , jokesTypes);
			}
		} catch (IOException x) { System.out.println("Server read error"); x.printStackTrace ();}
		 sock.close();
		// close this connection, but not the server; 
		 } catch (IOException ioe) {System.out.println(ioe);}
	}
	// Print the jokes or proverbs
	static void printJokesOrProverbs (String name, PrintStream out ,String uuid, String jokesTypes) { 	
		//Get jokes			
		out.println(jokesTypes); 	
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
	static LinkedList<String> getProverb(){
		
	    LinkedList<String> proverbList = new LinkedList<String>();
		//LinkedList<String> proverbListTemp = new LinkedList<String>();
		//String proverbStr = null;
		//HashMap <String,LinkedList<String>> test = new HashMap <>();
		
		proverbList.add("A proverb");
		proverbList.add("B proverb");
		proverbList.add("C proverb");
		proverbList.add("D proverb");
		proverbList.add("E proverb");
		
		Collections.shuffle(proverbList);		
		//proverbListTemp = proverbList;	
		//proverbStr = proverbListTemp.pop();
	
		return proverbList;
	}
	static String getJokes(String name){
			
			LinkedList<String> jokeList = new LinkedList<String>();
			LinkedList<String> jokeListTemp = new LinkedList<String>();
			String jokeStr = null;
					
			jokeList.add("A jokes");
			jokeList.add("B jokes");
			jokeList.add("C jokes");
			jokeList.add("D jokes");
			jokeList.add("E jokes");
			
			Collections.shuffle(jokeList);
			jokeListTemp = jokeList;	
			jokeStr= jokeListTemp.pop();
		
			return jokeStr;		
		}
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
		public static String adminInputMode = "Jokes";
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
	
	

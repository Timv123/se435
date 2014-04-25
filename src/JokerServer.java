
import java.io.*; // Get the
import java.net.*; // Get the
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.UUID;
import java.util.Map.Entry;

import com.sun.xml.internal.bind.v2.runtime.reflect.ListIterator;

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
			String mode = JokerServer.adminInputMode;
			String jokeProverbString =null;
			String joke = "joke";
			String proverb = "proverb";
			
			//listening to socket for "shutdown" if greater than -1, otherwise call printRemoteAd..() to print InetAddress address.
			if (name.indexOf("shutdown") > -1){
				JokerServer.controlSwitch = false;
				System.out.println("Worker has captured a shutdown request."); 
				out.println("Shutdown request has been noted by worker."); 
				out.println("Please send final shutdown request to listener.");
				sock.close(); 
			} else{
				//check for mode then generate jokes 
				if(mode.equals(joke) ){
				
					System.out.println("in the jokes");
					//initiate and create a new hash item uuid/joke for new client
					if(JokerServer.jokeList.get(uuid)==null ){	
						JokerServer.jokeList.put(uuid, randomizeProverbsOrJokes(mode));			
					}
					
					//pop from list then print to client
					LinkedList <String> proverbList = JokerServer.jokeList.get(uuid);
					jokeProverbString = proverbList.pop();
					JokerServer.jokeList.put(uuid, proverbList);
					//print to the client
					printJokesOrProverbs(out  , jokeProverbString);
					//check if list is null, put into hash table
					if(proverbList.peekLast()==null){			
						JokerServer.jokeList.put(uuid, randomizeProverbsOrJokes(mode));							
					}
					
					
	
				}
				// check for mode then generate proverb
				else if (mode.equals(proverb)){
					//initiate and create a new hash item uuid/proverb for new client
					if(JokerServer.proverbList.get(uuid)==null ){			
						JokerServer.proverbList.put(uuid, randomizeProverbsOrJokes(mode));
						}
					
					//pop from list then print to client
					LinkedList <String> proverbList = JokerServer.proverbList.get(uuid);
					jokeProverbString = proverbList.pop();
					JokerServer.proverbList.put(uuid, proverbList);
					printJokesOrProverbs(out  , jokeProverbString);
					//check if list is null, put into hash table
					if(proverbList.peekLast()==null){				
						JokerServer.proverbList.put(uuid, randomizeProverbsOrJokes(mode));
						}
					}			
			}
		
			
		} catch (IOException x) { System.out.println("Server read error"); x.printStackTrace ();}
		 sock.close();
		// close this connection, but not the server; 
		 } catch (IOException ioe) {System.out.println(ioe);}
	}
	// Send the jokes or proverbs to socket
	static void printJokesOrProverbs (PrintStream out , String jokesTypes) { 	
		//Get jokes			
		out.println(jokesTypes); 
	}
	static LinkedList<String> randomizeProverbsOrJokes(String mode ){
		
	    LinkedList<String> proverbList = new LinkedList<String>();
	    LinkedList <String> jokeList = new LinkedList<String>();
	    LinkedList <String> returnList = new LinkedList<String>();
	    String jok= "joke";
	    String provrb= "proverb";
		
		jokeList.add("A jokes");
		jokeList.add("B jokes");
		jokeList.add("C jokes");
		jokeList.add("D jokes");
		jokeList.add("E jokes");
		
		Collections.shuffle(jokeList);
		
		proverbList.add("A proverb");
		proverbList.add("B proverb");
		proverbList.add("C proverb");
		proverbList.add("D proverb");
		proverbList.add("E proverb");
		
		Collections.shuffle(proverbList);		
		
		if(jok.equals(mode))
			returnList = jokeList;	
		else if (provrb.equals(mode))
			returnList = proverbList;		
		else 
			System.out.println("can not randomize list : " + mode + returnList);
		return returnList;
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
		public static String adminInputMode = "joke";
		public static HashMap <String,LinkedList<String>> clientNameUUID = new HashMap <>();
		public static HashMap <String,LinkedList<String>> proverbList = new HashMap <>();
		public static HashMap <String,LinkedList<String>> jokeList = new HashMap <>();
		
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
	
	

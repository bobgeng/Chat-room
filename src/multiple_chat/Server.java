package multiple_chat;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.Vector;

public class Server {
    private static final String USAGE
            = "usage: " + Server.class.getName() + " <port>";

    /** Start een Server-applicatie op. */
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println(USAGE);
            System.exit(0);
        }
        
        Server server = new Server(Integer.parseInt(args[0]));
        server.run();
        
    }


    private int port;
    private List<ClientHandler> threads;
    /** Constructs a new Server object */
    public Server(int portArg) {
    	this.port = portArg;
    	this.threads = new Vector<ClientHandler>();
    }
    
    /**
     * Listens to a port of this Server if there are any Clients that 
     * would like to connect. For every new socket connection a new
     * ClientHandler thread is started that takes care of the further
     * communication with the Client.
     */
    public void run() {
    	try (ServerSocket ssock = new ServerSocket(port);){
			int i = 0;
			while (true) {
				Socket sock = ssock.accept();
				ClientHandler handler = new ClientHandler(this, sock);
				print("[client no. " + (++i)+ " connected.]");
				handler.announce();
				handler.start();
				addHandler(handler);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
    
    public void print(String message){
        System.out.println(message);
    }
    
    /**
     * Sends a message using the collection of connected ClientHandlers
     * to all connected Clients.
     * @param msg message that is send
     */
    public void broadcast(String msg) {
    	print(msg);
    	// threads could be modified in removeHandler() while iterating over
    	// it here. Therefore iterate over clone of threads to avoid
    	// concurrency problems.
    	(new Vector<>(threads)).forEach(handler -> handler.sendMessage(msg));
    }
    
    /**
     * Add a ClientHandler to the collection of ClientHandlers.
     * @param handler ClientHandler that will be added
     */
    public void addHandler(ClientHandler handler) {
    	threads.add(handler);
    }
    
    /**
     * Remove a ClientHandler from the collection of ClientHanlders. 
     * @param handler ClientHandler that will be removed
     */
    public void removeHandler(ClientHandler handler) {
    	threads.remove(handler);
    }
}

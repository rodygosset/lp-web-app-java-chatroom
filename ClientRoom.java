import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;


/**
 * This class is the entry point to our server.
 * It awaits clients to connect on the provided port at localhost,
 * znd delegates communication with the clients to a ServerThread for each client.
 * 
 * It can only be instatiated from within itself, 
 * to make sure only the main function declared here does it.
 */

public class ClientRoom {

    private ArrayList<ServerThread> clients;
    private int port;

    private ClientRoom(int p) {
        this.clients = new ArrayList<>();
        this.port = p;
    }

    
    /** 
     * Send a message to all clients
     * @param msg the message to be sent
     */
    public synchronized void broadcast(String msg) {
        for(ServerThread c : clients) {
            c.send(msg);
        }
    }

    
    /** 
     * When a client disconnects or quits,
     * Remove them from the list
     * @param client the thread that handles communication with the clients
     */
    public void removeClient(ServerThread client) {
        this.clients.remove(client);
    }


    /**
     * Await new connections,
     * and create a thread for each new client.
     */
    public void run() {
        // try to open a server socket 
        try (ServerSocket serv = new ServerSocket(port)) {
            // wait for clients
            while(true) {
                // when there's a new one, create a thread to handle communication
                // and add that thread to the list
                clients.add(new ServerThread(serv.accept(), this));
                // start the newly created server thread
                clients.get(clients.size() - 1).start();
            }
        } catch (IOException e) {
            e.printStackTrace(System.err);
        }
    }

    public static void main(String[] args) {

        if(args.length == 0) {
            System.out.println("Indicate which port to run the server on.");
            return;
        }

        int port = Integer.parseInt(args[0]);
        
        ClientRoom chatRoom = new ClientRoom(port);
        chatRoom.run();

    }
    
}

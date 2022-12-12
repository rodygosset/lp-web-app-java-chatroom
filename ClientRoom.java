import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ClientRoom {

    private ArrayList<ServerThread> clients;
    private int port;

    private ClientRoom(int p) {
        this.clients = new ArrayList<>();
        this.port = p;
    }

    public synchronized void broadcast(String msg) {
        for(ServerThread c : clients) {
            c.send(msg);
        }
    }

    public void removeClient(ServerThread client) {
        this.clients.remove(client);
    }

    public void run() {
        // try to open a server socket 
        try (ServerSocket serv = new ServerSocket(port)) {
            // wait for clients
            while(true) {
                clients.add(new ServerThread(serv.accept(), this));
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

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.Socket;

/**
 * Custom Thread class used by the server (ClientRoom)
 * to handle connections with clients.
 */
public class ServerThread extends Thread {

    private Socket client;
    private ClientRoom chatRoom;
    private BufferedReader inchan;
    private DataOutputStream outchan;

    /**
     * Instiate a ServerThread object
     * @param s the socket used to communicate with the client
     * @param c the chatroom our client is connected to
     */
    public ServerThread(Socket s, ClientRoom c) {
        this.client = s;
        this.chatRoom = c;
        try {
            // open input and output streams
            this.inchan = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.outchan =  new DataOutputStream(client.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }


    /**
     * Cleanly stop the thread.
     */
    @Override
    public void interrupt() {
        super.interrupt();
        try {
            this.client.close();
            this.chatRoom.removeClient(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Send a messafe to the client on the other end of the socket
     * @param msg the message to be sent
     */
    public void send(String msg) {
        try {
            outchan.writeBytes(msg + "\n"); // add "\n" to use BufferReader.readLine()
            // log to stdout
            System.out.println("Sending message to client : " + msg);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }  
    }

    @Override
    public void run() {
        try {
            while (true) {
                // remove client from the chatroom if they've disconnected
                if(this.client.isClosed()) {
                    this.chatRoom.removeClient(this);
                }
                // read the data sent by the client
                String command = inchan.readLine();
                // close the connection if the user's done
                if (command.equals("\\quit")) {
                    // remove the client from the chatroom
                    this.chatRoom.removeClient(this);
                    // close the socket
                    this.client.close();
                    return;
                }
                // forward the recieved message to the other clients in the chatroom
                this.chatRoom.broadcast(">>> message received: " + command);
            }
        } catch(InterruptedIOException e) {
            Thread.currentThread().interrupt();
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

}

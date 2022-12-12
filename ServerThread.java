import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.InterruptedIOException;
import java.net.Socket;

public class ServerThread extends Thread {

    private Socket client;
    private ClientRoom chatRoom;
    private BufferedReader inchan;
    private DataOutputStream outchan;

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

    public void send(String msg) {
        // write to socket of server
        try {
            outchan.writeBytes(msg + "\n"); // add "\n" to use BufferReader.readLine()
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
                // read data
                String command = inchan.readLine();
                // close the connection when receiving \quit
                if (command.equals("\\quit")) {
                    this.chatRoom.removeClient(this);
                    this.client.close();
                    return;
                }
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

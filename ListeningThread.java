import java.io.BufferedReader;
import java.io.IOException;


/**
 * Custom Thread class used by our ChatRoomClient 
 * to read and display messages sent to the chatroom.
 */
public class ListeningThread extends Thread {

    private BufferedReader inchan;

    public ListeningThread(BufferedReader in) {
        this.inchan = in;
    }

    /**
     * Cleanly stop the thread
     */
    @Override
    public void interrupt() {
        super.interrupt();
        try {
            this.inchan.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            // keep listening to the input channel while the thread is running
            while (true) {
                // when the thread is interrupted, don't try to read from the input channel
                // to avoid a socket closed exception
                if(isInterrupted()) { return; }
                // listen to the data sent by the server
                String command = inchan.readLine();
                if(command == null) { continue; }
                System.out.println("\n" + command);
                System.out.print("> ");
            }

        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }


    }
    
}

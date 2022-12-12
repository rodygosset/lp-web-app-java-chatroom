import java.io.BufferedReader;
import java.io.IOException;

public class ListeningThread extends Thread {

    private BufferedReader inchan;

    public ListeningThread(BufferedReader in) {
        this.inchan = in;
    }

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

            while (true) {
                if(isInterrupted()) { return; }
                // listen to the data sent from the server
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

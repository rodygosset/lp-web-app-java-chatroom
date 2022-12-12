import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

public class ChatRoomClient {

    public static void main(String[] args) {
        String address = args[0];
        int port = Integer.parseInt(args[1]);

        Scanner scanner = new Scanner(System.in);
        String message = null; 

        try (Socket sock = new Socket(address, port)) {

            BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            DataOutputStream outchan = new DataOutputStream(sock.getOutputStream());

            ListeningThread lThread = new ListeningThread(inchan);
            lThread.start();

            while(true) {

                System.out.print("> ");
                message = scanner.nextLine();
                outchan.writeBytes(message + "\n");

                if(message.equals("\\quit")) { 
                    lThread.interrupt();
                    break; 
                }

            }  

            scanner.close();        

        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception t) {
            t.printStackTrace(System.err);
        }
    }
    
}

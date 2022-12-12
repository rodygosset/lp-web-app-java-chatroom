import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Scanner;

/**
 * This class is used as the frontend to our chatroom app.
 * It connects to our server, running on the provided origin (address & port number),
 * waits for the user to type in a message, then sends it to the chatroom.
 * 
 * It also instiates a Listening Thread, which simply reads the answer from the server.
 * That is necessary so we can read and display all that is sent to the chatroom.
 */

public class ChatRoomClient {

    public static void main(String[] args) {
        // retrieve the full origin (address & port number) from the args
        String address = args[0];
        int port = Integer.parseInt(args[1]);


        // needed to interact with the user on the keyboard
        Scanner scanner = new Scanner(System.in);
        String message = null; 

        try (Socket sock = new Socket(address, port)) {

            // once we've connected to the server (opened a socket), we can start communicating
            // so we setup the input and output channels
            BufferedReader inchan = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            DataOutputStream outchan = new DataOutputStream(sock.getOutputStream());
            
            // needed to recieve messages sent to the chatroom 
            ListeningThread lThread = new ListeningThread(inchan);
            lThread.start();

            // handle user interaction
            while(true) {

                System.out.print("> ");
                message = scanner.nextLine();
                outchan.writeBytes(message + "\n");

                // if the user's done
                if(message.equals("\\quit")) { 
                    // make sure to interrupt the thread so we don't get an exception
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

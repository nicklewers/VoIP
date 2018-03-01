
package voip;

import javax.sound.sampled.LineUnavailableException;
import java.util.Scanner;

/**
 * Basic duplex for the default Datagram packet.
 * Simultaneously receives and sends.
 */
public class VoipDuplex {
        
    public static void main(String[] args) throws LineUnavailableException {
        
        Scanner in = new Scanner(System.in);
        
        int PORT = 55555;
        String clientIP = "localhost";
         
        VoipReceiver3 receiver = new VoipReceiver3(PORT);
        VoipSender3 sender = new VoipSender3(PORT, clientIP);
        receiver.start();
        sender.start();
        
        // Q/q to stdin to terminate both.
        char terminationStatus = in.next().charAt(0);
        if(terminationStatus == 'q' || terminationStatus == 'Q'){
            receiver.stop();
            sender.stop();
        }
        
    }
    
}


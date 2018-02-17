
package voip;

import javax.sound.sampled.LineUnavailableException;

/**
 * Basic duplex for the default Datagram packet.
 * Simultaneously receives and sends.
 */
public class VoipDuplex {
    
    // @throws LineUnavailableException if no headphones/mic detected.
    
    public static void main(String[] args) throws LineUnavailableException {
        
        int PORT = 55555;
        String clientIP = "localhost";
         
        VoipReceiver receiver = new VoipReceiver(PORT);
        VoipSender sender = new VoipSender(PORT, clientIP);
        receiver.start();
        sender.start();
    }
    
}

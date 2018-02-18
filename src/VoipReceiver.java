
package voip;

import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import uk.ac.uea.cmp.voip.DatagramSocket2;
import uk.ac.uea.cmp.voip.DatagramSocket3;
import uk.ac.uea.cmp.voip.DatagramSocket4;

import java.util.Vector;
import java.util.Iterator;
import java.net.*;

/**
 * Basic receiver for the default Datagram socket.
 * Note that there's no way to terminate sending and receiving yet.
 */
public class VoipReceiver implements Runnable {

    //Socket to receive data to.  
    static DatagramSocket2 receiving_socket;
    //Port to open socket on.
    int PORT;
    //Vector to store audio blocks. 
    Vector<byte[]> voiceVector;
    //Player to play audio blocks.
    AudioPlayer player;
    //Set to false while running to terminate receiving and recording.
    boolean running = true;
    

    /*
    * Construct receiver.
    * @param port to receive on.
    * @throws LineUnavailableException if no headphones/mic detected.
    */
    public VoipReceiver(int port) throws LineUnavailableException {
        this.PORT = port;
        this.player = new AudioPlayer();
        this.voiceVector = new Vector<byte[]>();
    }
    
    //Create thread for receiver.
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    
    /*
    *Replay data received.
    *@throws IOException if no blocks to play.
    */
    public void replay() throws IOException {
        Iterator<byte[]> voiceItr = voiceVector.iterator();
        
        while(voiceItr.hasNext()){
            player.playBlock(voiceItr.next());
        }
        
        player.close();
    }
    
    public void stop(){
        running = false;
    }
    
    @Override
    public void run() {
        
        try {
            //Open socket to receive to.
            receiving_socket = new DatagramSocket2(PORT);
            
            //Init, fill, receive and play in the loop.
            byte[] buffer;
            DatagramPacket packet;
            
            while(running){
                
                //Keep initializing new buffer and receiving while running.
                buffer = new byte[512];
                packet = new DatagramPacket(buffer, 0, buffer.length);
                receiving_socket.receive(packet);

                //Add to use in replay() and count number of packets received.
                voiceVector.add(packet.getData());
                
                //Play.
                player.playBlock(packet.getData());
                    
            }
            
            //Quit receiving.
            receiving_socket.close();
            //Running is false, close player.
            player.close();
            
            System.out.printf("Receiver is closed. Received %d packets.\n", voiceVector.size());
            
            //Uncomment to replay, used for some troubleshooting earlier,
            //kinda useless now.
            //replay();
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}

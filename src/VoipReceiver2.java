
package voip;

import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import uk.ac.uea.cmp.voip.DatagramSocket2;

import java.util.Vector;
import java.net.*;

/**
 * Receiver for DGS2.
 */
public class VoipReceiver2 implements Runnable {

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
    public VoipReceiver2(int port) throws LineUnavailableException {
        this.PORT = port;
        this.player = new AudioPlayer();
        this.voiceVector = new Vector<byte[]>();
    }
    
    //Create thread for receiver.
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
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
            DatagramPacket[] packets = new DatagramPacket[16];
            Interleaver interleaver = new Interleaver(packets.length);
            
            //Count number of packets received.
            int i = 0;
            
            while(running){
                
                buffer = new byte[512];
                packet = new DatagramPacket(buffer, 0, buffer.length);
                receiving_socket.receive(packet);
                
                //Collect the received packets in an array of size 16.
                packets[i] = packet;
                i++;
                
                //Once array is full uninterleave the packets and play in 
                //correct order.
                if(i == packets.length){
                    packets = interleaver.uninterleave(packets);
                    for(int j = 0; j < packets.length; j++){
                        
                        // ADD 
                        // CONCEALMENT
                        // SOMEWHERE
                        
                        player.playBlock(packets[j].getData());
                    }
                    
                    //Set counter back to 0/
                    i = 0;
                    //Create a new array to collect next set of incoming packets.
                    packets = new DatagramPacket[16];
                }
            }
            
            //Quit receiving.
            receiving_socket.close();
            //Running is false, close player.
            player.close();            
            
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}

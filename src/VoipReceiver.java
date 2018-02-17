
package voip;

import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import java.util.Vector;
import java.util.Iterator;
import java.net.*;

/**
 * Basic receiver for the default Datagram socket.
 * Note that there's no way to terminate sending and receiving yet.
 */
public class VoipReceiver implements Runnable {

    //Socket to receive data to.  
    static DatagramSocket receiving_socket;
    //Port to open socket on.
    int PORT;
    //Vector to store audio blocks. 
    Vector voiceVector;
    //Player to play audio blocks.
    final AudioPlayer player;
    

    /*
    * Instantiate receiver.
    * @param port to receive on.
    * @throws LineUnavailableException if no headphones/mic detected.
    */
    public VoipReceiver(int port) throws LineUnavailableException {
        this.PORT = port;
        this.player = new AudioPlayer();
        this.voiceVector = new Vector();
    }
    
    //Create thread for receiver.
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    
    @Override
    public void run() {
        
        try {
        //Open socket to receive to.
        receiving_socket = new DatagramSocket(PORT);
        //Iterator to simultaneously add and play received data.
        Iterator<byte[]> voiceItr;
        //Keep on receiving packets until false.
        boolean running = true; 
 
            //512 bytes per block, pre-configured in AudioRecorder.
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
            
            while(running){
                
                //Loop running, receive.
                receiving_socket.receive(packet);
                
                //Indicate received packet.
                System.out.println("Packet received.");
                
                //Extract audio data from packet, add to vector.
                voiceVector.add(packet.getData());
                
                //Iterate through received audio blocks.
                voiceItr = voiceVector.iterator();
                if(voiceItr.hasNext()){
                    //Play each block received.
                    player.playBlock(voiceItr.next());
                }
            }
            //Running is false, close player.
            player.close();
            //Quit receiving.
            receiving_socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}

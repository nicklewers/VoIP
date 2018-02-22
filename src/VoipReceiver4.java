
package voip;

import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import uk.ac.uea.cmp.voip.DatagramSocket4;

import java.util.Vector;
import java.util.Iterator;
import java.net.*;

/**
 * Receiver for DatagramSocket4.
 */

public class VoipReceiver4 implements Runnable {

    //Socket to receive data to.  
    static DatagramSocket receiving_socket;
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
    public VoipReceiver4(int port) throws LineUnavailableException {
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
            byte b = (byte) 0;
            //Open socket to receive to.
            receiving_socket = new DatagramSocket4(PORT);
            
            //Init, fill, receive and play in the loop.
            byte[] buffer;
            DatagramPacket packet;
            
            //This will keep the last non-corrupted packet.
            byte[] lastGood = new byte[512];
            
            while(running){
                
                //Keep initializing new buffer and receiving while running.
                buffer = new byte[513];
                packet = new DatagramPacket(buffer, 0, buffer.length);
                receiving_socket.receive(packet);
                //repetition 
                
                //Array for the decoded data.
                byte[] data = new byte[512];
                if(!CRC.isCorrupt(buffer)){
                    data = CRC.crcDecode(buffer);
                    lastGood = data;
                } else {
                    if(!(lastGood==null)){
                        data = lastGood;
                    } else {
                        //If the first packet is corrupt fill it with silence
                        //and play.
                        for(int i = 0; i < data.length; i++){
                            data[i] = (byte)0;
                        }
                    }
                }
                
                //Add to use in replay() and count number of packets received.
                voiceVector.add(data);
                
                //Play.
                player.playBlock(data);
                    
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

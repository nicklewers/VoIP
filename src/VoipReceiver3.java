package voip;

import CMPC3M06.AudioPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Iterator;
import java.util.Vector;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket3;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicklewers
 */
public class VoipReceiver3 implements Runnable {

    //Socket to receive data to.  
    static DatagramSocket3 receiving_socket;
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
    public VoipReceiver3(int port) throws LineUnavailableException {
        this.PORT = port;
        this.player = new AudioPlayer();
        this.voiceVector = new Vector<byte[]>();
    }

    //Create thread for receiver.
    public void start() {
        Thread thread = new Thread(this);
        thread.start();
    }

    /*
    *Replay data received.
    *@throws IOException if no blocks to play.
     */
    public void replay() throws IOException {
        Iterator<byte[]> voiceItr = voiceVector.iterator();

        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }

        player.close();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {

        try {
           
            
//            initiate the socket
            receiving_socket = new DatagramSocket3(PORT);
                        
            
//            scaffold the receiver skeleton
            
            byte[] buffer;
            DatagramPacket[] packets = new DatagramPacket[16];
            DatagramPacket packet;
            
//            initiate the interleaver
            Interleaver interleaver = new Interleaver(packets.length);
            
            // counter to calculate index of packet received
            int packetIndex = 0;
            
            while(running){
                
                buffer = new byte[512];
                packet = new DatagramPacket(buffer, 0, buffer.length);
                receiving_socket.receive(packet);
                
//                collect the received packets in an array of size 16.
                packets[packetIndex] = packet;
//                increment packet counter
                packetIndex++;
                
//                once array is full uninterleave the packets and play in 
//                correct order.
                if(packetIndex == packets.length){
                    packets = interleaver.uninterleave(packets);
                    for(int j = 0; j < packets.length; j++){
                        
                        // ADD 
                        // CONCEALMENT
                        // SOMEWHERE
                        
                        player.playBlock(packets[j].getData());
                    }
                    
                    //Set counter back to 0/
                    packetIndex = 0;
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

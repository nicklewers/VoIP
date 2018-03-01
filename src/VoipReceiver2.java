package voip;

import CMPC3M06.AudioPlayer;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Vector;
import javax.sound.sampled.LineUnavailableException;
import uk.ac.uea.cmp.voip.DatagramSocket2;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nicklewers
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
            receiving_socket = new DatagramSocket2(PORT);
                        
            
//            scaffold the receiver skeleton
            
            byte[] buffer;
            DatagramPacket[] packets = new DatagramPacket[16];
            DatagramPacket packet;
            
//            initiate the interleaver
            Interleaver interleaver = new Interleaver(packets.length);
            
            // counter to calculate index of packet received
            int packetIndex = 0;
            
            while(running){
                
                buffer = new byte[513];
                packet = new DatagramPacket(buffer, 0, 513);
                receiving_socket.receive(packet);
                packets[packetIndex] = packet;
//                collect the received packets in an array of size 16.
//                increment packet counter
                packetIndex++;
                
//                once array is full uninterleave the packets and play in 
//                correct order.
                if(packetIndex == packets.length){
                    packets = interleaver.sort(packets);
                    
                        // ADD 
                        // CONCEALMENT
                        // SOMEWHERE
                        for(int i = 0; i < 16; i++){
                            System.out.println(packets[i].getData()[512]);
                           player.playBlock(Arrays.copyOf(packets[i].getData(), 512));

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

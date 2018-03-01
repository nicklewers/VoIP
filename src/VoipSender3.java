/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author nicklewers
 */

package voip;
import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import uk.ac.uea.cmp.voip.DatagramSocket3;


import java.util.Iterator;
import java.util.Vector;
import java.net.*;



public class VoipSender3 implements Runnable {

        //Socket to send data to.
    static DatagramSocket sending_socket;
    //Port to open socket on on.
    int PORT;
    //IP address of machine data is being sent to.
    InetAddress clientIP;
    //Recorder to record audio to.
    final AudioRecorder recorder;
    //Set to false while running to terminate sending and recording.
    boolean running = true;
    Vector<byte[]> voiceVector;
    
    /*
    * Construct sender.
    * @param port to send on.
    * @throws LineUnavailableException is no headphones/mic detected.
    */
    public VoipSender3(int port, String clientIP) throws LineUnavailableException {
        this.PORT = port;
        this.recorder = new AudioRecorder();
        
        try {
            this.clientIP = InetAddress.getByName(clientIP);
        } catch (UnknownHostException e){
            e.printStackTrace();
        }
        
    }
    

    
    //Create thread for receiver.
    public void start(){
        Thread thread = new Thread(this);
        thread.start();
    }
    
    /*
    *Plays the data recorded and sent.
    *@throws IOException no blocks to play.
    */
    public void replay() throws IOException {
        Iterator <byte[]> voiceItr = voiceVector.iterator();
        
        AudioPlayer player = null;
        try {player = new AudioPlayer();}
        catch (LineUnavailableException e){
            e.printStackTrace();}
        
        
        while(voiceItr.hasNext()){
            player.playBlock(voiceItr.next());
        }
        
        player.close();
    }
//    public function to be called from master duplex
    public void stop(){
//        stops the main loop
        running = false;
    }
    
    @Override 
    public void run(){        
        
        try{
//        initate the socket
        sending_socket = new DatagramSocket3();
        
//        scaffold the sender skeleton
        DatagramPacket packet;
        DatagramPacket[] packets = new DatagramPacket[16];
        byte[] block;
        voiceVector = new Vector<byte[]>();
               
        
//        create a new interleaver with capacity of size 16.
        Interleaver interleaver = new Interleaver(packets.length);
        
//        counter to calculate the number of packets received
        int packetIndex = 0;
        
        while(running){         
            
            // Fill each block with audio data.
            // Put block into a newly created packet.
            // Keep on creating and sending packets while loop is running.
            // Block size 512 bytes.
            block = recorder.getBlock();
            packet = new DatagramPacket(block, block.length, clientIP, PORT);
            
            // Add packets to the array.
            packets[packetIndex] = packet;
            packetIndex++;
            
            // Once the array is full...
            if(packetIndex == packets.length){
                
                //...interleave contents of the array...
                packets = interleaver.interleave(packets);
                
                for(int j = 0; j < packets.length; j++){
                    
                    // ...and send them in the interleaved order.
                    sending_socket.send(packets[j]);
                }
                //New empty DatagramPacket array.
                packets = new DatagramPacket[16];
                
                //Reset counter back to 0.
                packetIndex = 0;
            }
        }
        //Loop over, finish recording.
        recorder.close();
        //Close the socket.
        sending_socket.close();
        System.out.printf("Sender is closed. Sent %d packets.\n", voiceVector.size());
        
        } catch (IOException e){
            e.printStackTrace();
        }
    }
    
}

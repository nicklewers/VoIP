
package voip;

import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import uk.ac.uea.cmp.voip.DatagramSocket2;


import java.util.Iterator;
import java.util.Vector;
import java.net.*;

/**
 * Sender for DGS2.
 */
public class VoipSender2 implements Runnable {
    
    //Socket to send data to.
    static DatagramSocket2 sending_socket;
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
    public VoipSender2(int port, String clientIP) throws LineUnavailableException {
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
    
    //Call from duplex. Terminates main loop. 
    public void stop(){
        running = false;
    }
    
    @Override 
    public void run(){        
        
        try{
        //Open socket to send from.
        sending_socket = new DatagramSocket2();
        
        //Init, fill and send in the loop.
        DatagramPacket packet;
        byte[] block;
        
        voiceVector = new Vector<byte[]>();
        //Create an array size 16 for 4*4 block interleaver.
        DatagramPacket[] packets = new DatagramPacket[16];
        
        //Create a new interleaver with capacity of size 16.
        Interleaver interleaver = new Interleaver(packets.length);
        
        //Keep the count of packets added to the array.
        int i = 0;
        
        while(running){         
            
            // Fill each block with audio data.
            // Put block into a newly created packet.
            // Keep on creating and sending packets while loop is running.
            // Block size 512 bytes.
            block = recorder.getBlock();
            packet = new DatagramPacket(block, block.length, clientIP, PORT);
            
            // Add packets to the array.
            packets[i] = packet;
            i++;
            
            // Once the array is full...
            if(i == packets.length){
                
                //...interleave contents of the array...
                packets = interleaver.interleave(packets);
                
                for(int j = 0; j < packets.length; j++){
                    
                    // ...and send them in the interleaved order.
                    sending_socket.send(packets[j]);
                }
                //New empty DatagramPacket array.
                packets = new DatagramPacket[16];
                
                //Reset counter back to 0.
                i = 0;
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

    


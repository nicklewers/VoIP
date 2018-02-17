
package voip;

import CMPC3M06.AudioRecorder;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import java.util.Vector;
import java.net.*;

/**
 * Basic sender for the default Datagram socket.
 * Note that there's no way to terminate sending and receiving yet.
 */
public class VoipSender implements Runnable {
    
    //Socket to send data to.
    static DatagramSocket sending_socket;
    //Port to open socket on on.
    int PORT;
    //IP address of machine data is being sent to.
    InetAddress clientIP;
    //Recorder to record audio to.
    final AudioRecorder recorder;
    
    /*
    * Construct sender.
    * @param port to send on.
    * @throws LineUnavailableException is no headphones/mic detected.
    */
    public VoipSender(int port, String clientIP) throws LineUnavailableException {
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
    
    @Override 
    public void run(){        
        
        try{
        //Open socket to send from.
        sending_socket = new DatagramSocket();
        
        //Keep on sending packets until false.
        boolean running = true;
        while(running){         
            /*
            * Fill each block with audio data.
            * Put block into a newly created packet.
            * Keep on creating and sending packets while loop is running.
            * Block size 512 bytes.
            */
            byte[] block = recorder.getBlock();
            DatagramPacket packet = new DatagramPacket(block, block.length, clientIP, PORT);
            sending_socket.send(packet);
            //Indicate sent packet.
            System.out.println("Packet sent.");
            
            }
        //Loop over, finish recording.
        recorder.close();
        //Close the socket.
        sending_socket.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

    



package voip;

import CMPC3M06.AudioRecorder;
import CMPC3M06.AudioPlayer;

import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;

import java.util.Iterator;
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
    //Set to false while running to terminate sending and recording.
    boolean running = true;
    Vector<byte[]> voiceVector;
    
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
    //Call from duplex. Terminates main loop. 
    public void stop(){
        running = false;
    }
    
    @Override 
    public void run(){        
        
        try{
        //Open socket to send from.
        sending_socket = new DatagramSocket();
        
        //Init, fill and send in the loop.
        DatagramPacket packet;
        byte[] block;
        
        voiceVector = new Vector<byte[]>();
        
        while(running){         
            
            // Fill each block with audio data.
            // Put block into a newly created packet.
            // Keep on creating and sending packets while loop is running.
            // Block size 512 bytes.
            block = recorder.getBlock();
            packet = new DatagramPacket(block, block.length, clientIP, PORT);
            
            //Add to use in replay() and count number of packets sent.
            voiceVector.add(packet.getData());
            
            //Send.
            sending_socket.send(packet);
        }
        //Loop over, finish recording.
        recorder.close();
        //Close the socket.
        sending_socket.close();
        System.out.printf("Sender is closed. Sent %d packets.\n", voiceVector.size());
        
        //Uncomment to replay sound recorded.
        //replay();
        
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}

    


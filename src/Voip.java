/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mvk15gsu
 */
import CMPC3M06.AudioPlayer;
import CMPC3M06.AudioRecorder;
import javax.sound.sampled.LineUnavailableException;
import java.io.IOException;
import java.net.*;
import java.util.Vector;
import java.util.Iterator;
/**
 *
 * @author gtx15abu
 */


public class Voip {

    static DatagramSocket receiving_socket;

    public void start(){
        Thread thread = new Thread((Runnable) this);
        thread.start();
    }
//    @Override
    public void run(){
        int PORT = 55555;
        AudioPlayer player = null;
        Vector voiceVector;

        try {
            receiving_socket = new DatagramSocket(PORT);
            player = new AudioPlayer();

        } catch (SocketException e){
            System.out.println("Coult not open UDP socket.");
            e.printStackTrace();
        } catch (LineUnavailableException e){
            System.out.println("No line in available.");
            e.printStackTrace();
        } finally {
        }
        voiceVector = new Vector();

        boolean running = true;
        Iterator<byte[]> voiceItr;
        try {
            byte[] buffer = new byte[512];
            DatagramPacket packet = new DatagramPacket(buffer, 512);
            while(running){
                receiving_socket.receive(packet);
                voiceVector.add(packet.getData());
                voiceItr = voiceVector.iterator();
                System.out.println("packet received");
                if(voiceItr.hasNext()){
                    player.playBlock(voiceItr.next());
                }
            }
            player.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Voip newVoip = new Voip();
        newVoip.run();
    }

}


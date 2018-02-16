/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mvk15gsu
 */
import uk.ac.uea.cmp.voip.*;
import java.util.Iterator;

import java.net.*;
import java.io.*;


public class AudioSend {

//    scaffold the boiletplate objects
    static DatagramSocket sending_socket;
    int PORT;
    String ADDRESS;
    AudioRecord AUDIO;

    public AudioSend(int port, String address, AudioRecord audio) {

        this.PORT = port;
        this.ADDRESS = address;
        this.AUDIO = audio;


    }

    public void _SEND(){
        InetAddress clientIP = null;
        try {
            clientIP = InetAddress.getByName(this.ADDRESS);
        } catch (UnknownHostException e) {
            System.out.println("ERROR: TextSender: Could not find client IP");
            e.printStackTrace();
            System.exit(0);
        }

        //Open a socket to send from
        //We dont need to know its port number as we never send anything to it.
        //We need the try and catch block to make sure no errors occur.

        //DatagramSocket sending_socket;
        try {
            sending_socket = new DatagramSocket();
        } catch (SocketException e) {
            System.out.println("ERROR: TextSender: Could not open UDP socket to send from.");
            e.printStackTrace();
            System.exit(0);
        }

//        main loop

        boolean running = true;

            try{
//                calculate number of blocks
                int blocks = (int) Math.ceil(this.AUDIO.recordTime / 0.032);

//                initiate the block buffer, with each block being a length of 512 samples
                byte[][] blockBuffer = new byte[blocks][512];

//                define the iterator for the audio vector
                Iterator<byte[]> voiceItr = this.AUDIO.voiceVector.iterator();

//                loop over the audio vector
                int i = 0;
                while (voiceItr.hasNext()) {
//                    store each block in byte form within the buffer
                    blockBuffer[i] = voiceItr.next();
                    i++;
                }

//                for each block within the buffer
                for (byte[] aBlockBuffer : blockBuffer) {
//                    create a new packet
                    System.out.println("sending packet");
                    DatagramPacket packet = new DatagramPacket(aBlockBuffer, aBlockBuffer.length, clientIP, PORT);
//                    and send it
                    sending_socket.send(packet);
                    System.out.println("packet sent.");
                }
                System.out.println(blockBuffer.length+" packets sent.");
                //Make a DatagramPacket from it, with client address and port number




            } catch (IOException e) {
                System.out.println("ERROR: TextSender: Some random IO error occured!");
                e.printStackTrace();
            }



        }
    }


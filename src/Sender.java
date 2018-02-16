/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author mvk15gsu
 */
public class Sender {



//    send it using datagram libs (voip lib)

    public static void main(String[] args) throws Exception {

//        create a audio record object
        AudioRecord recorder = new AudioRecord(1);

//        record a snippet of audio
        recorder._RECORD();

//        play that snippet (testing only)
        recorder._PLAY();

//        create a sender object
        AudioSend sender = new AudioSend(5555, "localhost", recorder);

//        send the audio snippet created earlier
        sender._SEND();


    }
}

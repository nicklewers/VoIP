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

import java.util.Iterator;
import java.util.Vector;

public class AudioRecord {

//    vector to store the audio blocks
    Vector<byte[]> voiceVector;

//    length of time to record for
    int recordTime;

//    define the AudioRecorder
    private AudioRecorder recorder;

//    define the AudioPlayer
    private AudioPlayer player;

    public AudioRecord(int length) {

        this.voiceVector = new Vector<byte[]>();
        this.recordTime = length;


    }

    public void _RECORD() throws Exception{

//        initialise the recorder
        this.recorder = new AudioRecorder();


        System.out.println("Recording Audio...");

//        capture audio data and add to voiceVector
        for (int i = 0; i < Math.ceil(this.recordTime / 0.032); i++) {
            byte[] block = recorder.getBlock();
            this.voiceVector.add(block);
        }

//        close the recorder
        recorder.close();

        System.out.println("Finished recording.");
    }

    public void _PLAY() throws Exception{

//        initialise the player
        this.player = new AudioPlayer();

        System.out.println("Playing Audio...");

//        iterate through voiceVector and play out each audio block
        Iterator<byte[]> voiceItr = voiceVector.iterator();
        while (voiceItr.hasNext()) {
            player.playBlock(voiceItr.next());
        }

        //Close audio output
        player.close();
    }
}



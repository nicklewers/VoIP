package voip;

import java.net.DatagramPacket;


/**
 *  Takes an array of packets and rotates them by 90 degrees in order to prevent
 *  large, long bursts of loss.
 */

final public class Interleaver {
    
    private final int CAPACITY;
    private final int DIMENSION;
    
    /*
    *   Init the interleaver.
    *   @param overall capacity rows*columns.
    *   @throw IllegalArgumentException if number of columns and rows not equal.
    */
    public Interleaver(int capacity){
        
        this.CAPACITY = capacity;
        this.DIMENSION = (int)Math.sqrt(CAPACITY);
        
        if(CAPACITY%DIMENSION!=0) throw new IllegalArgumentException();
        
    }
    
    //Get array of packets and put it in a rows*cols vector.
    private DatagramPacket[] from2dArr(DatagramPacket[][] arr){
        
        DatagramPacket[] result = new DatagramPacket[CAPACITY];
        
        for(int i = 0; i < DIMENSION; i++){
            for(int j = 0; j < DIMENSION; j++){
                result[i*DIMENSION+j] = arr[i][j];
            }
        }
        
       return result; 
    }
    
    //Get the vector and return it in an array.
    private DatagramPacket[][] to2dArr(DatagramPacket[] arr){
        
        DatagramPacket[][] result = new DatagramPacket[DIMENSION][DIMENSION];
        
        int k = 0;
        for(int i = 0; i < DIMENSION; i++){
            for(int j = 0; j < DIMENSION; j++){
                result[i][j] = arr[k++];
            }
        }
        return result;
    } 
    
    //Interleaving algorithm, sender side.
    public DatagramPacket[] interleave(DatagramPacket[] arr){
        
        DatagramPacket[][] vec = to2dArr(arr);
        
        for(int i = 0; i < DIMENSION; i++){
            for(int j = i + 1; j < DIMENSION; j++){
                DatagramPacket temp = vec[i][j];
                vec[i][j] = vec[j][i];
                vec[j][i] = temp;
            }
        }
        
        //Return the vector as an array.
        return from2dArr(vec);
    }
    
        public DatagramPacket[] sort(DatagramPacket[] arr){
        DatagramPacket temp;
        for(int i = 1; i < arr.length; i++){
            for(int j = i; j > 0; j--){
                if(arr[j].getData()[512] < arr[j-1].getData()[512]){
                    temp = arr[j];
                    arr[j] = arr[j-1];
                    arr[j-1] = temp;
                }
            }
        }
        return arr;
    }
    
}

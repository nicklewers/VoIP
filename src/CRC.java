package voip;
import java.util.Arrays;

    /**
    * Static methods to encode and decode data with CRC.
    * toBinary() and fromBinary(): http://stackoverflow.com/q/11528898/
    */
    
final public class CRC {
    
    //Final 33 divisor for CRC-32.
    final static int DIVISOR = 33;
        
    public static String crcXor(char[] a, char[] b){
            
        StringBuilder builder = new StringBuilder();
            
        for(int i = 1; i < b.length; i++){
            if(a[i] == b[i]){
                builder.append('0');
            } else {
                builder.append('1');
            }
        }
        return builder.toString();
    }
        
    public static String crcMod2(String divident, String divisor){            

        int bitsNo = divisor.length();
        char tmp[] = new char[bitsNo];
        char dividC[] = divident.toCharArray();
        char divisC[] = divisor.toCharArray();
        for (int i = 0; i < bitsNo; i++){
            tmp[i] = 0;
        }

        char[] zeroDivisor = new char[bitsNo];
                for(int i = 0; i < bitsNo; i++){
                    zeroDivisor[i] = '0';
                    }

        while(bitsNo < divident.length()){

            if(tmp[0] == '1'){
                tmp = (crcXor(divisC, tmp) + dividC[bitsNo]).toCharArray();
            }

            else {    
                tmp = (crcXor(zeroDivisor, tmp) + dividC[bitsNo]).toCharArray();
            }
            bitsNo++;                
        }

        if(tmp[0] == '1'){
            tmp = crcXor(divisC, tmp).toCharArray();
        }
        else {
            tmp = crcXor(zeroDivisor, tmp).toCharArray();
        }

        return String.valueOf(tmp);
    }

    static String toBinary(byte[] bytes) {

        StringBuilder sb = new StringBuilder(bytes.length * Byte.SIZE);

        for( int i = 0; i < Byte.SIZE * bytes.length; i++ ){
            sb.append((bytes[i / Byte.SIZE] << i % Byte.SIZE & 0x80) == 0 ? '0' : '1');
        }

        return sb.toString();
    }


    static byte[] fromBinary(String s) {
        
        int sLen = s.length();
        byte[] toReturn = new byte[(sLen + Byte.SIZE - 1) / Byte.SIZE];
        char c;

        for( int i = 0; i < sLen; i++ ){
            if( (c = s.charAt(i)) == '1' ){
                toReturn[i / Byte.SIZE] = (byte) (toReturn[i / Byte.SIZE] | (0x80 >>> (i % Byte.SIZE)));
            }
            else if ( c != '0' ) {
                throw new IllegalArgumentException();
            }
        }

    return toReturn;
    }


    public static byte[] crcCalc(byte[] b){

        String binDiv = Integer.toBinaryString(DIVISOR);
        String binRep = toBinary(b);
        String binRepOrig = binRep;


        for(int i = 0; i < binDiv.length()-1; i++){
            binRep = binRep + 0;
        }


        String remainder = crcMod2(binRep, binDiv);

        binRepOrig += remainder;


        b = fromBinary(binRepOrig);

        return b;
    }

    public static boolean isCorrupt(byte[] b){
        String binDiv = Integer.toBinaryString(DIVISOR);
        String binRep = toBinary(b);

        char[] rem = crcMod2(binRep, binDiv).toCharArray();

        for(int i = 0; i < rem.length; i++){
            if(rem[i] != '0'){
               return true;
            }
        }

        return false;
    }
        
    public static byte[] crcDecode(byte[] b){
        return Arrays.copyOf(b, 512);
    }
        
}
    
    


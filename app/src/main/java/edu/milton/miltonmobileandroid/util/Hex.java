package edu.milton.miltonmobileandroid.util;

public class Hex {
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    public static byte[] HexStringToByteArray(String s) {
        byte data[] = new byte[s.length()/2];
        for(int i=0;i < s.length();i+=2) {
            data[i/2] = (Integer.decode("0x"+s.charAt(i)+s.charAt(i+1))).byteValue();
        }
        return data;
    }
    //http://stackoverflow.com/questions/19607001/how-to-convert-from-any-base-to-base-10-in-java
    //symbols array
    private static final String SYMBOLS = "0123456789ABCDEF";

    //actual algorithm
    public static long rebase(String number, int base)
    {
        long result = 0;
        int position = number.length(); //we start from the last digit in a String (lowest value)
        for (char ch : number.toCharArray())
        {
            int value = SYMBOLS.indexOf(ch);
            result += value * Math.pow(base,--position); //this is your 1x2(pow 0)+0x2(pow 1)+0x2(pow 2)+1x2(pow 3)

        }
        return result;
    }
}

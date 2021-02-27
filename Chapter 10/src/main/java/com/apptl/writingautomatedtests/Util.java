package com.apptl.writingautomatedtests;

/**
 * @author Erik Hellman
 */
public class Util {
    public static int byteArrayToInt(byte[] bytes)
            throws IllegalArgumentException {
        if(bytes == null ||bytes.length != 4) {
            throw new IllegalArgumentException();
        }
        return bytes[3] & 0xFF |
                (bytes[2] & 0xFF) << 8 |
                (bytes[1] & 0xFF) << 16 |
                (bytes[0] & 0xFF) << 24;
    }
}

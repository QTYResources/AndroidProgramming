package com.apptl.writingautomatedtests.test;

import android.test.AndroidTestCase;
import com.apptl.writingautomatedtests.Util;

/**
 * @author Erik Hellman
 */
public class UtilTest extends AndroidTestCase {
    public void testBytesToIntConversion() {
        int result = Util.byteArrayToInt(new byte[] {(byte) 127,
                (byte) -1, (byte) -1, (byte) -1});
        assertEquals(Integer.MAX_VALUE, result);
        result = Util.byteArrayToInt(new byte[] {(byte) 0,
                (byte) 0, (byte) 0, (byte) 0});
        assertEquals(0, result);
        result = Util.byteArrayToInt(new byte[]{(byte) -128,
                (byte) 0, (byte) 0, (byte) 0});
        assertEquals(Integer.MIN_VALUE, result);
    }

    public void testBytesToIntWithNull() {
        try {
            int result = Util.byteArrayToInt(null);
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    public void testBytesToIntWithTooShortInput() {
        try {
            int result = Util.byteArrayToInt(new byte[] {1,2,3});
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

    public void testBytesToIntWithTooLongInput() {
        try {
            int result = Util.byteArrayToInt(new byte[] {1,2,3,4,5,6,7,8,9});
        } catch (IllegalArgumentException e) {
            return;
        }
        fail();
    }

}

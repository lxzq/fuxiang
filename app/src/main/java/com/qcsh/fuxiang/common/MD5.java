package com.qcsh.fuxiang.common;

import java.security.MessageDigest;

public class MD5 {
    public static String hexdigest(String param) {
        String str = null;
        char[] arrayOfChar1 = new char[16];
        arrayOfChar1[0] = 48;
        arrayOfChar1[1] = 49;
        arrayOfChar1[2] = 50;
        arrayOfChar1[3] = 51;
        arrayOfChar1[4] = 52;
        arrayOfChar1[5] = 53;
        arrayOfChar1[6] = 54;
        arrayOfChar1[7] = 55;
        arrayOfChar1[8] = 56;
        arrayOfChar1[9] = 57;
        arrayOfChar1[10] = 97;
        arrayOfChar1[11] = 98;
        arrayOfChar1[12] = 99;
        arrayOfChar1[13] = 100;
        arrayOfChar1[14] = 101;
        arrayOfChar1[15] = 102;
        try {
            MessageDigest localMessageDigest = MessageDigest.getInstance("MD5");
            localMessageDigest.update(param.getBytes());
            byte[] arrayOfByte = localMessageDigest.digest();
            char[] arrayOfChar2 = new char[32];
            int j = 0;
            int i = 0;
            while (true) {
                if (j >= 16) {
                    str = new String(arrayOfChar2);
                    break;
                }
                int m = arrayOfByte[j];
                int k = i + 1;
                arrayOfChar2[i] = arrayOfChar1[(0xF & m >>> 4)];
                i = k + 1;
                arrayOfChar2[k] = arrayOfChar1[(m & 0xF)];
                j++;
            }
        } catch (Exception localException) {
        }
        return str;
    }
}

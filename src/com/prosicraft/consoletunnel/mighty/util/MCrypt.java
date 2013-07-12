/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.prosicraft.consoletunnel.mighty.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author passi
 */
public class MCrypt {
    
    private static String convertToHex(byte[] data)
    {
        StringBuffer buf = new StringBuffer();
 
        for (int i = 0; i < data.length; i++)
        {
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do
            {
                if ((0 <= halfbyte) && (halfbyte <= 9))
                    buf.append((char) ('0' + halfbyte));
                else
                    buf.append((char) ('a' + (halfbyte - 10)));
                halfbyte = data[i] & 0x0F;
            }
            while(two_halfs++ < 1);
        }
        return buf.toString();
    }
 
     public static String getHash(int iterationNb, String password, String salt) {
        try {
          MessageDigest digest = MessageDigest.getInstance("SHA-1");
          digest.reset();
          digest.update(salt.getBytes());
          byte[] input;
           try {
               input = digest.digest(password.getBytes("UTF-8"));
               for (int i = 0; i < iterationNb; i++) {
                   digest.reset();
                   input = digest.digest(input);
               }
               return new String(input);
           } catch (UnsupportedEncodingException ex) {
               MLog.e("Failure while doing crypt algorithm.");
               return "";
           }       
        } catch (NoSuchAlgorithmException ex) {
            MLog.e("Failure while doing crypt algorithm.");
            return "";
        }       
   }
    
}

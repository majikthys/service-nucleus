/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */

package com.liaison.commons.util;

import java.security.MessageDigest;
import java.util.Formatter;

/**
 * @author robertchristian
 */
public class AuthUtil {

    final static MessageDigest messageDigest;

    static {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public static StringBuilder hexEncode(byte buf[], StringBuilder sb) {
        final Formatter formatter = new Formatter(sb);
        for (int i = 0; i < buf.length; i++) {
            formatter.format("%02x", buf[i]);
        }
        return sb;
    }

    protected static byte[] sha256(byte[] b) throws Exception {
        messageDigest.update(b);
        return messageDigest.digest();
    }

    /**
     * Determine the hash based on raw input and <b>base</b> iterations.
     * Client code is responsible for pre-salting prior to calling this method.
     * Total iterations will be calculated by adding a delta to the base
     * iterations provided by the caller.
     * 
     * This must match the hash algorithm used by py scripts in the dmz 
     * @see http://wiki/display/hubeng/Everest+3.9+M2M+SFTP+Server+Tech+Spec
     * 
     * @param raw
     * @param iterations
     * @return
     */
    public static String determineHash(String raw, int iterations) throws Exception {

        // first, obscure the number of iterations
        for (char c : raw.toCharArray()) {
            int ord = (int) (c);
            iterations += ord;
        }

        // Note, because this first hash is done outside the loop, we loop till
        // iterations - 1.
        // TODO update this to put the first hash in the loop, need to coordinate
        //      that change with the python version of this hashing algorithm.

        // hash first time...
        byte[] result = sha256(raw.getBytes("UTF-8"));

        // then hash times number of iterations-1 
        for (int x = 0; x < iterations-1; x++) {
            result = sha256(result);
        }

        return hexEncode(result, new StringBuilder()).toString();
    }
    
    // is login alphanumeric with/without dashes and underscores? 
    public static boolean validateLogin(String input) {
        input = input.toLowerCase();
        String validChars = "abcdefghijklmnopqrstuvwxyz0123456789-_";
        for (char c : input.toCharArray()) {
          if (!validChars.contains(c + "")) return false;
        }            
        return true;
    }

    public static void main(String args[]) throws Exception {
        String[] strings = new String[] {
                "dsfsdfsdfsdfs",
                "sdfsd0f8sdfsd9087fsd09f7",
                "DSAFASFDEAFD90897897834DSD",
                "0ss9SDS0SD09S",
                "sDv",
                "--dfd",
                "~~dfd_f",
                "sdfsdfsd*fsdF*",
                "dsfsdf",
                "FDSsdf__sdf34wc0)x",
                "FDSsdf__sdf34wcx",
                "DFS-Df",
        };  

        for (String s : strings) {     
          validateLogin(s);
        }
    }
}

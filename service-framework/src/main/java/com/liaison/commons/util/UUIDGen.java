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

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.SecureRandom;

/**
 * A Universally Unique Identifier (UUID) generator helper class
 */
public class UUIDGen {

    public static final Integer GUID_LENGTH = 32;

    private static String m_sPartialAddress1 = null;
    private static String m_sPartialAddress2 = null;
    private static boolean m_init = false;

    /**
     * This method sets some static values used each time a UUID is generated.
     * This method must be synchronized. Within the Engine, this method is
     * called by J2EE Servlet Initializer. Caller outside of the engine, such as
     * PartnerConnect needs to call this method ONCE prior to using this class
     */
    synchronized public static void init() {
        if (!m_init) {
            InetAddress inetaddress = null;
            try {
                inetaddress = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                // ??? what do we do here?
                // as per JDK 1.4 source code, it calls into getHostName()
                // native code and throws
                // when there's no DNS entry for the IP.
            }
            byte abyte0[] = inetaddress.getAddress();
            String sAddress = hexFormat(getInt(abyte0), 8);
            m_sPartialAddress1 = sAddress.substring(0, 4);
            m_sPartialAddress2 = sAddress.substring(4, 8);
            SecureRandom seeder = null;
            seeder = new SecureRandom();
            seeder.nextInt();
            m_init = true;
        }
    }

    /**
     *
     * @param abyte0
     *            byte[]
     * @return int
     */
    static int getInt(byte abyte0[]) {
        int i = 0;
        int j = 24;
        for (int k = 0; j >= 0; k++) {
            int l = abyte0[k] & 0xff;
            i += l << j;
            j -= 8;
        }
        return i;
    }

    /**
     *
     * @param i
     *            int
     * @param j
     *            int
     * @return String
     */
    static String hexFormat(int i, int j) {
        String s = Integer.toHexString(i);
        return padHex(s, j) + s;
    }

    /**
     *
     * @param s
     *            String
     * @param i
     *            int
     * @return String
     */
    static String padHex(String s, int i) {
        StringBuilder stringbuffer = new StringBuilder();
        if (s.length() < i) {
            for (int j = 0; j < i - s.length(); j++)
                stringbuffer.append("0");

        }
        return stringbuffer.toString();
    }

    /**
     * returns the next randomized value in hex format
     *
     * @param s
     *            String
     * @return String
     */
    private String getVal(String s) {
        long l = System.currentTimeMillis();
        int i = (int) l & 0xffffffff;
        SecureRandom seeder = new SecureRandom();
        int j = seeder.nextInt();
        return hexFormat(i, 8) + s + hexFormat(j, 8);
    }

    /**
     * Returns a 32 bytes UUID string in upper case. eg.
     * 009541A33B81101C92F3040224009C02
     *
     * @return String UUID string.
     */
    public String getUUID() {

        StringBuilder stringbuffer1 = new StringBuilder(32);
        String s1 = hexFormat(hashCode(), 8);
        stringbuffer1.append(m_sPartialAddress1); // Read-only access after
                                                    // init() called, does not
                                                    // need to be synchronized.
        stringbuffer1.append(s1.substring(0, 4));
        stringbuffer1.append(m_sPartialAddress2); // Read-only access after
                                                    // init() called, does not
                                                    // need to be synchronized.

        stringbuffer1.append(s1.substring(4));
        String midValueUnformated = stringbuffer1.toString();

        return getVal(midValueUnformated).toUpperCase();
    }

    /**
     * Returns a 36 bytes (Microsoft Registry Format) GUID string in upper case.
     * eg. 12345678-1234-1234-1234-123456789ABC (36 chars)
     *
     * @return String UUID string.
     */
    public String getMicrosoftGUID() {
        String value = getUUID();
        return addDashesToRaw32ByteUUIDString(value);
    }

    public static String addDashesToRaw32ByteUUIDString(String rawUUID) {
        char[] chars = rawUUID.toCharArray();

        StringBuilder sb = new StringBuilder(36);
        sb.append(chars, 0, 8)
         .append('-')
         .append(chars, 8, 4)
         .append('-')
         .append(chars, 12, 4)
         .append('-')
         .append(chars, 16, 4)
         .append('-').append(chars, 20, 12);
        return sb.toString();
    }

}

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

import java.util.Random;

public class MIMEUtil
{

    /**
     * Returns a unique String representing a MIME Boundary.
     * While not strictly required the string will contain no spaces.
     * The Format is
     *      "[Random Number with Time as seed]-[Time in milis]@HUBSPAN_MIME_AGENT"
     */
    public static String calculateBoundary()
    {
        Random random = new Random();
        long prefix = random.nextLong();

        StringBuilder oSB = new StringBuilder();
        oSB.append(prefix);
        oSB.append("-");
        oSB.append(System.currentTimeMillis());
        oSB.append("@");
        oSB.append(BOUNDARY_SUFFIX);

        return (oSB.toString());
    }

    /**
     * Returns true if the value contains LWS (Linear White Space)
     * @param value the String in question
     * @return true is the String contains LWS, owherwise false
     */
    public static boolean containsLWSP(String value)
    {
        String work = null;
        if (value == null) return false;
        work = value.trim();
        for (int i=0;i<LWSP.length;i++)
        {
            if (work.indexOf(LWSP[i]) > 0) return true;
        }
        return false;
    }

    public static boolean containsTSpecial(String value)
    {
        for (int i=0;i<T_SPECIALS.length;i++)
        {
            if (value.indexOf(T_SPECIALS[i]) > 0 ) return true;
        }
        return false;
    }


    /**
     * Enquotes a string if it does not already begins and ends with  '"'
     */
    public static String enquote(String value)
    {
        if (value == null) return null;
        if ( value.startsWith("\"") && value.endsWith("\"") )
            return value;
        else
            return "\"" + value + "\"";
    }

    // See RFC 2024 page 11
    protected static final byte[]  T_SPECIALS=
    {
     '(' , ')', '<', '>', '@',
     ',' , ';', ':', '\\', '"',
     '/' , '[', ']', '?', '=',
    };

    // Array of space characters
    protected static final byte[]  LWSP=
    {
     ' ' , '\t'
    };

    public static final byte   CR   = '\r';
    public static final byte   LF   = '\n';
    public static final byte[] CRLF = {CR, LF};
    public static final String ASCII_ENCODING = "US-ASCII";
    protected static final String BOUNDARY_SUFFIX = "HUBSPAN-MIME-AGENT";
    }
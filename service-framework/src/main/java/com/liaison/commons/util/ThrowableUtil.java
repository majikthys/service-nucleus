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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * <p>
 * java.util.Throwable utilities
 * </p>
 * @author israel.evans
 */
public class ThrowableUtil {

    /**
     * Find <b>root</b> cause.
     */
    public static Throwable getRootCause(Throwable cause) {
        while (cause.getCause() != null) {
            cause = cause.getCause();
        }
        return cause;
    }

    /**
     * Get the stack trace as a String
     * @param aThrowable
     * @return
     */
    public static String getStackTrace(Throwable cause) {
        final Writer result = new StringWriter();
        final PrintWriter printWriter = new PrintWriter(result);
        cause.printStackTrace(printWriter);
        return result.toString();
    }

}

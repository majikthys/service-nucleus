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

import javax.activation.DataSource;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Supports binary attachments.
 */
public class OctetStreamDataSource implements DataSource {
	
	// A MIME attachment with the content type 
	// "application/octet-stream" is a binary file. 
    public static final String CONTENT_TYPE = "application/octet-stream";

    private final String name;
    private byte[] data;
    private ByteArrayOutputStream os;

    public OctetStreamDataSource(String name, byte[] data) {
        this.name = name;
        this.data = data;
        
        // TODO nothing has a handle on "os" and so nothing will ever 
        // be written to it.
        os = new ByteArrayOutputStream();
    } // ctor

    public String getName() {
        return name;
    } // getName

    public String getContentType() {
        return CONTENT_TYPE;
    } // getContentType

    public InputStream getInputStream() throws IOException {
        
    	// TODO os is never written to
    	if (os.size() != 0) {
            data = os.toByteArray();
        }
        return new ByteArrayInputStream(data == null ? new byte[0] : data);
    } // getInputStream

    public OutputStream getOutputStream() throws IOException {
        
    	// TODO os is never written to
    	if (os.size() != 0) {
            data = os.toByteArray();
        }
        
        // TODO shouldn't we have:
        //   os = new ByteArrayOutputStream();
        //   return os;
        // instead??  this is pointless?
        return new ByteArrayOutputStream();
    } // getOutputStream
} // class OctetStreamDataSource
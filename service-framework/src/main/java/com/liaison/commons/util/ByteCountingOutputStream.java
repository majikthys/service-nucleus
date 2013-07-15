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

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * This class implements a filter output stream that is aware of its write size. 
 * This stream sits on top of an already existing output stream (the underlying 
 * output stream) which it uses as its basic sink of data.
 *
 * The class SizeAwareOutputStream itself simply overrides the write() methods of 
 * FilterOutputStream with versions that pass all requests to the underlying output 
 * stream, while maintaining a count of bytes written.
 * 
 * @author israel.evans
 *
 */
public class ByteCountingOutputStream extends FilterOutputStream {

    protected long _size = 0L;
    protected OutputStream _out = null;
    
    /**
     * @param out
     */
    public ByteCountingOutputStream(OutputStream out) {
        super(out);
        _out = out;
    }


    /**
     * Writes the specified byte to this output stream.
     * 
     * The write method of FilterOutputStream calls the write method of its underlying output stream, that is, it performs out.write(b).
     * 
     * Implements the abstract write method of OutputStream. 
     * 
     */
    @Override
    public void write(int b) 
        throws IOException 
    {
        _out.write(b);
        _size++;
    }
    
    /**
     * Writes b.length bytes to this output stream.
     * 
     * The write method of FilterOutputStream calls its write method of three arguments with the arguments b, 0, and b.length.
     * 
     * Note that this method calls the one-argument write method of its underlying stream with the single argument b.
     * 
     */
    @Override
    public void write(byte[] b) 
        throws IOException
    {
        _out.write(b);
        _size += b.length;
    }
    
    /**
     * Writes len bytes from the specified byte array starting at offset off to this output stream.
     * 
     * The write method of FilterOutputStream calls the write method of one argument on each byte to output.
     * 
     * Note that this method calls the write method of its underlying stream with the same arguments. 
     * 
     */
    @Override
    public void write(byte[] b, int off, int len) 
        throws IOException
    {
        _out.write(b, off, len);
        _size += len;
    }

    /**
     * @return the number of bytes that have been written to this OutputStream.
     */
    public long getSize() {
        return _size;
    }
}

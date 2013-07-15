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

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * @author israel.evans
 *
 */
public class ByteCountingInputStream extends FilterInputStream 
{
    protected long _size = 0L;
    
    /**
     * The input stream to be filtered. 
     */
    protected volatile InputStream _in;

    /**
     * Constructor
     * 
     * @param in
     */
    public ByteCountingInputStream(InputStream in) {
        super(in);
        _in = in;
    }


    /**
     * Reads the next byte of data from this input stream. The value 
     * byte is returned as an <code>int</code> in the range 
     * <code>0</code> to <code>255</code>. If no byte is available 
     * because the end of the stream has been reached, the value 
     * <code>-1</code> is returned. This method blocks until input data 
     * is available, the end of the stream is detected, or an exception 
     * is thrown. 
     * <p>
     * This method
     * simply performs <code>in.read()</code> and returns the result.
     *
     * @return     the next byte of data, or <code>-1</code> if the end of the
     *             stream is reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#_in
     */
    public int read() throws IOException {
        int l = _in.read();
        _size += l;
        return l;
    }

    /**
     * Reads up to <code>byte.length</code> bytes of data from this 
     * input stream into an array of bytes. This method blocks until some 
     * input is available. 
     * <p>
     * This method simply performs the call
     * <code>read(b, 0, b.length)</code> and returns
     * the  result. It is important that it does
     * <i>not</i> do <code>in.read(b)</code> instead;
     * certain subclasses of  <code>FilterInputStream</code>
     * depend on the implementation strategy actually
     * used.
     *
     * @param      b   the buffer into which the data is read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#read(byte[], int, int)
     */
    public int read(byte b[]) throws IOException {
        int l = read(b, 0, b.length);
        _size += l;
        return l;
    }

    /**
     * Reads up to <code>len</code> bytes of data from this input stream 
     * into an array of bytes. If <code>len</code> is not zero, the method
     * blocks until some input is available; otherwise, no
     * bytes are read and <code>0</code> is returned. 
     * <p>
     * This method simply performs <code>in.read(b, off, len)</code> 
     * and returns the result.
     *
     * @param      b     the buffer into which the data is read.
     * @param      off   the start offset in the destination array <code>b</code>
     * @param      len   the maximum number of bytes read.
     * @return     the total number of bytes read into the buffer, or
     *             <code>-1</code> if there is no more data because the end of
     *             the stream has been reached.
     * @exception  NullPointerException If <code>b</code> is <code>null</code>.
     * @exception  IndexOutOfBoundsException If <code>off</code> is negative, 
     * <code>len</code> is negative, or <code>len</code> is greater than 
     * <code>b.length - off</code>
     * @exception  IOException  if an I/O error occurs.
     * @see        java.io.FilterInputStream#_in
     */
    public int read(byte b[], int off, int len) throws IOException {
        int l = _in.read(b, off, len);
        _size += l;
        return l;
    }


    /**
     * @return the number of bytes that have been written to this OutputStream.
     */
    public long getSize() {
        return _size;
    }
}

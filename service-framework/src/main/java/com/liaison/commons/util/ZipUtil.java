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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * A utility class which aids in compressing and uncompressing
 * byte arrays of data
 */

public class ZipUtil
{
    /**
     * Compresses a byte array of data
     */
    public static byte[] compress (byte[] unzipped)
    throws IOException
    {
        byte[] zipped = null;
        if (unzipped != null)
        {
            // write uncompressed bytes to a GZIPOutputStream
            // for compression
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DeflaterOutputStream dos = new GZIPOutputStream(baos);
            dos.write(unzipped);
            dos.close();
            zipped = baos.toByteArray();
        }
        return (zipped);
    }

    /**
     * Uncompresses a byte array of data
     */
    public static byte[] uncompress (byte[] zipped)
    throws IOException
    {
        byte[] unzipped = null;
        if (zipped != null)
        {
            // get a GZIPOutputStream to inflate bytes
            ByteArrayInputStream bais = new ByteArrayInputStream(zipped);
            InflaterInputStream iis = new GZIPInputStream(bais);

            // read inflated bytes into a ByteArrayOutputStream
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buf = new byte[BUFFER_SIZE];
            int read = iis.read(buf, 0, BUFFER_SIZE);
            while (read != -1)
            {
                baos.write(buf, 0, read);
                read = iis.read(buf, 0, BUFFER_SIZE);
            }
            iis.close();

            unzipped = baos.toByteArray();
        }
        return (unzipped);
    }
    
    /**
     * Decompress a GZIP'ed input stream to the outputstream
     * @param is
     * @param os
     * @throws IOException
     * @return The number of bytes in the output.
     */
    public static long uncompress (InputStream is, OutputStream os) throws IOException {
        long bytesStreamed = 0L;

        if (is != null && os != null) {
    		InflaterInputStream gzipIS = new GZIPInputStream( is );
    		bytesStreamed = StreamUtil.streamToStream(gzipIS, os);
    	}

        return bytesStreamed;
    }
    
    /**
     * Compress the input stream to the output stream using GZIP
     * @param is
     * @param os
     * @throws IOException
     * @see #compress(byte[])
     * @return The number of bytes in the output.
     */
    public static long compress(InputStream is, OutputStream os ) throws IOException {
    	long bytesStreamed = 0L;

    	if (is != null && os != null) {
    		GZIPOutputStream gzipOS = new GZIPOutputStream( os );
    		bytesStreamed = StreamUtil.copyStream(is, gzipOS);
//    	     * Note:  GZIPOutputStream.finish() must be called before closing
//    	     * otherwise, during decompress, you will get 
//    	     * java.io.EOFException: Unexpected end of ZLIB input stream
  		
    		gzipOS.finish();
    		gzipOS.flush();
    		gzipOS.close();
    		is.close();
    	}

    	return bytesStreamed;
    }

    public static final Integer UNCOMPRESSED = new Integer(0);
    public static final Integer COMPRESSED_GZIP   = new Integer(2);

    protected static final int     BUFFER_SIZE  = 8192;
}
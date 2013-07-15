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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.TimeZone;
import java.util.zip.CRC32;

/**
 * Class that contains some miscellaneous utility methods.
 * 
 * <p>
 * I know it's lame, so sue me. 8-P
 */
public class MiscUtil {
	/**
	 * Calculate a CRC for the given String. The CRCs are used to improve the
	 * speed of searching for long strings in the database.
	 * 
	 * @param s
	 *            String to calculate a CRC for.
	 * 
	 * @return The CRC value for the given String.
	 */
	public static Integer calcCRC(String s) {
		CRC32 crc = null;

		crc = new CRC32();
		crc.update(s.getBytes());

		// The cast makes me uncomfortable, but it should work fine.
		// And an int is 32 bits, and this is supposed to be a 32 bit CRC.
		return new Integer((int) crc.getValue());
	}

	/**
	 * Combine the Array of Strings into a single string.
	 * 
	 * <p>
	 * This is done in a utility function so that the UI and RT can be sure they
	 * are concatenating strings in exactly the same manner (for any situations
	 * where it might matter, like when combining values from a Certificate to
	 * generate a CRC).
	 * 
	 * @param strings
	 *            Array of strings to combine.
	 * 
	 * @return A single string which is the combination of the Strings from the
	 *         array.
	 */
	public static String concatStrings(String[] strings) {
		StringBuilder buff = null;

		// The particular manner that strings are combined is:
		// 1. trim
		// 2. force to lower case.
		// 3. concatenate with no delimiter.
		//
		// Further note, if any string in the array is null, it will be skipped.

		buff = new StringBuilder();

		if (strings != null) {
			for (int i = 0; i < strings.length; i++) {
				if (strings[i] != null) {
					buff.append(strings[i].trim().toLowerCase());
				}
			}
		}

		return buff.toString();
	}

    public static String getServerFullyQualifiedDomainName() {
		try {
			// fqdn
			return InetAddress.getLocalHost().getCanonicalHostName().toString();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the formatted IP Address of current server
	 * 
	 * @return String Server Name "127.0.0.1" is returned if
	 *         UnknownHostException occurs
	 */
	public static String getServerIPAddress() {
		try {
			return InetAddress.getLocalHost().getHostAddress();
		} catch (UnknownHostException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Gets the network name of current server
	 * 
	 * <p>
	 * Be aware that this method can take 4 seconds in error conditions.
	 * 
	 * @return String Server Name "UNKNOWN_SERVER" is returned if
	 *         UnknownHostException occurs
	 */
	public static String getServerName() {
		String strServerName = null;
		try {
			strServerName = InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			strServerName = "UNKNOWN_SERVER";
		}
		return (strServerName);
	}

	/**
	 * Given a URL this method returns the port associated with it. It is smart
	 * enough to know the HTTP and HTTPS have default ports of 80 and 443
	 * respectively.
	 * 
	 * @param url
	 *            a URL
	 * @return the port
	 */
	public static int getPort(URL url) {
		int port = url.getPort();
		if (port < 1) {
			if (url.getProtocol().equalsIgnoreCase("HTTP")) {
				port = 80;
			} else if (url.getProtocol().equalsIgnoreCase("HTTPS")) {
				port = 443;
			}
		}
		return port;
	}

	public static String getServerDateTime(String strTimeZone, String strFormat) {
		// This is a thread-safe use of SimpleDateFormat.
		SimpleDateFormat oSDF = new SimpleDateFormat(strFormat);
		GregorianCalendar oCal = new GregorianCalendar(TimeZone
				.getTimeZone(strTimeZone));
		oSDF.setCalendar(oCal);
		String strDate = oSDF.format(new Date());
		return (strDate);
	}

	public static String getGMTTimeStamp(Long lTimeInMs) {
		Date date = new Date(lTimeInMs);
		SimpleDateFormat oSDF = new SimpleDateFormat(DT_FORMAT_LONG_TIMESTAMP);
		oSDF.setTimeZone(TimeZone.getTimeZone(TIMEZONE_GMT));
		return oSDF.format(date);
	}

	public static String getGMTTimeStamp(Date date) {
		SimpleDateFormat oSDF = new SimpleDateFormat(DT_FORMAT_LONG_TIMESTAMP);
		oSDF.setTimeZone(TimeZone.getTimeZone(TIMEZONE_GMT));
		return oSDF.format(date);
	}

	public static String getFormattedTimeStamp(Date date, String format,
			String timeZone) {
		SimpleDateFormat oSDF = new SimpleDateFormat(format);
		oSDF.setTimeZone(TimeZone.getTimeZone(timeZone));
		return oSDF.format(date);
	}

	public static String padLength(String sValue, int maxLength) {
		if (sValue == null)
			return BLANKS.substring(0, maxLength);
		else
			return (sValue + BLANKS).substring(0, maxLength);
	}

	/**
	 * Return a String containing the stack trace of an exception.
	 */
	public static String exceptionToString(Exception e) {
		StringWriter stringWriter = null;
		PrintWriter printWriter = null;

		stringWriter = new StringWriter();
		printWriter = new PrintWriter(stringWriter);

		e.printStackTrace(printWriter);

		return stringWriter.toString();
	}

	/**
	 * Strip the path off the given URL.
	 * 
	 * @param strUrl
	 *            URL to strip the path off.
	 * 
	 * @return The URL with the path removed.
	 */
	public static String stripPath(String strUrl) {
		URL url = null;
		URL url2 = null;
		String outputUrl = null;

		try {
			url = new URL(strUrl);
			url2 = new URL(url.getProtocol(), url.getHost(), url.getPort(), "/");
			outputUrl = url2.toString();
		} catch (MalformedURLException e) {
			// Do nothing, just return null.
		}

		return outputUrl;
	}

	/**
	 * Converts special characters into HTML code. This is used in places where
	 * these characters could stand for the end of a logical value.
	 * 
	 * @param text
	 *            to be converted
	 * @return text with special characters converted
	 */
	public static String HTMLEncode(String text) {
		if (text == null)
			return "";

		StringBuilder results = null;
		char[] orig = null;
		int beg = 0, len = text.length();

		for (int i = 0; i < len; ++i) {
			char c = text.charAt(i);

			switch (c) {
			case 0:
			case '&':
			case '\'':
			case '<':
			case '>':
			case '"':
				if (results == null) {
					orig = text.toCharArray();
					results = new StringBuilder(len + 10);
				}
				if (i > beg)
					results.append(orig, beg, i - beg);
				beg = i + 1;
				switch (c) {
				default: // case 0:
					continue;
				case '&':
					results.append("&amp;");
					break;
				case '\'': // IE doesn't like &apos;
					results.append("&#39;");
					break;
				case '<':
					results.append("&lt;");
					break;
				case '>':
					results.append("&gt;");
					break;
				case '"':
					results.append("&quot;");
					break;
				}
				break;
			}
		}
		if (results == null)
			return text;

		results.append(orig, beg, len - beg);

		return results.toString();
	}

	public static byte[] HTMLEncode(byte[] in) {
		if (in == null) {
			return new byte[0];
		}

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		out.reset();
		int beg = 0, len = in.length;

		try {

			for (int i = 0; i < len; ++i) {
				switch (in[i]) {
				case 0:
				case '&':
				case '<':
				case '>':
				case '"':
					if (i > beg) {
						out.write(in, beg, i - beg);
					}
					beg = i + 1;
					switch (in[i]) {
					default: // case 0:
						continue;
					case '&':
						out.write("&amp;".getBytes());
						break;
					case '<':
						out.write("&lt;".getBytes());
						break;
					case '>':
						out.write("&gt;".getBytes());
						break;
					case '"':
						out.write("&quot;".getBytes());
						break;
					}
					break;
				}
			}
		} catch (IOException e) {
			// ???
		}
		if (out.size() == 0)
			return in;

		out.write(in, beg, len - beg);

		return (out.toByteArray());
	}

	/**
	 * Filters input list by token match.
	 * 
	 * @param list
	 * @param token
	 * @return
	 */
	public static List<String> filter(List<String> list, String token,
			boolean caseSensitive) {
		List<String> filteredList = new ArrayList<String>();
		for (String s : list) {
			if (!caseSensitive && s.toUpperCase().contains(token.toUpperCase())) {
				filteredList.add(s);
			} else if (caseSensitive && s.contains(token)) {
				filteredList.add(s);
			}
		}

		return filteredList;
	}

	public static final String DT_FORMAT_YYMMDD = "yyMMdd";
	public static final String DT_FORMAT_YYYYMMDD = "yyyyMMdd";
	public static final String DT_FORMAT_YYYYMMDD_DASHED = "yyyy-MM-dd";
	public static final String DT_FORMAT_24HHMMSS = "HHmmss";
	public static final String DT_FORMAT_24HHMMSS_COLONS = "HH:mm:ss";
	public static final String DT_FORMAT_24HHMM = "HHmm";
	public static final String DT_FORMAT_12HHMMSSAMPM = "hhmmssa";
	public static final String DEFAULT_TIMEZONE = TimeZone.getDefault().getID();
	public static final String DT_FORMAT_LONG_TIMESTAMP = "EEE MMM dd HH:mm:ss z yyyy";
	public static final String TIMEZONE_GMT = "GMT";

	protected static final String BLANKS = "                                  ";
}

//
// End
//


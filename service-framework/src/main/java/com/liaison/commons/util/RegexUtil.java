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

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An implementation of a caching RegEx Util. 
 * 
 * Compiled Patterns are cached using a "Least Recently Used" strategy
 * with the RegEx String as the key.
 * 
 * The intended use case for this class is for RexEx functionality in XSLT.
 * Here is a simple XSLT that illustrates usage:
 * 
 * <pre>
 * <xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="1.0" 
 *     xmlns:regex="xalan://com.hubspan.shared.util.RegexUtil" 
 *     exclude-result-prefixes="regex">
 *     
 *     <xsl:output method="text"/>
 *     
 *     <xsl:template match="/">
 *         <xsl:value-of select="regex:replaceAll('a*b', 'aaaaab', 'ccc')" />
 *     </xsl:template>
 * </xsl:stylesheet>
 * </pre>
 *
 * 
 * @author israel.evans
 *
 */
public class RegexUtil implements Serializable {

    private static final long           serialVersionUID = 7512477849806975489L;
    public static final int             MAX_SIZE         = 100;                 // what is the right balance between size and performance?

    // RegEx string -> Pattern instance
    private static Map<String, Pattern> _patternCache;
    private static Map<String, Matcher> _matcherCache;
    static {
        _patternCache = Collections.synchronizedMap(new CacheMap<String, Pattern>(MAX_SIZE));
        _matcherCache = Collections.synchronizedMap(new CacheMap<String, Matcher>(MAX_SIZE));
    }

    /**
     * Compile and cache the Pattern
     * @param regex
     * @return
     */
    public static Pattern compile(String regex) {
        final Pattern pattern;

        if (_patternCache.containsKey(regex)) {
            pattern = _patternCache.get(regex);
        } else {
            pattern = Pattern.compile(regex);
            _patternCache.put(regex, pattern);
        }

        return pattern;
    }

    /**
     * Get a Matcher based on the input and a cached Pattern
     * @param regex
     * @param input
     * @return
     */
    public static Matcher getMatcher(String regex, String input) {
        final Matcher matcher;

        if (_matcherCache.containsKey(regex)) {
            matcher = _matcherCache.get(regex);
            matcher.reset(input);
        } else {
            Pattern pattern = compile(regex);
            matcher = pattern.matcher(input);
            _matcherCache.put(regex, matcher);
        }

        return matcher;
    }

    /**
     * Attempts to find the next subsequence of the input sequence that matches the pattern.
     * @param regex
     * @param input
     * @return
     */
    public static boolean find(String regex, String input) {
        return getMatcher(regex, input).find();
    }

    /**
     * Resets this matcher and then attempts to find the next subsequence of the input sequence that matches the pattern, starting at the specified index.
     * @param regex
     * @param input
     * @param start
     * @return
     */
    public static boolean find(String regex, String input, int start) {
        return getMatcher(regex, input).find(start);
    }

    /**
     * Attempts to match the entire region against the pattern.
     * @param regex
     * @param input
     * @return
     */
    public static boolean matches(String regex, String input) {
        return getMatcher(regex, input).matches();
    }

    /**
     * Replaces every subsequence of the input sequence that matches the pattern with the given replacement string.
     * @param regex
     * @param input
     * @param replacement
     * @return
     */
    public static String replaceAll(String regex, String input, String replacement) {
        return getMatcher(regex, input).replaceAll(replacement);
    }

    /**
     * Replaces the first subsequence of the input sequence that matches the pattern with the given replacement string.
     * @param regex
     * @param input
     * @param replacement
     * @return
     */
    public static String replaceFirst(String regex, String input, String replacement) {
        return getMatcher(regex, input).replaceFirst(replacement);
    }

    /**
     * Returns true if more input could change a positive match into a negative one.
     * @param regex
     * @param input
     * @return
     */
    public static boolean requireEnd(String regex, String input) {
        return getMatcher(regex, input).requireEnd();
    }

}

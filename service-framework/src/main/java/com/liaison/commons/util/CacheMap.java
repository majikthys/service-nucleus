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

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * An extension to java.util.LinkedHashMap that overrides removeEldestEntry()
 * so that the size of the Map will never exceed the specified maximum number of
 * entries. By default, the Map is access ordered. 
 * 
 * Constructors that DO NOT specify an initialCapacity will define the initial capacity 
 * and maximum size equal to the static constant DEFAULT_MAX_ENTRIES.
 * 
 * Constructors that specify an initialCapacity will define the maximum size
 * equal to the initialCapacity.
 * 
 * Constructors that DO NOT specify a loadFactor will default the loadFactor to 1.0 so
 * as to not incur the overhead of invoking rehash() and increasing capacity to beyond
 * the initial (and maximum) size. 
 * 
 * For constructors that specify a loadFactor, it is recommended to set the loadFactor to 
 * 1.0 for the reasons given above.
 * 
 * @author israel.evans
 *
 */
public class CacheMap<K,V> extends LinkedHashMap<K,V> {
	
	private static final long serialVersionUID = 713645586703955811L;
	public static final int DEFAULT_MAX_ENTRIES = 100;
	public static final boolean ACCESS_ORDERED = true;
	public static final boolean INSERT_ORDERED = false;

	private int maxEntries;
	
	/**
	 * Default constructor that sets maximum size equal to DEFAULT_MAX_ENTRIES
	 */
	public CacheMap() {
		super(DEFAULT_MAX_ENTRIES, 1.0F, ACCESS_ORDERED);
		this.maxEntries = DEFAULT_MAX_ENTRIES;
	}

	/**
	 * Constructor specifying maximum size equal to initialCapacity
	 * 
	 * @param initialCapacity
	 */
	public CacheMap(int initialCapacity) {
		super(initialCapacity, 1.0F, ACCESS_ORDERED);
		this.maxEntries = initialCapacity;
	}

	/**
	 * Constructor that creates a CacheMap from a Map and 
	 * sets maximum size equal to DEFAULT_MAX_ENTRIES
	 * 
	 * This is the one exception to the rule for setting initialCapacity
	 * and loadFactor. These values will be set by the call to super().
	 * 
	 * @param m
	 */
	protected CacheMap(Map<K,V> m) {
		super(m);
		this.maxEntries = DEFAULT_MAX_ENTRIES;
	}

	/**
	 * Constructor specifying maximum size equal to initialCapacity and a loadFactor parameter
	 * 
	 * Even though the loadFactor parameter may cause the Map to increase its capacity
	 * and invoke rehash(), the size will never exceed the initialCapacity.
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 */
	public CacheMap(int initialCapacity, float loadFactor) {
		super(initialCapacity, loadFactor, ACCESS_ORDERED);
		this.maxEntries = initialCapacity;
	}

	/**
	 * Constructor that will create Map corresponding to a FIFO cache or 
	 * an LRU cache depending on the value of the accessOrder parameter.
	 * 
	 * Note: In access-ordered linked hash maps (LRU cache), merely querying the map 
	 * with get() is a structural modification.
	 * 
	 * @param initialCapacity
	 * @param loadFactor
	 * @param accessOrder - the ordering mode - true for access-order, false for insertion-order 
	 */
	public CacheMap(int initialCapacity, float loadFactor, boolean accessOrder) {
		super(initialCapacity, loadFactor, accessOrder);
		this.maxEntries = initialCapacity;
	}

	/**
	 * This override will allow the map to grow up to "maxEntries" entries 
	 * and then delete the eldest entry each time a new entry is added, 
	 * maintaining a steady state of "maxEntries" entries.  
	 */
	@Override
    protected boolean removeEldestEntry(Map.Entry<K,V> eldest) {
        return this.size() > this.maxEntries;
    }

}

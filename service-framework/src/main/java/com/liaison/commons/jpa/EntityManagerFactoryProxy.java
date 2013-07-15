/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */

package com.liaison.commons.jpa;

import static com.liaison.commons.util.settings.PersistenceProperties.PERSISTENCE_UNIT_NAME_PROPERTY;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;

/**
 * Proxy for entity manager factory. This caches EntityManagerFactory by persistence unit name, in a thread safe manner.
 * 
 * A EntityManagerFactory is an expensive-to-create, threadsafe object intended to be shared by 
 * all application threads. It is created once, usually on application startup.
 * 
 * An EntityManager is an inexpensive, non-threadsafe object that should be used once, for a 
 * single business process, a single unit of work, and then discarded. An EntityManager will 
 * not obtain a JDBC Connection (or a Datasource) unless it is needed, so you may safely open 
 * and close an EntityManager even if you are not sure that data access will be needed to serve a particular request*. 
 * (This becomes important as soon as you are implementing some of the following patterns using request interception.)
 * 
 * @author mcoburn
 * @author ievans
 */
public class EntityManagerFactoryProxy {
	private static Map<String, EntityManagerFactory> _emfCache = new HashMap<String, EntityManagerFactory>();
	private static DecryptableConfiguration configuration = LiaisonConfigurationFactory.getConfiguration();


	/**
	 * Gets the default EntityManagerFactory
	 * @return
	 */
	public static EntityManagerFactory getEntityManagerFactory() {
		return getEntityManagerFactory(null);
	}
	
	/**
	 * Gets a cached EntityManagerFactory by persistence unit name
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
	public static EntityManagerFactory getEntityManagerFactory(String persistenceUnitName) {
		String persistenceUnit = persistenceUnitName;
		
		// make sure we have a name
        if (null == persistenceUnit || persistenceUnit.length() == 0) {
            persistenceUnit = getDefaultPersistenceUnitName();
        }
        
        // make sure there is a cached EntityManagerFactory for this name
		if ( !_emfCache.containsKey(persistenceUnit) ) {
			synchronized (EntityManagerFactoryProxy.class) {
			    // TODO - why is there another check for not found inside synchronized? (IE) 
			    //      - Good question, I'm guessing max was looking to entirely avoid thread contention in when not null while avoiding race condition if null.  (JFR)
				//      - (IH) Possible answer: 
				//            if you are blocking on the sync statement, it may means that someone else is IN the Sync Block.  
				//            once you pass the sync block, the cache may already have the object that you were/are looking for.
				
				if  ( !_emfCache.containsKey(persistenceUnit) ) {
				    EntityManagerFactory emf = Persistence.createEntityManagerFactory(persistenceUnit);
					_emfCache.put(persistenceUnit, emf);
				}
			}
		}
		
		return (_emfCache.get(persistenceUnit));
	}

	
	/**
	 * Gets the default persistence unit name from application properties
	 * 
	 * @return
	 */
	protected static String getDefaultPersistenceUnitName() {	
		return configuration.getString(PERSISTENCE_UNIT_NAME_PROPERTY);
	}

	
}

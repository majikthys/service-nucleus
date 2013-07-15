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

import static com.liaison.commons.util.settings.PersistenceProperties.INITIALIZATION_QUERY_PROPERTY;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.LockModeType;
import javax.persistence.Query;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;

/**
 * 
 * A set of utility methods to be used to execute persistence Operations 
 * on JPA entities.
 * 
 * See http://java.dzone.com/articles/saving_detatched_entities
 * 
 * @author Max
 * @see EntityManagerFactoryProxy
 * @see OperationDelegate
 */
public class DAOUtil {
	protected static Logger logger = LogManager.getLogger(DAOUtil.class);

	private static DecryptableConfiguration configuration = LiaisonConfigurationFactory.getConfiguration();

	protected static String getInitialQuery() {
		return configuration.getString(INITIALIZATION_QUERY_PROPERTY, "SELECT * FROM DUAL"); // assume oracle as default
	}

	/**
	 * Provides an entity manager when needed, from EntityManager Factory using the default
	 * persistence unit name.
	 * 
	 * @return	EntityManager
	 */
	public static EntityManager getEntityManager() {
		return getEntityManager(null);
	}

	/**
     * Provides an entity manager when needed, from EntityManager Factory
	 * 
	 * @param persistenceUnitName
	 * @return
	 */
    public static EntityManager getEntityManager(String persistenceUnitName) {
        return (EntityManagerFactoryProxy.getEntityManagerFactory(persistenceUnitName).createEntityManager());
    }
	
    /** ============================================================================= **/
    
    /**
     * Executes a query to get first connection hot.
     * 
     * It is advised that developers utilize this method when bringing up a container.
     * 
     */
    public static void init() {
    	EntityManager em = getEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            Query q = em.createNativeQuery(getInitialQuery());
            q.getResultList();
            tx.commit();
         } catch (Throwable t) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            // HACK!
            handlePersistenceException(t);
        } finally {

            if (em != null) {
                em.close();
            }
        }
    }
    
    // HACK!!
    //
    // Oracle UCP pooling is not officially supported by DataNucleus for JPA
    //  - See http://www.datanucleus.org/products/datanucleus/jpa/datastore_connection.html
    // 
    // Suppress specific DataNucleus initialization Exception that gets thrown on first exercise of
    // a JPA operation when using UCP pooling.
    protected static void handlePersistenceException(Throwable t) {
        String magicPhrase = "Universal Connection Pool already exists in the Universal Connection Pool Manager.";
        if (t instanceof javax.persistence.PersistenceException) {
            if (t.getMessage() != null && t.getMessage().contains(magicPhrase)) {          
            	// This is the hack. Swallow exception here.
            	logger.info("Swallong UCP Exception during init");
            } else {
            	throw (javax.persistence.PersistenceException)t;
            }
        } else if (t instanceof RuntimeException ) {
        	throw (RuntimeException) t;
        } else {
        	throw new RuntimeException("unexpected exception in init.", t);
        }
        
     }
    
    /**
     * Fetches a list of (potentially heterogeneous) entities via given Operation
     * 
     * @param o
     * @param em
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> List<T> fetch(OperationDelegate o, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        List<T> list = null;
        try {
            tx.begin();
            list = (List<T>)o.perform(em);
            tx.commit();
            return list;
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
        
    }	

    /**
     * Fetches a list of (potentially heterogeneous) entities via given Operation
     * 
     * @param o
     * @return
     */
    public static <T> List<T> fetch(OperationDelegate o) {
        EntityManager em = getEntityManager();
        try {
            return fetch(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }    
    
	/**
	 * Do something with an OperationDeligate inside a transaction boundary
	 * 
	 * @param o
     * @param em
	 */
	public static void perform(OperationDelegate o, EntityManager em) {
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            o.perform(em);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }

	/**
     * Do something with an OperationDeligate inside a transaction boundary
     * using the default persistence unit
	 * 
	 * @param o
	 */
    public static void perform(OperationDelegate o) {
        EntityManager em = getEntityManager();
        try {
            perform(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
	
	/**
	 * Refresh a detached instance with the current persisted state.
	 * 
	 * @param entity
	 * @param id
	 * @param em
	 */
	public static <T> void refreshDetached(T entity, Object primaryKey, EntityManager em)
	{
	    // already attached, NO OP
	    if (em.contains(entity)) {
	        return;
	    }
	    
	    // Check for any OTHER instances already attached to the session since
	    // refresh will not work if there are any.
	    @SuppressWarnings("unchecked")
        T attached = (T) em.find(entity.getClass(), primaryKey);
	    if (attached != entity)
	    {
	        em.detach(attached);
	        em.lock(entity, LockModeType.NONE);
	    }
	    em.refresh(entity);
	}	
	
	/** ============================================================================= **/
	/** JPA EntityManager methods **/
	
	/**
	 * Check if the instance is a managed entity instance belonging to the current persistence context.
	 *  
	 * @param o
	 * @param em
	 * @return - boolean 
	 */
    public static <T> boolean contains(T o, EntityManager em) { 
        return em.contains(o);
    }
    
    /**
     * Remove the given entity from the persistence context, causing a managed entity to become detached.
     * 
     * @param o
     * @param em
     */
    public static <T> void detach(T o, EntityManager em) { 
        em.detach(o);
    }
	
    /**
     * Find by primary key.
     * 
     * @param entityClass
     * @param primaryKey
     * @param em
     * @return  - the typed instance, loaded by primary key
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, EntityManager em) { 
        return em.find(entityClass, primaryKey);
    }
    
    /**
     * Find by primary key
     * using the default persistence unit
     * 
     * @param entityClass
     * @param primaryKey
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey) { 
        EntityManager em = getEntityManager();
        try {
            return find(entityClass, primaryKey, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     *  Find by primary key and lock
     *  
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param em
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, EntityManager em) { 
        return em.find(entityClass, primaryKey, lockMode);
    }
    
    /**
     * Find by primary key and lock
     * using the default persistence unit
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode) { 
        EntityManager em = getEntityManager();
        try {
            return find(entityClass, primaryKey, lockMode, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    
    /**
     * Find by primary key and lock, using the specified properties.
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties
     * @param em
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties, EntityManager em) { 
        return em.find(entityClass, primaryKey, lockMode, properties);
    }
    
    /**
     * Find by primary key and lock, using the specified properties.
     * using the default persistence unit
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties) { 
        EntityManager em = getEntityManager();
        try {
            return find(entityClass, primaryKey, lockMode, properties, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Find by primary key, using the specified properties.
     *   
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties
     * @param em
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, java.util.Map<java.lang.String,java.lang.Object> properties, EntityManager em) { 
        return em.find(entityClass, primaryKey, properties);
    }
    
    /**
     * Find by primary key, using the specified properties.
     * using the default persistence unit
     *   
     * @param entityClass
     * @param primaryKey
     * @param properties
     * @param em
     * @return
     */
    public static <T> T find(Class<T> entityClass, Object primaryKey, java.util.Map<java.lang.String,java.lang.Object> properties) { 
        EntityManager em = getEntityManager();
        try {
            return find(entityClass, primaryKey, properties, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Get an instance, whose state may be lazily fetched.
     *  
     * @param entityClass
     * @param primaryKey
     * @param em
     * @return
     */
    public static <T> T getReference(Class<T> entityClass, Object primaryKey, EntityManager em) { 
        return em.getReference(entityClass, primaryKey);
    }
    
    /**
     * Get an instance, whose state may be lazily fetched.
     * using the default persistence unit
     *  
     * @param entityClass
     * @param primaryKey
     * @param em
     * @return
     */
    public static <T> T getReference(Class<T> entityClass, Object primaryKey) { 
        EntityManager em = getEntityManager();
        try {
            return getReference(entityClass, primaryKey, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Merge the state of the given entity into the current persistence context.
     * 
     * @param o
     * @param em
     * @return
     */
    public static <T> T merge(T o, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        T persisted = null;
        try {
            tx.begin();
            persisted = em.merge(o);
            tx.commit();
            return persisted;
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }
    
    /**
     * Merge the state of the given entity into the current persistence context.
     * using the default persistence unit
     * 
     * @param o
     * @param em
     * @return
     */
    public static <T> T merge(T o) { 
        EntityManager em = getEntityManager();
        try {
            return merge(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Make an instance managed and persistent.
     * 
     * @param o
     * @param em
     */
    public static <T> void persist(T o, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.persist(o);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }
    
    /**
     * Make an instance managed and persistent.
     * using the default persistence unit
     * 
     * @param o
     * @param em
     */
    public static <T> void persist(T o) { 
        EntityManager em = getEntityManager();
        try {
            persist(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     * 
     * @param o
     * @param em
     */
    public static <T> void refresh(T o, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.refresh(o);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     * using the default persistence unit
     * 
     * @param o
     */
    public static <T> void refresh(T o) { 
        EntityManager em = getEntityManager();
        try {
            refresh(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
        
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any, and lock it with respect to given lock mode type.
     *  
     * @param o
     * @param lockMode
     * @param em
     */
    public static <T> void refresh(T o, LockModeType lockMode, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.refresh(o, lockMode);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any, and lock it with respect to given lock mode type.
     * using the default persistence unit
     *  
     * @param o
     * @param lockMode
     */
    public static <T> void refresh(T o, LockModeType lockMode) { 
        EntityManager em = getEntityManager();
        try {
            refresh(o, lockMode, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, 
     * if any, and lock it with respect to given lock mode type and with specified properties
     * 
     * @param o
     * @param lockMode
     * @param em
     */
    public static <T> void refresh(T o, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.refresh(o, lockMode, properties);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any, 
     * and lock it with respect to given lock mode type and with specified properties
     * using the default persistence unit
     * 
     * @param o
     * @param lockMode
     */
    public static <T> void refresh(T o, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties) { 
        EntityManager em = getEntityManager();
        try {
            refresh(o, lockMode, properties, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
    /**
     * Refresh the state of the instance from the database, using the specified properties, 
     * and overwriting changes made to the entity, if any.
     * 
     * @param o
     * @param properties
     * @param em
     */
    public static <T> void refresh(T o, java.util.Map<java.lang.String,java.lang.Object> properties, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();
        try {
            tx.begin();
            em.refresh(o, properties);
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }

    /**
     * Refresh the state of the instance from the database, using the specified properties, 
     * and overwriting changes made to the entity, if any.
     * using the default persistence unit
     * 
     * @param o
     * @param properties
     * @param em
     */
    public static <T> void refresh(T o, java.util.Map<java.lang.String,java.lang.Object> properties) { 
        EntityManager em = getEntityManager();
        try {
            refresh(o, properties, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    /**
     * Remove the entity instance from the persistence context.
     * 
     * @param o
     * @param em
     */
    @SuppressWarnings("unchecked")
    public static <T> void remove(T o, EntityManager em) { 
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            if (! em.contains(o) && (o instanceof Identifiable)) {
                // attempt to re-attach if not attached and is GenericEntity
                Identifiable go = (Identifiable)o;
                
                // Check for any OTHER instances already attached to the session since
                // refresh will not work if there are any.
                T attached = (T) em.find(go.getEntityClass(), go.getPrimaryKey());
                if (attached != o)
                {
                    em.lock(attached, LockModeType.NONE);
                    em.detach(attached);
                }
                
                go = (Identifiable)attached;
                attached = (T) em.find(go.getEntityClass(), go.getPrimaryKey());
                em.remove(attached);                
            } else{
                // otherwise just attempt to remove()
                em.remove(o);
            }
            tx.commit();
        } catch (RuntimeException re) {
            if (tx.isActive()) {
                tx.rollback();
            }
            
            throw (re);
        }
    }

    /**
     * Remove the entity instance from the persistence context.
     * using the default persistence unit
     * 
     * @param o
     * @param em
     */
    public static <T> void remove(T o) { 
        EntityManager em = getEntityManager();
        try {
            remove(o, em);
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }
    
}

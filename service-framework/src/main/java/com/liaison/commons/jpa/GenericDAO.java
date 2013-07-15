/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use iT only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.
 */

package com.liaison.commons.jpa;

import java.util.List;

import javax.persistence.LockModeType;

/**
 * 
 * So, when should I use persist and when merge?
 * see http://spitballer.blogspot.com/2010/04/jpa-persisting-vs-merging-entites.html
 * 
 * persist
 * 
 *     You want the method always creates a new entity and never updates an entity. Otherwise, 
 *     the method throws an exception as a consequence of primary key uniqueness violation.
 *     Batch processes, handling entities in a stateful manner (see Gateway pattern)
 *     Performance optimization
 * 
 * merge
 * 
 *     You want the method either inserts or updates an entity in the database.
 *     You want to handle entities in a stateless manner (data transfer objects in services)
 *     You want to insert a new entity that may have a reference to another entity that may 
 *     or may not be created yet (relationship must be marked MERGE). For example, inserting 
 *     a new photo with a reference to either a new or a preexisting album. *
 *      
 * More on persist/merge
 * See http://java.dzone.com/articles/saving_detatched_entities
 *      
 *  - When and only when (and preferably where) we create a new entity, invoke EntityManager.persist to save it. 
 *    This makes perfect sense when we view our domain access objects as collections. I call this the persist-on-new pattern.
 *    
 *  - When updating an existing entity, we do not invoke any EntityManager method; the JPA provider will 
 *    automatically update the database at flush or commit time.
 *    
 *  - When we receive an updated version of an existing simple entity (an entity with no references to other entities) 
 *    from outside of our application and want to save the new state, we invoke EntityManager.merge to copy that state 
 *    into the persistence context. Because of the way merging works, we can also do this if we are unsure whether the 
 *    object has been already persisted.
 *    
 *  - When we need more control over the merging process, we use a DIY merge pattern. 
 *  
 *  A DIY merge might look something like this:
 *  <pre>
 *     Order existingOrder = dao.findById(receivedOrder.getId());
 *     if(existingOrder == null) {
 *         dao.persist(receivedOrder);
 *     } else {
 *         existingOrder.setCustomerName(receivedOrder.getCustomerName());
 *         existingOrder.setDate(receivedOrder.getDate());
 *     }
 *  </pre>
 *      
 * @author israel.evans
 *
 */
public interface GenericDAO <T> {

    /**
     * Fetches a lisT of (potentially heterogeneous) entities via given Operation
     * 
     * @param o
     * @return  List<T>
     */
    public List<T> fetch(OperationDelegate o);

    /**
     * Do something with an OperationDeligate inside a transaction boundary
     * 
     * @param o
     */
    public void perform(OperationDelegate o);
    
    /**
     * Refresh from database a detached instance.
     * 
     * @param entity
     * @param id
     */
    public void refreshDetached(T entity, Object primaryKey);

    /**
     * Check if the instance is a managed entity instance belonging to the current persistence context.
     * 
     * @param entity
     * @return
     */
    public boolean contains(T entity);
    
    /**
     * Remove the given entity from the persistence context, causing a managed entity to become detached.
     * 
     * @param entity
     */
    public void detach(T entity);
    
    /**
     * Find by primary key.
     * 
     * @param entityClass
     * @param primaryKey
     * @return
     */
    public T find(java.lang.Class<T> entityClass, java.lang.Object primaryKey);

    /**
     * Find by primary key and lock.
     * 
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @return
     */
    public T find(java.lang.Class<T> entityClass, java.lang.Object primaryKey, LockModeType lockMode);

    /**
     *  Find by primary key and lock, using the specified properties.
     *  
     * @param entityClass
     * @param primaryKey
     * @param lockMode
     * @param properties
     * @return
     */
    public T find(java.lang.Class<T> entityClass, java.lang.Object primaryKey, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties);
    
    /**
     * Find by primary key, using the specified properties.
     * 
     * @param entityClass
     * @param primaryKey
     * @param properties
     * @return
     */
    public T find(java.lang.Class<T> entityClass, java.lang.Object primaryKey, java.util.Map<java.lang.String,java.lang.Object> properties);
    
    /**
     * Merge the state of the given entity into the current persistence context.
     * 
     * @param entity
     * @return
     */
    public T merge(T entity);
    
    /**
     * Make an instance managed and persistent.
     * 
     * @param entity
     */
    public void persist(T entity);
    
    /**
     *  Refresh the state of the instance from the database, overwriting changes made to the entity, if any.
     *  
     * @param entity
     */
    public void refresh(T entity);
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any, and lock it with respect to given lock mode type.
     * 
     * @param entity
     * @param lockMode
     */
    public void   refresh(T entity, LockModeType lockMode);
    
    /**
     * Refresh the state of the instance from the database, overwriting changes made to the entity, if any, and lock it with respect to given lock mode type and with specified properties.
     * 
     * @param entity
     * @param lockMode
     * @param properties
     */
    public void   refresh(T entity, LockModeType lockMode, java.util.Map<java.lang.String,java.lang.Object> properties);
    
    /**
     * Refresh the state of the instance from the database, using the specified properties, and overwriting changes made to the entity, if any.
     * 
     * @param entity
     * @param properties
     */
    public void   refresh(T entity, java.util.Map<java.lang.String,java.lang.Object> properties);
    
    /**
     * Remove the entity instance.
     * 
     * @param entity
     */
    public void remove(T entity);

}

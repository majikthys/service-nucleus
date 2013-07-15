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

import java.io.Serializable;

/**
 * JPA Entities that implement this interface have a well known way of obtaining the primary key identifier.
 * 
 * @author israel.evans
 *
 */
public interface Identifiable extends Serializable {

    /**
     * Return the Primary Key identifier for this entity.
     * 
     * @return
     */
    public Object getPrimaryKey();
    
    /**
     * Return the implementing class for this entity.
     * 
     * @return
     */
    public <T> Class<T> getEntityClass();
}

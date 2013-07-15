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

import java.util.List;

import javax.persistence.EntityManager;

/**
 * A persistence operation to be executed by DAOUtil.java 
 * @see com.liaison.commons.jpa.DAOUtil
 */
public abstract class OperationDelegate {
	public abstract <T> List<T> perform(EntityManager em);
}

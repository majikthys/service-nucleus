package com.liaison.commons.jpa;

import java.util.List;

import javax.persistence.EntityManager;

/**
 * A persistence operation to be executed by DAOUtil.java 
 * @see com.liaison.commons.jpa.DAOUtil
 */
public interface Operation {
	public <T> List<T> perform(EntityManager em);
}

package com.liaison.commons.jpa;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.Query;

import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;

import static com.liaison.commons.util.settings.PersistenceProperties.INITIALIZATION_QUERY_PROPERTY;

/**
 * 
 * A set of utility methods to be used to execute persistence Operations 
 * on JPA entities.
 * 
 * @author Max
 * @see EntityManagerFactoryProxy
 * @see Operation
 */
public class DAOUtil {
	private static DecryptableConfiguration configuration = LiaisonConfigurationFactory.getConfiguration();

	protected static String getInitialQuery() {
		return configuration.getString(INITIALIZATION_QUERY_PROPERTY, "SELECT * FROM DUAL"); // assume oracle as default
	}

	/**
	 * Executes a query to get first connection hot.
	 * 
	 * It is advised that developers utilize this method when bringing up a container.
	 * 
	 */
	public static void init() {
		EntityManager em = getEntityManager();
		System.out.println("open? " + em.isOpen());

		EntityTransaction tx = em.getTransaction();
		System.out.println("open? " + em.isOpen());

		
		try {
			tx.begin();
			Query q = em.createNativeQuery(getInitialQuery());
			q.getResultList();
			tx.commit();
			System.out.println("open? " + em.isOpen());

		} catch (Throwable t) {
			if (tx.isActive()) {
				tx.rollback();
			}
			throw (t);
		} finally {

			if (em != null) {
				em.close();
			}
		}
		System.out.println("open? " + em.isOpen());

	}



	/**
	 * Provides an entity manager when needed from EntityManager Factory
	 * @return
	 */
	public static EntityManager getEntityManager() {
		return (EntityManagerFactoryProxy.getEntityManagerFactory().createEntityManager());
	}

	/**
	 * Fetches a list of (potentially heterogenous) entities via given Operation
	 */
	public static <T> List<T> fetch(Operation o)    {
		EntityManager em = getEntityManager();
		try {
			return (o.perform(em));
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}


	public static <T> void persist(T o)  { 
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			em.persist(o);
			tx.commit();
		} catch (Throwable t) {
			if (tx.isActive()) {
				tx.rollback();
			}

			throw (t);
		} finally {
			if (em != null) {
				em.close();
			}
		}
	}
	

	public static void perform(Operation o) {
		EntityManager em = getEntityManager();
		EntityTransaction tx = em.getTransaction();

		try {
			tx.begin();
			o.perform(em);
			tx.commit();

		} catch (Throwable t) {

			if (tx.isActive()) {
				tx.rollback();
			}
			throw (t);
		} finally {

			if (em != null) {
				em.close();
			}
		}
	}
	

}

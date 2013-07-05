package com.liaison.commons.jpa;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import com.liaison.commons.util.settings.DecryptableConfiguration;
import com.liaison.commons.util.settings.LiaisonConfigurationFactory;
import static com.liaison.commons.util.settings.PersistenceProperties.PERSISTENCE_UNIT_NAME_PROPERTY;

/**
 * Proxy for entity manager factory. 
 * 
 * @author max
 */
public class EntityManagerFactoryProxy {
	private static DecryptableConfiguration configuration = LiaisonConfigurationFactory.getConfiguration();

	//TODO make this throw rather than use default unit name
	protected static String getPersistenceUnitName() {
		return configuration.getString(PERSISTENCE_UNIT_NAME_PROPERTY, "Hello");
	}

	public static final String PERISTENCE_UNIT_NAME = getPersistenceUnitName(); 
	
	private static EntityManagerFactory _emf = null;

	public static EntityManagerFactory getEntityManagerFactory() {
		if (_emf == null) {
			synchronized (EntityManagerFactoryProxy.class) {
				if (_emf == null) {
					_emf = Persistence.createEntityManagerFactory(PERISTENCE_UNIT_NAME);
				}
			}
		}
		return (_emf);
	}

}



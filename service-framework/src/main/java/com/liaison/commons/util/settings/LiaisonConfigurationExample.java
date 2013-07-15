/*
 * Copyright Liaison Technologies, Inc. All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Liaison Technologies, Inc. ("Confidential Information").  You shall 
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Liaison Technologies.package com.liaison.commons.util.settings;
 */
package com.liaison.commons.util.settings;

import static com.liaison.commons.util.settings.PersistenceProperties.DB_DRIVER;
import static com.liaison.commons.util.settings.PersistenceProperties.DB_PASSWORD;
import static com.liaison.commons.util.settings.PersistenceProperties.DB_URL;
import static com.liaison.commons.util.settings.PersistenceProperties.DB_USER;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;

/**
 *  A toy example for evaluation and discussion. 
 *  
 *  Intends to use system and project properties files while keeping road clear 
 *  for future archiaus implementation.
 *  
 * @author jeremyfranklin-ross
 * @see LiaisonConfigurationUtil.getConfiguration()
 */
public class LiaisonConfigurationExample extends CompositeConfiguration implements DecryptableConfiguration {
	static public final String DEFAULT_PROPERTIES_FILE_NAME = "project.properties";
	protected Logger logger = Logger.getLogger(this.getClass());
	
	/**
	 * TODO This temporary for testing only
	 */
	protected void initStubData() {
		addProperty(DB_USER, "JER_G3_G2Hello_DBA");
		addProperty(DB_URL, "jdbc:oracle:thin:@seadv01-db03:1521:kili1");
		addProperty(DB_DRIVER, "oracle.jdbc.OracleDriver");
		addProperty(DB_PASSWORD, "12345678");
	}
	
	protected LiaisonConfigurationExample() {
		
		//Will check system configuration first for property
		SystemConfiguration systemConf = new SystemConfiguration();
		addConfiguration(systemConf);
		
		//Will check given properties file next for property
		try {
			PropertiesConfiguration propertiesFileConf = new PropertiesConfiguration(DEFAULT_PROPERTIES_FILE_NAME);
			addConfiguration(propertiesFileConf);
		} catch (ConfigurationException e) {
			logger.error("Failed to load PropertiesConfiguration:" + DEFAULT_PROPERTIES_FILE_NAME , e);
		}

		//May add other properties sources.
		
		

		//--------------------------
		// ARR HERE BE ARCHEUS PSEUDO/STUB CODE
		// 
		// This is the manner by which we may back a commons-configuration with archeus' goodness:
		//--------------------------
		//
		//		  AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler(); // or use your own scheduler
		//		  PolledConfigurationSource source = new URLConfigurationSource(); // or use your own source
		//		  scheduler.setIgnoreDeletesFromSource(true); // don't treat properties absent from the source as deletes
		//		  scheduler.startPolling(source, this);
		//		  // ...
		//		  ConfigurationManager.install(this);
		//
		//--------------------------
		// For explanation, see:
		// https://github.com/Netflix/archaius/wiki/Users-Guide#1-use-an-implementation-of-comnetflixconfigabstractpollingscheduler-to-poll-the-dynamic-configuration-source-and-put-them-into-your-own-configuration
		// 
		// Note however, by retrieving Config via LiaisonConfigurationUtil, one may make a configuration 
		// backed by the archaius ConcurrentCompositeConfiguration (it extends apache 
		// commons-configuration's AbstractConfiguration), which is produced via archaius default 
		// behavior (instead of the above strategy of backing commons-configuration with archaius).
		// Example of default archaius ConcurrentCompositeConfiguration:
		//    (ConcurrentCompositeConfiguration) DynamicPropertyFactory.getInstance().getBackingConfigurationSource();
		//--------------------------
			
		//TODO remove this it's for test purposes only
		initStubData();
		
	}

	/**
	 * Means to get a decrypted char[] form of an encrypted property.
	 * Remember, please do not use Strings to handle sensitive data.
	 * 
	 * @param key name of property
	 * @param encryptionRequired 
	 * @return 
	 * If property is encrypted: Returns decrypted value
	 * If encryption not required and property is not encrypted: Returns still unencrypted value
	 * @throws SOMESORT OF RUNTIME ERROR If encryption required and property not encrypted.
	 */
	public char[] getDecryptedCharArray(String key, boolean encryptionRequired) {

		//Preflight check:
		//If encryption required and property is NOT encrypted throw some sort of runtime error
		if (encryptionRequired && !isEncryptedProperty(key)) {
			throw new RuntimeException("Property required to be encrypted, but is not");//TODO make better runtime
		}
		//Preflight check:
		//If property doesn't exist, throw exception
		if (!containsKey(key)) {
			throw new RuntimeException("There's no property of for: " + key);//TODO better exception
		}

		//If property is NOT encrypted return unencrypted char[]
		if (!isEncryptedProperty(key)) {
			return getString(key).toCharArray();
		}
		
		//TODO: IF property IS encrypted go decrypt it and return unencrypted char[]
		//
		//--------------------------
		throw new RuntimeException("Decryption of property not yet implemented");//place holder
	}

	
	/**
	 * Checks to see if key is advertised as encrypted.
	 * 
	 * NOTE:
	 * Perhaps we do so by means of looking for "child key" with ending in
	 * ".isEncrypted". 
	 * Only a true value of associated with this key will return true.
	 * 
	 * Or... perhaps this is backed by service?
	 * 
	 * @see IS_ENCRYPTED_SUFFIX
	 */
	public boolean isEncryptedProperty(String key) {
		return getBoolean(key+IS_ENCRYPTED_SUFFIX, false);
	}

	public static final String IS_ENCRYPTED_SUFFIX  =".isEncrypted";
}

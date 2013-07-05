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

import java.io.IOException;
import java.util.List;
import org.apache.commons.configuration.AbstractConfiguration;
import org.apache.log4j.Logger;

import com.netflix.config.ConcurrentCompositeConfiguration;
import com.netflix.config.ConfigurationManager;

/**
 *  A toy example for evaluation and discussion. 
 *  
 *  Intends to use system and project properties files while keeping road clear 
 *  for future archiaus implementation.
 *  
 * @author jeremyfranklin-ross
 * @see LiaisonConfigurationUtil.getConfiguration()
 */
public class LiaisonArchaiusConfiguration extends ConcurrentCompositeConfiguration implements DecryptableConfiguration {
	
	//@see isEncruptedProperty(String)
	public static final String IS_ENCRYPTED_SUFFIX  =".isEncrypted";

	
	protected static Logger logger = Logger.getLogger(LiaisonArchaiusConfiguration.class);

	/**
	 * 
	 * Loads CascadingConfiguration for AppID and Environment
	 * 
	 * Adapted from com.netflix.karyon.server.ArchaiusIntegrationInterceptor 
	 * @return AbstractConfiguration that is the result of an Archiaus ConfigurationManager 
	 * Cascading Properties Load. May return null if properties not found for given appID 
	 * and deploymentEnvironment
	 * 
	 * @throws RuntimeException if appID not set in properties 
	 * @see ConfigurationManager.getDeploymentContext().getApplicationId()
	 */
	protected static AbstractConfiguration loadAppCascadingConfiguration() {
		
		ConfigurationManager.getConfigInstance();
        String appId = ConfigurationManager.getDeploymentContext().getApplicationId();
        //Preflight check
        if (null == appId) {
            String fatalMessage = "Application identifier not defined, skipping application level properties loading. You must set a property 'archaius.deployment.applicationId' to be able to load application level properties."; 
            logger.fatal(fatalMessage);
            throw new RuntimeException(fatalMessage);      	
        }
        
		try {
			logger.info(String
					.format("Loading application properties with app id: %s and environment: %s",
							appId, ConfigurationManager.getDeploymentContext()
									.getDeploymentEnvironment()));
			ConfigurationManager.loadCascadedPropertiesFromResources(appId);
			return ConfigurationManager.getConfigInstance();
		} catch (IOException e) {
			logger.error(
					String.format(
							"Failed to load properties for application id: %s and environment: %s. This is ok, if you do not have application level properties.",
							appId, ConfigurationManager.getDeploymentContext()
									.getDeploymentEnvironment()), e);
		}         
        
		return null;
	}

	
	/**
	 * @see LiaisonConfigurationFactory to access this bad boy
	 */
	protected LiaisonArchaiusConfiguration() {}


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

	
	
	/**
	 * Super class throws some boring warnings about List<Object> versus List.
	 * So this method exists purely to squash those warnings.
	 */
	@SuppressWarnings("unchecked")
	@Override 
	public List<Object> getList(String key, @SuppressWarnings("rawtypes") List defaultValue) {
		return super.getList(key, defaultValue);
	}
	
}

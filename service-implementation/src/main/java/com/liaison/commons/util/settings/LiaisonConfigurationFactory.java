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

import org.apache.commons.configuration.SystemConfiguration;
import org.apache.log4j.Logger;

import com.netflix.config.AbstractPollingScheduler;
import com.netflix.config.ConcurrentMapConfiguration;
import com.netflix.config.ConfigurationManager;
import com.netflix.config.DynamicConfiguration;
import com.netflix.config.DynamicPropertyFactory;
import com.netflix.config.FixedDelayPollingScheduler;
import com.netflix.config.PolledConfigurationSource;
import com.netflix.config.sources.URLConfigurationSource;



/**
 * 
 * Factory for retrieving the DecryptableConfiguration for property interaction
 * Creates a Configuration such that polled dynamic config will override values in cascaded app/environment config, which override value in system properties.
 *
 * Order of preference: 
 * 1) Polled Dynamic Configuration:
 * Loads from defaultFileName URL (set via System Property "archaius.configurationSource.defaultFileName", which defaults to config.properties file resource)
 * 
 * 2) System Properties
 * 
 * 3) Cascaded App/Environment Config
 * Loads properties of filename derived from appID and environment. As
 * follows:
 * 
 * Setting system properties below as follows will load the resources 
 * "specificapp.properties" AND "specificapp-dev.properties" (wherein 
 * specificapp-dev.properties override specificapp.properties)
 * 
 * 		archaius.deployment.environment=dev
 * 		archaius.deployment.applicationId=specificapp 
 * 
 * Note: This can be done via gradle with a doFirst, something like:
 <pre>
 	test {
		systemProperty 'archaius.deployment.applicationId', 'specificapp'
		systemProperty 'archaius.deployment.environment', 'test'
 	}
 	
 	//or
	
	jettyRun.doFirst {
		System.setProperty("archaius.deployment.applicationId","specificapp")
		System.setProperty("archaius.deployment.environment","dev")
	}
 </pre>
 * 
 * 
 * 
 */
public abstract class LiaisonConfigurationFactory {
	private static final DecryptableConfiguration decryptableConfiguration;
	
	//Hardwire JMX always on.
	static {
		System.setProperty(DynamicPropertyFactory.ENABLE_JMX, "true");
	}
	
	//Wire up polling config , system config, and then load CascadingConfig
	static {
		LiaisonArchaiusConfiguration finalConfig = new LiaisonArchaiusConfiguration();
		AbstractPollingScheduler scheduler = new FixedDelayPollingScheduler(); // or use your own scheduler
		PolledConfigurationSource source = new URLConfigurationSource(); // or use your own source
		DynamicConfiguration dynamicConfig = new DynamicConfiguration(source, scheduler);

		ConcurrentMapConfiguration configFromSystemProperties = 
				new ConcurrentMapConfiguration(new SystemConfiguration());

		// add them in this order to make dynamicConfig override myConfiguration
		finalConfig.addConfiguration(dynamicConfig, "polledDynamicURL");
		finalConfig.addConfiguration(configFromSystemProperties, "systemProperties");
		ConfigurationManager.install(finalConfig);

		LiaisonArchaiusConfiguration.loadAppCascadingConfiguration();
	
		decryptableConfiguration = finalConfig;
	}
	
	// Or one might use example configuration.. 
	//	private static final DecryptableConfiguration decryptableConfiguration = new LiaisonConfigurationExample();
	protected static Logger logger = Logger.getLogger(LiaisonConfigurationFactory.class);
	   
	/**
	 * Gets 
	 * @return
	 */
	public static DecryptableConfiguration getConfiguration() {
		return decryptableConfiguration;
	}	
	
	
}

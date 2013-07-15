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

import com.netflix.karyon.server.InitializationPhaseInterceptor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.EnumSet;

/**
 * interceptor that should be registered into the karyon bootstrap to spark configuration
 * @author jeremyfranklin-ross
 *
 */
class LiaisonArchaiusIntegrationInterceptor implements InitializationPhaseInterceptor {

    protected static final Logger logger = LoggerFactory.getLogger(LiaisonArchaiusIntegrationInterceptor.class);

    @Override
    public void onPhase(Phase phase) {
    	LiaisonConfigurationFactory.getConfiguration(); //warm up factory.
    }

    @Override
    public EnumSet<Phase> interestedIn() {
        return EnumSet.of(Phase.OnCreate);
    }
}

/*
 * Copyright (C) 2011-2012 Intel Corporation
 * All rights reserved.
 */
package com.intel.mountwilson.common;

import com.intel.mtwilson.jpa.PersistenceManager;

/**
 *
 * @author jbuhacoff
 */
public class WLMPPersistenceManager extends PersistenceManager {

    @Override
    public void configure() {
        addPersistenceUnit("MSDataPU", WLMPConfig.getJpaProperties()); // for MwPortalUser
    }
    
}
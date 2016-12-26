/*******************************************************************************
 * Copyright (c) 2016 Sierra Wireless and others.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * and Eclipse Distribution License v1.0 which accompany this distribution.
 * 
 * The Eclipse Public License is available at
 *    http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 *    http://www.eclipse.org/org/documents/edl-v10.html.
 * 
 * Contributors:
 *     Sierra Wireless - initial API and implementation
 *******************************************************************************/
package org.leshan.server.configuration;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Set;
import java.util.logging.Logger;

public class DataBaseConfiguration {
    private static final Logger LOGGER = Logger.getLogger(DataBaseConfiguration.class.getCanonicalName());

    private final Properties configProp = new Properties();

    private DataBaseConfiguration() {
        InputStream in = this.getClass().getClassLoader().getResourceAsStream("app.properties");
        try {
            configProp.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class LazyHolder {
        private static final DataBaseConfiguration INSTANCE = new DataBaseConfiguration();
    }

    public static DataBaseConfiguration getInstance() {
        return LazyHolder.INSTANCE;
    }

    public String getPropertyString(String key) {
        return configProp.getProperty(key);
    }

    public int getPropertyInt(String key) {
        return Integer.parseInt(configProp.getProperty(key));
    }

    public Set<String> getAllPropertyNames() {
        return configProp.stringPropertyNames();
    }

    public boolean containsKey(String key) {
        return configProp.containsKey(key);
    }

}

/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share;

import java.io.IOException;
import java.util.Locale;
import java.util.Properties;

import org.alfresco.webdrone.Version;
import org.alfresco.webdrone.WebDroneProperties;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * The object returns the value of a key from the properties. This is used to
 * identify the HTML element id for either Alfresco cloud, community or Enterprise.
 * The {@link AlfrescoVersion} is used to determine which property file needs to load.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public class ShareProperties implements WebDroneProperties
{
    static final String DEFAULT_ALFRESCO = "share";
    private static Log logger = LogFactory.getLog(ShareProperties.class);
    private Properties properties;
    private String propertyFileName;
    private final AlfrescoVersion version;
    private final Locale locale;

    public ShareProperties()
    {
        this(DEFAULT_ALFRESCO);
    }

    public ShareProperties(String alfrescoVersion)
    {
        this(StringUtils.isBlank(alfrescoVersion) ? DEFAULT_ALFRESCO : alfrescoVersion, "en");
    }

    public ShareProperties(String alfrescoVersion, String locale)
    {
        this.version = AlfrescoVersion.fromString(alfrescoVersion);
        this.locale = StringUtils.isBlank(locale) ? Locale.ENGLISH : new Locale(locale);
        initProperties();
    }

    /**
     * Gets the properties file for the HTML element id.
     * 
     * @return Properties properties
     */
    private void initProperties()
    {
        properties = new Properties();
        properties.putAll(initUIProperties());
        properties.putAll(initLanguageProperties());

    }

    private Properties initUIProperties()
    {
        Properties alfrescoUI = new Properties();
        try
        {
            String alfrescoVersion = version.toString();
            String alfrescoType = alfrescoVersion.split("[^a-zA-Z]")[0].toLowerCase();
            String version = alfrescoVersion.substring(alfrescoType.length());
            if (version.isEmpty())
            {
                propertyFileName = String.format("%s.properties", alfrescoType);
            }
            else
            {
                propertyFileName = String.format("%s-%s.properties", alfrescoType, version);
            }
            // Load alfresco version based properties
            alfrescoUI.load(this.getClass().getClassLoader().getResourceAsStream(propertyFileName));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Properties file not found for the given input: " + this, e);
        }
        catch (NullPointerException ne)
        {
            logger.error("No matching properties file was found");
        }
        return alfrescoUI;
    }

    private Properties initLanguageProperties()
    {
        Properties languageProperties = new Properties();
        try
        {
            String fileName = String.format("%s.properties", locale.toString());
            languageProperties.load(this.getClass().getClassLoader().getResourceAsStream(fileName));
        }
        catch (IOException e)
        {
            throw new RuntimeException("Language property file was not found for the given input: " + locale.toString(), e);
        }
        return languageProperties;
    }

    /**
     * Gets the HTML element id value for the given key.
     * 
     * @param key String HTML element id
     * @return String value of key
     */
    public final String getElement(final String key)
    {
        final String value = properties.getProperty(key);
        if (value == null)
        {
            throw new RuntimeException("Property mapping not defined: " + key);
        }
        return value;
    }

    @Override
    public String toString()
    {
        return String.format("Share PO Properties file name [%s]", propertyFileName);
    }

    /**
     * The Alfresco Share version.
     * 
     * @return {@link AlfrescoVersion} version
     */
    @SuppressWarnings("unchecked")
    public Version getVersion()
    {
        return version;
    }

    public Locale getLocale()
    {
        return locale;
    }
}

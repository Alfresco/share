/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.config.packaging;

import org.alfresco.util.VersionNumber;
import org.apache.maven.artifact.versioning.ComparableVersion;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Properties;

/**
 * An implementation of a ModulePackage using a properties file.
 * @author Gethin James
 */
public class ModulePackageUsingProperties implements ModulePackage
{

    private final Properties properties;

    protected ModulePackageUsingProperties(Properties properties)
    {
        this.properties = properties;
    }

    public static ModulePackageUsingProperties loadFromResource(Resource resource) throws IOException
    {
        Properties props = new Properties();
        props.load(resource.getInputStream());
        cleanupProperties(props);
        return new ModulePackageUsingProperties(props);
    }

    protected static void cleanupProperties(Properties props)
    {
        //We haven't got a Share version min then use the repo version min.
        if (!props.containsKey(PROP_SHARE_VERSION_MIN) && props.containsKey(PROP_REPO_VERSION_MIN))
        {
           props.setProperty(PROP_SHARE_VERSION_MIN, props.getProperty(PROP_REPO_VERSION_MIN));
        }

        //We haven't got a Share version max then use the repo version max.
        if (!props.containsKey(PROP_SHARE_VERSION_MAX) && props.containsKey(PROP_REPO_VERSION_MAX))
        {
            props.setProperty(PROP_SHARE_VERSION_MAX, props.getProperty(PROP_REPO_VERSION_MAX));
        }
    }

    @Override
    public String getId()
    {
        return properties.getProperty(PROP_ID);
    }

    @Override
    public String getTitle()
    {
        return properties.getProperty(PROP_TITLE);
    }

    @Override
    public String getDescription()
    {
        return properties.getProperty(PROP_DESCRIPTION);
    }

    @Override
    public ComparableVersion getVersion()
    {
        String ver = properties.getProperty(PROP_VERSION);
        if (StringUtils.isEmpty(ver))
        {
            return new ComparableVersion(UNSET_VERSION);
        }
        else
        {
            return new ComparableVersion(ver);
        }
    }

    @Override
    public VersionNumber getVersionMin()
    {
        String ver = properties.getProperty(PROP_SHARE_VERSION_MIN);
        if (StringUtils.isEmpty(ver))
        {
            return VersionNumber.VERSION_ZERO;
        }
        else
        {
            return new VersionNumber(ver);
        }
    }

    @Override
    public VersionNumber getVersionMax()
    {
        String ver = properties.getProperty(PROP_SHARE_VERSION_MAX);
        if (StringUtils.isEmpty(ver))
        {
            return VersionNumber.VERSION_BIG;
        }
        else
        {
            return new VersionNumber(ver);
        }
    }

    @Override
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("ModulePackageUsingProperties{");
        sb.append("id='").append(getId()).append('\'');
        sb.append(", title='").append(getTitle()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", version=").append(getVersion());
        sb.append(", versionMin=").append(getVersionMin());
        sb.append(", versionMax=").append(getVersionMax());
        sb.append('}');
        return sb.toString();
    }
}

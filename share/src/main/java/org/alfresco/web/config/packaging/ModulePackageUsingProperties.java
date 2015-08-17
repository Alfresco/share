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
    //Copied from ModuleDetails in the Repo.
    String PROP_ID = "module.id";
    String PROP_VERSION = "module.version";
    String PROP_TITLE = "module.title";
    String PROP_DESCRIPTION = "module.description";
    String PROP_EDITIONS = "module.editions";
    String PROP_REPO_VERSION_MIN = "module.repo.version.min";
    String PROP_REPO_VERSION_MAX = "module.repo.version.max";
    String PROP_DEPENDS_PREFIX = "module.depends.";
    //End of Copied from ModuleDetails in the Repo.

    public static final String UNSET_VERSION = "0-ERROR_UNSET";

    private final Properties properties;

    protected ModulePackageUsingProperties(Properties properties)
    {
        this.properties = properties;
    }

    public static ModulePackageUsingProperties loadFromResource(Resource resource) throws IOException
    {
        Properties props = new Properties();
        props.load(resource.getInputStream());
        return new ModulePackageUsingProperties(props);
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
    public String toString()
    {
        final StringBuilder sb = new StringBuilder("ModulePackageUsingProperties{");
        sb.append("id='").append(getId()).append('\'');
        sb.append(", title='").append(getTitle()).append('\'');
        sb.append(", description='").append(getDescription()).append('\'');
        sb.append(", version=").append(getVersion());
        sb.append('}');
        return sb.toString();
    }
}

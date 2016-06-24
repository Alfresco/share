/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.config.packaging;

import org.alfresco.util.VersionNumber;
import org.apache.maven.artifact.versioning.ArtifactVersion;
import org.apache.maven.artifact.versioning.DefaultArtifactVersion;
import org.apache.maven.artifact.versioning.VersionRange;
import org.springframework.core.io.Resource;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * An implementation of a ModulePackage using a properties file.
 * @author Gethin James
 */
public class ModulePackageUsingProperties implements ModulePackage
{
    //Copied from ModuleDetails in the Repo.

    public static final String PROP_ID = "module.id";
    public static final String PROP_VERSION = "module.version";
    public static final String PROP_TITLE = "module.title";
    public static final String PROP_DESCRIPTION = "module.description";
    public static final String PROP_EDITIONS = "module.editions";
    public static final String PROP_REPO_VERSION_MIN = "module.repo.version.min";
    public static final String PROP_REPO_VERSION_MAX = "module.repo.version.max";
    public static final String PROP_DEPENDS_PREFIX = "module.depends.";
    //End of Copied from ModuleDetails in the Repo.

    public static final String PROP_SHARE_VERSION_MIN = "module.share.version.min";
    public static final String PROP_SHARE_VERSION_MAX = "module.share.version.max";
    private final Properties properties;
    private final List<ModulePackageDependency> dependencies = new ArrayList<>();

    protected ModulePackageUsingProperties(Properties properties)
    {
        validateProperties(properties);
        this.properties = properties;
    }

    public static ModulePackageUsingProperties loadFromResource(Resource resource) throws IOException
    {
        Properties props = new Properties();
        props.load(resource.getInputStream());
        return new ModulePackageUsingProperties(props);
    }

    protected void validateProperties(Properties props)
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
        dependencies.addAll(extractDependencies(props));
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
    public ArtifactVersion getVersion()
    {
        String ver = properties.getProperty(PROP_VERSION);
        if (StringUtils.isEmpty(ver))
        {
            return new DefaultArtifactVersion(UNSET_VERSION);
        }
        else
        {
            return new DefaultArtifactVersion(ver);
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
    public List<ModulePackageDependency> getDependencies()
    {
        return dependencies;
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
        sb.append(", dependencies=").append(dependencies);
        sb.append('}');
        return sb.toString();
    }


    /**
     * This method is copied from ModuleDetailsImpl "as-is".  It is hoped that this code can be REUSED in the
     * future instead of cutting and pasting.
     * @param properties
     * @return
     */
    private static List<ModulePackageDependency> extractDependencies(Properties properties)
    {
        int prefixLength = PROP_DEPENDS_PREFIX.length();

        List<ModulePackageDependency> dependencies = new ArrayList<ModulePackageDependency>(2);
        for (Map.Entry entry : properties.entrySet())
        {
            String key = (String) entry.getKey();
            String value = (String) entry.getValue();
            if (!key.startsWith(PROP_DEPENDS_PREFIX))
            {
                continue;
            }
            if (key.length() == prefixLength)
            {
                // Just ignore it
                continue;
            }
            final String dependencyId = key.substring(prefixLength);
            // Build the dependency
            ModulePackageDependency dependency = new ModulePackageDependencyOnlyId(dependencyId);
            // Add it
            dependencies.add(dependency);
        }
        // Done
        return dependencies;
    }

    /**
     * Basic implementation only uses the ID
     */
    public static class ModulePackageDependencyOnlyId implements ModulePackageDependency {

        String id;

        public ModulePackageDependencyOnlyId(String dependencyId)
        {
            this.id = dependencyId;

        }

        @Override
        public String getId()
        {
            return id;
        }

        @Override
        public VersionRange getVersionRange()
        {
            //Always ignore the version range for now.
            return null;
        }

        @Override
        public String toString()
        {
            final StringBuilder sb = new StringBuilder("");
            sb.append("id='").append(id).append('\'');
            return sb.toString();
        }
    }
}

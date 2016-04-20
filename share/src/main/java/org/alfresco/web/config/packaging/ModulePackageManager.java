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

import org.alfresco.web.scripts.ShareManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.util.Assert;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Examines Module Packages, on bootstrap lists the modules that are installed.
 * @author Gethin James
 */
public class ModulePackageManager implements InitializingBean
{
    public static final String MODULE_RESOURCES = "classpath*:alfresco/module/*/module.properties";
    private static Log logger = LogFactory.getLog(ModulePackageManager.class);

    private ShareManifest shareManifest;
    private List<ModulePackage> modules = new ArrayList<>();

    /**
     * Finds modules based on the resource path.
     * @param resourcePath path to resources
     * @return List<ModulePackage> the module packages
     */
    protected List<ModulePackage> resolveModules(String resourcePath)
    {
        Assert.notNull(resourcePath, "Resource path must not be null");
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<ModulePackage> modulesFound = new ArrayList<>();

        try
        {
            Resource[] resources = resolver.getResources(resourcePath);
            for(Resource resource : resources)
            {
                ModulePackage mp = asModulePackage(resource);
                if (mp != null) modulesFound.add(mp);
            }
        }
        catch (IOException ioe)
        {
            logger.error("Unable to resolve modules ", ioe);
        }
        return modulesFound;
    }

    /**
     * Takes a Resource and turns it into a ModulePackage
     * The current implementation only supports property files.
     * @param resource Spring resource
     * @return ModulePackage
     */
    protected static ModulePackage asModulePackage(Resource resource)
    {
        Assert.notNull(resource, "Resource must not be null");

        try
        {
           return ModulePackageUsingProperties.loadFromResource(resource);
        }
        catch (IOException e)
        {
            logger.error("Failed to load resource "+resource.toString(), e);
            return null;
        }
    }


    /**
     * Writes a list of ModulePackages
     * @param foundModules  the module packages
     * @return String list the modules
     */
    protected String writeModuleList(List<ModulePackage> foundModules)
    {
        StringBuilder b = new StringBuilder(128);
        for (ModulePackage module : foundModules)
        {
            b.append(module.getTitle()).append(", " + module.getVersion()).append(", "+module.getDescription());
            b.append("\n");
        }
        return b.toString();
    }

    /**
     * Returns the available module packages in the application.
     * @return List<ModulePackage> the module packages
     */
    public List<ModulePackage> getModulePackages()
    {
        return modules;
    }

    @Override
    public void afterPropertiesSet()

    {
        logger.debug("Resolving module packages.");
        modules = resolveModules(MODULE_RESOURCES);
        String moduleList = writeModuleList(modules);
        if (!modules.isEmpty())
        {
            logger.info("Found "+ modules.size() +" module package(s)");
            logger.info(moduleList);
            for (ModulePackage module : modules)
            {
                ModulePackageHelper.checkValid(module, shareManifest);
                ModulePackageHelper.checkDependencies(module, modules);
            }
        }
    }

    public void setShareManifest(ShareManifest shareManifest)
    {
        this.shareManifest = shareManifest;
    }
}

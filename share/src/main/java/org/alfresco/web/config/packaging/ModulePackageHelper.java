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

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.VersionNumber;
import org.alfresco.web.scripts.ShareManifest;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Helpful methods for working with ModulePackages.
 * @author Gethin James
 */
public class ModulePackageHelper
{
    private static Log logger = LogFactory.getLog(ModulePackageHelper.class);
    private static PropertyDescriptor[] descriptors;

    //see http://docs.oracle.com/javase/6/docs/technotes/guides/jar/jar.html#Main%20Attributes
    protected static final String REGEX_NUMBER_OR_DOT = "[0-9\\.]*";
    public static final String MANIFEST_SHARE = "Alfresco Share";

    static
    {
        try
        {
            BeanInfo moduleinfo = Introspector.getBeanInfo(ModulePackage.class);
            descriptors = moduleinfo.getPropertyDescriptors();
        }
        catch (IntrospectionException e)
        {
            logger.error("Unable to read bean info for ModulePackage");
        }
    }

    /**
     * Rhino Serialization didn't seem to like ModulePackageUsingProperties. It failed
     * to read the individual properties so this method converts a ModulePackage into a simple
     * Map so its easier to represent as Javascript.
     * @param modulePackage
     * @return Map<String, String> property, value
     */
    public static Map<String, String> toMap(ModulePackage modulePackage)
    {
        Map asMap = new HashMap(descriptors.length);
        for (int i = 0; i < descriptors.length; i++)
        {
            try
            {
                String propValue = String.valueOf(descriptors[i].getReadMethod().invoke(modulePackage));
                asMap.put(descriptors[i].getName(),propValue);
            }
            catch (IllegalAccessException iae)
            {
                logger.error("Unable to turn ModulePackageUsingProperties into a Map ", iae);
            }
            catch (InvocationTargetException e)
            {
                logger.error("Unable to turn ModulePackageUsingProperties into a Map ", e);
            }
        }
        return asMap;
    }

    /**
     * Checks the Module Packages is valid.
     */
    public static void checkValid(ModulePackage module, ShareManifest shareManifest)
    {
        checkVersions(new VersionNumber(shareManifest.getSpecificationVersion()), module);
    }

    protected static List<String> toIds(List<ModulePackage> mods)
    {
        //In Java 8 this is: mods.stream().map(module -> module.getId()).collect(toList());
        //In Groovy mods.collect { it.id }
        //In Java 7
        List<String> ids = new ArrayList<>(mods.size());
        for (ModulePackage mod : mods)
        {
            ids.add(mod.getId());
        }
        return ids;
    }

    /**
     * Compares the version information with the module details to see if their valid.  If they are invalid then it throws an exception.
     * @param warVersion VersionNumber
     * @param installingModuleDetails ModuleDetails
     * @throws AlfrescoRuntimeException
     */
    protected static void checkVersions(VersionNumber warVersion, ModulePackage installingModuleDetails)
    {
        if(warVersion.compareTo(installingModuleDetails.getVersionMin())==-1) {
            throw new AlfrescoRuntimeException("The module ("+installingModuleDetails.getTitle()+") must be installed on a Share version equal to or greater than "
                    +installingModuleDetails.getVersionMin()+". Share is version: "+warVersion+".");
        }
        if(warVersion.compareTo(installingModuleDetails.getVersionMax())==1) {
            throw new AlfrescoRuntimeException("The module ("+installingModuleDetails.getTitle()+") cannot be installed on a Share version greater than "
                    +installingModuleDetails.getVersionMax()+". Share is version: "+warVersion+".");
        }
    }

    /**
     * A BASIC dependency check this is only based on ID and ignores the version.
     * @param module
     * @param availableModules
     */
    protected static void checkDependencies(ModulePackage module, List<ModulePackage> availableModules)
    {
        List<ModulePackageDependency> dependencies = module.getDependencies();
        if (dependencies != null && !dependencies.isEmpty())
        {
            List<String> moduleIds = toIds(availableModules);
            List<ModulePackageDependency> missingDependencies = new ArrayList<>(0);

            for (ModulePackageDependency dependency : dependencies)
            {
                if(!moduleIds.contains(dependency.getId()))
                {
                    missingDependencies.add(dependency);
                }
            }

            //We have some missing dependencies
            if (!missingDependencies.isEmpty())
            {
                throw new AlfrescoRuntimeException("The module ("+module.getTitle()+") cannot be installed. The following modules must first be installed: " + missingDependencies);
            }
        }

    }
}

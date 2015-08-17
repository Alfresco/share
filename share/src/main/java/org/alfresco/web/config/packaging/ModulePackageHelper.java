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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

/**
 * Helpful methods for working with ModulePackages.
 * @author Gethin James
 */
public class ModulePackageHelper
{
    private static Log logger = LogFactory.getLog(ModulePackageHelper.class);
    private static PropertyDescriptor[] descriptors;

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
}

/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.alfresco.error.AlfrescoRuntimeException;
import org.apache.commons.lang.StringUtils;
import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Processor extension that provides access to the specified manifest file.
 * 
 * @author Matt Ward
 */
public class ShareManifest extends BaseProcessorExtension
{
    public static final String MANIFEST_SPECIFICATION_VERSION = "Specification-Version";
    public static final String MANIFEST_IMPLEMENTATION_VERSION = "Implementation-Version";
    public static final String MANIFEST_SPECIFICATION_TITLE   = "Specification-Title";
    public static final String MANIFEST_IMPLEMENTATION_TITLE  = "Implementation-Title";

    private final Resource resource; 
    private Manifest manifest;
    
    public ShareManifest(Resource resource)
    {
        if (resource == null)
        {
            throw new IllegalArgumentException("Manifest 'resource' parameter must not be null.");
        }
        this.resource = resource;
    }

    /**
     * Initialise the processor extension.
     */
    @Override
    public void register()
    {
        super.register();
        readManifest();
    }

    /**
     * Read the manifest file that was specified in the constructor.
     */
    public void readManifest()
    {
        try (InputStream is = resource.getInputStream())
        {
            manifest = new Manifest(is);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error reading manifest.", e);
        }
    }
    
    /**
     * Retrieve attribute value from the main section of a manifest.
     * 
     * @param key    The name of the attribute to fetch.
     * @return       The attribute value.
     */
    public String mainAttributeValue(String key)
    {
        String value = null;
        
        Attributes attributes = manifest.getMainAttributes();
        if (attributes != null)
        {
            value = attributes.getValue(key);
        }
        
        return value;
    }
    
    /**
     * Retrieve all key/value pairs for the main section of the manifest.
     * 
     * @return          Map of attribute name to value.
     */
    public Map<String, String> mainAttributesMap()
    {
        List<String> names = mainAttributeNames();
        Map<String, String> map = new HashMap<String, String>(names.size());
        for (String name : names)
        {
            String value = mainAttributeValue(name);
            map.put(name, value);
        }
        return map;
    }
    
    /**
     * Retrieve a list of attribute names (Strings) for the main
     * section of a manifest.
     * 
     * @return       The list of attribute names.
     */
    public List<String> mainAttributeNames()
    {
        List<String> names = Collections.emptyList();

        Attributes attributes = manifest.getMainAttributes();
        if (attributes != null)
        {
            names = namesToStrings(attributes.keySet());
        }
        
        return names;
    }
    
    /**
     * Retrieve an attribute value by name from the specific named section of a manifest.
     * 
     * @param section   Section name.
     * @param key       Attribute name.
     * @return          The attribute value.
     */
    public String attributeValue(String section, String key)
    {
        String value = null;
        
        Attributes attributes = manifest.getAttributes(section);
        if (attributes != null)
        {
            value = attributes.getValue(key);
        }
        
        return value;
    }
    
    /**
     * Retrieve all key/value pairs for a particular section in the manifest.
     * 
     * @param section   Section name.
     * @return          Map of attribute name to value.
     */
    public Map<String, String> attributesMap(String section)
    {
        List<String> names = attributeNames(section);
        Map<String, String> map = new HashMap<String, String>(names.size());
        for (String name : names)
        {
            String value = attributeValue(section, name);
            map.put(name, value);
        }
        return map;
    }
    
    /**
     * Retrieve a list of attribute names (Strings) for the named section of a manifest.
     * 
     * @param section    Section name.
     * @return           The list of attribute names.
     */
    public List<String> attributeNames(String section)
    {
        List<String> names = Collections.emptyList();

        Attributes attributes = manifest.getAttributes(section);
        if (attributes != null)
        {
            names = namesToStrings(attributes.keySet());
        }
        
        return names;
    }
    
    /**
     * Retrieve the set of named sections in the manifest.
     * 
     * @return The set of section names.
     */
    public Set<String> sectionNames()
    {
        return manifest.getEntries().keySet();
    }
    
    protected List<String> namesToStrings(Set<Object> names)
    {
        List<String> strings = new ArrayList<String>(names.size());
        for (Object name : names)
        {
            if (!String.class.isAssignableFrom(name.getClass()) &&
                !Attributes.Name.class.isAssignableFrom(name.getClass()))
            {
                throw new IllegalArgumentException("name parameter must be an Attributes.Name or String, but is "
                            + name.getClass().getCanonicalName());
            }
            strings.add(name.toString());
        }
        return strings;
    }

    /**
     * Returns the version of the war that has been specified
     * In general, prefer Specification Version over Implementation Version
     * @return String a version number
     */
    public String getSpecificationVersion()
    {
        return getVersion(MANIFEST_SPECIFICATION_VERSION);
    }

    /**
     * Returns the version of the war that has been implemented
     * May be a SNAPSHOT version.
     * @return String a version number
     */
    public String getImplementationVersion()
    {
        return getVersion(MANIFEST_IMPLEMENTATION_VERSION);
    }

    private String getVersion(String key)
    {
        String version = manifest.getMainAttributes().getValue(key);
        if (StringUtils.isEmpty(version))
        {
            throw new AlfrescoRuntimeException("Invalid MANIFEST.MF: Share "+key
                    +" is missing, are you using the valid MANIFEST.MF supplied with the Share.war?");

        }
        return version;
    }
}

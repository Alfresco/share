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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Processor extension that provides access to the specified manifest file.
 * 
 * @author Matt Ward
 */
public class ShareManifest extends BaseProcessorExtension
{
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
    protected void readManifest()
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
        return manifest.getMainAttributes().getValue(key);
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
        return namesToStrings(manifest.getMainAttributes().keySet());
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
        return manifest.getAttributes(section).getValue(key);
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
        return namesToStrings(manifest.getAttributes(section).keySet());
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
}

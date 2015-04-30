/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.wcm.client.impl;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.wcm.client.DictionaryService;
import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.apache.chemistry.opencmis.client.api.ObjectType;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.client.api.Tree;

/**
 * Dictionary service implementation
 * 
 * @author Roy Wetherall
 */
public class DictionaryServiceImpl implements DictionaryService
{
    /** CMIS type delimiter */
    private static final String TYPE_DELIMITER = ":";

    /** Root CMIS types */
    private static final String[] ROOT_TYPES = new String[] { TYPE_CMIS_DOCUMENT, TYPE_CMIS_FOLDER };
    private static final String[] ROOT_TYPE_PREFIXES = new String[] { TYPE_PREFIX_DOCUMENT, TYPE_CMIS_FOLDER };
    // TODO support other CMIS root types cmis:reltionship and cmis:policy

    /** Indicates whether the dictionary has been initialised or not */
    private boolean initialised = false;

    /** Map containing the parent maps for each root type */
    private Map<String, Map<String, String>> typeMaps = new TreeMap<String, Map<String, String>>();

    /** Map from root type to prefix */
    private Map<String, String> typePrefixMap;

    /**
     * Init method.
     */
    public void init()
    {
        if (initialised == false)
        {
            Session session = CmisSessionHelper.getSession();

            typePrefixMap = new TreeMap<String, String>();
            int index = 0;

            for (String rootType : ROOT_TYPES)
            {
                // Get the type hierarchy
                List<Tree<ObjectType>> typeHierarchy = session.getTypeDescendants(rootType, -1, false);

                // Create parent/child map
                Map<String, String> typeMap = new TreeMap<String, String>();
                addToMap(typeMap, typeHierarchy, rootType);

                // Add to type map
                typeMaps.put(rootType, typeMap);

                // Add to type prefix map
                typePrefixMap.put(rootType, ROOT_TYPE_PREFIXES[index]);
                index++;
            }

            initialised = true;
        }
    }

    /**
     * Add to map
     * 
     * @param map
     * @param types
     * @param parentType
     */
    private void addToMap(Map<String, String> map, List<Tree<ObjectType>> types, String parentType)
    {
        if (types != null)
        {
            for (Tree<ObjectType> type : types)
            {
                String typeName = type.getItem().getQueryName();
                map.put(typeName, parentType);
                addToMap(map, type.getChildren(), typeName);
            }
        }
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#getParentType(java.lang.String)
     */
    @Override
    public String getParentType(String type)
    {
        return getParentType(type, false);
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#getParentType(java.lang.String,
     *      boolean)
     */
    @Override
    public String getParentType(String type, boolean queryName)
    {
        String parentType = null;

        // Initialise
        init();

        // Remove the type prefix
        type = removeTypePrefix(type);

        type = translateToCmisRootType(type);

        // If the type is a root type then there is no parent
        if (typeMaps.containsKey(type) == false)
        {
            for (Map.Entry<String, Map<String, String>> entry : typeMaps.entrySet())
            {
                // Get the root type
                String rootType = entry.getKey();

                // See if there is an entry for the type
                parentType = entry.getValue().get(type);
                if (parentType != null)
                {
                    // Append the type prefix if required
                    if (queryName == false)
                    {
                        parentType = typePrefixMap.get(rootType) + TYPE_DELIMITER + parentType;
                    }
                    break;
                }
            }
        }

        return parentType;
    }

    private String translateToCmisRootType(String type)
    {
        // Support CMIS and non-CMIS interchangeably
        if ("cm:content".equals(type))
        {
            type = TYPE_CMIS_DOCUMENT;
        }
        else if ("cm:folder".equals(type))
        {
            type = TYPE_CMIS_FOLDER;
        }
        return type;
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#isRootType(java.lang.String)
     */
    @Override
    public boolean isRootType(String type)
    {
        // Initialise
        init();

        type = removeTypePrefix(type);
        type = translateToCmisRootType(type);
        return typeMaps.containsKey(type);
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#isContentType(java.lang.String)
     */
    @Override
    public boolean isDocumentSubType(String type)
    {
        // Initialise
        init();

        type = removeTypePrefix(type);
        type = translateToCmisRootType(type);
        return (TYPE_CMIS_DOCUMENT.equals(type) || typeMaps.get(TYPE_CMIS_DOCUMENT).containsKey(type));
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#isFolderSubType(java.lang.String)
     */
    @Override
    public boolean isFolderSubType(String type)
    {
        // Initialise
        init();

        type = removeTypePrefix(type);
        type = translateToCmisRootType(type);
        return (TYPE_CMIS_FOLDER.equals(type) || typeMaps.get(TYPE_CMIS_FOLDER).containsKey(type));
    }

    /**
     * @see org.alfresco.wcm.client.DictionaryService#removeTypePrefix(java.lang.String)
     */
    public String removeTypePrefix(String type)
    {
        String result = type;
        String[] values = type.split(TYPE_DELIMITER);
        if (values.length == 3)
        {
            result = values[1] + TYPE_DELIMITER + values[2];
        }
        return result;
    }
}

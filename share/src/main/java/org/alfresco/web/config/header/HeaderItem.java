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
package org.alfresco.web.config.header;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigException;

/**
 * This class element represents a header item for the client header.
 * 
 * @author Mike Hatfield.
 */
public class HeaderItem
{
    private static final long serialVersionUID = -8543180919661884269L;

    private static final String ATTR_ID = "id";
    private static final String ATTR_TYPE = "type";
    private static final String ATTR_ICON = "icon";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_PERMISSION = "permission";
    private static final String ATTR_CONDITION = "condition";

    private static Log logger = LogFactory.getLog(HeaderItem.class);

    private String generatedId;
    private String text;
    private final Map<String, String> attributes;
    private Map<String, HeaderItemsConfigElement> containers = new LinkedHashMap<String, HeaderItemsConfigElement>();
    
    public HeaderItem(String id, Map<String, String> attributes)
    {
        this(id, attributes, null);
    }

    public HeaderItem(String generatedId, Map<String, String> attributes, String text)
    {
        if (generatedId == null)
        {
            String msg = "Illegal null field id";
            if (logger.isWarnEnabled())
            {
                logger.warn(msg);
            }
            throw new ConfigException(msg);
        }
        this.generatedId = generatedId;
        if (attributes == null)
        {
            attributes = Collections.emptyMap();
        }
        this.attributes = attributes;
        this.text = text;
    }

    // The generated id contains the id's of all ancestors separated by "_"
    public String getGeneratedId()
    {
        return this.generatedId;
    }

    // The following are convenience accessor methods for certain known attributes.
    public String getId()
    {
        return this.attributes.get(ATTR_ID);
    }

    public String getType()
    {
        return this.attributes.get(ATTR_TYPE);
    }

    public String getIcon()
    {
        String icon = this.attributes.get(ATTR_ICON);
        if (icon == null)
        {
            icon = this.getId().concat(".png");
            this.attributes.put(ATTR_ICON, icon);
        }
        return icon;
    }

    public String getLabel()
    {
        String label = this.attributes.get(ATTR_LABEL);
        if (label == null)
        {
            label = "header.".concat(this.getId()).concat(".label");
            this.attributes.put(ATTR_LABEL, label);
        }
        return label;
    }
    
    public String getDescription()
    {
        String description = this.attributes.get(ATTR_DESCRIPTION);
        if (description == null)
        {
            description = "header.".concat(this.getId()).concat(".description");
            this.attributes.put(ATTR_DESCRIPTION, description);
        }
        return description;
    }

    public String getPermission()
    {
        String permission = this.attributes.get(ATTR_PERMISSION);
        if (permission == null)
        {
            permission = "";
            this.attributes.put(ATTR_PERMISSION, permission);
        }
        return permission;
    }

    public String getCondition()
    {
        String condition = this.attributes.get(ATTR_CONDITION);
        if (condition == null)
        {
            condition = "";
            this.attributes.put(ATTR_CONDITION, condition);
        }
        return condition;
    }
    
    public String getValue()
    {
        return this.text == null ? "" : this.text;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("HeaderItem: ").append(this.generatedId);
        if (this.text != null)
        {
            result.append(" value:").append(this.text);
        }
        return result.toString();
    }

    public HeaderItemsConfigElement[] getContainers()
    {
        return this.getContainersAsList().toArray(new HeaderItemsConfigElement[0]);
    }
    
    /**
     * This method returns an unmodifiable List of <code>HeaderItemsConfigElement</code>
     * objects that are associated with this HeaderItem.
     * @return an unmodifiable List of HeaderItemsConfigElement references.
     */
    public List<HeaderItemsConfigElement> getContainersAsList()
    {
        List<HeaderItemsConfigElement> result = new ArrayList<HeaderItemsConfigElement>(containers.size());
        for (Map.Entry<String, HeaderItemsConfigElement> entry : containers.entrySet())
        {
            result.add(entry.getValue());
        }
        return Collections.unmodifiableList(result);
    }

    void addContainedItem(String containerId, HeaderItemsConfigElement container)
    {
        this.containers.put(containerId, container);
    }
}
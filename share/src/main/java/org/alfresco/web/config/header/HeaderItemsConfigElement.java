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

import org.alfresco.web.config.header.HeaderItem;
import org.alfresco.web.config.header.HeaderItemsConfigElement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents &lt;items&gt; values for the
 * client.
 * 
 * @author Mike Hatfield.
 */
public class HeaderItemsConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 7464040585168773676L;
    private static Log logger = LogFactory.getLog(HeaderItemsConfigElement.class);
    
    private String id = "";
    private String label = "";
    private String permission = "";
    private String condition = "";

    public static final String DEFAULT_ELEMENT_ID = "app-items";
    private Map<String, HeaderItem> items = new LinkedHashMap<String, HeaderItem>();

    /**
     * This constructor creates an instance with the default name.
     */
    public HeaderItemsConfigElement()
    {
        super(DEFAULT_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public HeaderItemsConfigElement(String name)
    {
        super(name);
    }
    
    public HeaderItem[] getItems()
    {
        return this.getItemsAsList().toArray(new HeaderItem[0]);
    }
    
    /**
     * This method returns an unmodifiable List of <code>HeaderItem</code>
     * objects that are associated with this container.
     * @return an unmodifiable List of HeaderItem references.
     */
    public List<HeaderItem> getItemsAsList()
    {
        List<HeaderItem> result = new ArrayList<HeaderItem>(items.size());
        for (Map.Entry<String, HeaderItem> entry : items.entrySet())
        {
            result.add(entry.getValue());
        }
        return Collections.unmodifiableList(result);
    }

    /**
     * This method returns the item having the specified id string from within a
     * &lt;header&gt; tag. Items within containers and container-groups are prefixed
     * with each parent's id to create a unique id for that item.
     * 
     * @return the ItemConfigElement instance having the correct id, if one exists,
     * else null.
     */
    public HeaderItem getItem(String id)
    {
        return this.items.get(id);
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the default-controls config via the generic interfaces is not supported");
    }

    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        if (configElement == null)
        {
            return this;
        }

        return configElement;
    }

    public void setId(String id)
    {
        this.id = id;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public void setPermission(String permission)
    {
        this.permission = permission;
    }

    public void setCondition(String condition)
    {
        this.condition = condition;
    }

    public String getId()
    {
        return this.id == null ? "" : this.id;
    }

    public String getLabel()
    {
        String label = this.label;
        if (label == null)
        {
            label = "header.".concat(this.getId()).concat(".label");
            this.label = label;
        }
        return label;
    }
    
    public String getPermission()
    {
        return this.permission == null ? "" : this.permission;
    }

    public String getCondition()
    {
        return this.condition == null ? "" : this.condition;
    }

    HeaderItem addItem(String id, List<String> attributeNames, List<String> attributeValues)
    {
        return this.addItem(id, attributeNames, attributeValues, null);
    }

    HeaderItem addItem(String id, List<String> attributeNames, List<String> attributeValues, String itemText)
    {
        if (attributeNames == null)
        {
            attributeNames = Collections.emptyList();
        }
        if (attributeValues == null)
        {
            attributeValues = Collections.emptyList();
        }
        if (attributeNames.size() < attributeValues.size() && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("item ")
            .append(id)
            .append(" has ")
            .append(attributeNames.size())
            .append(" xml attribute names and ")
            .append(attributeValues.size())
            .append(" xml attribute values. The trailing extra data will be ignored.");
            logger.warn(msg.toString());
        }
        
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        for (int i = 0; i < attributeNames.size(); i++)
        {
            attrs.put(attributeNames.get(i), attributeValues.get(i));
        }
        HeaderItem hi = new HeaderItem(id, attrs, itemText);
        items.put(id, hi);
        return hi;
    }
}

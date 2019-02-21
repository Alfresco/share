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
package org.alfresco.web.config.header;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;app-items&gt; and &lt;user-items&gt; elements.
 * 
 * @author Mike Hatfield.
 */
class HeaderItemsElementReader implements ConfigElementReader
{
    public static final String ATTR_ID = "id";
    public static final String ATTR_LABEL = "label";
    public static final String ATTR_PERMISSION = "permission";
    public static final String ATTR_CONDITION = "condition";
    public static final String ELEMENT_APP_ITEMS = "app-items";
    public static final String ELEMENT_USER_ITEMS = "user-items";
    public static final String ELEMENT_CONTAINER_GROUP = "container-group";
    public static final String ELEMENT_LEGACY = "legacy-mode-enabled";
    public static final String ELEMENT_MAX_RECENT_SITES = "max-recent-sites";
    public static final String ELEMENT_MAX_DISPLAYED_SITE_PAGES = "max-displayed-site-pages";
    
    public static final String ID_SEPARATOR = "_";
    
    private String id_prefix = "";
    private String group_condition = null;
    private String group_permission = null;

    /**
     * This constructor creates an instance with no id prefix.
     */
    public HeaderItemsElementReader()
    {
    }

    /**
     * This constructor creates an instance with the specified id prefix.
     * 
     * @param id of the ancestor elements.
     */
    public HeaderItemsElementReader(String id)
    {
        this.id_prefix = (id == null || id.length() == 0) ? "" : id.concat(ID_SEPARATOR);
    }

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element headerItemsElement)
    {
        HeaderItemsConfigElement result = null;
        if (headerItemsElement == null)
        {
            return null;
        }

        String name = headerItemsElement.getName();
        if (!name.equals(ELEMENT_APP_ITEMS) && 
            !name.equals(ELEMENT_USER_ITEMS) && 
            !name.equals(ELEMENT_CONTAINER_GROUP) &&
            !name.equals(ELEMENT_LEGACY))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_APP_ITEMS + ", " + ELEMENT_USER_ITEMS + ", " + ELEMENT_CONTAINER_GROUP + ", " + ELEMENT_LEGACY
                    + " elements, the element passed was '" + name + "'");
        }

        result = new HeaderItemsConfigElement(name);
        
        parseId(headerItemsElement, result);
        parseLabel(headerItemsElement, result);
        parseCondition(headerItemsElement, result);
        parsePermission(headerItemsElement, result);

        parseItemTags(headerItemsElement, result);

        return result;
    }

    @SuppressWarnings("unchecked")
    private void parseItemTags(Element itemsElement, HeaderItemsConfigElement result)
    {
        HeaderItem lastItem;
        
        // xpath expressions.
        for (Object itemObj : itemsElement.selectNodes("./item"))
        {
            Element itemElem = (Element)itemObj;
            String itemText = itemElem.getTextTrim();
            //List<Attribute> itemAttributes = itemElem.selectNodes("./@*");
            List<Attribute> itemAttributes = new ArrayList<Attribute>();

            for (Object obj : itemElem.selectNodes("./@*")) {
                itemAttributes.add((Attribute) obj);
            }
            
            List<String> itemAttributeNames = new ArrayList<String>();
            List<String> itemAttributeValues = new ArrayList<String>();
            
            // Special handling for the mandatory "id" and optional condition & permission attributes
            String itemGeneratedId = null;
            String itemGroupCondition = this.group_condition;
            String itemGroupPermission = this.group_permission;
            for (Attribute nextAttr : itemAttributes)
            {
                String nextAttributeName = nextAttr.getName();
                String nextAttributeValue = nextAttr.getValue();
                
                // If the item specifies a condition or permission, it's overriding an optional group default
                if (nextAttributeName.equals(ATTR_CONDITION))
                {
                    itemGroupCondition = null;
                }
                else if (nextAttributeName.equals(ATTR_PERMISSION))
                {
                    itemGroupPermission = null;
                }
                else if (nextAttributeName.equals(ATTR_ID))
                {
                    itemGeneratedId = this.generateUniqueItemId(nextAttributeValue);
                }
                itemAttributeNames.add(nextAttributeName);
                itemAttributeValues.add(nextAttributeValue);
            }
            if (itemGeneratedId == null)
            {
                throw new ConfigException("<item> node missing mandatory id attribute.");
            }
            // If the group condition was set and not overridden, add it to the item here
            if (itemGroupCondition != null)
            {
                itemAttributeNames.add(ATTR_CONDITION);
                itemAttributeValues.add(itemGroupCondition);
            }
            // If the group permission was set and not overridden, add it to the item here
            if (itemGroupPermission != null)
            {
                itemAttributeNames.add(ATTR_PERMISSION);
                itemAttributeValues.add(itemGroupPermission);
            }
            
            lastItem = result.addItem(itemGeneratedId, itemAttributeNames, itemAttributeValues, itemText);

            // Go through ant of the <container-group> tags under <item>
            for (Object obj : itemElem.selectNodes("./container-group"))
            {
                Element containerElement = (Element)obj;
                
                HeaderItemsElementReader containerReader = new HeaderItemsElementReader(lastItem.getId());
                HeaderItemsConfigElement containerCE = (HeaderItemsConfigElement)containerReader.parse(containerElement);

                lastItem.addContainedItem(containerCE.getId(), containerCE);
            }
        }
    }

    private void parseId(Element itemsElement, HeaderItemsConfigElement result)
    {
        String id = itemsElement.attributeValue(ATTR_ID);
        
        // Cannot have lower-level items with a null id
        if (id == null && this.id_prefix.length() > 0)
        {
            throw new ConfigException(itemsElement.getName() + " node missing mandatory id attribute.");
        }
        result.setId(id);
        
        StringBuilder sb = new StringBuilder(this.id_prefix);
        if (id != null)
        {
            sb.append(id).append(ID_SEPARATOR);
        }
        this.id_prefix = sb.toString();
    }

    private void parseLabel(Element itemsElement, HeaderItemsConfigElement result)
    {
        String label = itemsElement.attributeValue(ATTR_LABEL);
        result.setLabel(label);
    }

    private void parseCondition(Element itemsElement, HeaderItemsConfigElement result)
    {
        String condition = itemsElement.attributeValue(ATTR_CONDITION);
        this.group_condition = condition;
        result.setCondition(condition);
    }

    private void parsePermission(Element itemsElement, HeaderItemsConfigElement result)
    {
        String permission = itemsElement.attributeValue(ATTR_PERMISSION);
        this.group_permission = permission;
        result.setPermission(permission);
    }

    private String generateUniqueItemId(String id)
    {
        if (id == null)
        {
            return null;
        }
        return this.id_prefix.concat(id);
    }
}

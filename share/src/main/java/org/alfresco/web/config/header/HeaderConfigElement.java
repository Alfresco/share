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

import java.util.List;

import org.alfresco.web.config.forms.DependenciesConfigElement;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents 'header' values for the client.
 * 
 * @author Mike Hatfield.
 */
public class HeaderConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 7721694406825674057L;

    public static final String HEADER_ID = "header";

    private boolean legacyMode = false;
    private int maxRecentSites;
    private int maxDisplayedSitePages;
    private HeaderItemsConfigElement appItemsConfigElement;
    private HeaderItemsConfigElement userItemsConfigElement;
    private DependenciesConfigElement dependenciesConfigElement;
    
    public HeaderConfigElement()
    {
        super(HEADER_ID);
    }

    public HeaderConfigElement(String name)
    {
        super(name);
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
            "Reading the header config via the generic interfaces is not supported");
    }

    public boolean getLegacyMode()
    {
        return this.legacyMode;
    }
    
    void setLegacyMode(boolean enabled)
    {
        this.legacyMode = enabled;
    }
    
    public int getMaxRecentSites()
    {
        return this.maxRecentSites;
    }
    
    void setMaxRecentSites(int n)
    {
        this.maxRecentSites = n;
    }
    
    public int getMaxDisplayedSitePages()
    {
        return this.maxDisplayedSitePages;
    }
    
    void setMaxDisplayedSitePages(int n)
    {
        this.maxDisplayedSitePages = n;
    }
    
    public HeaderItemsConfigElement getAppItems()
    {
        return this.appItemsConfigElement;
    }
    
    void setAppItems(HeaderItemsConfigElement items)
    {
        this.appItemsConfigElement = items;
    }

    public HeaderItemsConfigElement getUserItems()
    {
        return this.userItemsConfigElement;
    }

    void setUserItems(HeaderItemsConfigElement items)
    {
        this.userItemsConfigElement = items;
    }

    public DependenciesConfigElement getDependencies()
    {
        return this.dependenciesConfigElement;
    }

    void setDependencies(DependenciesConfigElement dependencies)
    {
        this.dependenciesConfigElement = dependencies;
    }
        
    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement otherConfigElement)
    {
        HeaderConfigElement otherHeaderElem = (HeaderConfigElement)otherConfigElement;
        HeaderConfigElement result = new HeaderConfigElement();

        /*
        for (String thisFormId : this.formElementsById.keySet())
        {
            if (otherFormsElem.formElementsById.containsKey(thisFormId))
            {
                FormConfigElement otherFormCE = otherFormsElem.getForm(thisFormId);
                FormConfigElement combinedElement = (FormConfigElement)formElementsById.get(thisFormId).combine(otherFormCE);
                result.addFormById(combinedElement, thisFormId);
            }
            else
            {
                result.addFormById(this.formElementsById.get(thisFormId), thisFormId);
            }
        }
        for (String otherFormId : otherFormsElem.formElementsById.keySet())
        {
            if (this.formElementsById.containsKey(otherFormId))
            {
                // Ignore it. The combination was handled in the previous loop.
            }
            else
            {
                result.addFormById(otherFormsElem.formElementsById.get(otherFormId), otherFormId);
            }
        }
        */
        
        // Combine dependencies
        ConfigElement combinedDependencies = this.dependenciesConfigElement == null ?
                otherHeaderElem.getDependencies()
                : this.dependenciesConfigElement.combine(otherHeaderElem.getDependencies());
        result.setDependencies((DependenciesConfigElement)combinedDependencies);
        
        return result;
    }
}
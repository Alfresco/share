/*
 * #%L
 * share-po
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
package org.alfresco.po.share.cmm.admin;

import org.alfresco.po.share.admin.ActionsSet;
import org.openqa.selenium.WebElement;

/**
 * Represents a row in the CMM types table.
 * 
 * @author Richard Smith
 */
public class ModelTypeRow
{

    private WebElement nameElement;

    private String name;

    private String displayLabel;

    private String parent;

    private String layout;

    private ActionsSet actions;

    /**
     * Instantiates a new custom model type row.
     */
    public ModelTypeRow()
    {
    }

    /**
     * Instantiates a new custom model type row.
     * 
     * @param name the name
     * @param displayLabel the display label
     * @param parent the parent
     * @param layout Yes if form layout is created
     * @param actions the actions
     */
    public ModelTypeRow(WebElement nameElement, String displayLabel, String parent, String layout, ActionsSet actions)
    {
        super();
        this.nameElement = nameElement;
        this.name = this.nameElement.getText().trim();
        this.displayLabel = displayLabel;
        this.parent = parent;
        this.layout = layout;
        this.actions = actions;
    }

    /**
     * Gets the name element.
     * 
     * @return the nameElement
     */
    public WebElement getNameElement()
    {
        return nameElement;
    }

    /**
     * Gets the name.
     * 
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Sets the name.
     * 
     * @param name the new name
     */
    public void setNameElement(WebElement nameElement)
    {
        this.nameElement = nameElement;
        this.name = this.nameElement.getText().trim();
    }

    /**
     * Gets the display label.
     * 
     * @return the display label
     */
    public String getDisplayLabel()
    {
        return displayLabel;
    }

    /**
     * Sets the display label.
     * 
     * @param displayLabel the new display label
     */
    public void setDisplayLabel(String displayLabel)
    {
        this.displayLabel = displayLabel;
    }

    /**
     * Gets the parent.
     * 
     * @return the parent
     */
    public String getParent()
    {
        return parent;
    }

    /**
     * Sets the parent.
     * 
     * @param parent the new parent
     */
    public void setParent(String parent)
    {
        this.parent = parent;
    }

    /**
     * Gets the Layout value.
     * 
     * @return the Layout
     */
    public String getLayout()
    {
        return layout;
    }

    /**
     * Sets the Layout yes or no.
     * 
     * @param layout
     */
    public void setLayout(String layout)
    {
        this.layout = layout;
    }

    /**
     * Gets the actions.
     * 
     * @return the actions
     */
    public ActionsSet getActions()
    {
        return actions;
    }

    /**
     * Sets the actions.
     * 
     * @param actions the new actions
     */
    public void setActions(ActionsSet actions)
    {
        this.actions = actions;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ModelTypeRow other = (ModelTypeRow) obj;
        if (name == null)
        {
            if (other.name != null)
                return false;
        }
        else if (!name.equals(other.name))
            return false;
        return true;
    }

    /*
     * (non-Javadoc) Implemented to fix sonar blocker
     */
    @Override
    public int hashCode()
    {
        return super.hashCode();
    }
}
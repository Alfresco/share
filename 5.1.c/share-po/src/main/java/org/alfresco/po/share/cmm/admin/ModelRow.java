/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

package org.alfresco.po.share.cmm.admin;

import org.alfresco.po.share.admin.ActionsSet;
import org.openqa.selenium.WebElement;

/**
 * Represents a table row in the Custom Models List View.
 * 
 * @author Charu
 * @author mbhave
 */
public class ModelRow
{
    private WebElement cmNameElement;

    private String cmName;

    private String cmNamespace;

    private String cmStatus;

    private ActionsSet cmActions;

    /**
     * Instantiates a new custom model row.
     */
    public ModelRow()
    {
    }

    /**
     * Instantiates a new custom model row.
     * 
     * @param cmName the Custom Model name
     */
    public ModelRow(String cmName)
    {
        this.cmName = cmName;
    }

    /**
     * Instantiates a new manage site row.
     * 
     * @param cmNameField the cm name field
     * @param cmName the cm name
     * @param cmNamespace the cm namespace
     * @param cmStatus the cm status
     * @param cmActions the cm actions
     */
    public ModelRow(WebElement cmNameField, String cmName, String cmNamespace, String cmStatus, ActionsSet cmActions)
    {
        super();
        this.cmNameElement = cmNameField;
        this.cmName = cmName;
        this.cmNamespace = cmNamespace;
        this.cmStatus = cmStatus;
        this.cmActions = cmActions;
    }

    /**
     * Sets the cm name.
     * 
     * @param cmNameElem the new cm name
     */
    public void setCmName(WebElement cmNameElem)
    {
        this.cmNameElement = cmNameElem;
        this.cmName = this.cmNameElement.getText().trim();
    }

    /**
     * Gets the custom model name.
     * 
     * @return the custom model name
     */
    public String getCMName()
    {
        return cmName;
    }

    /**
     * Sets the CM name.
     * 
     * @param cmName the new CM name
     */
    public void setCMName(String cmName)
    {
        this.cmName = cmName;
    }

    /**
     * Gets the custom model description.
     * 
     * @return the custom model description
     */
    public String getCmNamespace()
    {
        return cmNamespace;
    }

    /**
     * Sets the CM namespace.
     * 
     * @param cmNamespace the new CM namespace
     */
    public void setCMNamespace(String cmNamespace)
    {
        this.cmNamespace = cmNamespace;
    }

    /**
     * Gets the cm status.
     * 
     * @return the cm status
     */
    public String getCmStatus()
    {
        return cmStatus;
    }

    /**
     * Sets the cm status.
     * 
     * @param cmStatus the new cm status
     */
    public void setCmStatus(String cmStatus)
    {
        this.cmStatus = cmStatus;
    }

    /**
     * Gets the cm actions.
     * 
     * @return the cm actions
     */
    public ActionsSet getCmActions()
    {
        return cmActions;
    }

    /**
     * Gets the cm name element.
     * 
     * @return the cm name element
     */
    public WebElement getCmNameElement()
    {
        return cmNameElement;
    }

    /**
     * Sets the actions.
     * 
     * @param cmActions the new cm actions
     */
    public void setCmActions(ActionsSet cmActions)
    {
        this.cmActions = cmActions;
    }
}
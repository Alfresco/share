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
package org.alfresco.po.share.cmm.admin;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * Class represents FormEditorPage
 * 
 * @author mbhave
 */

public class FormEditorPage extends SharePage
{
    /** The logger */
    private static final Log LOGGER = LogFactory.getLog(FormEditorPage.class);

    private static final By BUTTON_BACK_TO_TYPES_PROPERTYGROUPS = By.cssSelector(".alfresco-buttons-AlfButton.backToTypesPropertyGroups > span");

    private static final By BUTTON_SAVE_LAYOUT = By.cssSelector(".alfresco-buttons-AlfButton.confirmationButton > span");

    private static final By BUTTON_DEFAULT_LAYOUT = By.cssSelector(".alfresco-buttons-AlfButton.editorDefaultLayoutButton > span");

    private static final By BUTTON_CLEAR_LAYOUT = By.cssSelector(".alfresco-buttons-AlfButton.editorClearLayoutButton > span");

	@SuppressWarnings("unchecked")
	public FormEditorPage render()
    {
		RenderTime renderTime = new RenderTime(maxPageLoadingTime);
        elementRender(
                renderTime,
                getVisibleRenderElement(BUTTON_SAVE_LAYOUT),
                getVisibleRenderElement(BUTTON_DEFAULT_LAYOUT),
                getVisibleRenderElement(BUTTON_CLEAR_LAYOUT));

        return this;
    }

    /**
     * Select back to types property groups button.
     * 
     * @return ManageTypesAndAspectsPage the manage types and aspects page
     */
    public HtmlPage selectBackToTypesPropertyGroupsButton()
    {
        try
        {
            WebElement backToTypesPropertyGroupsButton = findFirstDisplayedElement(BUTTON_BACK_TO_TYPES_PROPERTYGROUPS);
            backToTypesPropertyGroupsButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Button not visible: BUTTON_BACK_TO_TYPES_PROPERTYGROUPS");
    }

    /**
     * Select Save Layout button.
     * 
     * @return FormEditorPage the FormEditorPage
     */
    public HtmlPage selectSaveButton()
    {
        try
        {
            WebElement selectedButton = findFirstDisplayedElement(BUTTON_SAVE_LAYOUT);
            selectedButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Button not visible: BUTTON_SAVE_LAYOUT");
    }

    /**
     * Select Default Layout button.
     * 
     * @return the HtmlPage
     */
    public HtmlPage selectDefaultLayoutButton()
    {
        try
        {
            WebElement selectedButton = findFirstDisplayedElement(BUTTON_DEFAULT_LAYOUT);
            selectedButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }
        throw new PageOperationException("Button not visible: BUTTON_DEFAULT_LAYOUT");
    }

    /**
     * Select Clear Layout button.
     * 
     * @return the HtmlPage
     */
    public HtmlPage selectClearButton()
    {
        try
        {
            WebElement selectedButton = findFirstDisplayedElement(BUTTON_CLEAR_LAYOUT);
            selectedButton.click();
            return factoryPage.getPage(driver);
        }
        catch (TimeoutException e)
        {
            LOGGER.error("Unable to find the button: ", e);
        }

        throw new PageOperationException("Button not visible: BUTTON_CLEAR_LAYOUT");
    }
}

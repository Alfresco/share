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
package org.alfresco.po.share.cmm;

import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.FactorySharePage;
import org.alfresco.po.share.UnknownSharePage;
import org.alfresco.po.share.admin.AdminConsolePage;
import org.alfresco.po.share.cmm.admin.ApplyDefaultLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ClearFormLayoutPopUp;
import org.alfresco.po.share.cmm.admin.ConfirmDeletePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.CreateNewModelPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.CreateNewPropertyPopUp;
import org.alfresco.po.share.cmm.admin.EditCustomTypePopUp;
import org.alfresco.po.share.cmm.admin.EditModelPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyGroupPopUp;
import org.alfresco.po.share.cmm.admin.EditPropertyPopUp;
import org.alfresco.po.share.cmm.admin.FormEditorPage;
import org.alfresco.po.share.cmm.admin.ImportModelPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * FactoryShareCMMPage implements the pageFactory factorySharePage in share-po.
 * 
 * @author Meenal Bhave
 * @author Richard Smith
 * @since version 1.0
 */
public class FactoryShareCMMPage extends FactorySharePage
{
    private static Log logger = LogFactory.getLog(FactoryShareCMMPage.class);
    private static final String CMM_URL = "custom-model-manager";
    private static final String TPG_HASH = "view=types_property_groups";
    private static final String PROPERTIES_HASH = "view=properties";
    private static final String FORM_EDITOR_HASH = "view=editor";
    private static final String VIEW_MODELS_HASH = "view=models";

    protected static final String SHARE_DIALOGUE_AIKAU = "div.dijitDialogTitleBar";

    /**
     * Instantiates a new factory share cmm page.
     */
    public FactoryShareCMMPage()
    {
        super();
        pages.put("admin-console", AdminConsolePage.class);
        pages.put("ModelManager", ModelManagerPage.class);
        pages.put("ManageTypesAndAspects", ManageTypesAndAspectsPage.class);
        pages.put("ManageProperties", ManagePropertiesPage.class);
        pages.put("FormEditor", FormEditorPage.class);
    }
    public HtmlPage getPage(final WebDriver driver) throws PageException
    {
        HtmlPage page = resolvePage(driver);

        // check for Share Error Popup
        if (page instanceof UnknownSharePage)
        {
            try
            {
                List<WebElement> shareDialogues = driver.findElements(By.cssSelector("div.dijitDialogTitleBar"));
                for(WebElement shareDialogue:shareDialogues)
                {
                	if (shareDialogue.isDisplayed())
                	{
                		if(logger.isDebugEnabled()) { logger.debug(shareDialogue.getText()); }
                		return resolveShareCMMDialoguePage(driver, page);
                	}
                }
            }
            catch (Exception e) { }
            // check for SharePage
            if (driver.getCurrentUrl().contains(TPG_HASH))
            {
            	return instantiatePage(driver, pages.get("ManageTypesAndAspects"));
            }
            else if (driver.getCurrentUrl().contains(PROPERTIES_HASH))
            {
            	return instantiatePage(driver, pages.get("ManageProperties"));
            }
            else if (driver.getCurrentUrl().contains(FORM_EDITOR_HASH))
            {
            	return instantiatePage(driver, pages.get("FormEditor"));
            }
            else if (driver.getCurrentUrl().contains(CMM_URL))
            {
            	return instantiatePage(driver, pages.get("ModelManager"));
            }
            else if (page instanceof AdminConsolePage)
            {
                return instantiatePage(driver, AdminConsolePage.class);
            }
        }

        // check for SharePage
        if (driver.getCurrentUrl().contains(TPG_HASH))
        {
            return instantiatePage(driver, pages.get("ManageTypesAndAspects"));
        }
        else if (driver.getCurrentUrl().contains(PROPERTIES_HASH))
        {
            return instantiatePage(driver, pages.get("ManageProperties"));
        }
        else if (driver.getCurrentUrl().contains(FORM_EDITOR_HASH))
        {
            return instantiatePage(driver, pages.get("FormEditor"));
        }
        else if (driver.getCurrentUrl().endsWith(CMM_URL) || driver.getCurrentUrl().endsWith(VIEW_MODELS_HASH))
        {
            return instantiatePage(driver, pages.get("ModelManager"));
        }
        else if (page instanceof AdminConsolePage)
        {
            return instantiatePage(driver, AdminConsolePage.class);
        }
        else
        {
            return page;
        }
    }
    /**
     * Resolve share cmm dialogue page.
     * 
     * @param driver the driver
     * @return the html page
     */
    private HtmlPage resolveShareCMMDialoguePage(WebDriver driver, HtmlPage page)
    {
        HtmlPage sharePage = page;
        String createModel = getValue("cmm.dialogue.label.create.model");
        String importModel = getValue("cmm.dialogue.label.import.model");
        String createType = getValue("cmm.dialogue.label.create.type");
        String createPropGroup = getValue("cmm.dialogue.label.create.property.group");
        String createProperty = getValue("cmm.dialogue.label.create.property");
        String deleteModelConfirmation = getValue("cmm.dialogue.label.delete.model");
        String deleteTypeConfirmation = getValue("cmm.dialogue.label.delete.type");
        String deleteAspectConfirmation = getValue("cmm.dialogue.label.delete.aspect");
        String deletePropertyConfirmation = getValue("cmm.dialogue.label.delete.property");
        String editModel = getValue("cmm.dialogue.label.edit.model");
        String editType = getValue("cmm.dialogue.label.edit.type");
        String editAspect = getValue("cmm.dialogue.label.edit.aspect");
        String editProperty = getValue("cmm.dialogue.label.edit.property");
        String applyDefaultLayout = getValue("cmm.dialogue.label.applyDefault.form.layout");
        String clearLayout = getValue("cmm.dialogue.label.clear.form.layout");

        try
        {
        	List<WebElement>elements = driver.findElements(By.cssSelector(SHARE_DIALOGUE_AIKAU));
        	WebElement dialogue = null;
            for (WebElement element : elements)
            {
                if (element.isDisplayed())
                {
                	dialogue = element;
                	break;
                }
            }
        	
            if (dialogue != null && dialogue.isDisplayed())
            {
                String dialogueText = dialogue.getText();

                if (createModel.equals(dialogueText))
                {
                	sharePage = instantiatePage(driver, CreateNewModelPopUp.class);
                }
                else if (createType.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewCustomTypePopUp.class);
                }
                else if (createPropGroup.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewPropertyGroupPopUp.class);
                }
                else if (createProperty.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, CreateNewPropertyPopUp.class);
                }
                else if (editModel.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditModelPopUp.class);
                }
                else if (editType.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditCustomTypePopUp.class);
                }
                else if (editAspect.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditPropertyGroupPopUp.class);
                }
                else if (editProperty.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, EditPropertyPopUp.class);
                }
                else if (applyDefaultLayout.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ApplyDefaultLayoutPopUp.class);
                }
                else if (deleteModelConfirmation.equals(dialogueText) || deleteTypeConfirmation.equals(dialogueText)
                        || deleteAspectConfirmation.equals(dialogueText) || deletePropertyConfirmation.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ConfirmDeletePopUp.class);
                }
                else if (clearLayout.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ClearFormLayoutPopUp.class);
                }
                else if (importModel.equals(dialogueText))
                {
                    sharePage = instantiatePage(driver, ImportModelPopUp.class);
                }
            }
        }
        catch (NoSuchElementException nse)
        {
            logger.debug("No Share Dialogue (Aikau Style) open: ", nse);
        }

        return sharePage;
    }
}

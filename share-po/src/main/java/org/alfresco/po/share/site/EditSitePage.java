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
package org.alfresco.po.share.site;

import org.alfresco.po.ElementState;
import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderElement;
import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * Edit site page object, holds all element of the HTML page relating to
 * share's edit site page.
 * 
 * @author Michael Suzuki
 * @since 1.5
 */
@SuppressWarnings("unchecked")
public class EditSitePage extends CreateSitePage
{
    protected static final By SUBMIT_BUTTON = By.cssSelector("[id='EDIT_SITE_DIALOG_OK_label']");
    
    public EditSitePage render()
    {
    	RenderTime timer = new RenderTime(maxPageLoadingTime);
    	
    	DIALOG_ID = "#EDIT_SITE_DIALOG";
        
        elementRender(timer, RenderElement.getVisibleRenderElement(SITE_DIALOG));

        return this;
    }
    
    public HtmlPage editSite(String siteName, boolean isPrivate, boolean isModerated)
    {

                setSiteName(siteName);                
                selectSiteVisibility(isPrivate, isModerated);

                return selectOk().render();
    }

    /**
     * Submits the Edit Site Form
     * 
     * @return HtmlPage
     */
    @Override
    public HtmlPage selectOk()
    {
        return submit(SUBMIT_BUTTON, ElementState.DELETE_FROM_DOM).render();
    }

}
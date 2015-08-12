/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.site.contentrule;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * FolderRulesPreRender. This class need for resolve what is Folder rules page was displayed in FactorySharePage.
 * 
 * @author Aliaksei Boole
 * @since 1.0
 */
public class FolderRulesPreRender extends SharePage
{

    private boolean isNoRule = true;
    private static final By NO_RULE = By.cssSelector("div[class*='rules-none']");

    @SuppressWarnings("unchecked")
    @Override
    public SharePage render(RenderTime timer)
    {
        basicRender(timer);
        try
        {
            findAndWait(NO_RULE, 5);
        }
        catch (Exception e)
        {
            isNoRule = false;
        }
        if(isNoRule)
    	{
        	return factoryPage.instantiatePage(driver, FolderRulesPage.class);
    	}
        return factoryPage.instantiatePage(driver, FolderRulesPageWithRules.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SharePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}

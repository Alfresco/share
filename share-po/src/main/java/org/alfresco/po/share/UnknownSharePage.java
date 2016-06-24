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
package org.alfresco.po.share;

import org.alfresco.po.HtmlPage;
import org.openqa.selenium.WebDriver;

/**
 * An unknown page that will, at time of {@link #render()} produce a strongly-typed page.
 * <p/>
 * By using this page, source pages do not need to be responsible for determining the target page in functional methods.
 * 
 * <pre>
 *      public HtmlPage selectItem(Integer number)
 *     {
 *             ...
 *             item.click();
 *             ...
 *         return FactorySharePage.getUnknownPage(driver);
 *     }
 * </pre>
 * 
 * @author Derek Hulley
 * @since 1.8.0
 * @see FactorySharePage#getUnknownPage(WebDriver)
 */
public class UnknownSharePage extends SharePage
{

    /**
     * @see factoryPage.getPage(WebDriver)
     * @return the real page based on what is on the browser
     */
    private HtmlPage getActualPage()
    {
        return getCurrentPage();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends HtmlPage> T render()
    {
        HtmlPage actualPage = getActualPage();
        if (actualPage instanceof UnknownSharePage)
        {
            return (T) actualPage;
        }
        return (T) actualPage.render();
    }

}

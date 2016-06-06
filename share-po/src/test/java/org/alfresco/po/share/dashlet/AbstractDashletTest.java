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
package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.annotations.BeforeMethod;

/**
 * Abstract class with common method used in dashlet
 * based test cases.
 * @author Michael Suzuki
 *
 */
public class AbstractDashletTest extends AbstractDocumentTest
{
    private static Log logger = LogFactory.getLog(AbstractDashletTest.class);
    protected String siteName;
    protected String fileName;
    protected DashBoardPage dashBoard;
    String userName = "user" + System.currentTimeMillis() + "@test.com";
    String firstName = userName;
    String lastName = userName;
    
    @BeforeMethod
    public void startAtDashboard()
    {
        SharePage page = resolvePage(driver).render();
        dashBoard = page.getNav().selectMyDashBoard().render();
    }
    
    public void deleteSite()
    {
        try
        {
            siteUtil.deleteSite(username, password, siteName);
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
}

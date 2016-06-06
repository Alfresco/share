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
package org.alfresco.po.share.bulkimport;

import org.alfresco.po.AbstractTest;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Sergey Kardash
 */
@Listeners(FailedTestListener.class)
public class BulkImportPageTest extends AbstractTest
{
    private static Log logger = LogFactory.getLog(BulkImportPageTest.class);

    @Test(groups = "Enterprise-only")
    public void openConsolePageTest()
    {

        BulkImportPage bulkImportPage = null;
        try
        {
            loginAs(username, password).render();
            bulkImportPage = shareUtil.navigateToBulkImport(driver, false, username, password).render();
        }
        catch (Exception e)
        {
            if (logger.isDebugEnabled())
            {
                logger.debug("Following exception was occurred" + e);
            }
        }
        Assert.notNull(bulkImportPage, "Expected page not opened. Navigate to Bulk Import page is failed");
    }
}

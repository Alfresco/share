package org.alfresco.po.share.bulkimport;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.ShareUtil;
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
            bulkImportPage = ShareUtil.navigateToBulkImport(drone, false, username, password).render();
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
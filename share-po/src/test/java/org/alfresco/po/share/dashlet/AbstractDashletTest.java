package org.alfresco.po.share.dashlet;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.document.AbstractDocumentTest;
import org.alfresco.po.share.util.SiteUtil;
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
        SharePage page = drone.getCurrentPage().render();
        dashBoard = page.getNav().selectMyDashBoard().render();
    }
    
    public void deleteSite()
    {
        try
        {
            SiteUtil.deleteSite(drone, siteName);
        }
        catch (Exception e)
        {
            logger.error("Problem deleting site", e);
        }
    }
}

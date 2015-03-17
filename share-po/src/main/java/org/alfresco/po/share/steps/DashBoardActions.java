package org.alfresco.po.share.steps;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DashBoardActions extends CommonActions
{

    private static Log logger = LogFactory.getLog(DashBoardActions.class);

    /**
     * Navigate to User DashBoard page and waits for the page render to
     * complete. Assumes User is logged in
     * 
     * @param driver WebDrone Instance
     * @return DashBoardPage
     */
    public DashBoardPage refreshUserDashboard(WebDrone driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);

        logger.info("Opening User Dashboard");
        
        return page.getNav().selectMyDashBoard().render();
    }

    /**
     * Navigate to User DashBoard and waits for the page render to complete.
     * Assumes User is logged in
     * 
     * @param driver WebDrone Instance
     * @return DashBoardPage
     */
    public DashBoardPage openUserDashboard(WebDrone driver)
    {
        // Assumes User is logged in
        SharePage page = getSharePage(driver);
        if (page.getPageTitle().contains(MY_DASHBOARD))
        {
            logger.info("User Dashboard already Open");
            return (DashBoardPage) page;
        }

        return refreshUserDashboard(driver);
    }
}

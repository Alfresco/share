package org.alfresco.po.share.site.document;

import org.alfresco.po.share.FactorySharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.TimeoutException;

public class CopyOrMoveContentRulesPage extends CopyOrMoveContentPage
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentRulesPage.class);

    public CopyOrMoveContentRulesPage(WebDrone drone)
    {
        super(drone);
    }

    /**
     * This method finds the clicks on copy/move button.
     * 
     * @return HtmlPage Document library page/ Repository Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            drone.findAndWait(getCopyMoveOkButtonCss()).click();
            return FactorySharePage.resolvePage(drone);
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Button Css : ", e);
            throw new PageException("Unable to find the Copy/Move button on Copy/Move Dialog.");
        }
    }
}

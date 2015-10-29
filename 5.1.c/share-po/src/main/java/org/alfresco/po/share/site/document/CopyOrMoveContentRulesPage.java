package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.TimeoutException;

public class CopyOrMoveContentRulesPage extends CopyOrMoveContentPage
{
    private static Log logger = LogFactory.getLog(CopyOrMoveContentRulesPage.class);

    /**
     * This method finds the clicks on copy/move button.
     * 
     * @return HtmlPage Document library page/ Repository Page
     */
    public HtmlPage selectOkButton()
    {
        try
        {
            findAndWait(getCopyMoveOkButtonCss()).click();
            return getCurrentPage();
        }
        catch (TimeoutException e)
        {
            logger.error("Unable to find the Copy/Move Button Css : ", e);
            throw new PageException("Unable to find the Copy/Move button on Copy/Move Dialog.");
        }
    }
}

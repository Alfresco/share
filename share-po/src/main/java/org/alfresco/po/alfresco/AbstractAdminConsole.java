
package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;

/**
 * Abstract class to hold all common features in alfresco admin pages.
 * @author Michael Suzuki
 *
 */
public abstract class AbstractAdminConsole extends SharePage
{
    protected final static By SUBMIT_BUTTON = By.cssSelector("input.inline"); 
    private final By CLOSE_BUTTON = By.cssSelector("input[id$='Admin-console-title:_idJsp1']");

    /**
     * Method for click Close Button
     */
    public void clickClose()
    {
        findAndWait(CLOSE_BUTTON).click();
    }

    public String getResult()
    {
        return findAndWait(By.tagName("pre")).getText();
    }

}

package org.alfresco.po.share.site.document;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public abstract class AbstractCommentForm extends HtmlElement
{
    private final static By FORM_TITLE = By.cssSelector("div[class='comment-form']>h2.thin.dark");

    private final TinyMceEditor tinyMceEditor;

    public AbstractCommentForm(WebDrone drone)
    {
        super(drone);
        tinyMceEditor = new TinyMceEditor(drone);
    }

    public String getTitle()
    {
        return drone.findAndWait(FORM_TITLE).getText();
    }

    public TinyMceEditor getTinyMceEditor()
    {
        return tinyMceEditor;
    }

    protected void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    protected boolean isDisplay(By locator)
    {
        try
        {
            return drone.findAndWait(locator, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

    public boolean isButtonsEnable(By submit, By cancel)
    {
        try
        {
            return drone.findAndWait(submit).isEnabled() && drone.findAndWait(cancel).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }
}

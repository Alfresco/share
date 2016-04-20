package org.alfresco.po.share.site.document;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public abstract class AbstractCommentForm extends PageElement
{
    private final static By FORM_TITLE = By.cssSelector("div[class='comment-form']>h2.thin.dark");
    private TinyMceEditor tinyMceEditor;


    public String getTitle()
    {
        return findAndWait(FORM_TITLE).getText();
    }

    public TinyMceEditor getTinyMceEditor()
    {
        return tinyMceEditor;
    }

    protected void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    public boolean isButtonsEnable(By submit, By cancel)
    {
        try
        {
            return findAndWait(submit).isEnabled() && findAndWait(cancel).isEnabled();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }
}

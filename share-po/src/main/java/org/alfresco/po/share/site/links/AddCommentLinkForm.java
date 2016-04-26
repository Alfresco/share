package org.alfresco.po.share.site.links;

import org.alfresco.po.PageElement;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.po.share.site.document.TinyMceEditor;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * @author Aliaksei Boole
 */
public class AddCommentLinkForm extends PageElement
{

    protected TinyMceEditor tinyMceEditor;
    protected static final By SUBMIT_BTN = By.cssSelector("button[id$='-submit-button']");



    public TinyMceEditor getTinyMceEditor()
    {
        return tinyMceEditor;
    }

    protected void click(By locator)
    {
        WebElement element = findAndWait(locator);
        element.click();
    }

    /**
     * Method for inserting text into the Reply form
     *
     * @param txtLines
     */
    public void insertText(String txtLines)
    {
        try
        {
            waitUntilElementClickable(getSubmitBtnBy(), 3000);
            tinyMceEditor.setText(txtLines);
        }
        catch (TimeoutException toe)
        {
            throw new ShareException("Time out finding #tinymce", toe);
        }
    }

    /**
     * Method for clicking Submit button
     *
     * @return Topic view page
     */
    public LinksDetailsPage clickSubmit()
    {
        try
        {
            findAndWait(getSubmitBtnBy()).click();
            return factoryPage.instantiatePage(driver,LinksDetailsPage.class).waitUntilAlert().render();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Cannot find Submit button");
        }
        catch (TimeoutException te)
        {
            throw new ShareException("The operation has timed out");
        }
    }

    protected By getSubmitBtnBy()
    {
        return SUBMIT_BTN;
    }

}

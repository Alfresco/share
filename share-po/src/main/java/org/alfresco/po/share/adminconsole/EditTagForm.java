package org.alfresco.po.share.adminconsole;

import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Part of Tag Manager page. Showing when you edit tag on the page.
 * @author Olga Antonik
 */
public class EditTagForm extends HtmlElement 
{

    private final static By FORM = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-dialog");
    private final static By RENAME_TAG_INPUT = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-name");
    private final static By OK = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-ok-button");
    private final static By CANCEL = By.cssSelector("#page_x002e_ctool_x002e_admin-console_x0023_default-edit-tag-cancel-button");

    public EditTagForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Fill field with new tag name.
     *
     * @param text new tag name
     */
    public void fillTagField(String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(RENAME_TAG_INPUT);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }

    /**
     * Mimic click on OK button.
     */
    public void clickOk()
    {
        click(OK);
    }

    /**
     * Mimic click on Cancel button.
     */
    public void clickCancel()
    {
        click(CANCEL);
    }

    private void click(By locator)
    {
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    /**
     * Check that form display on page.
     *
     * @return true - if form displayed.
     */
    public boolean isDisplay()
    {
        try
        {
            return drone.findAndWait(FORM, 2000).isDisplayed();
        }
        catch (TimeoutException e)
        {
            return false;
        }
    }

}

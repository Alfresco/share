package org.alfresco.po.share.site.links;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

/**
 * An abstract of Link form
 *
 * @author Marina.Nenadovets
 */
public abstract class AbstractLinkForm extends SharePage
{
    @SuppressWarnings("unused")
    private Log logger = LogFactory.getLog(this.getClass());

    protected static final By TITLE_FIELD = By.cssSelector("input[id$='default-title']");
    protected static final By URL_FIELD = By.cssSelector("input[id$='default-url']");
    protected static final By DESCRIPTION_FIELD = By.cssSelector("textarea[id$='default-description']");
    protected static final By INTERNAL_CHKBOX = By.cssSelector("input[id$='default-internal']");
    protected static final By CANCEL_BTN = By.cssSelector("button[id$='default-cancel-button']");
    protected static final By TAG_INPUT = By.cssSelector("#template_x002e_linkedit_x002e_links-linkedit_x0023_default-tag-input-field");
    protected static final String LINK_TAG = "//a[@class='taglibrary-action']/span[text()='%s']";
    protected static final By ADD_TAG_BUTTON = By.cssSelector("#template_x002e_linkedit_x002e_links-linkedit_x0023_default-add-tag-button");

    /**
     * Constructor
     *
     * @param drone
     */
    protected AbstractLinkForm(WebDrone drone)
    {
        super(drone);
    }

    /**
     * Method for setting an input into the field
     *
     * @param input
     * @param value
     */
    private void setInput(final WebElement input, final String value)
    {
        try
        {
            input.clear();
            input.sendKeys(value);
        }
        catch (NoSuchElementException e)
        {
            throw new ShareException("Unable to find " + input);
        }
    }

    public void setTitleField(final String title)
    {
        setInput(drone.findAndWait(TITLE_FIELD), title);
    }

    public void setUrlField(final String title)
    {
        setInput(drone.findAndWait(URL_FIELD), title);
    }

    public void setDescriptionField(final String title)
    {
        setInput(drone.findAndWait(DESCRIPTION_FIELD), title);
    }

    protected void setInternalChkbox()
    {
        drone.findAndWait(INTERNAL_CHKBOX).click();
    }

    protected void addTag(final String tag)
    {
        WebElement tagField = drone.findAndWait(TAG_INPUT);
        tagField.clear();
        tagField.sendKeys(tag);
        drone.find(ADD_TAG_BUTTON).click();
    }

    /**
     * Method for removing tag
     * method validate by LinksPageTest.removeTags
     *
     * @param tag
     */
    protected void removeTag(String tag)
    {
        String tagXpath = String.format(LINK_TAG, tag);
        WebElement element;
        try
        {
            element = drone.findAndWait(By.xpath(tagXpath));
            element.click();
            drone.waitUntilElementDisappears(By.xpath(tagXpath), 3000);
        }
        catch (NoSuchElementException e)
        {
            throw new PageException("Unable to find tag " + tag + "");
        }
    }

    /**
     * Method for clicking Cancel button
     *
     * @param title
     */
    protected void clickCancelBtn(final String title)
    {
        try
        {
            drone.findAndWait(CANCEL_BTN).click();
        }
        catch (NoSuchElementException nse)
        {
            throw new ShareException("Unable to find " + CANCEL_BTN);
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Timed out finding " + CANCEL_BTN);
        }
    }

}

package org.alfresco.po.share.site.links;

import org.alfresco.po.share.exception.ShareException;
import org.alfresco.webdrone.HtmlElement;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * @author Marina.Nenadovets
 */
public class LinkDirectoryInfo extends HtmlElement
{
    private static final By EDIT_LINK = By.cssSelector(".edit-link>a");
    private static final By DELETE_LINK = By.cssSelector(".delete-link>a");
    private final static By TAGS_LINK = By.xpath(".//span[@class='tag-item']/span[@class='tag']/a");
    private final static By SELECT = By.xpath(".//input[@type='checkbox']");

    /**
     * Constructor
     */
    protected LinkDirectoryInfo(WebDrone drone, WebElement webElement)
    {
        super(webElement, drone);
    }

    /**
     * Method to click Edit
     *
     * @return AddLink form
     */
    public AddLinkForm clickEdit()
    {
        findAndWait(EDIT_LINK).click();
        return new AddLinkForm(drone).render();
    }

    /**
     * Method to click Delete
     */
    public void clickDelete()
    {
        try
        {
            findAndWait(DELETE_LINK).click();
        }
        catch (TimeoutException te)
        {
            throw new ShareException("Unable to find " + DELETE_LINK);
        }
    }

    /**
     * Method to verify whether edit link is displayed
     *
     * @return boolean
     */
    public boolean isEditDisplayed()
    {
        return findElement(EDIT_LINK).isDisplayed();
    }

    /**
     * Method to verify whether delete link is displayed
     *
     * @return boolean
     */
    public boolean isDeleteDisplayed()
    {
        return findElement(DELETE_LINK).isDisplayed();
    }

    /**
     * Mimic select tag in left filter panel.
     *
     * @param tagName String
     */
    public LinksPage clickOnTag(String tagName)
    {
        checkNotNull(tagName);
        List<WebElement> tagElements = findAllWithWait(TAGS_LINK);
        for (WebElement tagElement : tagElements)
        {
            if (tagName.equals(tagElement.getText()))
            {
                tagElement.click();
                return new LinksPage(drone).waitUntilAlert().render();
            }
        }
        throw new PageException(String.format("Tag with name[%s] don't found.", tagName));
    }

    /**
     * Check is selected.
     *
     * @return boolean
     */
    public boolean isSelected()
    {
        WebElement element = findElement(SELECT);
        return element.isSelected();
    }

    /**
     * Mimic click on checkbox.
     */
    public void clickOnCheckBox()
    {
        WebElement element = findElement(SELECT);
        element.click();
    }

}

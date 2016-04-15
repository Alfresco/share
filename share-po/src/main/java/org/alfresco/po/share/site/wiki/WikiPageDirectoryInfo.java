package org.alfresco.po.share.site.wiki;

import org.alfresco.po.PageElement;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.support.FindBy;

import ru.yandex.qatools.htmlelements.element.Link;

/**
 * @author Marina.Nenadovets
 */
public class WikiPageDirectoryInfo extends PageElement
{


    @FindBy(css=".actionPanel>div.editPage>a") Link edit;
    public void clickEdit()
    {
        edit.click();
    }

    @FindBy(css=".detailsPage>a") Link details;
    /**
     * Method to click Details link
     *
     * @return Wiki page
     */
    public void clickDetails()
    {
        details.click();
    }
    @FindBy(css=".deletePage>a")Link delete;
    /**
     * Method to click Delete
     *
     * @return Wiki page
     */
    public void clickDelete()
    {
        delete.click();
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isEditLinkPresent()
    {
        try
        {
            return edit.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }

    /**
     * Method to verify whether edit wiki page is displayed
     *
     * @return boolean
     */
    public boolean isDeleteLinkPresent()
    {
        try
        {
            return delete.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
        }
        return false;
    }
}

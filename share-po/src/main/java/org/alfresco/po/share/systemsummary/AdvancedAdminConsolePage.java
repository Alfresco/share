package org.alfresco.po.share.systemsummary;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.alfresco.po.share.SharePage;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;


/**
 * @author sergey.kardash on 4/11/14.
 */
public abstract class AdvancedAdminConsolePage extends SharePage
{

    // private Log logger = LogFactory.getLog(this.getClass());
    private final String CHECKBOX = "//span[@class='value']//img";
    private final String VALUE = "//span[@class='value']";

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    public HtmlPage openConsolePage(AdminConsoleLink adminConsoleLink)
    {
        findAndWait(adminConsoleLink.contentLocator).click();
        return getCurrentPage();
    }

    /**
     * Checks if tab present at a left column af tabs' list
     * @param adminConsoleLink AdminConsoleLink
     * @return boolean
     */
    public boolean isConsoleLinkPresent(AdminConsoleLink adminConsoleLink)
    {
          try
          {
              return findAndWait(adminConsoleLink.contentLocator).isDisplayed();
          }
          catch (NoSuchElementException nse)
          {
              return false;
          }
    }

    /**
     * gets value of the component
     *
     * @return true if any value present
     */
    public String getValueOfElement(String element)
    {
        try
        {
            WebElement getV = findAndWait(By.xpath(element + VALUE));
            return getV.getText();
        }
        catch (TimeoutException toe)
        {
            throw new PageException("Element isn't present", toe);
        }

    }

    /**
     * Checks if value of the component present
     *
     * @return true if any data is present
     */
    public boolean isDataPresent(String element)
    {
        try
        {
            WebElement data = findAndWait(By.xpath(element));
            return data.isDisplayed();
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }

    /**
     * Checks if a radio button of the component present
     *
     * @return true if the ratio button is present
     */
    public boolean isRadioButtonPresent(String element)
    {
        try
        {
            WebElement button = findAndWait(By.xpath(element + CHECKBOX));
            return button.isDisplayed();
        } 
        catch (NoSuchElementException te)
        {
            return false;
        }
    }

}

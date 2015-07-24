package org.alfresco.po.share.systemsummary;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.site.contentrule.FolderRulesPage;
import org.alfresco.po.share.user.UserSiteItem;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.alfresco.webdrone.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;

import java.util.ArrayList;
import java.util.List;


/**
 * @author sergey.kardash on 4/11/14.
 */
public abstract class AdvancedAdminConsolePage extends SharePage
{

    // private Log logger = LogFactory.getLog(this.getClass());
    private final String CHECKBOX = "//span[@class='value']//img";
    private final String VALUE = "//span[@class='value']";

    public AdvancedAdminConsolePage(WebDrone drone)
    {
        super(drone);
    }

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

    @SuppressWarnings("unchecked")
    @Override
    public AdvancedAdminConsolePage render(final long time)
    {
        return render(new RenderTime(time));
    }

    public SharePage openConsolePage(AdminConsoleLink adminConsoleLink)
    {
        drone.findAndWait(adminConsoleLink.contentLocator).click();
        return drone.getCurrentPage().render();
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
              return drone.findAndWait(adminConsoleLink.contentLocator).isDisplayed();
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
    public String getValue(String element)
    {
        try
        {
            WebElement getV = drone.findAndWait(By.xpath(element + VALUE));
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
            WebElement data = drone.findAndWait(By.xpath(element));
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
    public boolean isRadioButtonPresent(String element) {
        try {
            WebElement button = drone.find(By.xpath(element + CHECKBOX));
            return button.isDisplayed();
        } catch (NoSuchElementException te) {
            return false;
        }
    }

}

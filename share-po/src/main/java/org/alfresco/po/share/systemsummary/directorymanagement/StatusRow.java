package org.alfresco.po.share.systemsummary.directorymanagement;

import org.alfresco.po.PageElement;
import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

/**
 * Object associated with rows in sync status table. Can't interacting with rows.
 * Methods only return information.
 *
 * @author Aliaksei Boole
 */
public class StatusRow extends PageElement
{
    private final static By BEAN_NAME = By.xpath("./td[1]");
    private final static By SYNC_TIME = By.xpath("./td[2]");
    private final static By STATUS_COUNT = By.xpath("./td[3]");
    private final static By TOTAL_COUNT = By.xpath("./td[4]");

    public StatusRow(WebElement webElement, WebDriver driver)
    {
        setWrappedElement(webElement);
    }


    public String getBeanNameInfo()
    {
        try
        {
            return findAndWait(BEAN_NAME).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getBeanNameInfo();
        }
    }

    public String getSyncTimeInfo()
    {
        try
        {
            return findAndWait(SYNC_TIME).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getSyncTimeInfo();
        }
    }

    public String getStatusCountInfo()
    {
        try
        {
            return findAndWait(STATUS_COUNT).getText();
        }
        catch (StaleElementReferenceException e)
        {
            return getStatusCountInfo();
        }
    }

    public String getTotalCount()
    {
        return findAndWait(TOTAL_COUNT).getText();
    }

    /**
     * Return true if all information in row displayed.
     *
     * @return boolean
     */
    public boolean isAllInfoDisplayed()
    {
        boolean allOk;
        String beanNameInfo = getBeanNameInfo();
        String syncTimeInfo = getSyncTimeInfo();
        String statusCountInfo = getStatusCountInfo();
        String totalCount = getTotalCount();
        allOk = !(beanNameInfo == null || beanNameInfo.isEmpty());
        allOk = allOk && !(syncTimeInfo == null || syncTimeInfo.isEmpty());
        allOk = allOk && !(statusCountInfo == null || statusCountInfo.isEmpty());
        allOk = allOk && !(totalCount == null || totalCount.isEmpty());
        return allOk;
    }

}

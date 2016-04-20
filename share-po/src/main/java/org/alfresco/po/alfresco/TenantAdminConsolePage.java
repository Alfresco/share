package org.alfresco.po.alfresco;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.apache.commons.lang.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represents the tenant admin page found on alfresco.
 * @author Michael Suzuki
 * @since 5.0
 */
public class TenantAdminConsolePage extends AbstractAdminConsole
{

    private final static By INPUT_FIELD = By.name("tenant-cmd"); 

    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render(RenderTime timer)
    {
        basicRender(timer);
        while (true)
        {
            timer.start();
            try
            {
                if (driver.findElement(INPUT_FIELD).isDisplayed() && driver.findElement(SUBMIT_BUTTON).isDisplayed())
                {
                    break;
                }
            }
            catch (NoSuchElementException nse){ }
            finally
            {
                timer.end();
            }
        }
        return this;
    }
    @SuppressWarnings("unchecked")
    @Override
    public TenantAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Populates the input with the create tenant command and
     * submits the form.
     * @param tenantName String tenant name 
     * @param password String tenant password
     */
    public HtmlPage createTenant(String tenantName, String password)
    {
        WebElement input = driver.findElement(INPUT_FIELD);
        input.clear();
        input.sendKeys(String.format("create %s %s", tenantName, password));
        driver.findElement(SUBMIT_BUTTON).click();
        return this;
    }
    /**
     * Method for sending string to command input field.
     *
     * @param command String to pass.
     */
    public void sendCommands(final String command)
    {
        if(StringUtils.isEmpty(command))
        {
            throw new IllegalArgumentException("Command value is required");
        }
        WebElement input = driver.findElement(INPUT_FIELD);
        input.clear();
        input.sendKeys(String.format("%s", command));
        driver.findElement(SUBMIT_BUTTON).click();
    }
}

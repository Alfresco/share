package org.alfresco.po.share.systemsummary;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * @author  Olga Antonik
 */
public class TenantConsole extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By TENANT_FIELD = By.cssSelector("input[name='tenant-cmd']");
    @RenderWebElement
    private final static By EXECUTE_BUTTON = By.cssSelector("input[value='Execute']");


    public TenantConsole(WebDrone drone) {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantConsole render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantConsole render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public TenantConsole render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method for create tenant
     *
     * @param tenantName
     * @param password
     */
    public void createTenant(String tenantName, String password)
    {
        drone.findAndWait(TENANT_FIELD, 60000).clear();
        drone.findAndWait(TENANT_FIELD).sendKeys(String.format("create %s %s", tenantName, password));
        drone.findAndWait(EXECUTE_BUTTON).click();

    }

    /**
     * Method for send commands
     *
     * @param request
     * @return
     */
    public void sendCommand(String request)
    {
        drone.findAndWait(TENANT_FIELD, 60000).clear();
        drone.findAndWait(TENANT_FIELD).sendKeys(String.format("%s", request));
        drone.findAndWait(EXECUTE_BUTTON).click();
    }

    /**
     * Method for find text in Result section
     * @return String
     */
    public String findText()
    {
        return drone.findAndWait(By.xpath("//div[@class='column-full']/h2[text()='Result']/..//pre")).getText();
    }

}

package org.alfresco.po.share.systemsummary;

import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
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

    /**
     * Method for create tenant
     *
     * @param tenantName String
     * @param password String
     */
    public void createTenant(String tenantName, String password)
    {
        findAndWait(TENANT_FIELD, 60000).clear();
        findAndWait(TENANT_FIELD).sendKeys(String.format("create %s %s", tenantName, password));
        findAndWait(EXECUTE_BUTTON).click();

    }

    /**
     * Method for send commands
     *
     * @param request String
     */
    public void sendCommand(String request)
    {
        findAndWait(TENANT_FIELD, 60000).clear();
        findAndWait(TENANT_FIELD).sendKeys(String.format("%s", request));
        findAndWait(EXECUTE_BUTTON).click();
    }

    /**
     * Method for find text in Result section
     * @return String
     */
    public String findText()
    {
        return findAndWait(By.xpath("//div[@class='column-full']/h2[text()='Result']/..//pre")).getText();
    }

}

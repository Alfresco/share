package org.alfresco.po.share.systemsummary;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Created by olga.lokhach on 9/9/2014.
 */

public class FileServersPage extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By FILE_SYSTEMS = By.cssSelector("input[name$='filesystem.name']");
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CANCEL_BUTTON = By.xpath("//input[@value='Cancel']");

    //FTP
    @RenderWebElement
    private final static By FTP_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='ftp.enabled']");
    @RenderWebElement
    private final static By FTP_PORT_INPUT = By.cssSelector("input[name$='ftp.port']");
    @RenderWebElement
    private final static By FTP_DATAPORT_TO = By.cssSelector("input[name$='dataPortTo']");
    @RenderWebElement
    private final static By FTP_DATAPORT_FROM = By.cssSelector("input[name$='dataPortFrom']");

    //CIFS
    @RenderWebElement
    private final static By CIFS_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='cifs.enabled']");
    @RenderWebElement
    private final static By CIFS_SERVER_NAME = By.cssSelector("input[name$='cifs.serverName']");
    @RenderWebElement
    private final static By CIFS_DOMAIN = By.cssSelector("input[name$='cifs.domain']");
    @RenderWebElement
    private final static By CIFS_HOST_ANNOUNCE = By.cssSelector("input[onchange*='cifs.hostannounce']");
    @RenderWebElement
    private final static By CIFS_SESSION_TIMEOUT = By.cssSelector("input[name$='cifs.sessionTimeout']");

    public FileServersPage(WebDrone drone)
    {
        super(drone);
    }
    @SuppressWarnings("unchecked")
    @Override
    public synchronized FileServersPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileServersPage render(long time)
    {
        return render(new RenderTime(time));
    }

    @SuppressWarnings("unchecked")
    @Override
    public FileServersPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }


    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = drone.findAndWait(locator);
        element.click();
    }

    private void fillField(By selector, String text)
    {
        checkNotNull(text);
        WebElement inputField = drone.findAndWait(selector);
        inputField.clear();
        if (text != null)
        {
            inputField.sendKeys(text);
        }
    }
    /**
     * Change FTP port
     *
     * @param port
     *
     */

    public FileServersPage configFtpPort(String port)
    {
        fillField(FTP_PORT_INPUT, port);
        click(SAVE_BUTTON);
        return drone.getCurrentPage().render();
    }


    /**
     * Method to get value of the FTP port.
     *
     * @return
     */
    public String getPort()
    {
        return drone.findAndWait(FTP_PORT_INPUT).getAttribute("value");
    }

    /**
     * Method to enable or disable the FTP server
     */

    public FileServersPage selectFtpEnabledCheckbox()
    {
        drone.findAndWait(FTP_ENABLED_CHECKBOX).click();
        click(SAVE_BUTTON);
        return drone.getCurrentPage().render();
    }

    /**
     * Is check box selected
     *
     * @return - Boolean
     */
    public boolean isFtpEnabledSelected()
    {
        try
        {
            return (drone.find(FTP_ENABLED_CHECKBOX).isSelected());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}

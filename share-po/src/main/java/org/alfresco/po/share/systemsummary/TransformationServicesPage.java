
package org.alfresco.po.share.systemsummary;

import static com.google.common.base.Preconditions.checkNotNull;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.RenderWebElement;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Created by olga.lokhach
 */
public class TransformationServicesPage extends AdvancedAdminConsolePage
{
    @RenderWebElement
    private final static By SAVE_BUTTON = By.xpath("//input[@value='Save']");
    @RenderWebElement
    private final static By CANCEL_BUTTON = By.xpath("//input[@value='Cancel']");

    //Office Transform - JODConverter
    @RenderWebElement
    private final static By JODCONVERTER_ENABLED_CHECKBOX = By.cssSelector("input[onchange*='jodconverter.enabled']");
    @RenderWebElement
    private final static By JODCONVERTER_PORT_INPUT = By.cssSelector("input[name$='jodconverter.portNumbers']");
    @SuppressWarnings("unchecked")
    @Override
    public synchronized TransformationServicesPage render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }


    @SuppressWarnings("unchecked")
    @Override
    public TransformationServicesPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    private void click(By locator)
    {
        checkNotNull(locator);
        WebElement element = findAndWait(locator);
        element.click();
    }
    /**
     * Method to enable or disable the FTP server
     */

    public HtmlPage selectJODConverterEnabledCheckbox()
    {
        findAndWait(JODCONVERTER_ENABLED_CHECKBOX).click();
        click(SAVE_BUTTON);
        return getCurrentPage();
    }

    /**
     * Is check box selected
     *
     * @return - Boolean
     */
    public boolean isJODConverterEnabledSelected()
    {
        try
        {
            return (driver.findElement(JODCONVERTER_ENABLED_CHECKBOX).isSelected());
        }
        catch (NoSuchElementException nse)
        {
            return false;
        }
    }
}

package org.alfresco.po.alfresco;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * Represents the repository admin console page found on alfresco.
 * @author Michael Suzuki
 * @since 5.0
 */
public class RepositoryAdminConsolePage extends AbstractAdminConsole
{

    private final static By INPUT_FIELD = By.name("repo-cmd"); 

    @SuppressWarnings("unchecked")
    @Override
    public RepositoryAdminConsolePage render(RenderTime timer)
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
    public RepositoryAdminConsolePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
    /**
     * Populates the input with command and
     * submits the form.
     * @param command String command
     */
    public void sendCommands(final String command)
    {
        WebElement input = driver.findElement(INPUT_FIELD);
        input.clear();
        input.sendKeys(command);
        driver.findElement(SUBMIT_BUTTON).click();
    }
}

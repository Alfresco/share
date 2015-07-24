package org.alfresco.po.share.systemsummary;

import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.RenderWebElement;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * Created by olga.lokhach on 9/11/2014.
 */
public class ModelAndMessagesConsole  extends TenantConsole
{
    @RenderWebElement
    private final static By REPO_FIELD = By.cssSelector("input[name='repo-cmd']");
    @RenderWebElement
    private final static By EXECUTE_BUTTON = By.cssSelector("input[value='Execute']");

    public ModelAndMessagesConsole(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndMessagesConsole render(RenderTime timer)
    {
        webElementRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndMessagesConsole render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelAndMessagesConsole render(final long time)
    {
        return render(new RenderTime(time));
    }
    /**
     * Method for send commands
     *
     * @param request String
     */
    public void sendCommand(String request)
    {
        drone.findAndWait(REPO_FIELD, 60000).clear();
        drone.findAndWait(REPO_FIELD).sendKeys(String.format("%s", request));
        drone.findAndWait(EXECUTE_BUTTON).click();
    }
}
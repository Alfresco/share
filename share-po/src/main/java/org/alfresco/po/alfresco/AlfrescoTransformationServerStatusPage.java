package org.alfresco.po.alfresco;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

/**
 * Class containing page objects for Alfresco Transformation Server page
 *
 * @author Marina.Nenadovets
 */
public class AlfrescoTransformationServerStatusPage extends SharePage
{
    protected static Log logger = LogFactory.getLog(AlfrescoTransformationServerStatusPage.class);
    protected final By SERVER_STATUS = By.cssSelector("a[href='/transformation-server/home']");
    protected final By SERVER_HISTORY = By.cssSelector("a[href='/transformation-server/transformations']");
    protected final By SERVER_STATS = By.cssSelector("a[href='/transformation-server/stats']");
    protected final By SERVER_SETTINGS = By.cssSelector("a[href='/transformation-server/settings']");

    public AlfrescoTransformationServerStatusPage(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerStatusPage render(RenderTime renderTime)
    {
        elementRender(renderTime,
            getVisibleRenderElement(SERVER_STATUS),
            getVisibleRenderElement(SERVER_HISTORY),
            getVisibleRenderElement(SERVER_STATS),
            getVisibleRenderElement(SERVER_SETTINGS));
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerStatusPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    @SuppressWarnings("unchecked")
    @Override
    public AlfrescoTransformationServerStatusPage render(final long time)
    {
        return render(new RenderTime(time));
    }

    /**
     * Method to navigate to server history page
     *
     * @param driver WebDrone instance
     * @return AlfrescoTransformationServerHistoryPage
     */
    public AlfrescoTransformationServerHistoryPage openServerHistoryPage(WebDrone driver)
    {
        driver.findAndWait(SERVER_HISTORY).click();
        return new AlfrescoTransformationServerHistoryPage(driver).render();
    }
}

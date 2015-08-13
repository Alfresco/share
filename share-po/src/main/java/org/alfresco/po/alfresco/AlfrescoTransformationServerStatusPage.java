package org.alfresco.po.alfresco;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

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

    /**
     * Method to navigate to server history page
     *
     * @param driver WebDriver instance
     * @return AlfrescoTransformationServerHistoryPage
     */
    public AlfrescoTransformationServerHistoryPage openServerHistoryPage(WebDriver driver)
    {
        findAndWait(SERVER_HISTORY).click();
        return factoryPage.instantiatePage(driver, AlfrescoTransformationServerHistoryPage.class).render();
    }
}

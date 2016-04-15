
package org.alfresco.po.alfresco;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * Created by olga.lokhach on 6/16/2014.
 */
public class WebScriptsMaintenancePage extends WebScriptsPage
{

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsMaintenancePage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public WebScriptsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to verify console is opened
     *
     * @return boolean
     */

    public boolean isOpened()
    {
        return findAndWait(By.xpath("*//b[contains(text(), 'Maintenance Completed')]")).isDisplayed();
    }

}

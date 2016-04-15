
package org.alfresco.po.alfresco;

import static java.util.concurrent.TimeUnit.MILLISECONDS;
import static java.util.concurrent.TimeUnit.SECONDS;

import org.alfresco.po.RenderTime;
import org.openqa.selenium.By;

/**
 * Created by ivan.kornilov on 22.04.2014.
 */

public class MyAlfrescoPage extends LoginAlfrescoPage
{

    private final By logoutXPath = By.xpath("//*[@id='logout']");

    @SuppressWarnings("unchecked")
    @Override
    public MyAlfrescoPage render(RenderTime timer)
    {
        basicRender(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public MyAlfrescoPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Method to verify My Alfresco Page is opened
     *
     * @return boolean
     */
    public boolean userIsLoggedIn(String userName)
    {
        return findAndWait(logoutXPath, SECONDS.convert(maxPageLoadingTime, MILLISECONDS)).getText()
            .equalsIgnoreCase(String.format("Logout (%s)", userName));
    }
}

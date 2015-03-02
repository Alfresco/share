package org.alfresco.po.share.site.contentrule;

import org.alfresco.po.share.SharePage;
import org.alfresco.webdrone.RenderTime;
import org.alfresco.webdrone.WebDrone;
import org.openqa.selenium.By;

/**
 * FolderRulesPreRender. This class need for resolve what is Folder rules page was displayed in FactorySharePage.
 * 
 * @author Aliaksei Boole
 * @since 1.0
 */
public class FolderRulesPreRender extends SharePage
{

    private boolean isNoRule = true;
    private static final By NO_RULE = By.cssSelector("div[class*='rules-none']");

    public FolderRulesPreRender(WebDrone drone)
    {
        super(drone);
    }

    @SuppressWarnings("unchecked")
    @Override
    public SharePage render(RenderTime timer)
    {
        basicRender(timer);
        try
        {
            drone.findAndWait(NO_RULE, 5);
        }
        catch (Exception e)
        {
            isNoRule = false;
        }
        return (isNoRule ? new FolderRulesPage(drone) : new FolderRulesPageWithRules(drone));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SharePage render(long time)
    {
        return render(new RenderTime(time));
    }

    @Override
    @SuppressWarnings("unchecked")
    public SharePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}

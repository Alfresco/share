package org.alfresco.po.share.site.contentrule;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.SharePage;
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

    @SuppressWarnings("unchecked")
    @Override
    public SharePage render(RenderTime timer)
    {
        basicRender(timer);
        try
        {
            findAndWait(NO_RULE, 5);
        }
        catch (Exception e)
        {
            isNoRule = false;
        }
        if(isNoRule)
    	{
        	return factoryPage.instantiatePage(driver, FolderRulesPage.class);
    	}
        return factoryPage.instantiatePage(driver, FolderRulesPageWithRules.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public SharePage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}

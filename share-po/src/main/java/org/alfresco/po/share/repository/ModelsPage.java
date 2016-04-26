
package org.alfresco.po.share.repository;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.RepositoryPage;

/**
 * Model's page object, holds all element of the HTML page relating to share's repository > Models page.
 * 
 * @author mbhave
 * @since 5.0.2
 */

public class ModelsPage extends RepositoryPage
{


    @SuppressWarnings("unchecked")
    @Override
    public ModelsPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public ModelsPage render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        return isBrowserTitle("Repository Browser");
    }

}

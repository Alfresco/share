package org.alfresco.po.share;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

/**
 * Repository page object, holds all element of the HTML page relating to
 * share's repository page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class RepositoryPage extends DocumentLibraryPage
{

    @Override
    public RepositoryPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
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

package org.alfresco.po.share;

import org.alfresco.po.share.dashlet.Dashlet;

/**
 * Interface of Alfresco Dashboard view.
 * 
 * @author Michael Suzuki
 * @since 1.4
 */
public interface Dashboard
{
    /**
     * Gets dashlets in the dashboard view.
     * 
     * @param name String title of dashlet
     * @return HtmlPage page object
     * @throws Exception
     */
    Dashlet getDashlet(final String name);
}

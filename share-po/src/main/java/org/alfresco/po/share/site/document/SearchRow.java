package org.alfresco.po.share.site.document;

import org.alfresco.po.HtmlPage;

/**
 * @author nshah
 */
public interface SearchRow
{

    /**
     * Click Add button to add user in manage permission.
     * 
     * @return @HTMLPage
     */
    HtmlPage clickAdd();

    /**
     * Click User to navigate to user profile.
     * 
     * @return @HtmlPage
     */
    HtmlPage clickUser();

    /**
     * Get User name for User SearchPage.
     * 
     * @return String
     */
    String getUserName();
}

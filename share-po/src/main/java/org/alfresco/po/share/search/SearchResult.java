package org.alfresco.po.share.search;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.share.admin.ActionsSet;

/**
 * Interface that represent search result row.
 * @author Michael Suzuki
 * @since 2.5
 *
 */
public interface SearchResult
{
    /**
     * Title of search result item.
     * 
     * @return String title
     */
     String getTitle();
     /**
      * Name of search result item.
      * 
      * @return String title
      */
     String getName();
    /**
     * Select the link of the search result item.
     * 
     * @return true if link found and selected
     */
     HtmlPage clickLink();
     /**
      * Verify if folder or not, true if search row represent
      * a folder.
      * 
      * @return boolean true if search result is of folder
      */
     boolean isFolder();
     
     /**
      * Date of search result item.
      * 
      * @return String Date
      */
     
     String getDate();
     
     /**
      * Site of search result item.
      * 
      * @return String Site
      */
     
     String getSite();
     
     /**
      * Select the site link of the search result item.
      * 
      * @return true if link found and selected
      */
     HtmlPage clickSiteLink();
     
     /**
      * Select the Date link of the search result item.
      * 
      * @return true if link found and selected
      */
     
	 HtmlPage clickDateLink();
	 
	 /**
      * Actions of search result item.
      * 
      * @return enum ActionSet
      */
	 ActionsSet getActions();

    /**
     * Method to click on content path in the details section
     *
     * @return SharePage
     */
    public HtmlPage clickContentPath();

    /**
     * Method to get thumbnail url
     *
     * @return String
     */
    public String getThumbnailUrl();

    /**
     * Method to get preview url
     *
     * @return String
     */
    public String getPreViewUrl();

    /**
     * Method to get thumbnail of element
     *
     * @return String
     */
    public String getThumbnail();

    /**
     * Method to click on Download icon for the element
     */
    public void clickOnDownloadIcon();

    /**
     * Select the site link of the search result item.
     *
     * @return true if link found and selected
     */
    HtmlPage clickSiteName();
	 
	/**
     * Select the Image link of the search result item.
     * 
     * @return PreViewPopUpPage if link found and selected
     */
	PreViewPopUpPage clickImageLink();
}

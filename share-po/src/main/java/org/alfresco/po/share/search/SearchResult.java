/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */
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

	HtmlPage selectItemCheckBox();

	boolean isItemCheckBoxSelected();
	
}

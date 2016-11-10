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


	public enum SearchSelectedItemsMenu
	{
	    DELETE ("#onActionDelete_text"),
	    COPY_TO ("#onActionCopyTo_text"),
	    MOVE_TO ("#onActionMoveTo_text"),
	    //DESELECT_ALL (".onActionDeselectAll"),
	    START_WORKFLOW ("#onActionAssignWorkflow_text"),
	    DOWNLOAD_AS_ZIP ("#onActionDownload_text");
	   
	    private String linkValue;

	    private SearchSelectedItemsMenu(String link)
	    {
	        linkValue = link;
	    }

	    /**
	     * Get value of CSS from the page type.
	     * 
	     * @return String
	     */
	    public String getOption()
	    {
	        return linkValue;
	    }
	}



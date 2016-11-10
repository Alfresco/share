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

import org.alfresco.po.share.search.LiveSearchDropdown.ResultType;

/**
 * Holds the information of a search result item.
 * When completing a search the resulting page yields results
 * which each individual row is represented by SearchResultItem class.
 * 
 * @author Meenal Bhave
 * @since 5.2.N
 */
public class LiveSearchResultItem
{   

	private String resultItemName;
    private ResultType resultType;
    private String siteName;
    private String username;

    /**
     * Constructor
     * @param nodeName
     * @param resultType
     * @param siteName
     * @param username
     */
    public LiveSearchResultItem(ResultType resultType, String nodeName, String siteName, String username)
    {
        this.setResultType(resultType);
        this.setResultItemName(nodeName);
        this.setSiteName(siteName);
        this.setUsername(username);
    }
    
    public LiveSearchResultItem(ResultType resultType, String nodeName)
    {
        this.setResultType(resultType);
        this.setResultItemName(nodeName);
        this.setSiteName("");
        this.setUsername("");
    }

	public String getResultItemName() {
		return resultItemName;
	}

	public void setResultItemName(String resultItemName) {
		this.resultItemName = resultItemName;
	}

	public ResultType getResultType() {
		return resultType;
	}

	public void setResultType(ResultType resultType) {
		this.resultType = resultType;
	}

	public String getSiteName() {
		return siteName;
	}

	public void setSiteName(String siteName) {
		this.siteName = siteName;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}
   
	
}

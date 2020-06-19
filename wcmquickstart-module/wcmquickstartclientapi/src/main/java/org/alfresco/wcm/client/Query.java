/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
 */
package org.alfresco.wcm.client;

import java.io.Serializable;

import org.alfresco.wcm.client.util.QuerySanitizer;

public class Query implements Serializable
{
    private static final long serialVersionUID = -6767417568016378846L;

    private String phrase;
    private int maxResults = 10;
    private int resultsToSkip = 0;
    private String sectionId;
    private String tag;
    
    public Query()
    {
        
    }
    
    public Query(Query other)
    {
        this.phrase = other.phrase;
        this.tag = other.tag;
        this.maxResults = other.maxResults;
        this.resultsToSkip = other.resultsToSkip;
        this.sectionId = other.sectionId;
    }
    
    public void setPhrase(String phrase)
    {
        this.phrase = QuerySanitizer.sanitize(phrase).trim();
    }
    
    public String getPhrase()
    {
        return phrase;
    }
    
    public void setMaxResults(int maxResults)
    {
        this.maxResults = maxResults;
    }
    
    
    public int getMaxResults()
    {
        return maxResults;
    }
    
    
    public void setResultsToSkip(int resultsToSkip)
    {
        this.resultsToSkip = resultsToSkip;
    }
    
    
    public int getResultsToSkip()
    {
        return resultsToSkip;
    }

    public String getSectionId()
    {
        return sectionId;
    }

    public void setSectionId(String sectionId)
    {
        this.sectionId = sectionId;
    }

    public String getTag()
    {
        return tag;
    }

    public void setTag(String tag)
    {
        this.tag = QuerySanitizer.sanitize(tag).trim();
    }
}

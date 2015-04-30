/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

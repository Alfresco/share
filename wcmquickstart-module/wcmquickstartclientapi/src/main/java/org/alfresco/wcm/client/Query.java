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

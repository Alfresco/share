package org.alfresco.po.share.workflow;

import java.util.Set;

/**
 * Company Home represents the
 * 
 * @author Shan Nagarajan
 * @since 1.7.1
 */
public class CompanyHome
{

    private Set<Site> sites;

    private Set<Content> contents;

    public Set<Site> getSites()
    {
        return sites;
    }

    public void setSites(Set<Site> sites)
    {
        this.sites = sites;
    }

    public Set<Content> getContents()
    {
        return contents;

    }

    public void setContents (Set<Content> contents)
    {
        this.contents = contents;
    }
}

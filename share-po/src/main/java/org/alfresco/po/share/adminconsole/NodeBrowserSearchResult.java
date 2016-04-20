package org.alfresco.po.share.adminconsole;

import org.alfresco.po.share.ShareLink;

/**
 * Class to store the search results in Node Browser 
 *
 * @author Ranjith Manyam
 */
public class NodeBrowserSearchResult
{
    private ShareLink name;
    private String parent;
    private ShareLink reference;

    /**
     * Method to get Name share link
     * @return name
     */
    public ShareLink getName() {
        return name;
    }

    /**
     * Method to set Name
     * @param name ShareLink
     */
    public void setName(ShareLink name) {
        this.name = name;
    }

    /**
     * Method to get Parent
     * @return parent
     */
    public String getParent() {
        return parent;
    }

    /**
     * Method to set parent
     * @param parent String
     */
    public void setParent(String parent) {
        this.parent = parent;
    }

    /**
     * Method to get NodeRef
     * @return reference
     */
    public ShareLink getReference() {
        return reference;
    }

    /**
     * Method to set reference
     * @param reference ShareLink
     */
    public void setReference(ShareLink reference) {
        this.reference = reference;
    }
}

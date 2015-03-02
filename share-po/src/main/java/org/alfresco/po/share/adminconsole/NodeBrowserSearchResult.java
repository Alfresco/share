/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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
     * @param name
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
     * @param parent
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
     * @param reference
     */
    public void setReference(ShareLink reference) {
        this.reference = reference;
    }
}

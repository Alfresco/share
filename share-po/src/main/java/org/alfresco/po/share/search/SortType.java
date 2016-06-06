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

import org.apache.commons.lang3.StringUtils;

/**
 * This enums used to describe list of Sort item in search results page.
 * 
 * @author Subashni Prasanna
 * @since v1.7.1
 */
public enum SortType
{
    RELEVANCE("Relevance"),
    NAME("Name"), 
    TITLE("Title"),
    DESCRIPTION("Description"), 
    AUTHOR("Author"), 
    MODIFIER("Modifier"), 
    MODIFIED("Modified date"),
    CREATOR("Creator"), 
    CREATED("Created date"),
    SIZE("Size"), 
    MIMETYPE("Mime type"), 
    TYPE("Type");

    private String sortName;

    private SortType(String type)
    {
        sortName = type;
    }

    public String getSortName()
    {
        return sortName;
    }

    /**
     * Find the {@link SortType} based it is name.
     * 
     * @param name - Aspect's Name
     * @return {@link SortType}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static SortType getSortType(String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't null or empty, It is required.");
        }
        for (SortType sortType : SortType.values())
        {
            if (sortType.sortName != null && sortType.sortName.equalsIgnoreCase(name.trim()))
            {
                return sortType;
            }
        }
        throw new IllegalArgumentException("Not able to find the Sort Type for given name : " + name);
    }
}

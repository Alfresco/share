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
package org.alfresco.po.share.site.document;

import org.apache.commons.lang3.StringUtils;

public enum Categories
{
    CATEGORY_ROOT("Category Root"), LANGUAGES("Languages"), REGIONS("Regions"), SOFTWARE_DOCUMENT_CLASSIFICATION("Software Document Classification"),
    TAGS("Tags"), CATEGORY_TEST_1("TestCategory1"), CATEGORY_TEST_2("TestCategory2"), SUB_CATEGORY_TEST("SubCategory");

    private String value;

    private Categories(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Find the {@link Categories} based on name.
     *
     * @param name - category name
     * @return {@link Categories}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static Categories getCategory(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't be null or empty, It is required.");
        }
        for (Categories aspect : Categories.values())
        {
            if (aspect.value != null && aspect.value.equalsIgnoreCase(name.trim()))
            {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Not able to find the Category for given name : " + name);
    }

}

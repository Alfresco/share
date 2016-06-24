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

package org.alfresco.po.share.cmm.enums;

/**
 * The enum for Indexing Options for Properties
 * 
 * @author Meenal Bhave
 * @since 1.0
 */

public enum IndexingOptions
{
    None("cmm.property.index.none"),
    FreeText("cmm.property.index.freetext"),
    Basic("cmm.property.index.basic"),
    Enhanced("cmm.property.index.enhanced"),
    LOVWhole("cmm.property.index.lov.whole"),
    LOVPartial("cmm.property.index.lov.partial"),
    PatternUnique("cmm.property.index.pattern.unique"),
    PatternMany("cmm.property.index.pattern.many");

    private String listValue;

    IndexingOptions(String listVal)
    {
        this.listValue = listVal;
    }

    /**
     * @return the classifier for list value
     */
    public String getListValue()
    {
        return listValue;
    }

}

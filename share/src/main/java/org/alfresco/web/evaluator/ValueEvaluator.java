/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * @author: mikeh
 */
public class ValueEvaluator extends BaseEvaluator
{
    private Comparator comparator = null;
    private String accessor = null;

    /**
     * Comparator class
     * 
     * @param comparator
     */
    public void setComparator(Comparator comparator)
    {
        this.comparator = comparator;
    }

    /**
     * Accessor for value to compare against in dot notation format, e.g. "node.properties.cm:name"
     *
     * @param accessor
     */
    public void setAccessor(String accessor)
    {
        this.accessor = accessor;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (comparator == null || accessor == null)
        {
            return false;
        }

        Object nodeValue = getJSONValue(jsonObject, accessor);
        return this.comparator.compare(nodeValue);
    }
}

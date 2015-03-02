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
 * Checks that a property value on a node is not null (and exists)
 *
 * @author: mikeh
 */
public class PropertyNotNullEvaluator extends BaseEvaluator
{
    private String property = null;

    /**
     * Property name
     *
     * @param name
     */
    public void setProperty(String name)
    {
        this.property = name;
    }

    /**
     * Checks that a property value exists and is not null
     *
     *
     * @param jsonObject The object the action is for
     * @return
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;

        if (this.property != null)
        {
            Object value = getProperty(jsonObject, this.property);
            result = (value != null);
        }

        return result;
    }
}

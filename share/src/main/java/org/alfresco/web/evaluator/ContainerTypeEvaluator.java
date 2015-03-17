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

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check whether the node lives in a Site container of one of the listed types
 *
 * @author: mikeh
 */
public class ContainerTypeEvaluator extends BaseEvaluator
{
    private ArrayList<String> types;

    /**
     * Define the list of types to check for
     *
     * @param types
     */
    public void setTypes(ArrayList<String> types)
    {
        this.types = types;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (types.size() == 0)
        {
            return false;
        }

        try
        {
            if (!types.contains(getContainerType(jsonObject)))
            {
                return false;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return true;
    }
}

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
import org.alfresco.web.scripts.DictionaryQuery;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Evaluates whether a node is of a certain type, optionally checking for subtype
 *
 * @author: mikeh
 */
public class NodeTypeEvaluator extends BaseEvaluator
{
    private DictionaryQuery dictionary;
    private boolean allowSubtypes = true;
    private ArrayList<String> types;

    /**
     * Dictionary Query bean reference
     * 
     * @param dictionary
     */
    public void setDictionary(DictionaryQuery dictionary)
    {
        this.dictionary = dictionary;
    }

    /**
     * Whether subtypes are allowed or not. Default is that subtypes ARE allowed.
     *
     * @param allowSubtypes
     */
    public void setAllowSubtypes(boolean allowSubtypes)
    {
        this.allowSubtypes = allowSubtypes;
    }

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

        String nodeType = getNodeType(jsonObject);

        try
        {
            if (types.contains(nodeType))
            {
                return true;
            }

            if (allowSubtypes && dictionary != null)
            {
                for (String type : types)
                {
                    if (dictionary.isSubType(nodeType, type))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return false;
    }
}

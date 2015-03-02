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
package org.alfresco.web.evaluator.doclib.indicator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * "Folder has rules applied" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     hasAspect("rule:rules")
 * </pre>
 *
 * @author: mikeh
 */
public class RulesEvaluator extends BaseEvaluator
{
    private static final String ASPECT_RULES = "rule:rules";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            JSONArray nodeAspects = getNodeAspects(jsonObject);
            if (nodeAspects == null)
            {
                return false;
            }
            else
            {
                if (nodeAspects.contains(ASPECT_RULES))
                {
                    return true;
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run UI evaluator: " + err.getMessage());
        }
        return false;
    }
}

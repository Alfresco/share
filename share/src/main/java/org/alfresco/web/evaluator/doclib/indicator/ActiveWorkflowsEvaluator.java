/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
import org.json.simple.JSONObject;

/**
 * "Active workflows" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     activeWorkflows is a number > 0
 * </pre>
 *
 * @author mikeh
 */
public class ActiveWorkflowsEvaluator extends BaseEvaluator
{
    private final String VALUE_ACTIVEWORKFLOWS = "activeWorkflows";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            Number workflows = (Number) jsonObject.get(VALUE_ACTIVEWORKFLOWS);
            if (workflows != null && workflows.intValue() > 0)
            {
                return true;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run UI evaluator: " + err.getMessage());
        }
        return false;
    }
}

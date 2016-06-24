/*
 * #%L
 * Alfresco Share WAR
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
package org.alfresco.web.evaluator.doclib.action;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * "Current user can edit document" action evaluator.
 *
 * Checks if in such a state that the current user can't edit the document:
 * <pre>
 *     If node is locked it must be locked by the current user
 *     If node is a working copy the current user must be the owner
 * </pre>
 *
 * @author ewinlof
 */
public class EditableByCurrentUser extends BaseEvaluator
{
    private static final String PROP_WORKINGCOPYOWNER = "cm:workingCopyOwner";
    private static final String PROP_LOCKOWNER = "cm:lockOwner";
    private static final String PROP_LOCKTYPE = "cm:lockType";
    private static final String NODE_LOCK = "NODE_LOCK";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            Object lockType = getProperty(jsonObject, PROP_LOCKTYPE);
            if (lockType != null && ((String) lockType).equalsIgnoreCase(NODE_LOCK))
            {
               return false;
            }
            if (getIsLocked(jsonObject))
            {
                return getMatchesCurrentUser(jsonObject, PROP_LOCKOWNER);
            }
            else if (getIsWorkingCopy(jsonObject))
            {
                return getMatchesCurrentUser(jsonObject, PROP_WORKINGCOPYOWNER);
            }

            // Node is in normal state
            return true;
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run UI evaluator: " + err.getMessage());
        }
    }
}

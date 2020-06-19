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
package org.alfresco.web.evaluator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * Base class for all smart evaluators.
 * 
 * @author sdinuta
 *
 */
public abstract class VirtualBaseEvaluator extends BaseEvaluator
{
    /**
     * Checks if the node is a container.
     * 
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * 
     * @return Boolean <code>true</code> if {jsonObject} parameter is a container, or <code>false</code> otherwise.
     */
    Boolean isContainer(JSONObject jsonObject)
    {
        return (Boolean) getJSONValue(jsonObject,"node.isContainer");
    }

    /**
     * Checks if the node isn't in a smart folder context.
     * 
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * 
     * @return boolean <code>true</code> if {jsonObject} parameter isn't in a smart folder context, or <code>false</code> otherwise.
     */
    boolean notInVirtualContext(JSONObject jsonObject)
    {
        boolean virtual = hasAspect(jsonObject,"smf:smartFolder") || hasAspect(jsonObject,"smf:smartFolderChild");
        boolean isContainer = isContainer(jsonObject);
        boolean virtualContext = isContainer && hasAspect(jsonObject,"smf:smartFolderChild");
        if (!virtual && !virtualContext)
        {
            return true;
        }
        return false;
    }

    /**
     * Checks if the node has the specified aspect.
     * 
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @param aspect String 
     * 
     * @return boolean <code>true</code> if the node has specified aspect, or <code>false</code> otherwise. 
     */
    boolean hasAspect(JSONObject jsonObject, String aspect){
        JSONArray nodeAspects = getNodeAspects(jsonObject);
        return nodeAspects.contains(aspect);
    }
}

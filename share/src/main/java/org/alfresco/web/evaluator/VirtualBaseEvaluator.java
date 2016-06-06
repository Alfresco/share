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

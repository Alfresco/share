
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Evaluator for physical folders in smart folder context.
 * 
 * @author sdinuta
 */
public class VirtualFolderContextEvaluator extends VirtualBaseEvaluator
{
    /**
     * Evaluates if we have a folder and if it is in a smart folder context.
     * 
     * @param jsonObject The object the evaluation is for
     * 
     * @return <code>true</code> if the folder is in smart folder context, or <code>false</code> otherwise.
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean virtualContext = isContainer(jsonObject) && hasAspect(jsonObject,"smf:smartFolderChild");
        if (virtualContext)
        {
            return true;
        }
        return false;
    }
}

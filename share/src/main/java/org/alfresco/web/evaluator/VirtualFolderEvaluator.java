package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Evaluator for smart folders.
 * 
 * @author sdinuta
 *
 */
public class VirtualFolderEvaluator extends VirtualBaseEvaluator
{
    /**
     * Evaluates if we have a smart folder.
     * 
     * @param jsonObject The object the evaluation is for
     * 
     * @return <code>true</code> if the folder is smart, or <code>false</code> otherwise.
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if(hasAspect(jsonObject, "smf:smartFolder")){
            return true;
        }
        return false;
    }
}

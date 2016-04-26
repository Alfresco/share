
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Evaluator for documents in smart folder context.
 * 
 * @author sdinuta
 *
 */
public class VirtualDocumentEvaluator extends VirtualBaseEvaluator
{
    /**
     * Evaluates if we have a document and if it is in a smart folder context.
     * 
     * @param jsonObject The object the evaluation is for
     * 
     * @return <code>true</code> if the document is in smart folder context, or <code>false</code> otherwise.
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (hasAspect(jsonObject,"smf:smartFolderChild") && !isContainer(jsonObject))
        {
            return true;
        }
        return false;
    }
}

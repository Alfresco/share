package org.alfresco.web.evaluator.doclib.indicator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * "Node is participating in a simple workflow" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     hasAspect("app:simpleworkflow")
 * </pre>
 *
 * @author mikeh
 */
public class SimpleWorkflowEvaluator extends BaseEvaluator
{
    private static final String ASPECT_SIMPLEWORKFLOW = "app:simpleworkflow";

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
                if (nodeAspects.contains(ASPECT_SIMPLEWORKFLOW))
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

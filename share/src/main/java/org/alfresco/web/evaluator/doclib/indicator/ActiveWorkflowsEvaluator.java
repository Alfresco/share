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

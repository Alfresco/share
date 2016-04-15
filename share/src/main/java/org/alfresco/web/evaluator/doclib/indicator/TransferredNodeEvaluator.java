package org.alfresco.web.evaluator.doclib.indicator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * "Node has been transferred from another Repository" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     hasAspect("trx:transferred")
 * </pre>
 *
 * @author mikeh
 */
public class TransferredNodeEvaluator extends BaseEvaluator
{
    private static final String ASPECT_TRANSFERRED = "trx:transferred";

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
                if (nodeAspects.contains(ASPECT_TRANSFERRED))
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

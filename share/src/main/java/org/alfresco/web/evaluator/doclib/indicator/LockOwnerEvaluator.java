package org.alfresco.web.evaluator.doclib.indicator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * "Current user has document locked" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     node is locked
 *     NOT hasAspect("trx:transferred")
 *     NOT hasAspect("cm:workingcopy")
 *     property "cm:lockOwner" == (currentUser)
 * </pre>
 *
 * @author mikeh
 */
public class LockOwnerEvaluator extends BaseEvaluator
{
    private static final String ASPECT_TRANSFERRED = "trx:transferred";
    private static final String ASPECT_WORKINGCOPY = "cm:workingcopy";
    private static final String PROP_LOCKOWNER = "cm:lockOwner";

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        try
        {
            if (getIsLocked(jsonObject))
            {
                JSONArray nodeAspects = getNodeAspects(jsonObject);
                if (nodeAspects == null)
                {
                    return false;
                }
                else
                {
                    if (!nodeAspects.contains(ASPECT_TRANSFERRED) &&
                            !nodeAspects.contains(ASPECT_WORKINGCOPY))
                    {
                        return getMatchesCurrentUser(jsonObject, PROP_LOCKOWNER);
                    }
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

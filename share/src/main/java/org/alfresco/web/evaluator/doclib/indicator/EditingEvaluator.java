package org.alfresco.web.evaluator.doclib.indicator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 * "Being edited by you" status indicator evaluator.
 *
 * Checks the following conditions are met:
 * <pre>
 *     hasAspect("cm:workingcopy")
 *     property "cm:workingCopyOwner" == (currentUser)
 * </pre>
 *
 * @author mikeh
 */
public class EditingEvaluator extends BaseEvaluator
{
    private static final String ASPECT_WORKINGCOPY = "cm:workingcopy";
    private static final String PROP_WORKINGCOPYOWNER = "cm:workingCopyOwner";

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
                if (nodeAspects.contains(ASPECT_WORKINGCOPY))
                {
                    return getMatchesCurrentUser(jsonObject, PROP_WORKINGCOPYOWNER);
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


package org.alfresco.web.evaluator.doclib.action;

import org.alfresco.web.evaluator.BaseEvaluator;
import org.json.simple.JSONObject;

/**
 * Evaluator for the Locate document library action.
 * The action is only valid when the current filter is not "path"
 *
 * @author mikeh
 */
public class LocateActionEvaluator extends BaseEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        String filter = getArg("filter");
        if (filter instanceof String)
        {
            return !(filter.equalsIgnoreCase("path"));
        }

        return false;
    }
}

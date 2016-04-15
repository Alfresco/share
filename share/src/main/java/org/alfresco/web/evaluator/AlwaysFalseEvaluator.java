
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Convenience evaluator which always returns false
 * 
 * @author mikeh
 */
public class AlwaysFalseEvaluator extends BaseEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return false;
    }
}

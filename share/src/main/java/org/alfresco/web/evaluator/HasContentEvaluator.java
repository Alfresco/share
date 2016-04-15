
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Checks that node has binary content
 *
 * @author pavel.yurkevich
 */
public class HasContentEvaluator extends BaseEvaluator
{

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return getHasContent(jsonObject);
    }

}

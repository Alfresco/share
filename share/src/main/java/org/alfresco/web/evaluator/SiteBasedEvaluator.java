
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Determines whether a site-based node is being accessed via the Site Document Library
 *
 * @author mikeh
 */
public class SiteBasedEvaluator extends BaseEvaluator
{
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return getSiteId(jsonObject) != null;
    }
}

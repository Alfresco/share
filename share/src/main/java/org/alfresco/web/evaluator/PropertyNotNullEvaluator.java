
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Checks that a property value on a node is not null (and exists)
 *
 * @author mikeh
 */
public class PropertyNotNullEvaluator extends BaseEvaluator
{
    private String property = null;

    /**
     * Property name
     *
     * @param name String
     */
    public void setProperty(String name)
    {
        this.property = name;
    }

    /**
     * Checks that a property value exists and is not null
     *
     *
     * @param jsonObject The object the action is for
     * @return boolean
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = false;

        if (this.property != null)
        {
            Object value = getProperty(jsonObject, this.property);
            result = (value != null);
        }

        return result;
    }
}

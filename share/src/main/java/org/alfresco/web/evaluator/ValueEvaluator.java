package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * @author mikeh
 */
public class ValueEvaluator extends BaseEvaluator
{
    private Comparator comparator = null;
    private String accessor = null;

    /**
     * Comparator class
     * 
     * @param comparator
     */
    public void setComparator(Comparator comparator)
    {
        this.comparator = comparator;
    }

    /**
     * Accessor for value to compare against in dot notation format, e.g. "node.properties.cm:name"
     *
     * @param accessor
     */
    public void setAccessor(String accessor)
    {
        this.accessor = accessor;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (comparator == null || accessor == null)
        {
            return false;
        }

        Object nodeValue = getJSONValue(jsonObject, accessor);
        return this.comparator.compare(nodeValue);
    }
}

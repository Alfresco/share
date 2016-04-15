package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Tests metadata values against configured values using comparators
 *
 * @author mikeh
 */
public class MetadataValueEvaluator extends BaseEvaluator
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
     * Accessor for value to compare against in dot notation format, e.g. "custom.vtiServer"
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

        Object metaValue = getJSONValue(getMetadata(), accessor);
        return this.comparator.compare(metaValue);
    }
}

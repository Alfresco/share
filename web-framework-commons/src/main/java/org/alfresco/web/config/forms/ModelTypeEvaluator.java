package org.alfresco.web.config.forms;

import org.springframework.extensions.config.evaluator.Evaluator;

/**
 * Evaluator to determine whether the condition matches the content
 * model type provided.
 * 
 * @author Gavin Cornwell
 */
public class ModelTypeEvaluator implements Evaluator
{
    /**
     * Determines whether the given condition matches the type provided
     * by the object.
     * 
     * @see org.springframework.extensions.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            if (condition.equalsIgnoreCase((String)obj))
            {
                result = true;
            }
        }

        return result;
    }
}
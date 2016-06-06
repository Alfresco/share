package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

/**
 * Contract supported by all classes that provide dynamic evaluation for a UI element.
 * <p>
 * Evaluators are supplied with a Node instance context object.
 * <p>
 * The evaluator should decide if the precondition is valid based on the appropriate
 * logic and the properties etc. and return the result.
 *
 * @author mikeh
 */
public interface Evaluator
{
    /**
     * The evaluator should decide if the precondition is valid based on the appropriate
     * logic and the state etc. of the given object and return the result.
     *
     * @param jsonObject     The record the evaluation is for
     * @return result of whether the evaluation succeeded or failed.
     */
    public boolean evaluate(JSONObject jsonObject);
}

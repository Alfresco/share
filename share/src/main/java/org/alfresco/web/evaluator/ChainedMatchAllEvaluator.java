
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Calls multiple evaluators in turn until either the last one is called
 * or one of the evaluators returns false. Effectively becomes a logical
 * AND of the participating evaluators.
 *
 * @author mikeh
 */
public class ChainedMatchAllEvaluator extends BaseEvaluator
{
    private ArrayList<Evaluator> evaluators = null;

    /**
     * Evaluators to participate in the evaluation chain
     *
     * @param evaluators ArrayList<Evaluator>
     */
    public void setEvaluators(ArrayList<Evaluator> evaluators)
    {
        this.evaluators = evaluators;
    }

    /**
     * Run through each given evaluator until we either get to the end or one returns false
     *
     *
     * @param jsonObject The object the action is for
     * @return boolean
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        boolean result = true;

        if (evaluators != null)
        {
            ListIterator<Evaluator> evalIter = evaluators.listIterator();

            while (result && evalIter.hasNext())
            {
                BaseEvaluator evaluator = (BaseEvaluator)evalIter.next();
                evaluator.args = this.args;
                evaluator.metadata = this.metadata;
                result = evaluator.negateOutput ^ evaluator.evaluate(jsonObject);
            }
        }

        return result;
    }
}

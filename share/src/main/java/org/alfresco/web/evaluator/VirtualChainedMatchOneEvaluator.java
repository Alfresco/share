package org.alfresco.web.evaluator;

import java.util.ArrayList;
import java.util.ListIterator;

import org.json.simple.JSONObject;

/**
 * If the node isn't in smart folder context the evaluator returns always <code>true</code>.
 * 
 * If node is in smart folder context, multiple evaluators are called in turn until either the last one is called
 * or one of the evaluators returns true. Effectively becomes a logical
 * OR of the participating evaluators.
 * 
 * If no evaluators are configured and the node is not in smart folder context will return always <code>false</code>.
 * 
 * @author sdinuta
 *
 */
public class VirtualChainedMatchOneEvaluator extends VirtualBaseEvaluator
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
     * If the node isn't in smart folder context the evaluator returns always <code>true</code>.
     * 
     * If no evaluators are configured and the node is not in smart folder context will return always <code>false</code>.
     * 
     * If node is in smart folder context run through each given evaluator until we either get to the end or one returns false.
     * 
     * @param jsonObject The object the evaluation is for
     * 
     * @return boolean.
     */
    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if(notInVirtualContext(jsonObject))
        {
            return true;
        }
        boolean result = false;

        if(evaluators!=null){
            ListIterator<Evaluator> evalIter = evaluators.listIterator();

            while (!result && evalIter.hasNext())
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

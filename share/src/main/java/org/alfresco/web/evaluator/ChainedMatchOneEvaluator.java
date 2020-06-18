/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.evaluator;

import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.ListIterator;

/**
 * Calls multiple evaluators in turn until either the last one is called
 * or one of the evaluators returns true. Effectively becomes a logical
 * OR of the participating evaluators.
 *
 * @author mikeh
 */
public class ChainedMatchOneEvaluator extends BaseEvaluator
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
            result = false;
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

package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check for the presence of one or more aspects.
 * 
 * Unlike HasAspectEvaluator this returns true if ANY of the supplied aspects are present
 * 
 * Invert the output of this evaluator to act as a blacklist of aspects which should not be present
 *
 * @author wabson
 */
public class HasAnyAspectEvaluator extends BaseEvaluator
{
    private ArrayList<String> aspects;

    /**
     * Define the list of aspects to check for
     *
     * @param aspects
     */
    public void setAspects(ArrayList<String> aspects)
    {
        this.aspects = aspects;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (aspects.size() == 0)
        {
            return false;
        }

        try
        {
            JSONArray nodeAspects = getNodeAspects(jsonObject);
            if (nodeAspects == null)
            {
                return false;
            }
            else
            {
                for (String aspect : aspects)
                {
                    if (nodeAspects.contains(aspect))
                    {
                        return true;
                    }
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return false;
    }
}

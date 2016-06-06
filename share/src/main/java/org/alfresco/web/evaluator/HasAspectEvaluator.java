package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check for the presence of one or more aspects.
 * 
 * Where more than one aspect is supplied, all aspects must be present.
 *
 * @author mikeh
 */
public class HasAspectEvaluator extends BaseEvaluator
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
                    if (!nodeAspects.contains(aspect))
                    {
                        return false;
                    }
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return true;
    }
}

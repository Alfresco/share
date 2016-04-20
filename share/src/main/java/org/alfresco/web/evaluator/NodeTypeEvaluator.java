package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.DictionaryQuery;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Evaluates whether a node is of a certain type, optionally checking for subtype
 *
 * @author mikeh
 */
public class NodeTypeEvaluator extends BaseEvaluator
{
    private DictionaryQuery dictionary;
    private boolean allowSubtypes = true;
    private ArrayList<String> types;

    /**
     * Dictionary Query bean reference
     * 
     * @param dictionary
     */
    public void setDictionary(DictionaryQuery dictionary)
    {
        this.dictionary = dictionary;
    }

    /**
     * Whether subtypes are allowed or not. Default is that subtypes ARE allowed.
     *
     * @param allowSubtypes
     */
    public void setAllowSubtypes(boolean allowSubtypes)
    {
        this.allowSubtypes = allowSubtypes;
    }

    /**
     * Define the list of types to check for
     *
     * @param types
     */
    public void setTypes(ArrayList<String> types)
    {
        this.types = types;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (types.size() == 0)
        {
            return false;
        }

        String nodeType = getNodeType(jsonObject);

        try
        {
            if (types.contains(nodeType))
            {
                return true;
            }

            if (allowSubtypes && dictionary != null)
            {
                for (String type : types)
                {
                    if (dictionary.isSubType(nodeType, type))
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

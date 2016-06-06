
package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check whether the node lives in a Site container of one of the listed types
 *
 * @author mikeh
 */
public class ContainerTypeEvaluator extends BaseEvaluator
{
    private ArrayList<String> types;

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

        try
        {
            if (!types.contains(getContainerType(jsonObject)))
            {
                return false;
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return true;
    }
}

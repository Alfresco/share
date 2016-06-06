package org.alfresco.web.config.forms;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 * Evaluator that determines whether a given object has a particular node type.
 * 
 * @author Neil McErlean
 */
public class NodeTypeEvaluator extends NodeMetadataBasedEvaluator
{
    protected static final String JSON_TYPE = "type";
    
    private static Log logger = LogFactory.getLog(NodeTypeEvaluator.class);

    @Override
    protected Log getLogger()
    {
        return logger;
    }

    /**
     * This method checks if the specified condition is matched by the node type
     * within the specified jsonResponse String.
     * 
     * @return true if the node type matches the condition, else false.
     */
    @Override
    protected boolean checkJsonAgainstCondition(String condition, String jsonResponseString)
    {
        boolean result = false;
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(jsonResponseString));
            Object typeObj = null;
            if (json.has(JSON_TYPE))
            {
               typeObj = json.get(JSON_TYPE);
            }
            if (typeObj instanceof String)
            {
                String typeString = (String) typeObj;
                result = condition.equals(typeString);
            }
        } 
        catch (JSONException e)
        {
            if (getLogger().isWarnEnabled())
            {
                getLogger().warn("Failed to find node type in JSON response from metadata service: " + e.getMessage());
            }
        }
        return result;
    }
}

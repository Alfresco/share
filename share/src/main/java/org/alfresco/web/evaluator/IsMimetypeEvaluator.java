package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check whether the node's mimetype is within a configured list
 *
 * @author mikeh
 */
public class IsMimetypeEvaluator extends BaseEvaluator
{
    private ArrayList<String> mimetypes;

    /**
     * Define the list of mimetypes for this evaluator
     *
     * @param mimetypes
     */
    public void setMimetypes(ArrayList<String> mimetypes)
    {
        this.mimetypes = mimetypes;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (mimetypes.size() == 0)
        {
            return false;
        }
        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");
            if (node == null)
            {
                return false;
            }
            else
            {
                String mimetype = (String) node.get("mimetype");
                if (mimetype == null || !this.mimetypes.contains(mimetype))
                {
                    return false;
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

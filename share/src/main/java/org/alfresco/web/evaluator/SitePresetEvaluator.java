
package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check whether the node lives in a Site of one of the listed presets
 *
 * @author mikeh
 */
public class SitePresetEvaluator extends BaseEvaluator
{
    private ArrayList<String> presets;

    /**
     * Define the list of presets to check for
     *
     * @param presets
     */
    public void setPresets(ArrayList<String> presets)
    {
        this.presets = presets;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (presets.size() == 0)
        {
            return false;
        }

        try
        {
            if (!presets.contains(getSitePreset(jsonObject)))
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

package org.alfresco.web.evaluator;

import org.alfresco.web.scripts.SyncModeConfig;
import org.json.simple.JSONObject;

/**
 * Evaluates whether the sync mode is the specified value
 *
 * @author David Webster
 */
public class SyncModeEvaluator extends BaseEvaluator
{
    private SyncModeConfig syncMode;
    private String validMode;

    /**
     * Sync Mode bean reference
     * 
     * @param syncMode
     */
    public void setSyncMode(SyncModeConfig syncMode)
    {
        this.syncMode = syncMode;
    }

    /**
     * Define the value to check for
     *
     * @param validMode
     */
    public void setValidMode(String validMode)
    {
        this.validMode = validMode;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        return syncMode.getValue().equals(validMode);
    }
}

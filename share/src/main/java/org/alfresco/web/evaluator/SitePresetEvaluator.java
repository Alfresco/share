/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */

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

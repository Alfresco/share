/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Check whether the node's mimetype is within a configured list
 *
 * @author: mikeh
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

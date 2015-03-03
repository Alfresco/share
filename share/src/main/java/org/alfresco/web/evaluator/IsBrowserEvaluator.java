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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Check a browser userAgent string against a supplied regular expression
 *
 * @author: mikeh
 */
public class IsBrowserEvaluator extends BaseEvaluator
{
    private String regex;

    /**
     * Define the regular expression to test against
     *
     * @param regex
     */
    public void setRegex(String regex)
    {
        this.regex = regex;
    }

    @Override
    public boolean evaluate(JSONObject jsonObject)
    {
        if (regex == null)
        {
            return false;
        }

        try
        {
            String userAgent = getHeader("user-agent");
            if (userAgent != null)
            {
                Pattern p = Pattern.compile(this.regex);
                Matcher m = p.matcher(userAgent);
                return m.find();
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run action evaluator: " + err.getMessage());
        }

        return false;
    }
}

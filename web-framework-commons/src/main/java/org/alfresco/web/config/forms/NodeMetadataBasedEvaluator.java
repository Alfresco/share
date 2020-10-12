/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.config.forms;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.extensions.surf.exception.ConnectorServiceException;

/**
 * This class provides common behaviour for the evaluators which use node-based
 * metadata from a web repo web script as part of their implementation.
 * 
 * @author Neil McErlean
 */
public abstract class NodeMetadataBasedEvaluator extends ServiceBasedEvaluator
{
    protected static final Pattern nodeRefPattern = Pattern.compile(".+://.+/.+");

    /**
     * This method checks if the specified condition is matched by the specified
     * jsonResponse String.
     *
     * @param condition
     * @param jsonResponseString
     * 
     * @return true if there is a match, else false.
     */
    protected abstract boolean checkJsonAgainstCondition(String condition, String jsonResponseString);

    /**
     * Determines whether the given node type matches the path of the given
     * object
     * 
     * @see org.springframework.extensions.config.evaluator.Evaluator#applies(java.lang.Object,
     *      java.lang.String)
     */
    public boolean applies(Object obj, String condition)
    {
        boolean result = false;

        if (obj instanceof String)
        {
            String objAsString = (String) obj;
            // quick test before running slow match for full NodeRef pattern
            if (objAsString.indexOf(':') != -1)
            {
                Matcher m = nodeRefPattern.matcher(objAsString);
                if (m.matches())
                {
                    try
                    {
                        String jsonResponseString = callMetadataService(objAsString);
                        
                        if (jsonResponseString != null)
                        {
                            result = checkJsonAgainstCondition(condition, jsonResponseString);
                        }
                        else if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("Metadata service response appears to be null!");
                        }
                    }
                    catch (NotAuthenticatedException ne)
                    {
                       // ignore the fact that the lookup failed, the form UI component
                       // will handle this and return the appropriate status code.
                    }
                    catch (ConnectorServiceException e)
                    {
                        if (getLogger().isWarnEnabled())
                        {
                            getLogger().warn("Failed to connect to metadata service.", e);
                        }
                    }
                }
            }
        }

        return result;
    }

    protected String callMetadataService(String nodeString) throws ConnectorServiceException
    {
        return callService("/api/metadata?nodeRef=" + nodeString + "&shortQNames=true");
    }
}
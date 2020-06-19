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
package org.alfresco.web.evaluator;

import org.alfresco.error.AlfrescoRuntimeException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;

import java.util.HashMap;

/**
 * Base class for all UI evaluators.
 *
 * @author mikeh
 */
public abstract class BaseEvaluator implements Evaluator
{
    private static final String PORTLET_HOST = "portletHost";

    // optional args from the calling webscript
    protected HashMap<String, String> args = null;
    // metadata from the webscript response
    protected JSONObject metadata = null;
    // negate output flag
    protected boolean negateOutput = false;

    /**
     * Sets optional negateOutput flag which applies when one of the main entry points is used
     */
    public void setNegateOutput(boolean negateOutput)
    {
        this.negateOutput = negateOutput;
    }

    /**
     * Optional entry point from Rhino script. Converts JSON String to a JSONObject
     * and calls the overridable evaluate() method.
     *
     * @param record JSON String representing the record wrapping the node as received from a Rhino script
     * @return boolean indicating evaluator result
     */
    public final boolean evaluate(Object record)
    {
        return evaluate(record, null, null);
    }

    /**
     * Optional entry point from Rhino script. Converts JSON String to a JSONObject
     * and calls the overridable evaluate() method.
     *
     * @param record JSON String representing the record wrapping the node as received from a Rhino script
     * @param metadata JSON String containing metadata which may be relevant to the evaluation
     * @return boolean indicating evaluator result
     */
    public final boolean evaluate(Object record, Object metadata)
    {
        return evaluate(record, metadata, null);
    }

    /**
     * Main entry point from Rhino script. Converts JSON String to a JSONObject
     * and calls the overridable evaluate() method.
     *
     * @param record JSON String or JSONObject as received from a Rhino script
     * @param metadata JSON String or JSONObject as received from a Rhino script
     * @param args URL arguments passed to calling webscript
     * @return boolean indicating evaluator result
     */
    @SuppressWarnings({"WeakerAccess"})
    public final boolean evaluate(Object record, Object metadata, HashMap<String, String> args)
    {
        JSONObject jsonObject;
        this.args = args;

        try
        {
            if (record instanceof JSONObject)
            {
                jsonObject = (JSONObject)record;
            }
            else if (record instanceof String)
            {
                jsonObject = (JSONObject)JSONValue.parseWithException((String)record);
            }
            else
            {
                throw new IllegalArgumentException("Expecting either JSONObject or JSON String for 'record'");
            }
            if (metadata instanceof JSONObject)
            {
                this.metadata = (JSONObject)metadata;
            }
            else if (metadata instanceof String)
            {
                this.metadata = (JSONObject)JSONValue.parseWithException((String)metadata);
            }
            else
            {
                throw new IllegalArgumentException("Expecting either JSONObject or JSON String for 'metadata'");
            }
        }
        catch (ParseException perr)
        {
            throw new AlfrescoRuntimeException("Failed to parse JSON string: " + perr.getMessage());
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Failed to run UI evaluator: " + err.getMessage());
        }
        return (this.negateOutput ^ evaluate(jsonObject));
    }

    /**
     * Evaluator implementations abstract method.
     *
     * @param jsonObject The object the evaluation is for
     * @return boolean indicating evaluator result
     */
    public abstract boolean evaluate(JSONObject jsonObject);

    /**
     * Simple getter for optional webscript args
     *
     * @return HashMap args map (may be null)
     */
    @SuppressWarnings({"UnusedDeclaration"})
    public final HashMap<String, String> getArgs()
    {
        return this.args;
    }

    /**
     * Get webscript argument by name
     *
     * @param name Argument name
     * @return string argument value or null
     */
    public final String getArg(String name)
    {
        if (this.args != null && this.args.containsKey(name))
        {
            return this.args.get(name);
        }
        return null;
    }

    /**
     * Get metadata
     *
     * @return JSONObject metadata
     */
    public final JSONObject getMetadata()
    {
        return this.metadata;
    }

    /**
     * Get request header value
     *
     * @param name Header name to retrieve
     * @return string value or null
     */
    public final String getHeader(String name)
    {
        String header = null;
        if (name != null)
        {
            final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            header = rc.getHeader(name);
        }
        return header;
    }

    /**
     * Get flag indicating portlet or standalone mode
     *
     * @return boolean true for portlet mode, false otherwise
     */
    public final boolean getIsPortlet()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        return rc.getAttribute(PORTLET_HOST) != null;
    }

    /**
     * Retrieves the type for a node
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String containing the node type
     */
    public final String getNodeType(JSONObject jsonObject)
    {
        String type = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                type = (String) node.get("type");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return type;
    }

    /**
     * Retrieve a JSONArray of aspects for a node
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return JSONArray containing aspects on the node
     */
    public final JSONArray getNodeAspects(JSONObject jsonObject)
    {
        JSONArray aspects = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                aspects = (JSONArray) node.get("aspects");
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return aspects;
    }

    /**
     * Retrieve a JSONArray of aspects for a node
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @param propertyName Name of the property to retrieve
     * @return Object property value
     */
    public final Object getProperty(JSONObject jsonObject, String propertyName)
    {
        Object property = null;

        try
        {
            JSONObject node = (JSONObject) jsonObject.get("node");

            if (node != null)
            {
                JSONObject properties = (JSONObject) node.get("properties");
                if (properties != null)
                {
                    property = properties.get(propertyName);
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst running UI evaluator: " + err.getMessage());
        }

        return property;
    }

    /**
     * Get the current user associated with this request
     *
     * @return String userId
     */
    public final String getUserId()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final String userId = rc.getUserId();
        if (userId == null || AuthenticationUtil.isGuest(userId))
        {
            throw new AlfrescoRuntimeException("User ID must exist and cannot be guest.");
        }

        return userId;
    }

    /**
     * Get the site shortName applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String siteId or null
     */
    public final String getSiteId(JSONObject jsonObject)
    {
        String siteId = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject site = (JSONObject) location.get("site");
                if (site != null)
                {
                    siteId = (String) site.get("name");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying siteId from location: " + err.getMessage());
        }

        return siteId;
    }

    /**
     * Get the site preset (e.g. "site-dashboard") applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String site preset or null
     */
    public final String getSitePreset(JSONObject jsonObject)
    {
        String sitePreset = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject site = (JSONObject) location.get("site");
                if (site != null)
                {
                    sitePreset = (String) site.get("preset");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying site preset from location: " + err.getMessage());
        }

        return sitePreset;
    }

    /**
     * Get the container node type (e.g. "cm:folder") applicable to this node (if requested via a site-based page context)
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return String container type or null
     */
    public final String getContainerType(JSONObject jsonObject)
    {
        String containerType = null;

        try
        {
            JSONObject location = (JSONObject) jsonObject.get("location");

            if (location != null)
            {
                JSONObject container = (JSONObject) location.get("container");
                if (container != null)
                {
                    containerType = (String) container.get("type");
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst querying container type from location: " + err.getMessage());
        }

        return containerType;
    }

    /**
     * Get a boolean value indicating whether the node is locked or not
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return True if the node is locked
     */
    public final boolean getIsLocked(JSONObject jsonObject)
    {
        boolean isLocked = false;
        JSONObject node = (JSONObject) jsonObject.get("node");
        if (node != null)
        {
            isLocked = ((Boolean) node.get("isLocked"));
        }
        return isLocked;
    }

    /**
     * Get a boolean value indicating whether the node is a working copy or not
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return True if the node is a working copy
     */
    public final boolean getIsWorkingCopy(JSONObject jsonObject)
    {
        boolean isWorkingCopy = false;
        JSONObject workingCopy = (JSONObject) jsonObject.get("workingCopy");
        if (workingCopy != null)
        {
            isWorkingCopy = ((Boolean) workingCopy.get("isWorkingCopy"));
        }
        return isWorkingCopy;
    }

    /**
     * Checks whether the current user matches that of a given user property
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @param propertyName String containing dotted notation path to value
     * @return True if the property value matches the current user
     */
    public final boolean getMatchesCurrentUser(JSONObject jsonObject, String propertyName)
    {
        try
        {
            JSONObject user = (JSONObject)getProperty(jsonObject, propertyName);
            if (user != null)
            {
                if (user.get("userName").toString().equalsIgnoreCase(getUserId()))
                {
                    return true;
                }
            }
        }
        catch (Exception err)
        {
            throw new AlfrescoRuntimeException("Exception whilst matching current user: " + err.getMessage());
        }
        return false;
    }

    /**
     * Retrieve a JSON value given an accessor string containing dot notation (e.g. "node.isContainer")
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @param accessor String containing dotted notation path to value
     * @return Object value or null
     */
    public final Object getJSONValue(JSONObject jsonObject, String accessor)
    {
        String[] keys = accessor.split("\\.");
        Object record = jsonObject;

        for (String key : keys)
        {
            if (record instanceof JSONObject)
            {
                record = ((JSONObject)record).get(key);
            }
            else if (record instanceof JSONArray)
            {
                record = ((JSONArray)record).get(Integer.parseInt(key));
            }
            else
            {
                return null;
            }
        }
        return record;
    }

    /**
     * Get a boolean value indicating whether the node has binary content
     *
     * @param jsonObject JSONObject containing a "node" object as returned from the ApplicationScriptUtils class.
     * @return True if the node has content
     */
    public final boolean getHasContent(JSONObject jsonObject)
    {
        JSONObject node = (JSONObject) jsonObject.get("node");
        if (node != null)
        {
            return node.get("contentURL") != null;
        }
        return false;
    }
}

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
package org.alfresco.web.cmm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Handles CMM WebScript DELETE requests to perform a number of service related functions:
 * <p>
 * Delete the module configuration for a model.<br>
 * Delete a type or aspect from a model.<br>
 * Delete a form definition for a type/aspect from the model.<br>
 * 
 * @author Kevin Roast
 */
public class CMMServiceDelete extends CMMService
{
    private static final Log logger = LogFactory.getLog(CMMServiceDelete.class);

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("result", DEFAULT_OK_RESULT);
        try
        {
            JSONObject json = getJsonBody(req);
            if (json != null)
            {
                /**
                 * Standard JSON structure for all service requests:
                 * {
                 *    'modelName': "modelid",       // mandatory model name
                 *    'operation': "createModel",   // operation ID if a proxied repository operation is requested
                 *    'data': { ... },              // data blob to be proxied over
                 *    'arguments': { ... }          // name/value pairs to be applied to templated operation URL
                 * }
                 */
                String modelName = (String)json.get("modelName");
                if (modelName == null || modelName.length() == 0)
                {
                    throw new IllegalArgumentException("No 'modelName' was provided");
                }
                else
                {
                    if (json.get("operation") != null)
                    {
                        result.put("result", serviceModelOperation(status, modelName, json));
                    }
                }
            }
            else
            {
                // URL pattern based request
                final Map<String, String> params = req.getServiceMatch().getTemplateVars();
                String modelName = params.get("model");
                String entityId = params.get("entity");
                
                // modelName is mandatory first body param for all requests
                if (modelName != null && modelName.length() != 0 && entityId != null && entityId.length() != 0)
                {
                    String form = params.get("form");
                    if (form != null && form.length() != 0)
                    {
                        // 1. delete form definition for an entity
                        if (logger.isDebugEnabled())
                            logger.debug("Updating extension for model: " + modelName + " due to deleted form definition for entity: " + entityId);
                        
                        // rebuild the extension to remove the specified Form definition
                        buildExtensionModule(status, modelName, new FormOperation(FormOperationEnum.Delete, entityId, form));
                    }
                    else
                    {
                        // 2. delete an entity definition
                        if (logger.isDebugEnabled())
                            logger.debug("Updating extension for model: " + modelName + " due to deleted entity: " + entityId);
                        
                        //
                        // TODO: update dictionary to remove the type/aspect
                        //
                    }
                }
            }
        }
        catch (IOException err)
        {
            errorResponse(status, err.getMessage());
        }
        return result;
    }
}

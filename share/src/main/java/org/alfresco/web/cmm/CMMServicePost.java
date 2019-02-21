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
package org.alfresco.web.cmm;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.json.simple.JSONObject;
import org.springframework.extensions.surf.types.Extension;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.util.XMLUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Handles CMM WebScript POST requests to perform a number of service related functions:
 * <p>
 * Update a model {@link ExtensionModule} to the persisted {@link Extension}.<br>
 * Save a form definition for a type or aspect in a model.<br>
 * 
 * @author Kevin Roast
 */
public class CMMServicePost extends CMMService
{
    private static final Log logger = LogFactory.getLog(CMMServicePost.class);
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> result = new HashMap<>();
        result.put("result", DEFAULT_OK_RESULT);
        try
        {
            JSONObject json = getJsonBody(req);
            if (json == null)
            {
                throw new IllegalArgumentException("No JSON body was provided.");
            }
            else
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
                    else
                    {
                        // Form definition update for an entity in the model
                        String entityId = (String)json.get("entity");
                        if (entityId != null && entityId.length() != 0)
                        {
                            String form = (String)json.get("form");
                            String formOp = (String)json.get("formOperation");
                            // construct extension with new form config added
                            FormOperationEnum op = FormOperationEnum.Update;
                            if (formOp != null && formOp.length() != 0)
                            {
                                op = FormOperationEnum.valueOf(formOp);
                            }
                            buildExtensionModule(status, modelName, new FormOperation(op, entityId, form));
                        }
                        // perhaps an Import operation Form post?
                        else
                        {
                            String formExtension = (String)json.get("forms");
                            if (formExtension != null && formExtension.length() != 0)
                            {
                                // process XML and extract only forms - then regenerate the rest
                                Map<String, String> forms = new HashMap<>();
                                Document doc = XMLUtil.parse(formExtension);
                                //List<Element> formDefNodes = doc.selectNodes("/module/configurations/config[@condition='FormDefinition']/form-definition");
                                List<Element> formDefNodes = new ArrayList<Element>();

                                for (Object obj : doc.selectNodes("/module/configurations/config[@condition='FormDefinition']/form-definition")) {
                                    formDefNodes.add((Element) obj);
                                }


                                if (formDefNodes != null)
                                {
                                    for (Element node: formDefNodes)
                                    {
                                        forms.put(node.attributeValue("id"), node.getText());
                                    }
                                }
                                
                                buildExtensionModule(status, modelName, new FormOperation(FormOperationEnum.Create, forms), false);
                            }
                        }
                    }
                }
            }
        }
        catch (IOException | DocumentException err)
        {
            errorResponse(status, err.getMessage());
        }
        return result;
    }
}
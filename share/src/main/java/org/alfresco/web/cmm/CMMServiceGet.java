package org.alfresco.web.cmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Handles CMM WebScript GET requests to perform a number of service related functions:
 * <p>
 * Retrieve a form definition for a type or aspect in a model.<br>
 * Retrieve the extension module ID for a model.<br>
 * <p>
 * Note that this WebScript does not delegate the execute to the base CMMService impl
 * as it is only responsible for Read operations - not the CrUD lifecycle operations.
 * 
 * @author Kevin Roast
 */
public class CMMServiceGet extends CMMService
{
    private static final Log logger = LogFactory.getLog(CMMServiceGet.class);
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> result = new HashMap<>();
        
        String modelId = req.getServiceMatch().getTemplateVars().get("model");
        if (modelId == null || modelId.length() == 0)
        {
            throw new IllegalArgumentException("model name is mandatory");
        }
        
        String entityId = req.getServiceMatch().getTemplateVars().get("entity");
        if (entityId != null && entityId.length() != 0)
        {
            if (logger.isDebugEnabled())
                logger.debug("Retrieving form definition for model: " + modelId + " and entity: " + entityId);
            
            // get module and retrieve requested form definition
            String formDef = getFormDefinitions(modelId).get(entityId);
            
            if (logger.isDebugEnabled())
                logger.debug("Form definition: " + (formDef != null ? formDef : "null"));
            
            result.put("form", formDef != null ? formDef : "");
        }
        else
        {
            if (req.getServiceMatch().getTemplate().endsWith("/forms"))
            {
                if (logger.isDebugEnabled())
                    logger.debug("Retrieving form states for model: " + modelId);
                
                Map<String, String> defs = getFormDefinitions(modelId);
                result.put("forms", new ArrayList<>(defs.keySet()));
            }
            else
            {
                if (logger.isDebugEnabled())
                    logger.debug("Retrieving module ID for model: " + modelId);
                
                result.put("moduleId", getExtensionModule(modelId) != null ? buildModuleId(modelId) : "");
            }
        }
        return result;
    }
}
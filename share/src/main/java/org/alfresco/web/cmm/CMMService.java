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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletResponse;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.web.scripts.DictionaryQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.surf.ModuleDeploymentService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.ModuleDeployment;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.processor.FTLTemplateProcessor;

/**
 * Base class for CMM WebScript requests to perform a number of service related functions:
 * <p>
 * Each model has associated Share form configuration which is activated or deactivated
 * based on the current API state for that model. The dynamic form configuration allows
 * the user to make immediate use of the model without server restarts. 
 * <p>
 * Each CRUD HTTP method WebScript extends this service and it is responsible for the bulk
 * of the work for operations. It will proxy through API calls to the repo via the given
 * operation ID mapping to a templated repository API URL. The caller is responsible for
 * providing the appropriate bag of arguments to the templated URL and also the data JSON
 * as expected by the repository API. Errors and status codes from the repo API are
 * proxied back to the caller.
 * <p>
 * Besides proxying the API calls, the main purpose of this service is to provide business
 * logic hook points before and after the repository operations for Share. This allows say
 * local Data Dictionary modifications and updates based on the success of a repository API.
 * 
 * @author Kevin Roast
 */
public abstract class CMMService extends DeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(CMMService.class);
    
    /** JSON string constants */
    private static final String JSON_APPEARANCE = "appearance";
    private static final String JSON_LABEL = "label";
    private static final String JSON_STYLECLASS = "styleclass";
    private static final String JSON_STYLE = "style";
    private static final String JSON_MAXLENGTH = "maxlength";
    private static final String JSON_READ_ONLY = "read-only";
    private static final String JSON_HIDDEN = "hidden";
    private static final String JSON_FORCE = "force";
    private static final String JSON_ANY = "any";
    private static final String JSON_FOR_MODE = "for-mode";
    private static final String JSON_CONTROLTYPE = "controltype";
    private static final String JSON_ELEMENTCONFIG = "elementconfig";
    private static final String JSON_ID = "id";
    private static final String JSON_COLUMN = "column";
    private static final String JSON_PSEUDONYM = "pseudonym";
    private static final String JSON_PROPERTIES = "properties";
    private static final String JSON_TITLE = "title";
    private static final String JSON_PREFIXEDNAME = "prefixedName";
    private static final String JSON_ENTRY = "entry";
    private static final String JSON_ACTIVE = "ACTIVE";
    private static final String JSON_STATUS = "status";
    private static final String JSON_ARGUMENTS = "arguments";
    private static final String JSON_DATA = "data";
    private static final String JSON_OPERATION = "operation";
    private static final String JSON_TYPES = "types";
    
    /** template output string constants */
    private static final String TEMPLATE_SET = "set";
    private static final String TEMPLATE_LABEL = "label";
    private static final String TEMPLATE_APPEARANCE = "appearance";
    private static final String TEMPLATE_PASSWORD = "password";
    private static final String TEMPLATE_STYLE = "style";
    private static final String TEMPLATE_STYLECLASS = "styleclass";
    private static final String TEMPLATE_MAXLENGTH = "maxLength";
    private static final String TEMPLATE_READONLY = "readonly";
    private static final String TEMPLATE_FORCE = "force";
    private static final String TEMPLATE_MODE = "mode";
    private static final String TEMPLATE_PARAMS = "params";
    private static final String TEMPLATE_ID = "id";
    private static final String TEMPLATE_FIELDS = "fields";
    private static final String TEMPLATE_SETS = "sets";
    private static final String TEMPLATE_PROPERTIES = "properties";
    private static final String TEMPLATE_TITLE = "title";
    private static final String TEMPLATE_FORM = "form";
    private static final String TEMPLATE_NAME = "name";
    private static final String TEMPLATE_ENTITIES = "entities";
    private static final String TEMPLATE_ASPECTS = "aspects";
    private static final String TEMPLATE_SUBTYPES = "subtypes";
    private static final String TEMPLATE_TYPES = "types";
    private static final String TEMPLATE_MODULE_NAME = "moduleName";
    private static final String TEMPLATE_TEMPLATE = "template";
    
    /** control types */
    private static final String CONTROLTYPE_DEFAULT     = "default";
    private static final String CONTROLTYPE_PASSWORD    = "password";
    private static final String CONTROLTYPE_RICHTEXT    = "richtext";
    private static final String CONTROLTYPE_TEXTAREA    = "textarea";
    private static final String CONTROLTYPE_CONTENT     = "content";
    private static final String CONTROLTYPE_TEXTFIELD   = "textfield";
    private static final String CONTROLTYPE_HIDDEN      = "hidden";
    private static final String CONTROLTYPE_SIZE        = "size";
    private static final String CONTROLTYPE_MIMETYPE    = "mimetype";
    private static final String CONTROLTYPE_TAGGABLE    = "taggable";
    private static final String CONTROLTYPE_CATEGORIES  = "categories";
    
    /** well known DD types */
    private static final String CM_FOLDER   = "cm:folder";
    private static final String CM_CONTENT  = "cm:content";

    /** Prefix used for all CMM related modules - the suffix is the model ID */
    private static final String MODULE_PREFIX = "CMM_";
    
    /** path to the FreeMarker template used to render the module configuration for a model */
    private static final String MODULE_TEMPLATE_PATH = "/org/alfresco/cmm/components/module-configuration.ftl";
    
    /** simple default JSON response for services when result value is proxied from the repository */
    protected static final String DEFAULT_OK_RESULT = "{\"success\":true}";
    
    /** Repository Operations available from client API */
    private static final String OP_DELETE_PROPERTY          = "deleteProperty";
    private static final String OP_EDIT_PROPERTY            = "editProperty";
    private static final String OP_CREATE_PROPERTY          = "createProperty";
    private static final String OP_DELETE_PROPERTY_GROUP    = "deletePropertyGroup";
    private static final String OP_EDIT_PROPERTY_GROUP      = "editPropertyGroup";
    private static final String OP_CREATE_PROPERTY_GROUP    = "createPropertyGroup";
    private static final String OP_DELETE_TYPE              = "deleteType";
    private static final String OP_EDIT_TYPE                = "editType";
    private static final String OP_CREATE_TYPE              = "createType";
    private static final String OP_DELETE_MODEL             = "deleteModel";
    private static final String OP_DEACTIVATE_MODEL         = "deactivateModel";
    private static final String OP_ACTIVATE_MODEL           = "activateModel";
    private static final String OP_EDIT_MODEL               = "editModel";
    private static final String OP_CREATE_MODEL             = "createModel";
    
    /**
     * Mapping of client-side operation name to repository API templated URL
     * The caller is responsible for passing the named arguments to the service in the JSON params. This service
     * will then apply the template arguments to the URL and then proxy over any associated JSON data blob.
     */
    protected static Map<String, String> operationMapping = new HashMap<String, String>() {
        {
            put(OP_CREATE_MODEL,          "/-default-/private/alfresco/versions/1/cmm");
            put(OP_EDIT_MODEL,            "/-default-/private/alfresco/versions/1/cmm/{name}");
            put(OP_ACTIVATE_MODEL,        "/-default-/private/alfresco/versions/1/cmm/{name}?select=status");
            put(OP_DEACTIVATE_MODEL,      "/-default-/private/alfresco/versions/1/cmm/{name}?select=status");
            put(OP_DELETE_MODEL,          "/-default-/private/alfresco/versions/1/cmm/{name}");
            put(OP_CREATE_TYPE,           "/-default-/private/alfresco/versions/1/cmm/{name}/types");
            put(OP_EDIT_TYPE,             "/-default-/private/alfresco/versions/1/cmm/{name}/types/{typeName}");
            put(OP_DELETE_TYPE,           "/-default-/private/alfresco/versions/1/cmm/{name}/types/{typeName}");
            put(OP_CREATE_PROPERTY_GROUP, "/-default-/private/alfresco/versions/1/cmm/{name}/aspects");
            put(OP_EDIT_PROPERTY_GROUP,   "/-default-/private/alfresco/versions/1/cmm/{name}/aspects/{aspectName}");
            put(OP_DELETE_PROPERTY_GROUP, "/-default-/private/alfresco/versions/1/cmm/{name}/aspects/{aspectName}");
            put(OP_CREATE_PROPERTY,       "/-default-/private/alfresco/versions/1/cmm/{name}/{entityClass}/{entityName}?select=props");
            put(OP_EDIT_PROPERTY,         "/-default-/private/alfresco/versions/1/cmm/{name}/{entityClass}/{entityName}?select=props&update={propertyName}");
            put(OP_DELETE_PROPERTY,       "/-default-/private/alfresco/versions/1/cmm/{name}/{entityClass}/{entityName}?select=props&delete={propertyName}");
        }
    };
    
    
    /**
     * <p>A @link ModuleDeploymentService} is required as it is used to refresh the configured module list.</p> 
     */
    protected ModuleDeploymentService moduleDeploymentService;
    
    /**
     * @param moduleDeploymentService       ModuleDeploymentService
     */
    public void setModuleDeploymentService(ModuleDeploymentService moduleDeploymentService)
    {
        this.moduleDeploymentService = moduleDeploymentService;
    }
    
    protected DictionaryQuery dictionary;
    
    /**
     * Dictionary Query bean reference
     * 
     * @param dictionary                    DictionaryQuery
     */
    public void setDictionary(DictionaryQuery dictionary)
    {
        this.dictionary = dictionary;
    }
    
    protected FTLTemplateProcessor templateProcessor;
    
    /**
     * @param templateProcessor             FTLTemplateProcessor
     */
    public void setTemplateProcessor(FTLTemplateProcessor templateProcessor)
    {
        this.templateProcessor = templateProcessor;
    }
    
    public final static Cache CACHE_NEVER = new Cache(new Description.RequiredCache() {
        @Override
        public boolean getNeverCache()
        {
            return true;
        }

        @Override
        public boolean getIsPublic()
        {
            return false;
        }

        @Override
        public boolean getMustRevalidate()
        {
            return true;
        }
    });
    
    
    /**
     * Model operation service call. Provide a proxy through to the given repo API and provides a hook
     * for client business logic pertinent that may be required for each operation.
     * 
     * @param status
     * @param modelName
     * @param json
     * @throws IOException
     */
    protected String serviceModelOperation(Status status, String modelName, JSONObject json) throws IOException
    {
        final String opId = (String)json.get(JSON_OPERATION);
        
        // repository API mapping operation - collect arguments, data blob - http method is as called
        JSONObject data = (JSONObject)json.get(JSON_DATA);
        
        // map operation to URL and apply arguments
        String url = operationMapping.get(opId);
        if (url == null)
        {
            throw new IllegalArgumentException("Specified API operation does not map to a known URL: " + opId);
        }
        final Map<String, String> args = new HashMap<>();
        JSONObject arguments = (JSONObject)json.get(JSON_ARGUMENTS);
        if (arguments != null)
        {
            for (String key: (Set<String>)arguments.keySet())
            {
                args.put(key, URLEncoder.encode((String)arguments.get(key)));
            }
        }
        url = UriUtils.replaceUriTokens(url, args);
        
        if (logger.isDebugEnabled())
            logger.debug("Executing service operation: " + opId + " with URL: " + url + " method: " + this.getDescription().getMethod() +
                         " - using data:\n" + (data != null ? data.toJSONString() : "null"));
        
        // pre operation business logic
        Map<String, String> updatedForms = null;
        Response preResponse = null;
        switch (opId)
        {
            case OP_DELETE_MODEL:
            case OP_DEACTIVATE_MODEL:
            {
                // get model ready to remove it from dictionary if deactive is successful
                JSONObject model = getModel(modelName);
                String prefix = (String) model.get("namespacePrefix");
                preResponse = getConnector().call("/api/dictionary?model=" + URLEncoder.encode(prefix) + ":" + URLEncoder.encode(modelName));
                break;
            }
            
            case OP_EDIT_MODEL:
            {
                // if a model has form definitions, they may need updating to ensure a modified Model Prefix is applied
                // to the widget IDs within the forms - we use the "prefix:field" approach for widget IDs for form elements
                JSONObject model = getModel(modelName);
                String oldPrefix = (String) model.get("namespacePrefix");
                String newPrefix = (String) data.get("namespacePrefix");
                // if the prefix has changed then the IDs of the widgets in the form definitions will now be incorrect
                if (!newPrefix.equals(oldPrefix))
                {
                    ExtensionModule module = getExtensionModule(modelName);
                    if (module != null)
                    {
                        // retrieve existing form definitions from extension configuration
                        updatedForms = getFormDefinitions(module);
                        if (updatedForms.size() != 0)
                        {
                            for (String formId: updatedForms.keySet())
                            {
                                // modify the form JSON string - we want to replace "oldprefix:fieldid" with "newprefix:fieldid" to
                                // ensure the widget IDs in the form will match the expected namespace ID of the custom model
                                String form = updatedForms.get(formId);
                                updatedForms.put(formId, form.replace("\"id\":\"" + oldPrefix + ":", "\"id\":\"" + newPrefix + ":"));
                            }
                        }
                    }
                }
                break;
            }
        }
        
        // prepare proxied JSON body data and make the call
        Response res;
        if (data != null)
        {
            // make the request with the given data payload
            res = getAPIConnector().call(
                url,
                new ConnectorContext(HttpMethod.valueOf(this.getDescription().getMethod())),
                new ByteArrayInputStream(data.toJSONString().getBytes("UTF-8")));
        }
        else
        {
            // no body required for this request
            res = getAPIConnector().call(
                url,
                new ConnectorContext(HttpMethod.valueOf(this.getDescription().getMethod())));
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Response: " + res.getStatus().getCode() + "\n" + res.getResponse());
        
        int statusCode = res.getStatus().getCode();
        if (statusCode >= 200 && statusCode < 300)
        {
            // if we get here successfully, then perform post operation business logic
            switch (opId)
            {
                case OP_ACTIVATE_MODEL:
                {
                    if (logger.isDebugEnabled())
                        logger.debug("ACTIVATE model config id: " + modelName);
                    
                    updateDictionaryForModel(modelName);
                    
                    buildExtensionModule(status, modelName, null, true);
                    
                    break;
                }
                
                case OP_DEACTIVATE_MODEL:
                {
                    if (logger.isDebugEnabled())
                        logger.debug("DEACTIVATE model config id: " + modelName);
                    
                    // update dictionary - remove classes relating to this namespace
                    if (preResponse != null && preResponse.getStatus().getCode() == Status.STATUS_OK)
                    {
                        this.dictionary.updateRemoveClasses(preResponse.getResponse());
                    }
                    else
                    {
                        if (logger.isWarnEnabled())
                            logger.warn("Unable to update Share local Data Dictionary as Repository API call failed.");
                    }
                    
                    buildExtensionModule(status, modelName, null, false);
                    
                    break;
                }
                
                case OP_CREATE_MODEL:
                {
                    // NOTE: no need to update Dictionary - new model begins lifecycle as deactivated
                    break;
                }
                
                case OP_EDIT_MODEL:
                {
                    // NOTE: no need to update Dictionary - only deactivated models can be edited
                    
                    // updating to ensure form definitions are updated after a Model Prefix change 
                    if (updatedForms != null && updatedForms.size() != 0)
                    {
                        buildExtensionModule(status, modelName, new FormOperation(FormOperationEnum.Create, updatedForms), false);
                    }
                    break;
                }
                
                case OP_DELETE_MODEL:
                {
                    if (logger.isDebugEnabled())
                        logger.debug("Deleting extension and form definitions for model: " + modelName);
                    
                    // Delete the model - so delete the entire module definition and related configurations
                    deleteExtensionModule(status, modelName);
                    
                    // NOTE: no need to update Dictionary - only inactive models can be deleted and therefore already processed
                    break;
                }
                
                case OP_CREATE_TYPE:
                case OP_EDIT_TYPE:
                {
                    // update the dictionary is the model is currently active
                    if (isModelActive(getModel(modelName)))
                    {
                        updateDictionaryForModel(modelName);
                        
                        buildExtensionModule(status, modelName, null, true);
                    }
                    
                    break;
                }
                
                case OP_DELETE_TYPE:
                case OP_DELETE_PROPERTY_GROUP:
                {
                    // NOTE: no need to update Dictionary - only inactive models can have types or aspects deleted!
                    break;
                }
                
                case OP_CREATE_PROPERTY_GROUP:
                case OP_EDIT_PROPERTY_GROUP:
                {
                    // update the dictionary is the model is currently active
                    if (isModelActive(getModel(modelName)))
                    {
                        buildExtensionModule(status, modelName, null, true);
                        
                        updateDictionaryForModel(modelName);
                    }
                    
                    break;
                }
                
                case OP_CREATE_PROPERTY:
                case OP_DELETE_PROPERTY:
                {
                    if (isModelActive(getModel(modelName)))
                    {
                        // TODO: could update Dictionary if the granularity of properties are ever used...?
                        
                        buildExtensionModule(status, modelName, null, true);
                    }
                    break;
                }
            }
        }
        status.setCode(statusCode);
        return res.getResponse();
    }
    
    /**
     * Update the Share local Data Dictionary based on the current state of the given model. The model
     * is retrieved and merged into the local data dictionary - adding or updating classes as required.
     * 
     * @param modelName     Name of the model to update dictionary for
     */
    private void updateDictionaryForModel(final String modelName)
    {
        // update dictionary
        if (logger.isDebugEnabled())
            logger.debug("Updating dictionary for model: " + modelName);
        JSONObject model = getModel(modelName);
        String prefix = (String) model.get("namespacePrefix");
        Response res = getConnector().call("/api/dictionary?model=" + URLEncoder.encode(prefix) + ":" + URLEncoder.encode(modelName));
        if (logger.isDebugEnabled())
            logger.debug("Dictionary get response " + res.getStatus().getCode() + "\n" + res.getResponse());
        if (res.getStatus().getCode() == Status.STATUS_OK)
        {
            this.dictionary.updateAddClasses(res.getResponse());
        }
    }
    
    /**
     * Return the JSON object for the meta description of the given model
     * @param modelName Model to retrieve meta for
     * @return JSON meta:
     * {
     *    "author":"Kevin Roast",
     *    "name":"DemoModel",
     *    "description":"a demo model",
     *    "namespaceUri":"http://www.mycompany.com/model/demo/1.0",
     *    "namespacePrefix":"demo",
     *    "status":"DRAFT"
     * }
     */
    protected JSONObject getModel(String modelName)
    {
        Response res = getAPIConnector().call("/-default-/private/alfresco/versions/1/cmm/" + URLEncoder.encode(modelName));
        if (res.getStatus().getCode() == Status.STATUS_OK)
        {
            return ((JSONObject)getJsonBody(res).get(JSON_ENTRY));
        }
        else
        {
            throw new AlfrescoRuntimeException("Unable to retrieve model information: " + modelName + " (" + res.getStatus().getCode() + ")");
        }
    }
    
    /**
     * @return the extension module ID for a given modelName
     */
    protected String buildModuleId(String modelName)
    {
        return MODULE_PREFIX + modelName;
    }
    
    /**
     * @param model     JSON model object
     * @return true if the given model is active, false if deactivated
     */
    private boolean isModelActive(JSONObject model)
    {
        return model.get(JSON_STATUS).equals(JSON_ACTIVE);
    }
    
    protected void buildExtensionModule(Status status, String modelName, FormOperation formOp)
    {
        // is the model active?
        boolean active = isModelActive(getModel(modelName));
        buildExtensionModule(status, modelName, formOp, active);
    }
    
    protected void buildExtensionModule(Status status, String modelName, FormOperation formOp, JSONObject model)
    {
        // is the model active?
        boolean active = isModelActive(model);
        buildExtensionModule(status, modelName, formOp, active);
    }

    /**
     * Construct the Surf Extension Module for a given model.
     * <p>
     * A Freemarker template is used to build the final extension module config from a hiearchy of template objects. See the
     * various TEMPLATE_ constants and module-configuration.ftl for the template model object names and template structure.
     * <p>
     * Each model maps to an extension and associated Share Forms and Share Document Library configuration output. If the model
     * is active then a number of Share Forms may be generated from persisted JSON form layouts. The template model transforms
     * the generic JSON form structure to the esoteric Share Form XML configuration.
     * 
     * @param status    WebScript status object - used to set error codes
     * @param modelName Model name to construct extension config for
     * @param formOp    Optional form operation to apply to current Forms before extension module is generated
     * @param active    Model active/deactive status
     */
    protected void buildExtensionModule(Status status, String modelName, FormOperation formOp, boolean active)
    {
        final String moduleId = buildModuleId(modelName);
        
        // construct the model used to render the module template configuration
        TWrapper model = new TWrapper(8);
        model.put(TEMPLATE_MODULE_NAME, moduleId);
        
        List<Object> typeList = new ArrayList<>();
        model.put(TEMPLATE_TYPES, typeList);
        List<Object> subtypesList = new ArrayList<>();
        model.put(TEMPLATE_SUBTYPES, subtypesList);
        List<Object> aspectsList = new ArrayList<>();
        model.put(TEMPLATE_ASPECTS, aspectsList);
        List<Object> entitiesList = new ArrayList<>();
        model.put(TEMPLATE_ENTITIES, entitiesList);
        
        // retrieve form configuration if present already for this module to update new module definition
        Map<String, String> formDefs = new HashMap<>();
        ExtensionModule module = getExtensionModule(modelName);
        if (module != null)
        {
            // retrieve existing form definitions from extension configuration e.g.
            formDefs = getFormDefinitions(module);
        }
        
        // perform optional form CrUD operation 
        if (formOp != null)
        {
            formOp.perform(formDefs);
        }
        
        // add form definitions to template model map
        for (String entityId : formDefs.keySet())
        {
            TWrapper wrapper = new TWrapper(4);
            wrapper.put(TEMPLATE_NAME, entityId)
                   .put(TEMPLATE_FORM, formDefs.get(entityId));
            entitiesList.add(wrapper);
        }
        
        // if the model is active, we want to generate the Share config for types/aspects/forms
        if (active)
        {
            // get all types and aspects for the model and process them
            Response response = getAPIConnector().call("/-default-/private/alfresco/versions/1/cmm/"+URLEncoder.encode(modelName)+"?select=all");
            if (response.getStatus().getCode() == Status.STATUS_OK)
            {
                JSONObject jsonData = getJsonBody(response);
                
                // process types
                final JSONArray types = (JSONArray)((JSONObject)jsonData.get(JSON_ENTRY)).get(JSON_TYPES);
                
                // walk the types and use form definitions to generate the form config objects
                // and also generate the sub-types list
                Map<String, List<TWrapper>> subtypeMap = new HashMap<>();
                for (final Object t : types)
                {
                    final JSONObject type = (JSONObject)t;
                    String typeName = (String)type.get(JSON_PREFIXEDNAME);
                    
                    // generate form wrapper objects for this type
                    TWrapper formWrappers = processFormWidgets(formDefs, type);
                    
                    // form definition present for this type?
                    if (formWrappers.size() != 0)
                    {
                        // add type wrapper for template output
                        TWrapper typeWrapper = new TWrapper(8);
                        typeWrapper.put(TEMPLATE_NAME, typeName)
                                   .put(TEMPLATE_TITLE, (String)type.get(JSON_TITLE));
                        typeList.add(typeWrapper);
                        
                        // add all form wrapper objects for the type
                        typeWrapper.putAll(formWrappers);
                        
                        // for each type, firstly ensure is subtype of cm:content,
                        // then walk the parent hiearchy and add this type as a subtype of each parent type up to and including cm:content 
                        if (this.dictionary.isSubType(typeName, CM_CONTENT) || this.dictionary.isSubType(typeName, CM_FOLDER))
                        {
                            String parentType = typeName;
                            do
                            {
                                // walk hiearchy to prepare for next loop iteration
                                parentType = this.dictionary.getParent(parentType);
                                
                                List<TWrapper> subtypes = subtypeMap.get(parentType);
                                if (subtypes == null)
                                {
                                    subtypes = new ArrayList<>(4);
                                    subtypeMap.put(parentType, subtypes);
                                }
                                
                                // check for existing - hierachies of types can repeat the same type from other hierachy
                                boolean found = false;
                                for (TWrapper st: subtypes)
                                {
                                    if (st.get(TEMPLATE_NAME).equals(typeName))
                                    {
                                        found = true;
                                        break;
                                    }
                                }
                                
                                // add subtype wrapper for template output
                                if (!found)
                                {
                                    TWrapper subtypeWrapper = new TWrapper(4);
                                    subtypeWrapper.put(TEMPLATE_NAME, typeName)
                                                  .put(TEMPLATE_TITLE, this.dictionary.getTitle(typeName));
                                    subtypes.add(subtypeWrapper);
                                }
                                
                            } while (!(CM_CONTENT.equals(parentType) || CM_FOLDER.equals(parentType)));
                        }
                    }
                }
                // convert map to List for templates - each parent type then has an associated list of sub-type wrappers
                for (final String type : subtypeMap.keySet())
                {
                    TWrapper stypeWrapper = new TWrapper(4);
                    stypeWrapper.put(TEMPLATE_NAME, type)
                                .put(TEMPLATE_SUBTYPES, subtypeMap.get(type));
                    subtypesList.add(stypeWrapper);
                }
                
                // process aspects
                final JSONArray aspects = (JSONArray)((JSONObject)jsonData.get(JSON_ENTRY)).get(TEMPLATE_ASPECTS);
                
                for (final Object a : aspects)
                {
                    final JSONObject aspect = (JSONObject)a;
                    final String aspectName = (String)aspect.get(JSON_PREFIXEDNAME);
                    
                    // generate form wrapper objects for this aspect
                    TWrapper formWrappers = processFormWidgets(formDefs, aspect);
                    
                    // add aspect wrapper for template output
                    TWrapper aspectWrapper = new TWrapper(8);
                    aspectWrapper.put(TEMPLATE_NAME, aspectName)
                                 .put(TEMPLATE_TITLE, (String)aspect.get(JSON_TITLE));
                    aspectsList.add(aspectWrapper);
                    
                    // add all form wrapper objects for the type
                    aspectWrapper.putAll(formWrappers);
                }
            }
            else
            {
                throw new AlfrescoRuntimeException("Unable to retrieve types and aspects for model id: " + modelName);
            }
        }
        
        // render the template to generate the final module configuration and persist it
        Writer out = new StringBuilderWriter(4096);
        try
        {
            this.templateProcessor.process(MODULE_TEMPLATE_PATH, model, out);
            
            if (logger.isDebugEnabled())
                logger.debug("Attempting to save module config:\r\n" + out.toString());
            
            if (module == null)
            {
                this.moduleDeploymentService.addModuleToExtension(out.toString());
            }
            else
            {
                this.moduleDeploymentService.updateModuleToExtension(out.toString());
            }
            
            if (logger.isDebugEnabled())
                logger.debug("addModuleToExtension() completed.");
        }
        catch (WebScriptException | DocumentException | ModelObjectPersisterException err)
        {
            // template error - probably developer exception so report in log
            logger.error("Failed to execute template to construct module configuration.", err);
            errorResponse(status, err.getMessage());
        }
    }

    /**
     * Read, process and transform the JSON entity that represents the generic Aikau Form widget tree.
     * The elements are nested within panels with varying numbers of column. Each widget within the column
     * has a number of configuration parameters.
     * <p>
     * Consume the JSON entity and transform the generic tree into a template model for rendering Share Forms
     * configuration for properties and rendering sets of associated fields.
     * <p>
     * See module-configuration.ftl
     * 
     * @param forms     List of current Form state
     * @param entity    JSON object containing Form widget hiearchy
     * 
     * @return Template wrapper objects containing the hierarchical model ready for template rendering
     */
    protected TWrapper processFormWidgets(Map<String, String> forms, JSONObject entity)
    {
        TWrapper formPropertyWrappers = new TWrapper(8);
        
        String entityName = (String)entity.get(TEMPLATE_NAME);
        
        String formDef = forms.get(entityName);
        if (formDef != null)
        {
            // form definition present for this type - transform it into Share Forms Runtime configuration
            try
            {
                Object o = new JSONParser().parse(formDef);
                if (o instanceof JSONArray)
                {
                    JSONArray formElements = (JSONArray)o;
                    
                    if (formElements.size() != 0)
                    {
                        // construct the wrapper collections to hold our properties, sets and field wrappers
                        List<TWrapper> properties = new ArrayList<>();
                        formPropertyWrappers.put(TEMPLATE_PROPERTIES, properties);
                        List<TWrapper> sets = new ArrayList<>();
                        formPropertyWrappers.put(TEMPLATE_SETS, sets);
                        List<TWrapper> fields = new ArrayList<>();
                        formPropertyWrappers.put(TEMPLATE_FIELDS, fields);
                        // used to ensure a single Set of fields i.e. one per property id
                        Map<String, TWrapper> fieldMap = new HashMap<>();
                        
                        // process well known component names and output wrappers
                        for (Object item: formElements)
                        {
                            // avoid garbage - there should not be any arrays etc. at root
                            if (!(item instanceof JSONObject))
                            {
                                throw new IllegalStateException("Unexpected item in form structure: " + formDef);
                            }
                            
                            // prepare state - set by lookup table against the various column layout options
                            int numCols = 0;
                            String columnSetTemplate = null;
                            final String name = (String) ((JSONObject)item).get(JSON_PSEUDONYM);
                            switch (name)
                            {
                                case "cmm/editor/layout/1cols":
                                {
                                    numCols = 1;
                                    break;
                                }
                                case "cmm/editor/layout/2cols":
                                {
                                    numCols = 2;
                                    columnSetTemplate = "/org/alfresco/components/form/2-column-set.ftl";
                                    break;
                                }
                                case "cmm/editor/layout/2colswideleft":
                                {
                                    numCols = 2;
                                    columnSetTemplate = "/org/alfresco/components/form/2-column-wide-left-set.ftl";
                                    break;
                                }
                                case "cmm/editor/layout/3cols":
                                {
                                    numCols = 3;
                                    columnSetTemplate = "/org/alfresco/components/form/3-column-set.ftl";
                                    break;
                                }
                            }
                            
                            if (numCols != 0)
                            {
                                // process properties containing within the column child object
                                List<TWrapper> colProperties = new ArrayList<>();
                                JSONArray column = (JSONArray) ((JSONObject)item).get(JSON_COLUMN);
                                if (column != null)
                                {
                                    // process widget list within each column - form fields automatically wrap
                                    // at the appropriate column index when rendered by the Forms Runtime template
                                    for (Object w : column)
                                    {
                                        // process widget list - wraps automatically at column index
                                        JSONObject widget = ((JSONObject)w);
                                        String pseudonym = (String) widget.get(JSON_PSEUDONYM);
                                        String id = (String) widget.get(JSON_ID);
                                        
                                        if (logger.isDebugEnabled())
                                            logger.debug("Processing widget: " + id + " of type: " + pseudonym);
                                        
                                        // generate a template wrapper for the property widget Form config
                                        TWrapper controlProperties = new TWrapper(4).put(TEMPLATE_NAME, id);
                                        colProperties.add(controlProperties);
                                        
                                        final JSONObject config = (JSONObject) widget.get(JSON_ELEMENTCONFIG);
                                        if (config != null)
                                        {
                                            if (logger.isDebugEnabled())
                                                logger.debug("Found 'elementconfig' for widget - processing...");
                                            
                                            // generate wrappers for control params and field properties
                                            Map<String, Object> controlParams = new HashMap<>(4);
                                            TWrapper fieldWrapper = new TWrapper(4).put(TEMPLATE_ID, id)
                                                    .put(TEMPLATE_PARAMS, controlParams);
                                            fieldMap.put(id, fieldWrapper);
                                            
                                            // map element config to Forms Config values
                                            // this is fiddly - the simple list of properties is remapped to attributes on
                                            // both the control property and on the associated field mapping for it
                                            String controlType = (String) config.get(JSON_CONTROLTYPE);
                                            String mode = (String) config.get(JSON_FOR_MODE);
                                            if (mode != null && !mode.equals(JSON_ANY)) controlProperties.put(TEMPLATE_MODE, mode);
                                            // deal with annoying checkbox = string when not used, but boolean when clicked nonsense
                                            if (config.get(JSON_FORCE) instanceof Boolean)
                                            {
                                                Boolean force = (Boolean) config.get(JSON_FORCE);
                                                if (Boolean.TRUE == force) controlProperties.put(TEMPLATE_FORCE, true);
                                            }
                                            if (config.get(JSON_HIDDEN) instanceof Boolean)
                                            {
                                                Boolean hidden = (Boolean) config.get(JSON_HIDDEN);
                                                if (Boolean.TRUE == hidden) controlType = CONTROLTYPE_HIDDEN;
                                            }
                                            if (config.get(JSON_READ_ONLY) instanceof Boolean)
                                            {
                                                Boolean readOnly = (Boolean) config.get(JSON_READ_ONLY);
                                                if (Boolean.TRUE == readOnly) fieldWrapper.put(TEMPLATE_READONLY, true);
                                            }
                                            Number maxLength = (Number) config.get(JSON_MAXLENGTH);
                                            if (maxLength != null) controlParams.put(TEMPLATE_MAXLENGTH, maxLength);
                                            String style = (String) config.get(JSON_STYLE);
                                            if (style != null && style.length() != 0) controlParams.put(TEMPLATE_STYLE, style);
                                            String styleClass = (String) config.get(JSON_STYLECLASS);
                                            if (styleClass != null && styleClass.length() != 0) controlParams.put(TEMPLATE_STYLECLASS, styleClass);
                                            
                                            // control type for field wrapper - each control type maps to a wrapper template and params as per Share Forms config
                                            String template = null;
                                            if (controlType != null)
                                            {
                                                switch (controlType)
                                                {
                                                    case CONTROLTYPE_TEXTFIELD:
                                                        template = "/org/alfresco/components/form/controls/textfield.ftl";
                                                        break;
                                                    case CONTROLTYPE_TEXTAREA:
                                                        template = "/org/alfresco/components/form/controls/textarea.ftl";
                                                        break;
                                                    case CONTROLTYPE_CONTENT:
                                                        template = "/org/alfresco/components/form/controls/content.ftl";
                                                        break;
                                                    case CONTROLTYPE_RICHTEXT:
                                                        template = "/org/alfresco/components/form/controls/richtext.ftl";
                                                        break;
                                                    case CONTROLTYPE_PASSWORD:
                                                        template = "/org/alfresco/components/form/controls/textfield.ftl";
                                                        controlParams.put(TEMPLATE_PASSWORD, "true");
                                                        break;
                                                    case CONTROLTYPE_HIDDEN:
                                                        template = "/org/alfresco/components/form/controls/hidden.ftl";
                                                        break;
                                                    case CONTROLTYPE_SIZE:
                                                        template = "/org/alfresco/components/form/controls/size.ftl";
                                                        break;
                                                    case CONTROLTYPE_MIMETYPE:
                                                        template = "/org/alfresco/components/form/controls/mimetype.ftl";
                                                        break;
                                                    case CONTROLTYPE_TAGGABLE:
                                                        controlParams.put("compactMode", "true");
                                                        controlParams.put("params", "aspect=cm:taggable");
                                                        controlParams.put("createNewItemUri", "/api/tag/workspace/SpacesStore");
                                                        controlParams.put("createNewItemIcon", "tag");
                                                        break;
                                                    case CONTROLTYPE_CATEGORIES:
                                                        controlParams.put("compactMode", "true");
                                                        break;
                                                    case CONTROLTYPE_DEFAULT:
                                                        break;
                                                    default:
                                                        if (logger.isDebugEnabled())
                                                            logger.debug("WARNING: unknown control type for template mapping: " + controlType);
                                                }
                                                if (template != null)
                                                {
                                                    fieldWrapper.put(TEMPLATE_TEMPLATE, template);
                                                    if (logger.isDebugEnabled())
                                                        logger.debug("Widget control template: " + template);
                                                }
                                            }
                                        }
                                    }
                                }
                                // output a layout set - if number of columns > 1 then output a set template to render columns
                                // Example:
                                // <set template="/org/alfresco/components/form/2-column-set.ftl" appearance="title" label-id="CMM Reference Model" id="refmodel"/>
                                // also see web-framework-commons/.../form.lib.ftl
                                final JSONObject config = (JSONObject) ((JSONObject)item).get(JSON_ELEMENTCONFIG);
                                String panelLabel = (String) config.get(JSON_LABEL);
                                boolean hasLabel = (panelLabel != null && panelLabel.length() != 0);
                                final String setId = entity.get(JSON_PREFIXEDNAME) + "_cmm_set" + sets.size();
                                TWrapper setWrapper = new TWrapper(8);
                                setWrapper.put(TEMPLATE_APPEARANCE, hasLabel ? config.get(JSON_APPEARANCE) : "whitespace")
                                          .put(TEMPLATE_ID, setId);
                                if (numCols > 1) setWrapper.put(TEMPLATE_TEMPLATE, columnSetTemplate);
                                if (hasLabel) setWrapper.put(TEMPLATE_LABEL, config.get(JSON_LABEL));
                                sets.add(setWrapper);
                                
                                // bind properties via fields to the column set
                                // Example:
                                // <field set="refmodel" id="cmm:simple_string" />
                                for (TWrapper property: colProperties)
                                {
                                    String id = (String)property.get(TEMPLATE_NAME);
                                    TWrapper fieldWrapper = fieldMap.get(id);
                                    if (fieldWrapper == null)
                                    {
                                        fieldWrapper = new TWrapper(4).put(TEMPLATE_ID, id);
                                        fieldMap.put(id, fieldWrapper);
                                    }
                                    fieldWrapper.put(TEMPLATE_SET, setId);
                                    if (logger.isDebugEnabled())
                                        logger.debug("Field mapping of: " + id + " mapped to set:" + setId);
                                }
                                
                                // add all the properties gathered for this column set
                                properties.addAll(colProperties);
                                
                            } // end num cols != check
                        } // end form elements processing loop
                        
                        // add all fields from the map to the list structure used by the template
                        fields.addAll(fieldMap.values());
                    }
                }
            }
            catch (ParseException e)
            {
                logger.warn("Unable to parse Form definition for entity: " + entityName + "\n" + formDef + "\n" + e.getMessage());
            }
        }
        return formPropertyWrappers;
    }
    
    /**
     * Delete extension module for a given model
     * 
     * @param status    WebScript status object - used to set error codes
     * @param modelName Model name to delete extension config for
     */
    protected void deleteExtensionModule(Status status, String modelName)
    {
        if (logger.isDebugEnabled())
            logger.debug("Attempting to delete module: " + buildModuleId(modelName));
        
        try
        {
            this.moduleDeploymentService.deleteModuleFromExtension(buildModuleId(modelName));
        }
        catch (DocumentException | ModelObjectPersisterException err)
        {
            // template error - probably developer exception so report in log
            logger.error("Failed to execute template to construct module configuration.", err);
            errorResponse(status, err.getMessage());
        }
        
        if (logger.isDebugEnabled())
            logger.debug("deleteModuleFromExtension() completed.");
    }
    
    /**
     * @param modelName Model name to get extension module for
     * @return ExtensionModule
     */
    protected ExtensionModule getExtensionModule(final String modelName)
    {
        final String moduleId = buildModuleId(modelName);
        ExtensionModule module = null;
        for (ModuleDeployment m : this.moduleDeploymentService.getDeployedModules())
        {
            if (moduleId.equals(m.getId()))
            {
                module = m.getExtensionModule();
                if (logger.isDebugEnabled())
                    logger.debug("Found existing module for ID: " + moduleId);
            }
        }
        if (module == null)
        {
            // no module yet - lazy create on module save
            if (logger.isDebugEnabled())
                logger.debug("No module found for ID: " + moduleId);
        }
        return module;
    }
    
    /**
     * @param modelName Model to get the Form Definitions map for
     * @return the Form Definitions map for a given model
     */
    protected Map<String, String> getFormDefinitions(String modelName)
    {
        return getFormDefinitions(getExtensionModule(modelName));
    }
    
    protected Map<String, String> getFormDefinitions(ExtensionModule module)
    {
        Map<String, String> forms = new HashMap<>();
        if (module != null)
        {
            List<Element> configs = module.getConfigurations();
            for (Element config: configs)
            {
                for (Element form : (List<Element>)config.selectNodes("config/form-definition"))
                {
                    String formId = form.attributeValue(JSON_ID);
                    String formJSON = form.getText();
                    forms.put(formId, formJSON);
                }
            }
        }
        return forms;
    }
    
    protected Connector getConnector()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        try
        {
            return rc.getServiceRegistry().getConnectorService().getConnector("alfresco", rc.getUserId(), ServletUtil.getSession());
        }
        catch (ConnectorServiceException e)
        {
            throw new AlfrescoRuntimeException("Connector exception.", e);
        }
    }
    
    protected Connector getAPIConnector()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        try
        {
            return rc.getServiceRegistry().getConnectorService().getConnector("alfresco-api", rc.getUserId(), ServletUtil.getSession());
        }
        catch (ConnectorServiceException e)
        {
            throw new AlfrescoRuntimeException("Connector exception.", e);
        }
    }
    
    protected JSONObject getJsonBody(final WebScriptRequest req)
    {
        try
        {
            JSONObject jsonData = null;
            final String content = req.getContent().getContent();
            if (content != null && content.length() != 0)
            {
                Object o = new JSONParser().parse(content);
                if (o instanceof JSONObject)
                {
                    jsonData = (JSONObject) o;
                }
            }
            return jsonData;
        }
        catch (ParseException | IOException e)
        {
            throw new AlfrescoRuntimeException("Failed to retrieve or parse JSON body.", e);
        }
    }
    
    protected JSONObject getJsonBody(final Response res)
    {
        try
        {
            JSONObject jsonData = null;
            final String content = res.getResponse();
            if (content != null && content.length() != 0)
            {
                Object o = new JSONParser().parse(content);
                if (o instanceof JSONObject)
                {
                    jsonData = (JSONObject) o;
                }
            }
            return jsonData;
        }
        catch (ParseException e)
        {
            throw new AlfrescoRuntimeException("Failed to retrieve or parse JSON body.", e);
        }
    }
    
    protected void errorResponse(Status status, String msg)
    {
        status.setCode(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        status.setMessage(msg);
        status.setRedirect(true);
    }
    
    
    /**
     * Enum that represents the operations that can be performed on a Form definition for an entity
     */
    enum FormOperationEnum
    {
        Create, Update, Delete
    }
    
    
    /**
     * Wrapper class that encapsulates a CRUD operation for a Form definition
     */
    class FormOperation
    {
        private final FormOperationEnum op;
        private final String entityId;
        private final String form;
        private final Map<String, String> forms;
        
        FormOperation(FormOperationEnum op, String entityId, String form)
        {
            this.op = op;
            if (entityId == null || entityId.length() == 0)
            {
                throw new IllegalArgumentException("EntityID is mandatory.");
            }
            this.entityId = entityId;
            this.form = form;
            this.forms = null;
        }
        
        FormOperation(FormOperationEnum op, Map<String, String> forms)
        {
            this.op = op;
            if (forms == null)
            {
                throw new IllegalArgumentException("Forms map is mandatory.");
            }
            this.entityId = null;
            this.form = null;
            this.forms = forms;
        }
        
        /**
         * Perform the given operation on the given forms map onto the given output list 
         * @param forms     Map of entity Ids to forms defs
         */
        void perform(Map<String, String> forms)
        {
            switch (this.op)
            {
                case Create:
                {
                    forms.putAll(this.forms);
                    break;
                }
                case Update:
                {
                    forms.put(this.entityId, this.form);
                    break;
                }
                case Delete:
                {
                    forms.remove(this.entityId);
                    break;
                }
            }
        }
    }
    
    
    /**
     * Simple wrapper class for a template Map object - to avoid verbose Map generics code.
     */
    public static class TWrapper extends HashMap<String, Object> implements Map<String, Object>
    {
        public TWrapper(int size)
        {
            super(size);
        }
        
        public TWrapper put(String key, Object value)
        {
            super.put(key, value);
            return this;
        }
        
        public TWrapper putAll(Object... args)
        {
            for (int i=0; i<args.length; i+=2)
            {
                super.put((String)args[i], args[i+1]);
            }
            return this;
        }
    }
}
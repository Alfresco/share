/*
 * #%L
 * Alfresco Web Framework common libraries
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
package org.alfresco.web.scripts.forms;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.alfresco.web.config.forms.ConstraintHandlerDefinition;
import org.alfresco.web.config.forms.ConstraintHandlersConfigElement;
import org.alfresco.web.config.forms.Control;
import org.alfresco.web.config.forms.ControlParam;
import org.alfresco.web.config.forms.DefaultControlsConfigElement;
import org.alfresco.web.config.forms.FormConfigElement;
import org.alfresco.web.config.forms.FormField;
import org.alfresco.web.config.forms.FormSet;
import org.alfresco.web.config.forms.FormsConfigElement;
import org.alfresco.web.config.forms.Mode;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.AbstractMessageHelper;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.ConfigModel;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.HttpMethod;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.json.JSONWriter;
import org.springframework.util.StringUtils;

/**
 * Form UI Component web script implementation.
 * 
 * Requests the form definition from the server, combines that with the form
 * configuration for the item being requested resulting in the form UI model
 * which gets passed to the FreeMarker engine for rendering.
 * 
 * NOTE: The general approach to naming in this class is as follows:
 * - processXYZ: Logic
 * - discoverXYZ: Searching context for appropriate value
 * - generateXYZ: Creating model representations
 * - retrieveXYZ: Fetches data from another service
 * 
 * @author Gavin Cornwell
 */
public class FormUIGet extends DeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(FormUIGet.class);
    
    protected static final String PROPERTY = "property";
    protected static final String ASSOCIATION = "association";
    protected static final String PROP_PREFIX = "prop:";
    protected static final String FIELD = "field";
    protected static final String SET = "set";
    protected static final String ASSOC_PREFIX = "assoc:";
    protected static final String OLD_DATA_TYPE_PREFIX = "d:";
    protected static final String ENDPOINT_ID = "alfresco";
    protected static final String ALFRESCO_PROXY = "/proxy/alfresco";
    protected static final String CM_NAME_PROP = "prop_cm_name";
    protected static final String MSG_DEFAULT_SET_LABEL = "form.default.set.label";
    protected static final String MSG_DEFAULT_FORM_ERROR = "form.error";
    protected static final String INDENT = "   ";
    protected static final String DELIMITER = "#alf#";
    
    protected static final String SUBMIT_TYPE_MULTIPART = "multipart";
    protected static final String SUBMIT_TYPE_JSON = "json";
    protected static final String SUBMIT_TYPE_URL = "urlencoded";
    
    protected static final String ENCTYPE_MULTIPART = "multipart/form-data";
    protected static final String ENCTYPE_JSON = "application/json";
    protected static final String ENCTYPE_URL = "application/x-www-form-urlencoded";
    
    protected static final String DEFAULT_MODE = "edit";
    protected static final String DEFAULT_SUBMIT_TYPE = SUBMIT_TYPE_MULTIPART;
    protected static final String DEFAULT_METHOD = "post";
    protected static final String DEFAULT_FIELD_TYPE = "text";
    protected static final String DEFAULT_CONSTRAINT_EVENT = "blur";
    
    protected static final String CONFIG_FORMS = "forms";
    
    protected static final String PARAM_ITEM_KIND = "itemKind";
    protected static final String PARAM_ITEM_ID = "itemId";
    protected static final String PARAM_FORM_ID = "formId";
    protected static final String PARAM_SUBMIT_TYPE = "submitType";
    protected static final String PARAM_SUBMISSION_URL = "submissionUrl";
    protected static final String PARAM_JS = "js";
    protected static final String PARAM_ERROR_KEY = "err";
    
    protected static final String CONSTRAINT_MANDATORY = "MANDATORY";
    protected static final String CONSTRAINT_LIST = "LIST";
    protected static final String CONSTRAINT_LENGTH = "LENGTH";
    protected static final String CONSTRAINT_NUMBER = "NUMBER";
    protected static final String CONSTRAINT_MINMAX = "MINMAX";
    protected static final String CONSTRAINT_REGEX = "REGEX";
    protected static final String CONSTRAINT_NODE_HANDLER = "Alfresco.forms.validation.nodeName";
    protected static final String CONSTRAINT_FILE_NAME_HANDLER = "Alfresco.forms.validation.fileName";
    
    protected static final String CONSTRAINT_MSG_LENGTH = "form.field.constraint.length";
    protected static final String CONSTRAINT_MSG_MINMAX = "form.field.constraint.minmax";
    protected static final String CONSTRAINT_MSG_NUMBER = "form.field.constraint.number";
    
    protected static final String CONTROL_SELECT_MANY = "/org/alfresco/components/form/controls/selectmany.ftl";
    protected static final String CONTROL_SELECT_ONE = "/org/alfresco/components/form/controls/selectone.ftl";
    protected static final String CONTROL_PARAM_OPTIONS = "options";
    protected static final String CONTROL_PARAM_OPTION_SEPARATOR = "optionSeparator";
    
    protected static final String MODEL_DATA = "data";
    protected static final String MODEL_DEFINITION = "definition";
    protected static final String MODEL_FIELDS = "fields";
    protected static final String MODEL_FORM_DATA = "formData";
    protected static final String MODEL_FORCE = "force";
    protected static final String MODEL_MESSAGE = "message";
    protected static final String MODEL_PROTECTED_FIELD = "protectedField";
    protected static final String MODEL_REPEATING = "repeating";
    protected static final String MODEL_DEFAULT_VALUE = "defaultValue";
    protected static final String MODEL_FORM = "form";
    protected static final String MODEL_ERROR = "error";
    protected static final String MODEL_NAME = "name";
    protected static final String MODEL_MODE = "mode";
    protected static final String MODEL_METHOD = "method";
    protected static final String MODEL_ENCTYPE = "enctype";
    protected static final String MODEL_SUBMISSION_URL = "submissionUrl";
    protected static final String MODEL_SHOW_CANCEL_BUTTON = "showCancelButton";
    protected static final String MODEL_SHOW_RESET_BUTTON = "showResetButton";
    protected static final String MODEL_SHOW_SUBMIT_BUTTON = "showSubmitButton";
    protected static final String MODEL_SHOW_CAPTION = "showCaption";
    protected static final String MODEL_DESTINATION = "destination";
    protected static final String MODEL_REDIRECT = "redirect";
    protected static final String MODEL_ARGUMENTS = "arguments";
    protected static final String MODEL_STRUCTURE = "structure";
    protected static final String MODEL_CONSTRAINTS = "constraints";
    protected static final String MODEL_VIEW_TEMPLATE = "viewTemplate";
    protected static final String MODEL_EDIT_TEMPLATE = "editTemplate";
    protected static final String MODEL_CREATE_TEMPLATE = "createTemplate";
    protected static final String MODEL_TYPE = "type";
    protected static final String MODEL_LABEL = "label";
    protected static final String MODEL_DESCRIPTION = "description";
    protected static final String MODEL_MANDATORY = "mandatory";
    protected static final String MODEL_DATA_TYPE = "dataType";
    protected static final String MODEL_DATA_TYPE_PARAMETERS = "dataTypeParameters";
    protected static final String MODEL_DATA_KEY_NAME = "dataKeyName";
    protected static final String MODEL_ENDPOINT_TYPE = "endpointType";
    protected static final String MODEL_ENDPOINT_MANDATORY = "endpointMandatory";
    protected static final String MODEL_ENDPOINT_MANY = "endpointMany";
    protected static final String MODEL_ENDPOINT_DIRECTION = "endpointDirection";
    protected static final String MODEL_JAVASCRIPT = "javascript";
    protected static final String MODEL_CAPABILITIES = "capabilities";
    protected static final String MODEL_PARAMETERS = "parameters";
    protected static final String MODEL_MAX_LENGTH = "maxLength";
    protected static final String MODEL_GROUP = "group";
    protected static final String MODEL_INDEX_TOKENISATION_MODE = "indexTokenisationMode";
    
    private static final String TYPE_INT ="int";
    private static final String TYPE_LONG ="long";
    private static final String TYPE_DOUBLE ="double";
    private static final String TYPE_FLOAT ="float";
     
    protected ConfigService configService;
    
    private MessageHelper messageHelper = null;
    
    /**
     * Sets the ConfigService instance
     * 
     * @param configService ConfigService
     */
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }

    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = null;
        
        String itemKind = getParameter(req, PARAM_ITEM_KIND);
        String itemId = getParameter(req, PARAM_ITEM_ID);
         
        if (logger.isDebugEnabled())
        {
            logger.debug(PARAM_ITEM_KIND + " = " + itemKind);
            logger.debug(PARAM_ITEM_ID + " = " + itemId);
        }
        
        if (itemKind != null && itemId != null && itemKind.length() > 0 && itemId.length() > 0)
        {
            model = generateModel(itemKind, itemId, req, status, cache);
        }
        else
        {
            // an item kind and id have not been provided so return a model
            // with a 'form' entry but set to null, this prevents FreeMarker
            // adding a default 'form' taglib object to the model.
            model = new HashMap<String, Object>(1);
            model.put(MODEL_FORM, null);
        }
        
        return model;
    }
    
    /**
     * Generates the model to send to the FreeMarker engine.
     * 
     * @param itemKind The form itemKind
     * @param itemId The form itemId
     * @param request The WebScriptRequest
     * @param status The response status
     * @param cache Cache control
     * @return Map
     */
    protected Map<String, Object> generateModel(String itemKind, String itemId, 
                WebScriptRequest request, Status status, Cache cache)
    {
        Map<String, Object> model = null;
        
        // get mode and optional formId
        String modeParam = getParameter(request, MODEL_MODE, DEFAULT_MODE);
        String formId = getParameter(request, PARAM_FORM_ID);
        Mode mode = Mode.modeFromString(modeParam);
        
        if (logger.isDebugEnabled())
            logger.debug("Showing " + mode + " form (id=" + formId + ") for item: [" + itemKind + "]" + itemId);
        
        // get the form configuration and list of fields that are visible (if any)
        FormConfigElement formConfig = getFormConfig(itemId, formId);
        List<String> visibleFields = getVisibleFields(mode, formConfig);
        
        // get the form definition from the form service
        Response formSvcResponse = retrieveFormDefinition(itemKind, itemId, visibleFields, formConfig);
        if (formSvcResponse.getStatus().getCode() == Status.STATUS_OK)
        {
            model = generateFormModel(request, mode, formSvcResponse, formConfig);
        }
        else if (formSvcResponse.getStatus().getCode() == Status.STATUS_UNAUTHORIZED)
        {
            // set status to 401 and return null model
            status.setCode(Status.STATUS_UNAUTHORIZED);
            status.setRedirect(true);
        }
        else
        {
            String errorKey = getParameter(request, PARAM_ERROR_KEY);
            model = generateErrorModel(formSvcResponse, errorKey);
        }
        
        return model;
    }
    
    /**
     * Returns the named parameter.
     * 
     * @param req The WebScriptRequest
     * @param name The name of the parameter to find
     * @return The value of the parameter or null if not found
     */
    protected String getParameter(WebScriptRequest req, String name)
    {
        return getParameter(req, name, null);
    }
    
    /**
     * Returns the named parameter, returning the given default 
     * value if the parameter can not be found.
     * 
     * @param req The WebScriptRequest
     * @param name The name of the parameter to find
     * @param defaultValue The default value
     * @return The value of the parameter or the default value if the 
     *         parameter is not found
     */
    protected String getParameter(WebScriptRequest req, String name, String defaultValue)
    {
        // get the value from the webscript parameters, this should include all context
        // properties as well as all the mapped query string parameters
        String value = req.getParameter(name);
        
        // if the value is still null or empty use default value, if one
        if ((value == null || value.length() == 0) && defaultValue != null)
        {
            value = defaultValue;
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Returning \"" + value + "\" from getParameter for \"" + name + "\"");
        
        return value; 
    }
    
    /**
     * Returns the form configuration for the given item id and optional form id.
     * 
     * @param itemId The form itemId
     * @param formId The id of the form to lookup
     * @return The FormConfigElement object or null if no configuration is found
     */
    protected FormConfigElement getFormConfig(String itemId, String formId)
    {
        FormConfigElement formConfig = null;
        FormsConfigElement formsConfig = null;
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        ConfigModel extendedTemplateConfigModel = requestContext.getExtendedTemplateConfigModel(null);
        
        if(extendedTemplateConfigModel != null) {
        	@SuppressWarnings("unchecked")
	        Map<String, ConfigElement> configs = (Map<String, ConfigElement>) extendedTemplateConfigModel.getScoped().get(itemId);
	        formsConfig = (FormsConfigElement) configs.get(CONFIG_FORMS);
        }
        
        if(formsConfig == null)
        {
        	Config configResult = this.configService.getConfig(itemId);
            formsConfig = (FormsConfigElement)configResult.getConfigElement(CONFIG_FORMS);
        }
        
        if (formsConfig != null)
        {
           // Extract the form we are looking for
            if (formsConfig != null)
            {
                // try and retrieve the specified form 
                if (formId != null && formId.length() > 0)
                {
                    formConfig = formsConfig.getForm(formId);
                }
                
                // fall back to the default form
                if (formConfig == null)
                {
                    formConfig = formsConfig.getDefaultForm();
                }
            }
        }
        else if (logger.isWarnEnabled())
        {
            logger.warn("Could not lookup form configuration as configService has not been set");
        }
        return formConfig;
    }
    
    /**
     * Returns the list of visible field names for the given mode.
     * 
     * @param mode The form mode
     * @param formConfig The form configuration
     * @return List of field names configured to be visible
     */
    protected List<String> getVisibleFields(Mode mode, FormConfigElement formConfig)
    {
        List<String> visibleFields = null;
        
        if (formConfig != null)
        {
            // get visible fields for the current mode
            switch (mode) 
            {
                case VIEW:
                    visibleFields = formConfig.getVisibleViewFieldNamesAsList();
                    break;
                case EDIT:
                    visibleFields = formConfig.getVisibleEditFieldNamesAsList();
                    break;
                case CREATE:
                    visibleFields = formConfig.getVisibleCreateFieldNamesAsList();
                    break;
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Fields configured to be visible for " + mode + " mode = " + visibleFields);
        
        return visibleFields;
    }
    
    /**
     * Returns the list of visible field names for the given set.
     * 
     * @param context The context
     * @param setConfig Set configuration
     * @return List of field names configured to be visible for the set
     */
    protected List<String> getVisibleFieldsInSet(ModelContext context, FormSet setConfig)
    {
        List<String> visibleFields = null;
        Mode mode = context.getMode();
            
        if (setConfig != null)
        {
            switch (mode)
            {
                case VIEW:
                    visibleFields = context.getFormConfig().getVisibleViewFieldNamesForSetAsList(
                                setConfig.getSetId());
                    break;
                case EDIT:
                    visibleFields = context.getFormConfig().getVisibleEditFieldNamesForSetAsList(
                                setConfig.getSetId());
                    break;
                case CREATE:
                    visibleFields = context.getFormConfig().getVisibleCreateFieldNamesForSetAsList(
                                setConfig.getSetId());
                    break;
            }
            
            if (logger.isDebugEnabled())
                logger.debug("Fields configured to be visible for set \"" + setConfig.getSetId() + "\" = " + visibleFields);
        }
        
        return visibleFields;
    }
    
    /**
     * Retrieves the form definition from the repository FormService for the
     * given item.
     * 
     * @param itemKind The form item kind
     * @param itemId The form item id
     * @param visibleFields The list of field names to return or null
     *        to return all fields
     * @param formConfig The form configuration
     * @return Response object from the remote call
     */
    protected Response retrieveFormDefinition(String itemKind, String itemId, 
                List<String> visibleFields, FormConfigElement formConfig)
    {
        Response response = null;
        
        try
        {
            // setup the connection
            ConnectorService connService = FrameworkUtil.getConnectorService();
            RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
            String currentUserId = requestContext.getUserId();
            HttpSession currentSession = ServletUtil.getSession(true);
            Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);
            ConnectorContext context = new ConnectorContext(HttpMethod.POST, null, buildDefaultHeaders());
            context.setContentType("application/json");
            
            // call the form service
            response = connector.call("/api/formdefinitions", context, generateFormDefPostBody(itemKind,
                        itemId, visibleFields, formConfig));
            
            if (logger.isDebugEnabled())
                logger.debug("Response status: " + response.getStatus().getCode());
        }
        catch (Exception e)
        {
            if (logger.isErrorEnabled())
                logger.error("Failed to get form definition: ", e);
        }
        
        return response;
    }
    
    /**
     * Helper to build a map of the default headers for script requests - we send over
     * the current users locale so it can be respected by any appropriate REST APIs.
     *  
     * @return map of headers
     */
    private static Map<String, String> buildDefaultHeaders()
    {
        Map<String, String> headers = new HashMap<String, String>(1, 1.0f);
        headers.put("Accept-Language", I18NUtil.getLocale().toString().replace('_', '-'));
        return headers;
    }
    
    /**
     * Retrieves a localized message string.
     * 
     * @param messageKey The message key to lookup
     * @param args Optional replacement arguments
     * @return The localized string
     */
    protected String retrieveMessage(String messageKey, final Object... args)
    {
        if (this.messageHelper == null)
        {
            this.messageHelper = new MessageHelper(this);
        }
        
        return this.messageHelper.get(messageKey, args);
    }
    
    /**
     * Generates the POST body to send to the FormService.
     * 
     * @param itemKind The form item kind
     * @param itemId The form item id
     * @param visibleFields The list of field names to return or null
     *        to return all fields
     * @param formConfig The form configuration
     * @return ByteArrayInputStream representing the POST body
     * @throws IOException
     */
    protected ByteArrayInputStream generateFormDefPostBody(String itemKind, String itemId, 
                List<String> visibleFields, FormConfigElement formConfig) throws IOException
    {
        StringBuilderWriter buf = new StringBuilderWriter(512);
        JSONWriter writer = new JSONWriter(buf);
        
        writer.startObject();
        writer.writeValue(PARAM_ITEM_KIND, itemKind);
        writer.writeValue(PARAM_ITEM_ID, itemId.replace(":/", ""));

        List<String> forcedFields = null;
        if (visibleFields != null && visibleFields.size() > 0)
        {
            // list the requested fields
            writer.startValue(MODEL_FIELDS);
            writer.startArray();

            forcedFields = new ArrayList<String>(visibleFields.size());
            for (String fieldId : visibleFields)
            {
                // write out the fieldId
                writer.writeValue(fieldId);
                
                // determine which fields need to be forced
                if (formConfig.isFieldForced(fieldId))
                {
                    forcedFields.add(fieldId);
                }
            }
            
            // close the fields array
            writer.endArray();
        }
        
        // list the forced fields, if present
        if (forcedFields != null && forcedFields.size() > 0)
        {
            writer.startValue(MODEL_FORCE);
            writer.startArray();
            
            for (String fieldId : forcedFields)
            {
                writer.writeValue(fieldId);
            }
            
            writer.endArray();
        }
        
        // end the JSON object
        writer.endObject();
        
        if (logger.isDebugEnabled())
            logger.debug("Generated JSON POST body: " + buf.toString());
        
        // return the JSON body as a stream
        return new ByteArrayInputStream(buf.toString().getBytes());
    }
    
    /**
     * Generates the "form" model passed to the FreeMarker engine.
     * 
     * @param request The WebScriptRequest
     * @param mode The mode of the form
     * @param formSvcResponse Response representing the form definition
     * @param formConfig The form configuration
     * @return Map representing the "form" model
     */
    protected Map<String, Object> generateFormModel(WebScriptRequest request, Mode mode, 
                Response formSvcResponse, FormConfigElement formConfig)
    {
        try
        {
            String jsonResponse = formSvcResponse.getResponse();
            
            if (logger.isDebugEnabled())
                logger.debug("form definition JSON = \n" + jsonResponse);
                
            // create JSON representation of form defintion from response
            JSONObject formDefinition = new JSONObject(new JSONTokener(jsonResponse));
        
            // create model to return
            Map<String, Object> model = new HashMap<String, Object>(1);
            model.put(MODEL_FORM, generateFormUIModel(new ModelContext(request, mode, formDefinition, formConfig)));
            return model;
        }
        catch (JSONException je)
        {
            if (logger.isErrorEnabled())
                logger.error(je);
            
            return null;
        }
    }
    
    /**
     * Generates the model that will be processed by the FreeMarker engine
     * and thus render the form UI.
     * 
     * @param context The context
     * @return Map representing the form UI model
     */
    protected Map<String, Object> generateFormUIModel(ModelContext context)
    {
        // generate the form UI model and add to the context
        Map<String, Object> formUIModel = new HashMap<String, Object>(8);
        context.setFormUIModel(formUIModel);
        
        // populate the model
        formUIModel.put(MODEL_MODE, context.getMode().toString());
        formUIModel.put(MODEL_METHOD, getParameter(context.getRequest(), MODEL_METHOD, DEFAULT_METHOD));
        formUIModel.put(MODEL_ENCTYPE, discoverEncodingFormat(context));
        formUIModel.put(MODEL_SUBMISSION_URL, discoverSubmissionUrl(context));
        formUIModel.put(MODEL_ARGUMENTS, discoverArguments(context));
        formUIModel.put(MODEL_DATA, discoverData(context));
        formUIModel.put(MODEL_SHOW_CANCEL_BUTTON, discoverBooleanParam(context, MODEL_SHOW_CANCEL_BUTTON));
        formUIModel.put(MODEL_SHOW_RESET_BUTTON, discoverBooleanParam(context, MODEL_SHOW_RESET_BUTTON));
        formUIModel.put(MODEL_SHOW_SUBMIT_BUTTON, discoverBooleanParam(context, MODEL_SHOW_SUBMIT_BUTTON, true));
        
        String destination = getParameter(context.getRequest(), MODEL_DESTINATION);
        if (destination != null && destination.length() > 0)
        {
            formUIModel.put(MODEL_DESTINATION, destination);
        }
        
        String redirect = getParameter(context.getRequest(), MODEL_REDIRECT);
        if (redirect != null && redirect.length() > 0)
        {
            formUIModel.put(MODEL_REDIRECT, redirect);
        }
        
        // process the capabilities
        processCapabilities(context, formUIModel);
        
        // process the optional templates
        processTemplates(context, formUIModel);
        
        // process the fields to generate the 'fields', 'structure' and 'constraints'
        // properties of the model
        processFields(context, formUIModel);
        
        // detect 'showCaption' after 'constraints' were generated
        formUIModel.put(MODEL_SHOW_CAPTION, discoverBooleanParam(context, MODEL_SHOW_CAPTION, getDefaultShowCaption(context)));
        
        // dump the model for debugging
        dumpFormUIModel(formUIModel);
        
        return formUIModel;
    }
    
    /**
     * Returns default showCaption flag
     * 
     * @param context The context
     * @return Default showCaption flag
     */
    private boolean getDefaultShowCaption(ModelContext context)
    {
        if (context.getMode() == Mode.VIEW)
        {
            return false;
        }
        for (Constraint constraint : context.getConstraints())
        {
            if (CONSTRAINT_MANDATORY.equals(constraint.getId()))
            {
                return true;
            }
        }
        return false;
    }

    /**
     * Determines the "enctype" that should be used for the form.
     * 
     * @param context The context
     * @return The enctype the form should use
     */
    protected String discoverEncodingFormat(ModelContext context)
    {
        String submitType = getParameter(context.getRequest(), PARAM_SUBMIT_TYPE, DEFAULT_SUBMIT_TYPE);
        
        String enctype = null;
        
        if (SUBMIT_TYPE_MULTIPART.equals(submitType))
        {
            enctype = ENCTYPE_MULTIPART;
        }
        else if (SUBMIT_TYPE_JSON.equals(submitType))
        {
            enctype = ENCTYPE_JSON;
        }
        else if (SUBMIT_TYPE_URL.equals(submitType))
        {
            enctype = ENCTYPE_URL;
        }
        else
        {
            enctype = ENCTYPE_MULTIPART;
        }
        
        return enctype;
    }
    
    /**
     * Determines the "submissionUrl" that should be used for the form.
     * 
     * @param context The context
     * @return The submissionUrl the form should use
     */
    protected String discoverSubmissionUrl(ModelContext context)
    {
        String submissionUrl = null;
        
        if (context.getFormConfig() != null && context.getFormConfig().getSubmissionURL() != null)
        {
            submissionUrl = context.getFormConfig().getSubmissionURL();
        }
        else
        {   
            String defaultSubmissionUrl = null;
            
            try
            {
                // get the submission url from the form definition
                JSONObject data = context.getFormDefinition().getJSONObject(MODEL_DATA);
                defaultSubmissionUrl = data.getString(MODEL_SUBMISSION_URL);
            }
            catch (JSONException je)
            {
                // just use the default submission url defined above
                throw new WebScriptException("Failed to find default submission URL", je);
            }
            
            // if a submission url has been provided use that otherwise use the default
            submissionUrl = getParameter(context.getRequest(), PARAM_SUBMISSION_URL, defaultSubmissionUrl);
        }
        
        // build the full submission url
        submissionUrl = getProxyPath(context) + submissionUrl;
        
        return submissionUrl;
    }

    /**
     * Returns the base path to the proxy to use
     *
     * @param context Contains the request and context path
     * @return The base path to the proxy to use
     */
    protected String getProxyPath(ModelContext context)
    {
        return context.getRequest().getContextPath() + ALFRESCO_PROXY;
    }
    
    /**
     * Determines the "arguments" that should be used for the form.
     * 
     * @param context The context
     * @return The arguments the form should use
     */
    protected Map<String, String> discoverArguments(ModelContext context)
    {
        Map<String, String> arguments = new HashMap<String, String>(3);
        
        arguments.put(PARAM_ITEM_KIND, getParameter(context.getRequest(), PARAM_ITEM_KIND));
        arguments.put(PARAM_ITEM_ID, getParameter(context.getRequest(), PARAM_ITEM_ID));
        arguments.put(PARAM_FORM_ID, getParameter(context.getRequest(), PARAM_FORM_ID));
        
        return arguments; 
    }
    
    /**
     * Determines the "data" that should be used for the form.
     * 
     * @param context The context
     * @return The data the form should use
     */
    protected Map<String, Object> discoverData(ModelContext context)
    {
        Map<String, Object> dataModel = null;
        
        try
        {
            // get the formData section of the form definition
            JSONObject data = context.getFormDefinition().getJSONObject(MODEL_DATA);
            JSONObject formData = data.getJSONObject(MODEL_FORM_DATA);
            
            // copy formData into map
            JSONArray names = formData.names();
            if (names != null)
            {
                dataModel = new HashMap<String, Object>(names.length());
                for (int x = 0; x < names.length(); x++)
                {
                    String key = names.getString(x);
                    dataModel.put(key, formData.get(key));
                }
            }
            else
            {
                dataModel = Collections.emptyMap();
            }
        }
        catch (JSONException je)
        {
            throw new WebScriptException("Failed to find form data", je);
        }
        
        return dataModel;
    }
    
    /**
     * Returns the value of the given boolean parameter.
     * 
     * @param context The context
     * @param name The name of the parameter
     * @return The value of parameter, false is returned if
     *         the parameter is not found
     */
    protected boolean discoverBooleanParam(ModelContext context, String name)
    {
        return discoverBooleanParam(context, name, false);
    }
    
    /**
     * Returns the value of the given boolean parameter.
     * 
     * @param context The context
     * @param name The name of the parameter
     * @param defaultValue The default value to use if the parameter 
     *        is not found
     * @return The value of parameter, defaultValue is returned if
     *         the parameter is not found
     */
    protected boolean discoverBooleanParam(ModelContext context, String name, boolean defaultValue)
    {
        String value = getParameter(context.getRequest(), name, Boolean.toString(defaultValue));
        
        return ("true".equals(value)) ? true : false;
    }
    
    /**
     * Returns the field definition for the given field name.
     * 
     * @param context The context
     * @param fieldName The name of the field to get the definition for
     * @return JSONObject representing the field definition
     */
    protected JSONObject discoverFieldDefinition(ModelContext context, String fieldName)
    {
        JSONObject fieldDefinition = null;
        
        JSONObject propertyDefinition = context.getPropertyDefinitions().get(fieldName);
        JSONObject associationDefinition = context.getAssociationDefinitions().get(fieldName);
        
        if (propertyDefinition == null && associationDefinition == null)
        {
            // if a field definition has not been found yet check for prop: and assoc: prefixes
            if (fieldName.indexOf(PROP_PREFIX) != -1)
            {
                propertyDefinition = context.getPropertyDefinitions().get(
                            fieldName.substring(PROP_PREFIX.length()));
            }
            else if (fieldName.indexOf(ASSOC_PREFIX) != -1)
            {
                associationDefinition = context.getAssociationDefinitions().get(
                            fieldName.substring(ASSOC_PREFIX.length()));
            }
        }

        // determine if field was a property or association
        if (propertyDefinition != null)
        {
            fieldDefinition = propertyDefinition;
        }
        else if (associationDefinition != null)
        {
            fieldDefinition = associationDefinition;
        }
        
        return fieldDefinition;
    }
    
    /**
     * Determines the label to use for the given set configuration.
     * 
     * @param setConfig Set configuration
     * @return The label of the set.
     */
    protected String discoverSetLabel(FormSet setConfig)
    {
        String label = null;
        
        if (setConfig.getLabelId() != null)
        {
            label = retrieveMessage(setConfig.getLabelId());
        }
        else if (setConfig.getLabel() != null)
        {
            label = setConfig.getLabel();
        }
        else
        {
            // if there is no label specified in the config,
            // use the label from the properties file otherwise
            // use the set id
            if ("".equals(setConfig.getSetId()))
            {
                label = retrieveMessage(MSG_DEFAULT_SET_LABEL);
            }
            else
            {
                label = setConfig.getSetId();
            }
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Returning label for set: " + label);
        
        return label;
    }
    
    /**
     * Determines the set membership hierarchy.
     * 
     * @param context The context
     * @return Map of field name lists representing the set hierarchy
     */
    protected Map<String, List<String>> discoverSetMembership(ModelContext context)
    {
        Map<String, List<String>> setMemberships = new HashMap<String, List<String>>(4);
        
        try
        {
            // get list of fields from form definition
            JSONObject data = context.getFormDefinition().getJSONObject(MODEL_DATA);
            JSONObject definition = data.getJSONObject(MODEL_DEFINITION);
            JSONArray fieldsFromServer = definition.getJSONArray(MODEL_FIELDS);
            
            // iterate around fields to determine what set they belong to
            for (int x = 0; x < fieldsFromServer.length(); x++)
            {
                JSONObject fieldDefinition = fieldsFromServer.getJSONObject(x); 
                String fieldName = fieldDefinition.getString(MODEL_NAME);
           
                // determine if this field should even be shown
                if (context.getFormConfig().isFieldHidden(fieldName, context.getMode()) == false)
                {
                    // if its visible determine its set membership
                    String set = "";
                    if (fieldDefinition.has(MODEL_GROUP))
                    {
                        set = fieldDefinition.getString(MODEL_GROUP);
                    }
          
                    FormField fieldConfig = context.getFormConfig().getFields().get(fieldName);
                    if (fieldConfig != null && fieldConfig.getSet().equals("") == false)
                    {
                        set = fieldConfig.getSet();
                    }
          
                    // get the array for the set and add the field to it
                    List<String> fieldsForSet = setMemberships.get(set);
                    if (fieldsForSet == null)
                    {
                        // setup array for the set
                        fieldsForSet = new ArrayList<String>(4);
                        fieldsForSet.add(fieldName);
                        setMemberships.put(set, fieldsForSet);
                    }
                    else
                    {
                        fieldsForSet.add(fieldName);
                    }
                }
                else if (logger.isDebugEnabled())
                {
                    logger.debug("Ignoring \"" + fieldName + "\" as it is configured to be hidden");
                }
            }
        }
        catch (JSONException je)
        {
            // log the error and return the empty set
            if (logger.isErrorEnabled())
                logger.error("Failed to discover set membership", je);
        }
        
        if (logger.isDebugEnabled())
           logger.debug("Set membership = " + setMemberships);
        
        return setMemberships;
    }
    
    /**
     * Processes the "js" parameter, if present.
     * 
     * @param context The context
     * @param formUIModel The form UI model
     */
    protected void processCapabilities(ModelContext context, Map<String, Object> formUIModel)
    {
        String jsEnabled = getParameter(context.getRequest(), PARAM_JS);
        if (jsEnabled != null && ("off".equalsIgnoreCase(jsEnabled) || 
            "false".equalsIgnoreCase(jsEnabled) || "disabled".equalsIgnoreCase(jsEnabled)))
        {
            Map<String, Object> capabilities = new HashMap<String, Object>(1);
            capabilities.put(MODEL_JAVASCRIPT, false);
            formUIModel.put(MODEL_CAPABILITIES, capabilities);
           
            if (logger.isDebugEnabled())
                logger.debug("JavaScript disabled flag detected, added form capabilties: " + capabilities);
        }
    }
    
    /**
     * Processes the optional custom templates configuration.
     * 
     * @param context The context
     * @param formUIModel The form UI model
     */
    protected void processTemplates(ModelContext context, Map<String, Object> formUIModel)
    {
        FormConfigElement formConfig = context.getFormConfig();
        
        if (formConfig != null && formConfig.getViewTemplate() != null)
        {
            formUIModel.put(MODEL_VIEW_TEMPLATE, formConfig.getViewTemplate());
            
            if (logger.isDebugEnabled())
                logger.debug("Set viewTemplate to \"" + formConfig.getViewTemplate() + "\"");
        }
        
        if (formConfig != null && formConfig.getEditTemplate() != null)
        {
            formUIModel.put(MODEL_EDIT_TEMPLATE, formConfig.getEditTemplate());
            
            if (logger.isDebugEnabled())
                logger.debug("Set editTemplate to \"" + formConfig.getEditTemplate() + "\"");
        }
        
        if (formConfig != null && formConfig.getCreateTemplate() != null)
        {
            formUIModel.put(MODEL_CREATE_TEMPLATE, formConfig.getCreateTemplate());
            
            if (logger.isDebugEnabled())
                logger.debug("Set createTemplate to \"" + formConfig.getCreateTemplate() + "\"");
        }
    }
    
    /**
     * Processes the "fields" section of the model.
     * 
     * @param context The context
     * @param formUIModel The form UI model
     */
    protected void processFields(ModelContext context, Map<String, Object> formUIModel)
    {
        List<String> visibleFields = getVisibleFields(context.getMode(), context.getFormConfig());
        
        if (context.getFormConfig() != null && visibleFields != null && visibleFields.size() > 0)
        {
           processVisibleFields(context);
        }
        else
        {
            processServerFields(context);
        }
        
        formUIModel.put(MODEL_FIELDS, context.getFields());
        formUIModel.put(MODEL_STRUCTURE, context.getStructure());
        formUIModel.put(MODEL_CONSTRAINTS, context.getConstraints());
    }
    
    /**
     * Processes the fields configured to be visible for the form.
     * 
     * @param context The context
     */
    protected void processVisibleFields(ModelContext context)
    {
        // iterate over the root sets and generate a model for each one
        for (FormSet setConfig : context.getFormConfig().getRootSetsAsList())
        {
            Set set = generateSetModelUsingVisibleFields(context, setConfig);
            
            // if the set got created (as it contained fields or other sets) 
            // add it to the structure list in the model context
            if (set != null)
            {
                context.getStructure().add(set);
            }
        }
    }
    
    /**
     * Processes the fields returned from the server (the form definition), this
     * method is called when there are no visible fields configured.
     * 
     * @param context The context
     */
    protected void processServerFields(ModelContext context)
    {
        if (context.getFormConfig() != null)
        {
            // discover the set membership of the fields using the form definition
            Map<String, List<String>> setMembership = discoverSetMembership(context);
            
            // get root sets from config and build set structure using config and lists built above
            for (FormSet setConfig : context.getFormConfig().getRootSetsAsList())
            {
                Set set = generateSetModelUsingServerFields(context, setConfig, setMembership);
                
                // if the set got created (as it contained fields or other sets) 
                // add it to the structure list in the model context
                if (set != null)
                {
                    context.getStructure().add(set);
                }
            }
        }
        else
        {
            // as there is no config at all generate a default set that contains
            // all the fields returned in the form definition
            Set set = generateDefaultSetModelUsingServerFields(context);
            context.getStructure().add(set);
        }
    }
    
    /**
     * Generates the model for the given set, this method also recursively generates any
     * child sets the given set has. The contents of the sets are purely driven by the
     * configured visible fields. 
     * 
     * @param context The context
     * @param setConfig The set configuration
     * @return The set model
     */
    protected Set generateSetModelUsingVisibleFields(ModelContext context, FormSet setConfig)
    {
        Set set = null;
        
        List<String> fieldsInSet = getVisibleFieldsInSet(context, setConfig);
        
        // if there is something to show in the set create the set object
        if ((fieldsInSet != null && fieldsInSet.size() > 0) || setConfig.getChildrenAsList().size() > 0)
        {
            set = generateSetModel(context, setConfig, fieldsInSet);
            
            // recursively setup child sets
            for (FormSet childSetConfig : setConfig.getChildrenAsList())
            {
                Set childSet = generateSetModelUsingVisibleFields(context, childSetConfig);
                set.addChild(childSet);
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Ignoring set \"" + setConfig.getSetId() + "\" as it does not have any fields or child sets");
        }
        
        return set;
    }
    
    /**
     * Generates the model for the given set, this method also recursively generates any
     * child sets the given set has. The contents of the sets are the result of combining
     * the form configuration and form definition.
     * 
     * @param context The context
     * @param setConfig Set configuration
     * @param setMembership The set hierarchy
     * @return The set model
     */
    protected Set generateSetModelUsingServerFields(ModelContext context, FormSet setConfig, 
                Map<String, List<String>> setMembership)
    {
        Set set = null;
        
        List<String> fieldsInSet = setMembership.get(setConfig.getSetId());
        
        // if there is something to show in the set create the set object
        if ((fieldsInSet != null && fieldsInSet.size() > 0) || setConfig.getChildrenAsList().size() > 0)
        {
            set = generateSetModel(context, setConfig, fieldsInSet);
            
            // recursively setup child sets
            for (FormSet childSetConfig : setConfig.getChildrenAsList())
            {
                Set childSet = generateSetModelUsingServerFields(context, childSetConfig, setMembership);
                set.addChild(childSet);
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Ignoring set \"" + setConfig.getSetId() + "\" as it does not have any fields or child sets");
        }
        
        return set;
    }
    
    /**
     * Generates the model for the given set and it's fields. This method does NOT
     * recurse through any child sets.
     * 
     * @param context The context
     * @param setConfig The set configuration
     * @param fields List of field in the set
     * @return The set model
     */
    protected Set generateSetModel(ModelContext context, FormSet setConfig, List<String> fields)
    {
        // create the set from the configuration
        Set set = new Set(setConfig);
        
        // create and all the fields to the set
        for (String fieldName : fields)
        {
            FormField fieldConfig = context.getFormConfig().getFields().get(fieldName);
            
            // attempt to generate a field
            Field field = generateFieldModel(context, fieldName, fieldConfig);
            
            // if a field was created add it to the map of fields in the model context
            // and add a pointer to the field to the set's list of children
            if (field != null)
            {
                set.addChild(new FieldPointer(field.getId()));
                context.getFields().put(field.getId(), field);
            }
        }
        
        return set;
    }
    
    /**
     * Generates the model for the default set, this will contain all the fields
     * returned from the server.
     * 
     * @param context The context
     * @return The set model
     */
    protected Set generateDefaultSetModelUsingServerFields(ModelContext context)
    {
        if (logger.isDebugEnabled())
            logger.debug("No configuration was found therefore showing all fields in the default set...");
         
        // setup the default set object
        Set set = new Set("", retrieveMessage(MSG_DEFAULT_SET_LABEL));
         
        try
        {
            // add all the fields from the server to the default set
            JSONObject data = context.getFormDefinition().getJSONObject(MODEL_DATA);
            JSONObject definition = data.getJSONObject(MODEL_DEFINITION);
            JSONArray fieldsFromServer = definition.getJSONArray(MODEL_FIELDS);
            
            for (int x = 0; x < fieldsFromServer.length(); x++)
            {
                String fieldName = fieldsFromServer.getJSONObject(x).getString(MODEL_NAME);
                
                // attempt to generate a field
                Field field = generateFieldModel(context, fieldName, null);
                
                // if a field was created add it to the map of fields in the model context
                // and add a pointer to the field to the set's list of children
                if (field != null)
                {
                    set.addChild(new FieldPointer(field.getId()));
                    context.getFields().put(field.getId(), field);
                }
            }
        }
        catch (JSONException je)
        {
            // log the error and return the empty set
            if (logger.isErrorEnabled())
                logger.error("Failed to generate default set from server fields", je);
        }
        
        return set;
    }
    
    /**
     * Generates the model for the given field. The form definition from the form service
     * and the given form configuration are combined to give the field model to send to
     * the template for rendering.
     * 
     * @param context The context
     * @param fieldName The name of the field to be generated
     * @param fieldConfig The configuration for the field
     * @return A field model
     */
    protected Field generateFieldModel(ModelContext context, String fieldName, FormField fieldConfig)
    {
        if (logger.isDebugEnabled())
            logger.debug("Generating model for field \"" + fieldName + "\"");
        
        Field field = null;
        
        try
        {
            // make sure the field is not ambiguous
            if (isFieldAmbiguous(context, fieldName))
            {
                field = generateTransientFieldModel(fieldName, "/org/alfresco/components/form/controls/ambiguous.ftl");
            }
            else
            {
                JSONObject fieldDefinition = discoverFieldDefinition(context, fieldName);
                
                if (fieldDefinition != null)
                {
                    // create the initial field model
                    field = new Field();
                    
                    // populate the model with the appropriate data
                    processFieldIdentification(context, field, fieldDefinition, fieldConfig);
                    processFieldState(context, field, fieldDefinition, fieldConfig);
                    processFieldText(context, field, fieldDefinition, fieldConfig);
                    processFieldData(context, field, fieldDefinition, fieldConfig);
                    processFieldControl(context, field, fieldDefinition, fieldConfig);
                    processFieldConstraints(context, field, fieldDefinition, fieldConfig);
                    processFieldContent(context, field, fieldDefinition, fieldConfig);
                }
                else
                {
                    // the field does not have a definition but may be a 'transient' field
                    field = generateTransientFieldModel(context, fieldName, fieldDefinition, fieldConfig);
                    
                    if (field == null && logger.isDebugEnabled())
                        logger.debug("Ignoring field \"" + fieldName + 
                                     "\" as neither a field definition or sufficient configuration could be located");
                }
            }
        }
        catch (JSONException je)
        {
            if (logger.isErrorEnabled())
                logger.error("Failed to generate field model for \"" + fieldName + "\"", je);
            
            field = null;
        }
        
        return field;
    }
    
    /**
     * Determines whether the given field is ambiguous (a property and association
     * have the same name).
     * 
     * @param context The context
     * @param fieldName The name of the field
     * @return true if the field is ambiguous
     */
    protected boolean isFieldAmbiguous(ModelContext context, String fieldName)
    {
        boolean ambiguous = false;
        
        // check whether there is a property and association definition
        // for the given field name
        if (context.getPropertyDefinitions().get(fieldName) != null &&
            context.getAssociationDefinitions().get(fieldName) != null)
        {
            ambiguous = true;
            
            if (logger.isWarnEnabled())
               logger.warn("\"" + fieldName + "\" is ambiguous, a property and an association exists with this name," +
               		       " prefix with either \"prop:\" or \"assoc:\" to uniquely identify the field");
        }
        
        return ambiguous;
    }
    
    /**
     * Generates a model for a "transient" field.
     * 
     * @param fieldName The name of the field
     * @param template The control template to use
     * @return The field model
     */
    protected Field generateTransientFieldModel(String fieldName, String template)
    {
        Field field = new Field();
        
        // replace colons for name and id
        String name = fieldName.replace(":", "_");
        
        field.setConfigName(fieldName);
        field.setName(name);
        field.setId(name);
        field.setLabel(fieldName);
        field.setValue("");
        field.setTransitory(true);
        field.setControl(new FieldControl(template));
        
        return field;
    }
    
    /**
     * Generates a model for a "transient" field.
     * 
     * @param context The context
     * @param fieldName The name of the field to be added
     * @return The field model
     */
    protected Field generateTransientFieldModel(ModelContext context, String fieldName,
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        // we can't continue without at least a control template
        if (fieldConfig == null || 
               fieldConfig.getControl() == null || 
               fieldConfig.getControl().getTemplate() == null || 
               fieldConfig.getAttributes() == null || 
               (fieldConfig.getAttributes().get("set") != null && !fieldConfig.getAttributes().get("set").isEmpty()))
        {
            return null;
        }
        
        if (logger.isDebugEnabled())
            logger.debug("Generating transient field for \"" + fieldName + "\"");
        
        // generate the basic transient field
        Field field = generateTransientFieldModel(fieldName, fieldConfig.getControl().getTemplate());
        
        // setup parameters, if present
        List<ControlParam> params = fieldConfig.getControl().getParamsAsList();
        if (params.size() > 0)
        {
            // get the field's control
            FieldControl control = field.getControl();
            
            for (ControlParam param : params)
            {
                // add parameter to field control
                control.getParams().put(param.getName(), param.getValue());
            }
        }
        
        // apply any configured text
        processFieldText(context, field, fieldDefinition, fieldConfig);
        
        return field;
    }
    
    /**
     * Processes the identification part of the field model.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldIdentification(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        field.setConfigName(fieldDefinition.getString(MODEL_NAME));
        field.setType(fieldDefinition.getString(MODEL_TYPE));
        
        String name = field.getConfigName();
        
        if (field.getType().equals(ASSOCIATION)) 
        {
           // add assoc prefix if missing
           if (!name.startsWith(ASSOC_PREFIX))
           {
              name = ASSOC_PREFIX + field.getConfigName();
           }
        }
        else
        {
           // add prop prefix if missing
           if (!name.startsWith(PROP_PREFIX))
           {
              name = PROP_PREFIX + field.getConfigName();
           }
        }
        
        // replace : with _ so it can be used as JSON/JavaScript key/property
        name = name.replace(":", "_");
        
        // set the id of the field
        field.setId(name);
        
        // set name of the field (ALF-5146: escape any dots in the name)
        name = name.replace(".", "#dot#");
        field.setName(name);
    }
    
    /**
     * Processes the "state" part of the field model i.e. disabled, mandatory
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldState(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        // configure read-only state (but only if the field definition indicates
        // that it is not a protected field)
        boolean disabled = false;
        if (fieldDefinition.has(MODEL_PROTECTED_FIELD))
        {
            disabled = fieldDefinition.getBoolean(MODEL_PROTECTED_FIELD);
        }
        if (!disabled && fieldConfig != null && fieldConfig.isReadOnly())
        {
           disabled = true;
        }
        
        field.setDisabled(disabled);
        
        // configure mandatory state (but only if the field definition indicates
        // that it is an optional field)
        boolean mandatory = false;
        if (fieldDefinition.has(MODEL_MANDATORY))
        {
            // properties will have "mandatory" 
            mandatory = fieldDefinition.getBoolean(MODEL_MANDATORY);
        }
        if (fieldDefinition.has(MODEL_ENDPOINT_MANDATORY))
        {
            // associations will have "endpointMandatory" 
            mandatory = fieldDefinition.getBoolean(MODEL_ENDPOINT_MANDATORY);
        }
        if (!mandatory && fieldConfig != null && fieldConfig.isMandatory())
        {
           mandatory = true;
        }
        
        field.setMandatory(mandatory);
        
        // configure repeating state
        if (fieldDefinition.has(MODEL_REPEATING))
        {
            field.setRepeating(fieldDefinition.getBoolean(MODEL_REPEATING));
        }
        if (fieldDefinition.has(MODEL_ENDPOINT_MANY))
        {
            field.setRepeating(fieldDefinition.getBoolean(MODEL_ENDPOINT_MANY));
        }
        
        // configure association direction (if appropriate)
        if (fieldDefinition.has(MODEL_ENDPOINT_DIRECTION))
        {
            field.setEndpointDirection(fieldDefinition.getString(MODEL_ENDPOINT_DIRECTION));
        }
    }
    
    /**
     * Processes the "text" part of the field model i.e. label, description, help
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldText(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        // set the initial label and description from the field definition, if present
        if (fieldDefinition != null)
        {
            if (fieldDefinition.has(MODEL_LABEL))
            {
                field.setLabel(fieldDefinition.getString(MODEL_LABEL));
            }
            if (fieldDefinition.has(MODEL_DESCRIPTION))
            {
                field.setDescription(fieldDefinition.getString(MODEL_DESCRIPTION));
            }
        }
        
        if (fieldConfig != null)
        {
           // process configured label
           String configLabel = null;
           
           if (fieldConfig.getLabelId() != null)
           {
              configLabel = retrieveMessage(fieldConfig.getLabelId());
           }
           else if (fieldConfig.getLabel() != null)
           {
              configLabel = fieldConfig.getLabel();
           }
           
           if (configLabel != null)
           {
              field.setLabel(configLabel);
           }

           // process configured description
           String configDesc = null;
           
           if (fieldConfig.getDescriptionId() != null)
           {
              configDesc = retrieveMessage(fieldConfig.getDescriptionId());
           }
           else if (fieldConfig.getDescription() != null)
           {
              configDesc = fieldConfig.getDescription();
           }
           
           if (configDesc != null)
           {
              field.setDescription(configDesc);
           }
           
           // process configured help text
           String configHelp = null;
           
           if (fieldConfig.getHelpTextId() != null)
           {
              configHelp = retrieveMessage(fieldConfig.getHelpTextId());
           }
           else if (fieldConfig.getHelpText() != null)
           {
              configHelp = fieldConfig.getHelpText();
           }
           
           if (configHelp != null)
           {
              field.setHelp(configHelp);
           }
           // Only override the default value if explicitly specified in the config
           if (fieldConfig.getHelpEncodeHtml() != null)
           {
               field.setHelpEncodeHtml(fieldConfig.getHelpEncodeHtml().equalsIgnoreCase("true"));
           }
        }
    }
    
    /**
     * Processes the "data" part of the field model.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldData(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        if (fieldDefinition.has(MODEL_DATA_TYPE))
        {
            field.setDataType(fieldDefinition.getString(MODEL_DATA_TYPE));
        }
        if (fieldDefinition.has(MODEL_ENDPOINT_TYPE))
        {
            field.setDataType(fieldDefinition.getString(MODEL_ENDPOINT_TYPE));
        }
        field.setDataKeyName(fieldDefinition.getString(MODEL_DATA_KEY_NAME));
        field.setValue("");

        // extract the data for the field from the 'formData' JSON object
        JSONObject formDefinition = context.getFormDefinition().getJSONObject(MODEL_DATA);
        if (formDefinition.has(MODEL_FORM_DATA))
        {
            JSONObject formData = formDefinition.getJSONObject(MODEL_FORM_DATA);
            if (formData.has(field.getDataKeyName()))
            {
                field.setValue(formData.get(field.getDataKeyName()));
            }
        }
        
        // if the value is still empty, we're in create mode and the 
        // field has a default value use it for initial value
        if (field.getValue().equals("") && context.getMode().equals(Mode.CREATE) &&
            fieldDefinition.has(MODEL_DEFAULT_VALUE))
        {
            field.setValue(fieldDefinition.getString(MODEL_DEFAULT_VALUE));
        }
        
        if (fieldDefinition.has(MODEL_INDEX_TOKENISATION_MODE))
        {
            if (fieldDefinition.getString(MODEL_INDEX_TOKENISATION_MODE).toUpperCase().equals("FALSE"))
            {
                field.setIndexTokenisationMode(fieldDefinition.getString(MODEL_INDEX_TOKENISATION_MODE));
            }
        }
    }
    
    /**
     * Processes the constraints for the field.
     * 
     * NOTE: This method MUST be called with the Field object having it's
     *       identification, state and text already processed.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldConstraints(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        // setup mandatory constraint if field is marked as such
        if (!field.isDisabled())
        {
            if (field.isMandatory())
            {
                Constraint constraint = generateConstraintModel(context, field, fieldDefinition, fieldConfig, CONSTRAINT_MANDATORY);
                if (constraint != null)
                {
                    // add the constraint to the context
                    context.getConstraints().add(constraint);
                }
            }
      
            if  (fieldConfig != null && fieldConfig.getConstraintDefinitionMap() != null)
            {
                // add form constraints defined in custom config
                Map<String, ConstraintHandlerDefinition> fieldConstraints = fieldConfig.getConstraintDefinitionMap();
                for (String constraintId : fieldConstraints.keySet())
                {
                    Constraint constraint = null;
                    // get the custom handler for the constraint
                    ConstraintHandlerDefinition customConstraintConfig = fieldConstraints.get(constraintId);
                    if (customConstraintConfig != null)
                    {
                        // generate and process the constraint model
                        constraint = generateConstraintModel(context, field, fieldConfig, constraintId, new JSONObject(), customConstraintConfig);
                    }
                    if (constraint != null)
                    {
                        // add the constraint to the context
                        context.getConstraints().add(constraint);
                    }
                }
            }
        }
        
        // look for model defined constraints on the field definition
        if (fieldDefinition.has(MODEL_CONSTRAINTS))
        {
            JSONArray constraints = fieldDefinition.getJSONArray(MODEL_CONSTRAINTS);
            
            for (int x = 0; x < constraints.length(); x++)
            {
                Constraint constraint = generateConstraintModel(context, field, 
                            fieldDefinition, fieldConfig, constraints.getJSONObject(x));
                
                if (constraint != null)
                {
                    // add the constraint to the context
                    context.getConstraints().add(constraint);
                }
            }
        }
        
        // add a number constraint if the field has a number data type
        String dataType = field.getDataType();
        Map<String, ConstraintHandlerDefinition> constraintDefinitionMap = (fieldConfig == null) ? null : fieldConfig.getConstraintDefinitionMap();

        if (isConstraintHandlerExist(constraintDefinitionMap, CONSTRAINT_NUMBER) ||isDataTypeNumber(dataType))
        {
            Constraint constraint = generateConstraintModel(context, field, fieldDefinition, 
                        fieldConfig, CONSTRAINT_NUMBER);
            
            if (constraint != null)
            {
                // if the field is repeating add a flag to indicate this
                // so the client side validation handler can take the appropriate action
                if (field.isRepeating())
                {
                    constraint.getJSONParams().put("repeating", true);
                }
                
                // add the constraint to the context
                context.getConstraints().add(constraint);
            }
        }        
    }

    private boolean isConstraintHandlerExist(Map<String, ConstraintHandlerDefinition> constraintDefinitionMap, String constraint)
    {
        if (constraintDefinitionMap != null)
        {
           return constraintDefinitionMap.containsKey(constraint);
        }
        return false;
    }
    
    private boolean isDataTypeNumber(String dataType)
    {
        if (TYPE_INT.equals(dataType) ||TYPE_LONG.equals(dataType) ||TYPE_DOUBLE.equals(dataType)
                    ||TYPE_FLOAT.equals(dataType)) 
        { 
            return true; 
        }

        return false;
    }
    
    /**
     * Generates the model for a single constraint.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @param constraintId The constraint identifier
     * @return Constraint
     * @throws JSONException
     */
    protected Constraint generateConstraintModel(ModelContext context, Field field,
                JSONObject fieldDefinition, FormField fieldConfig, 
                String constraintId) throws JSONException
    {
        // create a JSONObject containing the constraint id
        JSONObject constraintDef = new JSONObject();
        constraintDef.put(MODEL_TYPE, constraintId);
        
        // generate the constraint model 
        return generateConstraintModel(context, field, fieldDefinition,
                    fieldConfig, constraintDef);
    }
    
    /**
     * Generates the model for a single constraint.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @param constraintDefinition The constraint definition
     * @return Constraint
     * @throws JSONException
     */
    protected Constraint generateConstraintModel(ModelContext context, Field field,
                JSONObject fieldDefinition, FormField fieldConfig, 
                JSONObject constraintDefinition) throws JSONException
    {
        Constraint constraint = null;        
        String constraintId = null;
        JSONObject constraintParams = null;
        
        // extract the constraint id
        if (constraintDefinition.has(MODEL_TYPE))
        {
            constraintId = constraintDefinition.getString(MODEL_TYPE);
        }
        
        if (constraintDefinition.has(MODEL_PARAMETERS))
        {
            constraintParams = constraintDefinition.getJSONObject(MODEL_PARAMETERS);
        }
        else
        {
            constraintParams = new JSONObject();
        }

        // retrieve the default constraints configuration
        ConstraintHandlersConfigElement defaultConstraintHandlers = null;
        FormsConfigElement formsGlobalConfig = 
            (FormsConfigElement)this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null)
        {
            defaultConstraintHandlers = formsGlobalConfig.getConstraintHandlers();
        }
        
        if (defaultConstraintHandlers == null)
        {
            throw new WebScriptException("Failed to locate default constraint handlers configurarion");
        }
        
        // get the default handler for the constraint
        ConstraintHandlerDefinition defaultConstraintConfig = 
            defaultConstraintHandlers.getItems().get(constraintId);
        
        if (defaultConstraintConfig != null)
        {
            // generate and process the constraint model
            constraint = generateConstraintModel(context, field, fieldConfig, 
                        constraintId, constraintParams, defaultConstraintConfig);
            processFieldConstraintControl(context, field, fieldConfig, constraint);
            processFieldConstraintHelp(context, field, fieldConfig, constraint);
        }
        else if (logger.isWarnEnabled())
        {
            logger.warn("No default constraint configuration found for \"" + constraintId + 
                        "\" constraint whilst processing field \"" + field.getConfigName() + "\"");
        }
        
        return constraint;
    }
    
    
    /**
     * Generates the model for a single constraint.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @param constraintId The constraint identifier
     * @param constraintParams The constraint parameters
     * @param defaultConstraintConfig ConstraintHandlerDefinition
     * @return The constraint moel
     * @throws JSONException
     */
    protected Constraint generateConstraintModel(ModelContext context, Field field, FormField fieldConfig,
                String constraintId, JSONObject constraintParams, 
                ConstraintHandlerDefinition defaultConstraintConfig) throws JSONException
    {
        // get the validation handler from the config
        String validationHandler = defaultConstraintConfig.getValidationHandler();
        
        Constraint constraint = new Constraint(field.getId(), constraintId, 
                    validationHandler, constraintParams);
        
        if (defaultConstraintConfig.getEvent() != null)
        {
            constraint.setEvent(defaultConstraintConfig.getEvent());
        }
        else
        {
            constraint.setEvent(DEFAULT_CONSTRAINT_EVENT);
        }
        
        // look for an overridden message in the field's constraint config, 
        // if none found look in the default constraint config
        String constraintMsg = null;
        if (fieldConfig != null && 
            fieldConfig.getConstraintDefinitionMap().get(constraintId) != null)
        {
           ConstraintHandlerDefinition fieldConstraintConfig = 
               fieldConfig.getConstraintDefinitionMap().get(constraintId);
           if (fieldConstraintConfig.getMessageId() != null)
           {
               constraintMsg = retrieveMessage(fieldConstraintConfig.getMessageId());
           }
           else if (fieldConstraintConfig.getMessage() != null)
           {
               constraintMsg = fieldConstraintConfig.getMessage();
           }

           // look for overridden validation handler
           if (fieldConstraintConfig.getValidationHandler() != null)
           {
               constraint.setValidationHandler(fieldConstraintConfig.getValidationHandler());
           }
           
           // look for overridden event
           if (fieldConstraintConfig.getEvent() != null)
           {
               constraint.setEvent(fieldConstraintConfig.getEvent());
           }
        }
        else if (defaultConstraintConfig.getMessageId() != null)
        {
            constraintMsg = retrieveMessage(defaultConstraintConfig.getMessageId());
        }
        else if (defaultConstraintConfig.getMessage() != null)
        {
            constraintMsg = defaultConstraintConfig.getMessage();
        }
        if (constraintMsg == null)
        {
            constraintMsg = retrieveMessage(validationHandler + ".message");
        }
        
        // add the message if there is one
        if (constraintMsg != null)
        {
            constraint.setMessage(constraintMsg);
        }
        
        return constraint;
    }
    
    /**
     * Processes the given constraint to ensure the field's control
     * adheres to the constraint.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @param constraint The constraint
     * @throws JSONException
     */
    protected void processFieldConstraintControl(ModelContext context, Field field, 
                FormField fieldConfig, Constraint constraint) throws JSONException
    {
        // process special constraint behaviour
        if (CONSTRAINT_LIST.equals(constraint.getId()))
        {
            // if the constraint is the list of values constraint, force the control
            // template to be selectone.ftl or selectmany.ftl depending on whether
            // the field has multiple values, but only if an overridden control has
            // not been supplied
            if (fieldConfig == null || fieldConfig.getControl() == null || 
                fieldConfig.getControl().getTemplate() == null)
            {
                if (field.isRepeating())
                {
                    field.getControl().setTemplate(CONTROL_SELECT_MANY);
                }
                else
                {
                    field.getControl().setTemplate(CONTROL_SELECT_ONE);
                }
            }
            
            // setup the options string and set as control params, but only if the control
            // does not already have an "options" parameter
            if (field.getControl().getParams().containsKey(CONTROL_PARAM_OPTIONS) == false)
            {
                JSONArray options = constraint.getJSONParams().getJSONArray("allowedValues");
                List<String> optionsList = new ArrayList<String>(options.length());
                for (int x = 0; x < options.length(); x++)
                {
                    optionsList.add(options.getString(x));
                }
                
                // Sort the options based on the label...
                if (fieldConfig != null && fieldConfig.isSorted())
                {
                    Collections.sort(optionsList, new OptionsComparator());
                }
                
                // ALF-7961: don't use a comma as the list separator
                field.getControl().getParams().put(CONTROL_PARAM_OPTIONS, 
                            StringUtils.collectionToDelimitedString(optionsList, DELIMITER));
                field.getControl().getParams().put(CONTROL_PARAM_OPTION_SEPARATOR, DELIMITER);
            }
        }
        else if (CONSTRAINT_LENGTH.equals(constraint.getId()))
        {
            int maxLength = -1;
            if (constraint.getJSONParams().has(MODEL_MAX_LENGTH))
            {
                maxLength = constraint.getJSONParams().getInt(MODEL_MAX_LENGTH);
            }
            
            // if the constraint is the length constraint, pass the maxLength
            // parameter to the control if appropriate
            if (maxLength != -1)
            {
                field.getControl().getParams().put("maxLength", Integer.toString(maxLength));
                
                // set the crop argument to true so that textareas also restrict characters 
                constraint.getJSONParams().put("crop", true);
            }
        }
        else if (CONSTRAINT_REGEX.equals(constraint.getId()))
        {
            // if the cm:name property is being processed switch the validation handler
            // to the JavaScript specific nodeName handler.
            if (CM_NAME_PROP.equals(field.getName()))
            {
                constraint.setValidationHandler(CONSTRAINT_FILE_NAME_HANDLER);
                constraint.setJSONParams(new JSONObject());
            }
        }
    }
    
    /**
     * Processes the given constraint to add help text to the field's control
     * if relevant.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @param constraint The constraint
     * @throws JSONException
     */
    protected void processFieldConstraintHelp(ModelContext context, Field field, 
                FormField fieldConfig, Constraint constraint) throws JSONException
    {
        // add help text appropriate for the constraint, but only if the 
        // field doesn't already have any help text set
        if (field.getHelp() == null)
        {
            if (CONSTRAINT_LENGTH.equals(constraint.getId()))
            {
                String text = retrieveMessage(CONSTRAINT_MSG_LENGTH, 
                            constraint.getJSONParams().getInt("minLength"), 
                            constraint.getJSONParams().getInt("maxLength"));
                field.setHelp(text);
            }
            else if (CONSTRAINT_MINMAX.equals(constraint.getId()))
            {
                String text = retrieveMessage(CONSTRAINT_MSG_MINMAX,
                            constraint.getJSONParams().getInt("minValue"),
                            constraint.getJSONParams().getInt("maxValue"));
                field.setHelp(text);
            }
            else if (CONSTRAINT_NUMBER.equals(constraint.getId()))
            {
                field.setHelp(retrieveMessage(CONSTRAINT_MSG_NUMBER));
            }
        }
    }
    
    /**
     * Processes the control for the field.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldControl(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        FieldControl control = null;
        
        // retrieve the default controls configuration
        DefaultControlsConfigElement defaultControls = null;
        FormsConfigElement formsGlobalConfig = 
            (FormsConfigElement)this.configService.getGlobalConfig().getConfigElement(CONFIG_FORMS);
        if (formsGlobalConfig != null)
        {
            defaultControls = formsGlobalConfig.getDefaultControls();
        }
        
        // ensure we found the default controls config
        if (defaultControls == null)
        {
            throw new WebScriptException("Failed to locate default controls configuration");
        }
        
        boolean isPropertyField = !ASSOCIATION.equals(fieldDefinition.getString(MODEL_TYPE));
        
        Control defaultControlConfig = null;
        if (isPropertyField)
        {
            // get the default control for the property data type
            defaultControlConfig = defaultControls.getItems().get(
                        fieldDefinition.getString(MODEL_DATA_TYPE));
           
            // for backwards compatibility also check d:<dataType>
            if (defaultControlConfig == null)
            {
                defaultControlConfig = defaultControls.getItems().get(
                            OLD_DATA_TYPE_PREFIX + fieldDefinition.getString(MODEL_DATA_TYPE));
            }
        }
        else
        {
            // look for a specific type based default control for associations
            defaultControlConfig = defaultControls.getItems().get(
                        ASSOCIATION + ":" + fieldDefinition.getString(MODEL_ENDPOINT_TYPE));
           
            // get the generic default control for associations if a type specific one was not found
            if (defaultControlConfig == null)
            {
                defaultControlConfig = defaultControls.getItems().get(ASSOCIATION);
            }
        }
        
        // see if the fieldConfig already has a template defined, if not 
        // retrieve the default template for the field's data type
        if (fieldConfig != null && fieldConfig.getControl() != null && 
            fieldConfig.getControl().getTemplate() != null)
        {
            control = new FieldControl(fieldConfig.getControl().getTemplate());
        }
        else
        {
            if (defaultControlConfig != null)
            {
                control = new FieldControl(defaultControlConfig.getTemplate());
            }
            else if (logger.isWarnEnabled())
            {
                if (isPropertyField)
                {
                    logger.warn("No default control found for data type \"" + 
                                fieldDefinition.getString(MODEL_DATA_TYPE) + 
                                "\" whilst processing field \"" + 
                                fieldDefinition.getString(MODEL_NAME) + "\"");
                }
                else
                {
                    logger.warn("No default control found for associations" + 
                                "\" whilst processing field \"" + 
                                fieldDefinition.getString(MODEL_NAME) + "\"");
                }
            }
        }
        
        // send any type parameters returned from the server to the control
        if (isPropertyField && control != null && 
            fieldDefinition.has(MODEL_DATA_TYPE_PARAMETERS))
        {
            control.getParams().put(MODEL_DATA_TYPE_PARAMETERS, 
                        fieldDefinition.get(MODEL_DATA_TYPE_PARAMETERS).toString());
        }
        
        // get control parameters for the default control (if there is one)
        if (defaultControlConfig != null && control != null)
        {
           List<ControlParam> paramsConfig = defaultControlConfig.getParamsAsList();
           for (ControlParam param : paramsConfig)
           {
               control.getParams().put(param.getName(), param.getValue());
           }
        }
        
        // get overridden control parameters (if there are any)
        if (fieldConfig != null && control != null)
        {
            List<ControlParam> paramsConfig = fieldConfig.getControl().getParamsAsList();
            for (ControlParam param : paramsConfig)
            {
                control.getParams().put(param.getName(), param.getValue());
            }
        }
        
        // finally set the control model on the field model
        field.setControl(control);
    }
    
    /**
     * Processes the field for content. This method is used when a content field 
     * is being used in a form where JavaScript is disabled and thus AJAX is 
     * unavailable to retrieve the content, it must therefore be done server side.
     * 
     * @param context The context
     * @param field The field to be processed
     * @param fieldDefinition The definition of the field to be processed
     * @param fieldConfig The configuration of the field to be processed
     * @throws JSONException
     */
    protected void processFieldContent(ModelContext context, Field field, 
                JSONObject fieldDefinition, FormField fieldConfig) throws JSONException
    {
        // if the field is a content field and JavaScript is disabled
        // we need to retrieve the content here and store in model
        if (context.getFormUIModel().get(MODEL_CAPABILITIES) != null && "content".equals(field.getDataType()))
        {
            // NOTE: In the future when other capabilties are added the 'javascript'
            //       flag will need to be checked, for now it's the only reason
            //       the capabilities object will be present so a check is redundant
           
            if (logger.isDebugEnabled())
                logger.debug("Retrieving content for \"" + field.getConfigName() + "\" as JavaScript is disabled");
           
            // get the nodeRef of the content and then the content itself
            String nodeRef = getParameter(context.getRequest(), "itemId");
           
            try
            {
                ConnectorService connService = FrameworkUtil.getConnectorService();
                RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
                String currentUserId = requestContext.getUserId();
                HttpSession currentSession = ServletUtil.getSession(true);
                Connector connector = connService.getConnector(ENDPOINT_ID, currentUserId, currentSession);
               
                // call the form service
                Response response = connector.call("/api/node/content/" + nodeRef.replace("://", "/"));
                if (response.getStatus().getCode() == Status.STATUS_OK)
                {
                    field.setContent(response.getText());
                }
            }
            catch (Exception e)
            {
                if (logger.isErrorEnabled())
                    logger.error("Failed to get field content: ", e);
            }
        }
    }

    /**
     * Generates the "error" model used when an error occurs.
     *
     * @param errorResponse Response object representing the error
     * @return The "error" model
     */
    protected Map<String, Object> generateErrorModel(Response errorResponse)
    {
        return generateErrorModel(errorResponse, null);
    }

    /**
    * Generates the "error" model used when an error occurs.
    *
    * @param errorResponse Response object representing the error
    * @param errorKey String
    * @return The "error" model
    */
    protected Map<String, Object> generateErrorModel(Response errorResponse, String errorKey)
    {
        String error = "";
        
        // retrieve and log the error
        try
        {
            JSONObject json = new JSONObject(new JSONTokener(errorResponse.getResponse()));
            if (json.has(MODEL_MESSAGE))
            {
                error = json.getString(MODEL_MESSAGE);
                
                // Common AccessDeniedException is reported as a 500 server error from the repository
                if ((error.indexOf("org.alfresco.repo.security.permissions.AccessDeniedException") == -1) &&
                        (errorKey == null || errorKey.isEmpty()))
                {
                   if (logger.isErrorEnabled())
                       logger.error(error);
                }
            }
        }
        catch (JSONException je)
        {
            error= "";
        }
        
        if (errorKey == null || errorKey.isEmpty())
        {
            errorKey = MSG_DEFAULT_FORM_ERROR;
        }
        String id = errorKey + "." + errorResponse.getStatus().getCode();
        error = retrieveMessage(id);
        if (error.equals(id))
        {
            // use key if key+"."+status is not found
            error = retrieveMessage(errorKey);
        }

        // create model with error
        Map<String, Object> model = new HashMap<String, Object>(1);
        model.put(MODEL_ERROR, error);
        return model;
    }
    
    /**
     * Dumps the given form UI model to debug output (when debug is enabled).
     * 
     * @param model The form UI model to dump
     */
    protected void dumpFormUIModel(Map<String, Object> model)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("formUIModel = " + dumpMap(model, INDENT));
        }
    }
    
    @SuppressWarnings("unchecked")
    protected String dumpMap(Map<String, Object> map, String indent)
    {
        StringBuilder builder = new StringBuilder();
        
        // start
        builder.append("\n");
        if (indent.length() > INDENT.length())
        {
            builder.append(indent.substring(INDENT.length()));
        }
        builder.append("{");
        
        // name & values
        boolean firstKey = true;
        for (String key : map.keySet())
        {
            if (firstKey)
                firstKey = false;
            else
                builder.append(",");
            
            builder.append("\n");
            builder.append(indent);
            builder.append(key);
            builder.append(": ");
            
            Object value = map.get(key);
            if (value instanceof String)
            {
                builder.append("\"");
                builder.append(value);
                builder.append("\"");
            }
            else if (value instanceof Map)
            {
                builder.append(dumpMap((Map)value, indent + INDENT));
            }
            else if (value instanceof List)
            {
                boolean firstItem = true;
                builder.append("\n").append(INDENT).append("[");
                for (Object item : ((List)value))
                {
                    if (firstItem)
                        firstItem = false;
                    else
                        builder.append(",");
                    
                    builder.append("\n").append(INDENT).append(INDENT);
                    builder.append(item);
                }
                builder.append("\n").append(INDENT).append("]");
            }
            else
            {
                builder.append(value);
            }
        }
        
        // end
        builder.append("\n");
        if (indent.length() > INDENT.length())
        {
            builder.append(indent.substring(INDENT.length()));
        }
        builder.append("}");
        
        return builder.toString();
    }
    
    /**
     * Inner class used to hold all the context required to generate the model
     * and the model itself.
     *
     * <br><br>author Gavin Cornwell
     */
    protected class ModelContext
    {
        private Map<String, Object> formUIModel;
        
        private Map<String, JSONObject> propDefs;
        private Map<String, JSONObject> assocDefs;
        
        private WebScriptRequest request;
        private Mode mode;
        private JSONObject formDefinition;
        private FormConfigElement formConfig;
        
        private List<Constraint> constraints;
        private List<Element> structure;
        private Map<String, Field> fields;
        
        protected ModelContext(WebScriptRequest request, Mode mode, 
                    JSONObject formDefinition, FormConfigElement formConfig)
        {
            this.request = request;
            this.mode = mode;
            this.formDefinition = formDefinition;
            this.formConfig = formConfig;
            
            cacheFieldDefinitions();
        }
        
        public void cacheFieldDefinitions()
        {
            this.propDefs = new HashMap<String, JSONObject>(8);
            this.assocDefs = new HashMap<String, JSONObject>(8);
            
            try
            {
                JSONObject data = this.formDefinition.getJSONObject(MODEL_DATA);
                JSONObject definition = data.getJSONObject(MODEL_DEFINITION);
                JSONArray fields = definition.getJSONArray(MODEL_FIELDS);
                
                for (int x = 0; x < fields.length(); x++)
                {
                    JSONObject fieldDef = fields.getJSONObject(x);
                    if (fieldDef.getString(MODEL_TYPE).equals(PROPERTY))
                    {
                        this.propDefs.put(fieldDef.getString(MODEL_NAME), fieldDef);
                    }
                    else if (fieldDef.getString(MODEL_TYPE).equals(ASSOCIATION))
                    {
                        this.assocDefs.put(fieldDef.getString(MODEL_NAME), fieldDef);
                    }
                }
            }
            catch (JSONException je)
            {
                if (logger.isErrorEnabled())
                    logger.error("Failed to cache field definitions", je);
            }
        }

        public void setFormUIModel(Map<String, Object> formUIModel)
        {
            this.formUIModel = formUIModel;
        }
        
        public Map<String, Object> getFormUIModel()
        {
            return this.formUIModel;
        }

        public Map<String, JSONObject> getPropertyDefinitions()
        {
            return this.propDefs;
        }

        public Map<String, JSONObject> getAssociationDefinitions()
        {
            return this.assocDefs;
        }

        public WebScriptRequest getRequest()
        {
            return this.request;
        }

        public Mode getMode()
        {
            return this.mode;
        }

        public JSONObject getFormDefinition()
        {
            return this.formDefinition;
        }

        public FormConfigElement getFormConfig()
        {
            return this.formConfig;
        }

        public List<Constraint> getConstraints()
        {
            if (this.constraints == null)
            {
                this.constraints = new ArrayList<Constraint>(2);
            }
            
            return this.constraints;
        }

        public List<Element> getStructure()
        {
            if (this.structure == null)
            {
                this.structure = new ArrayList<Element>(4);
            }
            
            return this.structure;
        }

        public Map<String, Field> getFields()
        {
            if (this.fields == null)
            {
                this.fields = new HashMap<String, Field>(8);
            }
            
            return this.fields;
        }
    }
    
    /**
     * Base inner class to represent form elements i.e a field or set
     * 
     * NOTE: This class has to be public for the template engine to
     *       access the object correctly.
     *
     * <br><br>author Gavin Cornwell
     */
    public abstract class Element
    {
        protected String kind;
        protected String id;
        
        public String getKind()
        {
            return this.kind;
        }
        
        public String getId()
        {
            return this.id;
        }

        @Override
        public String toString()
        {
            StringBuilder builder = new StringBuilder();
            builder.append(this.kind);
            builder.append("(");
            builder.append(this.id);
            builder.append(")");
            return builder.toString();
        }
    }
    
    /**
     * Represents a pointer to a field, used in the form UI model.
     */
    public class FieldPointer extends Element
    {
        FieldPointer(String id)
        {
            this.kind = FIELD;
            this.id = id;
        }
    }

    /**
     * Represents a set of fields and/or nested sets.
     */
    public class Set extends Element
    {
        protected String appearance;
        protected String template;
        protected String label;
        protected List<Element> children;
        
        Set(FormSet setConfig)
        {
            this.kind = SET;
            this.id = setConfig.getSetId();
            this.appearance = setConfig.getAppearance();
            this.template = setConfig.getTemplate();
            this.label = discoverSetLabel(setConfig);
            this.children = new ArrayList<Element>(4);
        }
        
        Set(String id, String label)
        {
            this.kind = SET;
            this.id = id;
            this.label = label;
            this.children = new ArrayList<Element>(1);
        }

        public void addChild(Element child)
        {
            this.children.add(child);
        }
        
        public String getAppearance()
        {
            return this.appearance;
        }

        public String getTemplate()
        {
            return this.template;
        }

        public String getLabel()
        {
            return this.label;
        }

        public List<Element> getChildren()
        {
            return this.children;
        }
        
        @Override
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.kind);
            buffer.append("(id=").append(this.id);
            buffer.append(" appearance=").append(this.appearance);
            buffer.append(" label=").append(this.label);
            buffer.append(" template=").append(this.template);
            buffer.append(" children=[");
            boolean first = true;
            for (Element child : this.children)
            {
                if (first)
                    first = false;
                else
                    buffer.append(", ");
                
                buffer.append(child);
            }
            buffer.append("])");
            return buffer.toString();
        }
    }
    
    /**
     * Represents a field on a form.
     */
    public class Field extends Element
    {
        protected String name;
        protected String configName;
        protected String label;
        protected String description;
        protected String help;
        protected boolean helpEncodeHtml = true;
        protected FieldControl control; 
        protected String dataKeyName;
        protected String dataType;
        protected String type;
        protected String content;
        protected String endpointDirection;
        protected Object value;
        protected boolean disabled = false;
        protected boolean mandatory = false;
        protected boolean transitory = false;
        protected boolean repeating = false;
        
        protected String indexTokenisationMode;
        
        Field()
        {
            this.kind = FIELD;
        }
        
        public void setId(String id)
        {
            this.id = id;
        }
        
        public String getConfigName()
        {
            return this.configName;
        }

        public void setConfigName(String configName)
        {
            this.configName = configName;
        }

        public FieldControl getControl()
        {
            return this.control;
        }

        public void setControl(FieldControl control)
        {
            this.control = control;
        }

        public String getDataKeyName()
        {
            return this.dataKeyName;
        }

        public void setDataKeyName(String dataKeyName)
        {
            this.dataKeyName = dataKeyName;
        }

        public String getDataType()
        {
            return this.dataType;
        }

        public void setDataType(String dataType)
        {
            this.dataType = dataType;
        }

        public String getDescription()
        {
            return this.description;
        }

        public void setDescription(String description)
        {
            this.description = description;
        }

        public boolean isDisabled()
        {
            return this.disabled;
        }

        public void setDisabled(boolean disabled)
        {
            this.disabled = disabled;
        }

        public String getLabel()
        {
            return this.label;
        }

        public void setLabel(String label)
        {
            this.label = label;
        }

        public boolean isMandatory()
        {
            return this.mandatory;
        }

        public void setMandatory(boolean mandatory)
        {
            this.mandatory = mandatory;
        }

        public boolean isTransitory()
        {
            return this.transitory;
        }

        public void setTransitory(boolean transitory)
        {
            this.transitory = transitory;
        }

        public String getName()
        {
            return this.name;
        }

        public void setName(String name)
        {
            this.name = name;
        }

        public boolean isRepeating()
        {
            return this.repeating;
        }

        public void setRepeating(boolean repeating)
        {
            this.repeating = repeating;
        }

        public String getType()
        {
            return this.type;
        }

        public void setType(String type)
        {
            this.type = type;
        }

        public Object getValue()
        {
            return this.value;
        }

        public void setValue(Object value)
        {
            this.value = value;
        }

        public String getContent()
        {
            return this.content;
        }

        public void setContent(String content)
        {
            this.content = content;
        }

        public String getHelp()
        {
            return this.help;
        }

        public void setHelp(String help)
        {
            this.help = help;
        }

        public boolean getHelpEncodeHtml()
        {
            return this.helpEncodeHtml;
        }

        public void setHelpEncodeHtml(boolean encode)
        {
            this.helpEncodeHtml = encode;
        }
        
        public String getEndpointDirection()
        {
            return this.endpointDirection;
        }

        public void setEndpointDirection(String endpointDirection)
        {
            this.endpointDirection = endpointDirection;
        }

        public String getEndpointType()
        {
            return getDataType();
        }
        
        public boolean isEndpointMandatory()
        {
            return this.mandatory;
        }
        
        public boolean isEndpointMany()
        {
            return this.repeating;
        }
        
        public String getIndexTokenisationMode()
        {
            return indexTokenisationMode;
        }

        public void setIndexTokenisationMode(String indexTokenisationMode)
        {
            this.indexTokenisationMode = indexTokenisationMode;
        }

        @Override
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append(this.kind);
            buffer.append("(id=").append(this.id);
            buffer.append(" name=").append(this.name);
            buffer.append(" configName=").append(this.configName);
            buffer.append(" type=").append(this.type);
            buffer.append(" value=").append(this.value);
            buffer.append(" label=").append(this.label);
            buffer.append(" description=").append(this.description);
            buffer.append(" help=").append(this.help);
            buffer.append(" helpEncodeHtml=").append(this.helpEncodeHtml);
            buffer.append(" dataKeyName=").append(this.dataKeyName);
            buffer.append(" dataType=").append(this.dataType);
            buffer.append(" endpointDirection=").append(this.endpointDirection);
            buffer.append(" disabled=").append(this.disabled);
            buffer.append(" mandatory=").append(this.mandatory);
            buffer.append(" repeating=").append(this.repeating);
            buffer.append(" transitory=").append(this.transitory);
            buffer.append(" indexTokenisationMode=").append(this.indexTokenisationMode);
            buffer.append(" ").append(this.control);
            buffer.append(")");
            return buffer.toString();
        }
    }
    
    /**
     * Represents the control used by a form field.
     */
    public class FieldControl
    {
        protected String template;
        protected Map<String, String> params;
        
        FieldControl(String template)
        {
            this.template = template;
            this.params = new HashMap<String, String>(4);
        }
        
        public String getTemplate()
        {
            return this.template;
        }

        public void setTemplate(String template)
        {
            this.template = template;
        }

        public Map<String, String> getParams()
        {
            return this.params;
        }
        
        @Override
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append("control(template=").append(this.template);
            buffer.append(" params=").append(this.params);
            buffer.append(")");
            return buffer.toString();
        }
    }
    
    /**
     * Represents a field constraint.
     */
    public class Constraint
    {
        private String fieldId;
        private String id;
        private String validationHandler;
        private JSONObject params;
        private String message;
        private String event;
        
        Constraint(String fieldId, String id, String handler, JSONObject params)
        {
            this.fieldId = fieldId;
            this.id = id;
            this.validationHandler = handler;
            this.params = params;
        }

        public String getFieldId()
        {
            return this.fieldId;
        }

        public String getId()
        {
            return this.id;
        }

        public String getValidationHandler()
        {
            return this.validationHandler;
        }

        public void setValidationHandler(String validationHandler)
        {
            this.validationHandler = validationHandler;
        }

        /**
         * Returns the parameters formatted as a JSON string.
         * 
         * @return String
         */
        public String getParams()
        {
            if (this.params == null)
            {
                this.params = new JSONObject();
            }
            
            return this.params.toString();
        }
        
        public JSONObject getJSONParams()
        {
            return this.params;
        }

        public void setJSONParams(JSONObject params)
        {
            this.params = params;
        }

        public String getMessage()
        {
            return this.message;
        }

        public void setMessage(String message)
        {
            this.message = message;
        }

        public String getEvent()
        {
            return this.event;
        }

        public void setEvent(String event)
        {
            this.event = event;
        }
        
        @Override
        public String toString()
        {
            StringBuilder buffer = new StringBuilder();
            buffer.append("constraint(fieldId=").append(this.fieldId);
            buffer.append(" id=").append(this.id);
            buffer.append(" validationHandler=").append(this.validationHandler);
            buffer.append(" event=").append(this.event);
            buffer.append(" message=").append(this.message);
            buffer.append(")");
            return buffer.toString();
        }
    }
    
    /**
     * Helper class used to retrieve localized messages.
     */
    protected class MessageHelper extends AbstractMessageHelper
    {
        public MessageHelper(WebScript webscript)
        {
            super(webscript);
        }
        
        public String get(final String id, final Object... args)
        {
            return this.resolveMessage(id, args);
        }
    }
}




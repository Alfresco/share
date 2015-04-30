/*
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.directives;

import java.io.IOException;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.ServerConfigElement;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.CssImageDataHandler;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DojoDependencies;
import org.springframework.extensions.surf.DojoDependencyHandler;
import org.springframework.extensions.surf.I18nDependencyHandler;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.AbstractModelObject;
import org.springframework.extensions.surf.uri.UriUtils;
import org.springframework.extensions.webscripts.LocalWebScriptContext;
import org.springframework.extensions.webscripts.LocalWebScriptRuntime;
import org.springframework.extensions.webscripts.LocalWebScriptRuntimeContainer;
import org.springframework.extensions.webscripts.Match;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.WebScriptProcessor;
import org.springframework.extensions.webscripts.json.JSONWriter;

import freemarker.core.Environment;
import freemarker.template.SimpleHash;
import freemarker.template.SimpleScalar;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateHashModel;
import freemarker.template.TemplateModelException;

/**
 * <p>The purpose of this widget is to allow "New Share" pages to be created via a single WebScript. This
 * directive processes the contents of the model defined by the JavaScript controller into the client-side
 * JavaScript required to define a new page.</p>
 * 
 * @author David Draper
 */
public class ProcessJsonModelDirective extends JavaScriptDependencyDirective
{
    private static final Log logger = LogFactory.getLog(DojoDependencyHandler.class);
    
    public ProcessJsonModelDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }
    
    public static final String DIRECTIVE_NAME = "processJsonModel";
    
    public static final String MODEL_KEY = "jsonModel";
    
    public static final String TARGET_DOM_ID_KEY = "rootNodeId";
    
    public static final String PUB_SUB_SCOPE_KEY = "pubSubScope";
    
    public static final String GROUP_MEMBERSHIPS_KEY = "groupMemberships";
    
    /**
     * <p>A {@link DojoDependencyHandler} is required for accessing the Dojo configuration for Surf and for processing the 
     * dependencies.</p>
     */
    private DojoDependencyHandler dojoDependencyHandler = null;

    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }

    private I18nDependencyHandler i18nDependencyHandler = null;
    public void setI18nDependencyHandler(I18nDependencyHandler i18nDependencyHandler)
    
    {
        this.i18nDependencyHandler = i18nDependencyHandler;
    }

    private LocalWebScriptRuntimeContainer webScriptsContainer;
    public void setWebScriptsContainer(LocalWebScriptRuntimeContainer webScriptsContainer)
    {
        this.webScriptsContainer = webScriptsContainer;
    }
    private ConfigService configService;
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * This is a custom token matching pattern to use to override the default one defined in {@link UriUtils}. It
     * is roughly the same but uses double curly braces {{ }} rather than single to avoid potential clashes within
     * the JSON structure.
     */
    public static Pattern pattern = Pattern.compile("\\$\\$([A-Za-z0-9_\\-]*)\\$\\$");
    
    /**
     * <p>Looks up the object from the model defined by the WebScript controller and processes all the dependencies that it declares. When running
     * in aggregation mode (which should be used for production) it generates the JavaScript source that simulates a Dojo build layer and creates
     * the JavaScript code to create the root Dojo page object (defined in the Surf Dojo configuration) and places a requirement on that generated
     * build layer.</p> 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id, 
                                                                       String action, 
                                                                       String target,
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        // Get the FreeMarker model...
        TemplateHashModel model = env.getDataModel();
        
        // Get the group...
        String group = getStringProperty(params, DirectiveConstants.GROUP_PARAM, false); // null is acceptable as a group (it is effectively the default group)
       
        // We need to build a JSON representation of the services required for the page..
        // A "Service" is a client-side JavaScript object with no visible content on the page. They are used to 
        // decouple widgets from common actions. Examples might include a DialogService for controlling the Dialogs
        // used on a page and an UploadService for controlling content uploads.
        String servicesJSONStr;
        List<String> services = Collections.<String>emptyList();
        
        // We need to build a JSON representation of the widgets required for the page...
        String widgetsJSONStr;
        List<String> widgets = Collections.<String>emptyList();
        
        // We need to build a JSON representation of topics to publish on page load...
        String publishOnLoadJSONStr;
        List<String> publishOnLoad = Collections.<String>emptyList();
        
        String targetDomId = "content";
        String pubSubScope = null;
        String groupMemberships = null;
        
        // Get the root object from the model and get the services and widgets from it...
        Object o = model.get(MODEL_KEY);
        
        // Check the supplied jsonModel type to see if it is has been provided as a JSON string
        // rather than a JavaScript object literal. This will always be the case when a unit test
        // is being processed from Share as the model will be defined as JSON. In this eventuality
        // it is necessary to parse the String and then convert the resulting object into a SimpleHash
        // and then continue as before...
        if (o instanceof SimpleScalar)
        {
            String jsonString = (String) o.toString();
            JSONParser jp = new JSONParser();
            try
            {
                Object parsedObject = jp.parse(jsonString);
                if (parsedObject instanceof JSONObject)
                {
                    JSONObject jo = (JSONObject) parsedObject;
                    o = new SimpleHash(jo);
                }
            }
            catch (ParseException e)
            {
                throw new TemplateException(e, env);
            }
        }
        
        if (o instanceof SimpleHash)
        {
            SimpleHash pageConfig = (SimpleHash) o;
            
            // Get the id of the DOM node to attach the root Dojo widget to. This will default to
            // 'content' if nothing is specified...
            Object _targetDomId = pageConfig.get(TARGET_DOM_ID_KEY);
            if (_targetDomId instanceof SimpleScalar)
            {
                targetDomId = _targetDomId.toString().trim();
            }
            
            Object _pubSubScope = pageConfig.get(PUB_SUB_SCOPE_KEY);
            if (_pubSubScope instanceof SimpleScalar)
            {
                pubSubScope = _pubSubScope.toString().trim();
            }
            
            Object _groupMemberships = pageConfig.get(GROUP_MEMBERSHIPS_KEY);
            if (_groupMemberships instanceof SimpleScalar)
            {
                StringBuilder groupsJSONStr = new StringBuilder("{");
                groupMemberships = _groupMemberships.toString().trim();
                String[] groups = groupMemberships.split(",");
                for (String g: groups)
                {
                    if (g != null && !g.trim().equals(""))
                    {
                        groupsJSONStr.append("\"");
                        groupsJSONStr.append(g);
                        groupsJSONStr.append("\":true,");
                    }
                }
                if (groupsJSONStr.length() > 1)
                {
                    groupsJSONStr.deleteCharAt(groupsJSONStr.length() - 1);
                }
                groupsJSONStr.append("}");
                groupMemberships = groupsJSONStr.toString();
            }
            
            o = pageConfig.get("services");
            if (o instanceof SimpleSequence)
            {
                services = ((SimpleSequence) o).toList();
            }
            
            o = pageConfig.get("widgets");
            if (o instanceof SimpleSequence)
            {
                widgets = ((SimpleSequence) o).toList();
            }
            
            o = pageConfig.get("publishOnReady");
            if (o instanceof SimpleSequence)
            {
                publishOnLoad = ((SimpleSequence) o).toList();
            }
        }
        
        // Create string of JSON data encoding any tokens that have been used (e.g. to allow access to request parameters, etc)
        servicesJSONStr = UriUtils.replaceTokens(JSONWriter.encodeToJSON(services), getRequestContext(), pattern, 1, "");
        widgetsJSONStr = UriUtils.replaceTokens(JSONWriter.encodeToJSON(widgets), getRequestContext(), pattern, 1, "");
        publishOnLoadJSONStr = UriUtils.replaceTokens(JSONWriter.encodeToJSON(publishOnLoad), getRequestContext(), pattern, 1, "");
        
        // Define an ordered map of the dependencies required for the Page... we will use this to construct the
        // relevant files to import on the page (ideally in an aggregated form)...
        final Map<String, DojoDependencies> dependenciesForCurrentRequest = new LinkedHashMap<String, DojoDependencies>(256);
        
        // Check to see if the default root page widget has been overridden...
        // This has been done to allow WebScripts to vary from the default. The use case for this is allowing for 
        // the alfresco/core/FilteredPage module to be selectively used instead of the alfresco/core/Page module (which
        // is the default configured value in Surf)...
        String rootModule = getStringProperty(params, "rootModule", false);
        if (rootModule == null)
        {
            final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            ScriptConfigModel config = rc.getExtendedScriptConfigModel(null);
            Map<String, ConfigElement> configs = (Map<String, ConfigElement>)config.getScoped().get("WebFramework");
            if (configs != null)
            {
                WebFrameworkConfigElement wfce = (WebFrameworkConfigElement) configs.get("web-framework");
                rootModule = wfce.getDojoPageWidget();
            }
            else
            {
                rootModule = this.getWebFrameworkConfig().getDojoPageWidget();
            }
        }
        
        // We know that we will definitely require the root "Page" object as that's the root object that will be instantiated
        // The actual widget used for the Page is defined in the Surf configuration. We will get its dependencies and then add
        // its source to the end of the aggregate file (so that it is passed in as the object to be instantiated)...
        final String pagePath = this.dojoDependencyHandler.getPath(null, rootModule) + ".js";
        final DojoDependencies pageDeps = this.dojoDependencyHandler.getDependencies(pagePath);
        this.dojoDependencyHandler.recursivelyProcessDependencies(pageDeps, dependenciesForCurrentRequest);
        
        String domReadyPath = this.dojoDependencyHandler.getPath(null, "dojo/domReady") + ".js";
        DojoDependencies domReadyDeps = this.dojoDependencyHandler.getDependencies(domReadyPath);
        dependenciesForCurrentRequest.put(domReadyPath, domReadyDeps);
        this.dojoDependencyHandler.recursivelyProcessDependencies(domReadyDeps, dependenciesForCurrentRequest);
        
        // Get all the dependencies for the defined services...
        if (services != null)
        {
            // Originally it was assumed that services would be defined as simple strings, however in order
            // to allow services to have configurable options they can either be defined as Strings or Objects
            // within the same array...
            for (Object service: services)
            {
                String servicePath = null;
                if (service instanceof Map)
                {
                    Map map = (Map) service;
                    Object s = map.get(DojoDependencyHandler.WIDGET_NAME);
                    if (s instanceof String)
                    {
                        servicePath = this.dojoDependencyHandler.getPath(null, (String)s) + ".js";
                    }
                }
                else if (service instanceof String)
                {
                    servicePath = this.dojoDependencyHandler.getPath(null, (String)service) + ".js";
                }
                if (servicePath != null)
                {
                    DojoDependencies serviceDeps = this.dojoDependencyHandler.getDependencies(servicePath);
                    dependenciesForCurrentRequest.put(servicePath, serviceDeps);
                    this.dojoDependencyHandler.recursivelyProcessDependencies(serviceDeps, dependenciesForCurrentRequest);
                }
            }
        }
        
        // Get all the dependencies for the defined widgets...
        this.dojoDependencyHandler.processControllerWidgets(widgets, dependenciesForCurrentRequest);
        
        // It's also necessary to run the JSON strings through the dependency analysis in order to capture
        // any "strangely" nested widget requirements and also to leverage the "widgets*" RegEx pattern that
        // the processControllerWidgets method will miss. 
        DojoDependencies dd = new DojoDependencies();
        this.dojoDependencyHandler.processString(widgetsJSONStr, dd, dependenciesForCurrentRequest);
        this.dojoDependencyHandler.processString(servicesJSONStr, dd, dependenciesForCurrentRequest);
        
        // It doesn't actually matter what we declare the initial dependency as being, it will just get output in
        // the built cache object as "null". What's important is that we have a starting point to build from...
        // it's what goes into the DojoDependencies instance that is important...
        dependenciesForCurrentRequest.put("alfresco/dummy/module.js", dd);
        
        // Build the checksum for the generated output that includes all the files
        // This will construct and cache the output for a given set of dependencies if it has not done so.
        final String checksum = this.dojoDependencyHandler.getChecksumForDependencies(dependenciesForCurrentRequest, pagePath, pageDeps);
        
        // This resource controller prefix is required when aggregate resources are not enabled...
        String prefix = this.dependencyAggregator.getServletContext().getContextPath() + this.dependencyHandler.getResourceControllerMapping() + CssImageDataHandler.FORWARD_SLASH;
        
        // Process all the additional CSS requirements for the widgets that will be required on the page...
        OutputCSSContentModelElement outputCss = null;
        DeferredContentTargetModelElement te = getModel().getDeferredContent(OutputCSSDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME);
        if (te instanceof OutputCSSContentModelElement)
        {
            outputCss = ((OutputCSSContentModelElement) te);

            // Work through all the dependencies and add all the dependencies to the relevant deferred content model...
            this.dojoDependencyHandler.processCssDependencies(dependenciesForCurrentRequest, outputCss, prefix, group);
        }
        
        // Process all non AMD requirements for the widgets required on the page...
        OutputJavaScriptContentModelElement outputJs = null;
        te = getModel().getDeferredContent(OutputJavaScriptDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME);
        if (te instanceof OutputJavaScriptContentModelElement)
        {
            outputJs = ((OutputJavaScriptContentModelElement) te);

            // Work through all the dependencies and add all the dependencies to the relevant deferred content model...
            this.dojoDependencyHandler.processNonAmdDependencies(dependenciesForCurrentRequest, outputJs, prefix, group);
        }
        
        // Build JavaScript content that defines the i18n maps. This content is then merged into the extensibility model
        // as a JavaScript dependency and will be requested on the page. This content is static for the JSON model (e.g.
        // it is not effected by variables passed into the model. Therefore it can be safely cached by the browser.
        // Keeping this out of the HTML page will improve performance...
        String i18nContent = buildI18nForPage(dependenciesForCurrentRequest, model);
        DeferredContentTargetModelElement targetElement = getModel().getDeferredContent(OutputJavaScriptDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME);
        DependencyDirectiveData directiveData = new DependencyDirectiveData(id, 
                                                                            action, 
                                                                            target, 
                                                                            getDirectiveName(), 
                                                                            body, 
                                                                            env, 
                                                                            i18nContent, 
                                                                            group,
                                                                            true, 
                                                                            targetElement);
        try
        {
            merge(directiveData, params);
        }
        catch (IOException e)
        {
            throw new TemplateException(e, env);
        }
        
        String constantsContent = buildConstantsForPage();
        DependencyDirectiveData constantsDirectiveData = new DependencyDirectiveData(id, 
                                                                                     action, 
                                                                                     target, 
                                                                                     getDirectiveName(), 
                                                                                     body, 
                                                                                     env, 
                                                                                     constantsContent, 
                                                                                     group,
                                                                                     true, 
                                                                                     targetElement);
        try
        {
            merge(constantsDirectiveData, params);
        }
        catch (IOException e)
        {
            throw new TemplateException(e, env);
        }
        
        // Construct the JavaScript string required to instantiate the page...
        String jsContent = buildJavaScriptForPage(checksum, servicesJSONStr, widgetsJSONStr, publishOnLoadJSONStr, targetDomId, pubSubScope, groupMemberships, dependenciesForCurrentRequest, model);
        
        // Output the generated JavaScript directly to the output stream...
        // Here we create an anonymous inner class for the TemplateDirectiveBody and inject the JavaScript content
        // directly into it. This is then passed into the DirectiveData object so that it is rendered directly to the
        // output stream...
        TemplateDirectiveBody tdb = new TemplateDirectiveBody()
        {
            private String content = "";
            public TemplateDirectiveBody setContent(String content)
            {
                this.content = content;
                return this;
            }
            public void render(Writer writer) throws TemplateException, IOException
            {
                writer.write(content);
            }
        }.setContent(jsContent);
        
        DefaultExtensibilityDirectiveData d = 
           new DefaultExtensibilityDirectiveData(id,
                                                 ExtensibilityDirective.ACTION_MERGE, 
                                                 target, 
                                                 ProcessJsonModelDirective.DIRECTIVE_NAME, 
                                                 tdb, 
                                                 env);
        return d;
    }
    
    /**
     * Constructs the i18n properties JavaScript content for the page. This will be loaded into the page
     * as a separate resource.
     * 
     * @param dependenciesForCurrentRequest
     * @param model
     * @return
     */
    protected String buildI18nForPage(Map<String, DojoDependencies> dependenciesForCurrentRequest, 
                                      TemplateHashModel model)
    {
        StringBuilder content = new StringBuilder(10240);
        content.append(DependencyAggregator.INLINE_AGGREGATION_MARKER);
        
        // Generate the JavaScript to setup the messages objects...
        content.append(this.i18nDependencyHandler.generateI18nJavaScript(dependenciesForCurrentRequest));
        
        try
        {
            // Add in any messages specified by the WebScript that defines the page...
            String messages = "";
            Object _messages = model.get(DirectiveConstants.MESSAGES);
            if (_messages instanceof SimpleScalar)
            {
                messages = ((SimpleScalar) _messages).toString();
                String globalMessagesObject = getWebFrameworkConfig().getDojoMessagesObject();
                content.append("var pageScopeMsgs = ");
                content.append(messages);
                content.append(";\n   if (");
                content.append(globalMessagesObject);
                content.append(".messages.pageScope) {\n   for (var key in pageScopeMsgs) { \n      ");
                content.append(globalMessagesObject);
                content.append(".messages.pageScope[key] = pageScopeMsgs[key];\n}\n}\nelse {\n   ");
                content.append(globalMessagesObject);
                content.append(".messages.pageScope = ");
                content.append(messages);
                content.append(";\n}\n");
            }
        }
        catch (TemplateModelException e)
        {
        // No action required.
        }
        return content.toString();
    }
    
    /**
     * <p>Constructs the JavaScript required to create a page object. 
     * </p>
     * @param servicesJSONStr A JSON string containing all the services required on the page.
     * @param widgetsJSONStr A JSON string containing all the widgets required on the page.
     * @param rootDomId The ID of the DOM node to bind the page widget to
     * @param pubSubScope A pubSubScope to apply to the page widget
     * @param groupMemberships The group membership data for the current user (to set on the page object)
     * @param dependenciesForCurrentRequest The dependencies for this page
     * @param model The Freemarker model
     * @return The <script> tag to output that will build the page.
     */
    protected String buildJavaScriptForPage(String checksum, 
                                            String servicesJSONStr, 
                                            String widgetsJSONStr,
                                            String publishOnLoadJSONStr,
                                            String rootDomId, 
                                            String pubSubScope, 
                                            String groupMemberships, 
                                            Map<String, DojoDependencies> dependenciesForCurrentRequest, 
                                            TemplateHashModel model)
    {
        StringBuilder content = new StringBuilder(10240);
        content.append(DirectiveConstants.OPEN_SCRIPT);
        
        // Enable the Dojo loader trace if configured to do so (this can be useful for determining that aggregate loading
        // behaviour)...
        if (getWebFrameworkConfig().isDojoLoaderTraceEnabled())
        {
            content.append("require.trace.set(\"loader-inject\", 1);\nrequire.trace.set(\"loader-define\", 0);\n");
        }
        
        // Set a logging attribute on the "messages" global object (this isn't ideal, but is acceptable for now)...
        // The reason for this is to allow the Core Alfresco module to determine whether or not to output console log or not...
        // This value could also be set with a filter to control *what* gets output...
        if (this.dependencyAggregator.isDebugMode())
        {
            content.append(getWebFrameworkConfig().getDojoMessagesObject());
            content.append(".logging = true;\n");
        }
        
        // Set the correct requirements depending upon whether dependency aggregation is enabled or not
        content.append("require(['");
        content.append(checksum.substring(3, checksum.length()-3));
        content.append("','dojo/domReady!");
        content.append("'], function(Page) {\n var p = new Page({services:");
        content.append(servicesJSONStr);
        content.append(",widgets:");
        content.append(widgetsJSONStr);
        content.append(",publishOnReady:");
        content.append(publishOnLoadJSONStr);
        
        if (pubSubScope != null && !pubSubScope.trim().equals(""))
        {
            content.append(",pubSubScope:\"");
            content.append(pubSubScope);
            content.append("\"");
        }
        
        if (groupMemberships != null && !groupMemberships.trim().equals(""))
        {
            content.append(",groupMemberships:");
            content.append(groupMemberships);
        }
        
        // Get the ID of the WebScript being executed, this is done for debugging purposes...
        String url = this.getModelObject().getProperty("url");
        if (url != null && this.webScriptsContainer != null && this.webScriptsContainer.getRegistry() != null)
        {
            Match match = this.webScriptsContainer.getRegistry().findWebScript("GET", url);
            String wsId = match.getWebScript().getDescription().getId();
            if (wsId != null)
            {
                content.append(",webScriptId:\"");
                content.append(wsId);
                content.append("\"");
            }
        }
        
        content.append("\n}, '");
        content.append(rootDomId);
        content.append("');\n});\n");
        content.append(DirectiveConstants.CLOSE_SCRIPT);
        return content.toString();
    }
    
    /**
     * Builds a Dojo cache JavaScript object containing the output from the /constants/Default WebScript
     * that includes the context specific data (such as user information). This is can be immediately processed
     * into the Dojo cache such that an asynchronous request isn't required to to be made. 
     * 
     * @return
     */
    protected String buildConstantsForPage()
    {
        StringBuilder content = new StringBuilder(10240);
        content.append(DependencyAggregator.INLINE_AGGREGATION_MARKER);
        content.append("require({cache:{\n");
        content.append("'service/constants/Default':function(){\n");
        Writer writer = new StringWriter();
        this.runWebScript("/constants/Default", writer);
        content.append(writer.toString());
        content.append("   }\n}});");
        return content.toString();
    }
    
    /**
     * Run a WebScript against the supplied {@link Writer}. This by-passes the {@link ExtensibilityModel and so that
     * we can build a cache file directly from the processed content.
     * 
     * @param uri
     * @param writer
     */
    protected void runWebScript(String uri, Writer writer)
    {
        RequestContext context = getRequestContext();
        
        // Construct a "context" object that the Web Script engine will utilise
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        
        // Copy in request parameters into a HashMap
        // This is so as to be compatible with UriUtils (and Token substitution)
        webScriptContext.setTokens(context.getParameters());
        
        // Begin to process the actual web script
        // Get the web script url, perform token substitution and remove query string
        String url = UriUtils.replaceTokens(uri, context, null, null, "");
        webScriptContext.setScriptUrl((url.indexOf('?') == -1 ? url : url.substring(0, url.indexOf('?'))));
        
        // Get up the request path.
        // If none is supplied, assume the servlet path.
        String requestPath = (String) context.getValue("requestPath");
        if (requestPath == null)
        {
            requestPath = context.getContextPath();
        }
        
        webScriptContext.setExecuteUrl(requestPath + WebScriptProcessor.WEBSCRIPT_SERVICE_SERVLET + url);
        
        // Set up state onto the local web script context
        webScriptContext.setRuntimeContainer(this.webScriptsContainer);
        webScriptContext.setRequestContext(context);
        
        ModelObject dmo = new DummyModelObject();
        webScriptContext.setModelObject(dmo);
        
        ServerProperties serverProperties;
        Config config = this.configService.getConfig("Server");
        serverProperties = (ServerConfigElement)config.getConfigElement(ServerConfigElement.CONFIG_ELEMENT_ID);
        
        // Construct the Web Script Runtime
        // This bundles the container, the context and the encoding
        LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(writer, this.webScriptsContainer, serverProperties, webScriptContext);
        
        // set the method onto the runtime
        if (context.getRequestMethod() != null)
        {
            runtime.setScriptMethod(LocalWebScriptRuntime.DEFAULT_METHOD_GET);
        }
        
        // Suppress extensibility on the container...
        // This has the effect of ensuring that the WebScript doesn't attempt to output the content into an extensibility
        // model but will instead write the content directly to the StringWriter provided. This means that we can then grab
        // the generated response directly... 
        this.webScriptsContainer.suppressExtensibility();
        try
        {
            runtime.executeScript();
        }
        finally
        {
            // Make sure to unsupress extensibility on the container!!
            this.webScriptsContainer.unsuppressExtensibility();
        }
    }

    /**
     * This inner-class is required to run the WebScript for generating the default constants file (see the runWebScript method)
     * @author dave
     */
    @SuppressWarnings("serial")
    class DummyModelObject extends AbstractModelObject
    {
        @Override
        public String getTypeId()
        {
            return null;
        }

        @Override
        public Map<String, Serializable> getProperties()
        {
            return new HashMap<String, Serializable>(16, 1.0f);
        }

        @Override
        public Map<String, Serializable> getCustomProperties()
        {
            return new HashMap<String, Serializable>(16, 1.0f);
        }
        
        
    }
}

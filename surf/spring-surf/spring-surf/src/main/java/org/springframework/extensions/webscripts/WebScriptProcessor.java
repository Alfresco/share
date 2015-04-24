/**
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

package org.springframework.extensions.webscripts;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.ServerConfigElement;
import org.springframework.extensions.config.ServerProperties;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.render.AbstractProcessor;
import org.springframework.extensions.surf.render.ProcessorContext;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.types.SurfBugData;
import org.springframework.extensions.surf.uri.UriUtils;

/**
 * The WebScriptRenderer is an implementation of Renderable which describes
 * a rendering engine that the Web Framework can use to execute a web script.
 * <p>
 * A WebScriptRenderer can be used to execute a web script for any purpose
 * so long as an appropriate RendererContext instance is passed to it.
 * <p>
 * Most commonly, the RendererContext passed in will describe a Component.
 * <p>
 * The renderer supports "full page refresh" link backs to a webscript. The
 * LocalWebScriptResponse object is responsible for encoding compatible links
 * via the scripturl() template method.
 * 
 * @author muzquiano
 * @author kevinr
 */
public class WebScriptProcessor extends AbstractProcessor
{
    /** The WebScript service servlet path */
    public static final String WEBSCRIPT_SERVICE_SERVLET = "/service";
    
    /** The parameter identifying a clicked webscript ID */
    static final String PARAM_WEBSCRIPT_ID  = "_wsId";
    
    /** The parameter identifying a clicked webscript url */
    static final String PARAM_WEBSCRIPT_URL = "_wsUrl";
    
    /** The web script container */
    private LocalWebScriptRuntimeContainer webScriptContainer;
    
    /** The server properties */
    private ServerProperties serverProperties;

    private ConfigService configService;

    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
        Config config = this.configService.getConfig("Server");
        serverProperties = (ServerConfigElement)config.getConfigElement(ServerConfigElement.CONFIG_ELEMENT_ID);
    }
       
    /**
     * Gets the web scripts registry.
     * 
     * @return the registry
     */
    public Registry getRegistry()
    {
        return this.webScriptContainer.getRegistry();
    }
        
    /**
     * Sets the container bean.
     * 
     * @param containerBean the new container bean
     */
    public void setContainer(LocalWebScriptRuntimeContainer containerBean)
    {
        this.webScriptContainer = containerBean;
    }
    
    /**
     * Gets the container bean.
     * 
     * @return the container bean
     */
    public LocalWebScriptRuntimeContainer getContainer()
    {
        return this.webScriptContainer;
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext pc, ModelObject object)
        throws RendererExecutionException
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();
        String url = this.getProperty(pc, "uri");
        
        /**
         * If the ModelObject being rendered is a component, then we will
         * allow for the execution of .head template files ahead of the
         * actual WebScript execution.
         */
        if (object instanceof Component || object instanceof SubComponent)
        {
            // Get the component and its URL.  Do a token replacement
            // on the URL right away and remove the query string
            SurfBugData component = (SurfBugData) object;
            
            url = UriUtils.replaceTokens(url, context, null, null, "");
            
            if (url.indexOf('?') != -1)
            {
                url = url.substring(0, url.indexOf('?'));
            }
            
            // Find the web script
            Match match = getRegistry().findWebScript(context.getRequestMethod(), url);
            if (match != null)
            {
                if (match.getKind() == Match.Kind.URI)
                {
                    match = getRegistry().findWebScript(LocalWebScriptRuntime.DEFAULT_METHOD_GET, url);
                }
                if (match != null)
                {
                    WebScript webScript = match.getWebScript();
                    if (webScript != null)
                    {
                        // Modify the path to resolve the .head.<extension> file
                        String path = webScript.getDescription().getId() + ".head";
    
                        // Store the WebScript that was resolved for the component...
                        component.setResolvedWebScript(webScript);
                        /**
                         * If the .head template file exists, we can execute
                         * it against a template model.
                         * 
                         * We then trap the results and append them into the
                         * request context "tags" buffer for output later.
                         */
                        String validTemplatePath = getContainer().getTemplateProcessorRegistry().findValidTemplatePath(path);
                        if (validTemplatePath != null)
                        {
                            ExtensibilityModel extModel = context.openExtensibilityModel();
                            extModel.addUnboundContent();
                            Writer writer = null;
                            
                            try
                            {
                                writer = context.getResponse().getWriter();
                                
                                Map<String, Object> model = new HashMap<String, Object>(32);
                                processorModelHelper.populateTemplateModel(context, model, object);

                                // commit to output stream
                                TemplateProcessor templateProcessor = getContainer().getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                                templateProcessor.process(validTemplatePath, model, writer);
                                
                                context.updateExtendingModuleDependencies(webScript.getDescription().getId(), model);

                                // Get the module handler from the container and retrieve any templates that provide extensions to the
                                // the current WebScript...
                                for (String moduleTemplatePath: context.getExtendingModuleFiles(validTemplatePath))
                                {
                                    String modulePath = getContainer().getTemplateProcessorRegistry().findValidTemplatePath(moduleTemplatePath);
                                    if (modulePath != null)
                                    {
                                        templateProcessor.process(modulePath, model, writer);
                                    }
                                }
                            }
                            catch (UnsupportedEncodingException uee)
                            {
                                throw new RendererExecutionException(uee);
                            }
                            catch (IOException ioe)
                            {
                                throw new RendererExecutionException(ioe);
                            }
                            finally
                            {
                                context.closeExtensibilityModel(extModel, writer);
                            }
                        }
                    }
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#doExecuteBody()
     */
    public void executeBody(ProcessorContext pc, ModelObject object)
        throws RendererExecutionException    
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();
        String uri = this.getProperty(pc, "uri");
        
        // Construct a "context" object that the Web Script engine will utilise
        LocalWebScriptContext webScriptContext = new LocalWebScriptContext();
        
        // Copy in request parameters into a HashMap
        // This is so as to be compatible with UriUtils (and Token substitution)
        webScriptContext.setTokens(buildArgs(context));
        
        // Begin to process the actual web script.
        
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
        
        // if this webscript has been clicked, update the script URL - 
        if (object.getId().equals(context.getParameter(PARAM_WEBSCRIPT_ID)))
        {
            webScriptContext.setExecuteUrl(context.getParameter(PARAM_WEBSCRIPT_URL));
        }
        else
        {
            // else use the webscript default url
            webScriptContext.setExecuteUrl(requestPath + WEBSCRIPT_SERVICE_SERVLET + url);
        }
        
        // Set up state onto the local web script context
        webScriptContext.setRuntimeContainer(webScriptContainer);
        webScriptContext.setRequestContext(context);
        webScriptContext.setModelObject(object);
        
        try
        {
            // Construct the Web Script Runtime
            // This bundles the container, the context and the encoding
            LocalWebScriptRuntime runtime = new LocalWebScriptRuntime(
                    context.getResponse().getWriter(),
                    webScriptContainer, serverProperties, webScriptContext);
            
            // set the method onto the runtime
            if (context.getRequestMethod() != null)
            {
                String method = context.getRequestMethod();
                
                // test for method existance - fall back to GET if not implemented
                // this allow pages to accept say a POST request but the components bound
                // to it do not need to implement it, and can fall back to GET.
                if (!LocalWebScriptRuntime.DEFAULT_METHOD_GET.equals(method))
                {
                    Match match = getRegistry().findWebScript(method, runtime.getScriptUrl());
                    if (match != null && match.getKind() == Match.Kind.URI)
                    {
                        // found a match - but not to this method - fall back
                        method = LocalWebScriptRuntime.DEFAULT_METHOD_GET;
                    }
                }
                
                runtime.setScriptMethod(method);
            }
            
            /*
             * Bind the RequestContext to the Web Script Container using a
             * thread local variable.  The Web Script Container methods for
             * setting model properties are not request scoped, so this is the
             * only way to do this (it seems)
             * 
             * Note: The models for script and template processing are created
             * later on and will use getScriptParameters and
             * getTemplateParameters from the container.  The container looks up
             * the thread local variable and does its thing. 
             */
            webScriptContainer.bindRequestContext(context);
            webScriptContainer.bindModelObject(object);
            
            // Execute the script
            try
            {
                runtime.executeScript();
            }
            finally
            {
                webScriptContainer.unbindRequestContext();
                webScriptContainer.unbindModelObject();
            }
        }
        catch (IOException exc)
        {
            throw new RendererExecutionException("Unable to read back response from Web Script Runtime buffer", exc);
        }
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#exists(org.alfresco.web.framework.render.ProcessorContext)
     */
    public boolean exists(ProcessorContext pc, ModelObject object)
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();
        String uri = this.getProperty(pc, "uri");
        
        Match match = webScriptContainer.getRegistry().findWebScript(context.getRequestMethod(), uri);
        
        return (match != null);
    }
    
    
    /**
     * Helper to build argument map from the servlet request parameters.
     * 
     * @param request context the request context
     * 
     * @return the map< string, string>
     */
    private static Map<String, String> buildArgs(RequestContext context)
    {
        return context.getParameters();
    }
}

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.ExtensibilityModuleHandler;
import org.springframework.extensions.surf.extensibility.HandlesExtensibility;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRuntime;

/**
 * <p>WebScript Runtime Container for a Surf WebScript based component.
 * </p><p>
 * The web-tier PresentationContainer is extended to provide additional script and
 * template parameters. Also a ThreadLocal is used to maintain and provide access to
 * the Surf RequestContext for the currently executing thread.
 * </p>
 * @author kevinr
 * @author David Draper
 */
public class LocalWebScriptRuntimeContainer extends PresentationContainer implements HandlesExtensibility
{
    /** ThreadLocal responsible for the current RequestContext */
    private ThreadLocal<RequestContext> renderContext = new ThreadLocal<RequestContext>();

    private ThreadLocal<ModelObject> modelObject = new ThreadLocal<ModelObject>();

    /**
     * <p>A <code>ProcessorModelHelper</code> is required to populate the model. It is supplied by the Spring Framework
     * providing that this bean is correctly configured.</p>
     */
    private ProcessorModelHelper processorModelHelper;

    public void setProcessorModelHelper(ProcessorModelHelper processorModelHelper)
    {
        this.processorModelHelper = processorModelHelper;
    }

    /**
     * Bind the RequestContext to the current thread.
     */
    public void bindRequestContext(RequestContext context)
    {
        renderContext.set(context);
    }

    public void bindModelObject(ModelObject object)
    {
        modelObject.set(object);
    }

    /**
     * Unbind the RequestContext from the current thread - must be called at some
     * point after the bind() method, in a finally block or similar.
     */
    public void unbindRequestContext()
    {
        renderContext.remove();
    }

    public void unbindModelObject()
    {
        modelObject.remove();
    }

    /**
     * @return the RequestContext for this thread.
     */
    protected RequestContext getRequestContext()
    {
        return renderContext.get();
    }

    protected ModelObject getModelObject()
    {
        return modelObject.get();
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.AbstractRuntimeContainer#reset()
     */
    @Override
    public void reset()
    {
        super.reset();

        // called early when WSF initialises - can safely ignore until we have a WF app context
        if (getApplicationContext() != null)
        {
            this.getScriptProcessorRegistry().reset();
            this.getTemplateProcessorRegistry().reset();
        }
    }

    @Override
    public Map<String, Object> getScriptParameters()
    {
        // NOTE: returns unmodifable map from super
        Map<String, Object> params = new HashMap<String, Object>(8, 1.0f);
        params.putAll(super.getScriptParameters());

        // render context
        RequestContext context = getRequestContext();
        ModelObject object = getModelObject();

        // if we have a render context, populate root script properties
        if (context != null)
        {
            processorModelHelper.populateScriptModel(context, params, object);
        }

        return params;
    }

    @Override
    public Map<String, Object> getTemplateParameters()
    {
        // NOTE: unmodifable map returned from super class
        Map<String, Object> params = new HashMap<String, Object>(32, 1.0f);
        params.putAll(super.getTemplateParameters());

        // render context
        RequestContext context = getRequestContext();
        ModelObject object = getModelObject();

        // in the case of an internal error such as script compliation failing
        // the rendercontext can be null - as we are building template model
        // for the webscript status page - no futher work required here.
        if (context != null)
        {
            try
            {
                processorModelHelper.populateTemplateModel(context, params, object);
            }
            catch (RendererExecutionException ree)
            {
                // This exception is only thrown when processing
                // template objects, thus it shouldn't occur for web scripts
            }
            catch (UnsupportedEncodingException uee)
            {
                // should not occur as UTF-8 is always supported
            }
        }

        return params;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.scripts.PresentationContainer#executeScript(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse, org.alfresco.web.scripts.Authenticator)
     */
    public void executeScript(WebScriptRequest scriptReq, WebScriptResponse scriptRes, Authenticator auth)
        throws IOException
    {
        boolean handleBinding = false;

        RequestContext rc = null;

        try
        {
            // ensure the request is stored onto the request attributes
            if (ServletUtil.getRequest() == null)
            {
                HttpServletRequest request = WebScriptServletRuntime.getHttpServletRequest(scriptReq);
                if (request != null)
                {
                    try
                    {
                        rc = RequestContextUtil.initRequestContext(getApplicationContext(), request);
                    }
                    catch (Exception e)
                    {
                        throw new IOException("Failed to initialize RequestContext for local WebScript runtime: " + e.getMessage());
                    }
                }
            }

            // check whether a render context already exists
            RequestContext context = getRequestContext();
            if (context == null)
            {
                HttpServletResponse response = WebScriptServletRuntime.getHttpServletResponse(scriptRes);
                if (response != null)
                {
                    context = ThreadLocalRequestContext.getRequestContext();
                    context.setResponse(response);

                    // flag that we will manually handle the bindings
                    handleBinding = true;
                }
            }

            // manually handle binding of RequestContext to current thread
            if (handleBinding)
            {
                bindRequestContext(context);
            }

            try
            {
                // call through to the parent container to perform the WebScript processing
                ExtensibilityModel extModel = openExtensibilityModel();
                super.executeScript(scriptReq, scriptRes, auth);
                closeExtensibilityModel(extModel, scriptRes.getWriter());
            }
            finally
            {
                // manually handle unbinding of RequestContext from current thread
                if (handleBinding)
                {
                    unbindRequestContext();
                }
            }
        }
        finally
        {
            // unbind RequestContext from current thread
            if (rc != null)
            {
                rc.release();
            }
        }
    }

    private ExtensibilityModuleHandler extensibilityModuleHandler = null;
    
    public void setExtensibilityModuleHandler(ExtensibilityModuleHandler extensibilityModuleHandler)
    {
        this.extensibilityModuleHandler = extensibilityModuleHandler;
    }
    
    public ExtensibilityModuleHandler getExtensibilityModuleHandler()
    {
        return this.extensibilityModuleHandler;
    }
    
    public ExtensibilityModel getCurrentExtensibilityModel()
    {
        RequestContext context = getRequestContext();
        ExtensibilityModel extModel = context.getCurrentExtensibilityModel();
        return extModel;
    }

    public ExtensibilityModel openExtensibilityModel()
    {
        RequestContext context = getRequestContext();
        ExtensibilityModel extModel = context.openExtensibilityModel();
        return extModel;
    }

    public void closeExtensibilityModel(ExtensibilityModel model, Writer out)
    {
        RequestContext context = getRequestContext();
        if (context != null)
        {
            context.closeExtensibilityModel(model, out);
        }
        else
        {
            model.flushModel(out);
        }
    }

    public void updateExtendingModuleDependencies(String pathBeingProcessed, Map<String, Object> model)
    {
        // Get the context bound to the current thread of execution, we can then check this for
        // the current module evaluation results. This allows us to only perform a single evalution 
        // per extension module per request...
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        context.updateExtendingModuleDependencies(pathBeingProcessed, model);
    }

    public List<String> getExtendingModuleFiles(String pathBeingProcessed)
    {
        // Get the context bound to the current thread of execution, we can then check this for
        // the current module evaluation results. This allows us to only perform a single evalution 
        // per extension module per request...#
        List<String> files = null;
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            files = context.getExtendingModuleFiles(pathBeingProcessed);
        }
        else
        {
            files = new ArrayList<String>();
        }
        return files;
    }

    /**
     * <p>Retrieves the path of the current file being processed. This request is delegated to the associated
     * {@link RequestContext}.</p> 
     */
    public String getFileBeingProcessed()
    {
        String file = null;
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            file = context.getFileBeingProcessed();
        }
        return file;
    }

    /**
     * <p>Sets the path of the current file being processed. This request is delegated to the associated
     * {@link RequestContext}.</p>
     */
    public void setFileBeingProcessed(String file)
    {
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            context.setFileBeingProcessed(file);
        }
    }
    
    private URLModelFactory urlModelFactory = null;

    @Override
    public URLModelFactory getUrlModelFactory()
    {
        return this.urlModelFactory;
    }

    public void setUrlModelFactory(URLModelFactory urlModelFactory)
    {
        this.urlModelFactory = urlModelFactory;
    }

    /**
     * <p>Checks the current {@link RequestContext} to see if it has cached an extended bundle (that is a basic {@link ResourceBundle} that
     * has had extension modules applied to it. Extended bundles can only be safely cached once per request as the modules
     * applied can vary for each request.</p>
     * 
     * @param webScriptId The id of the WebScript to retrieve the extended bundle for.
     * @return A cached bundle or <code>null</code> if the bundle has not previously been cached.
     */
    public ResourceBundle getCachedExtendedBundle(String webScriptId)
    {
        ResourceBundle bundle = null;
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            bundle = context.getCachedExtendedBundle(webScriptId);
        }
        return bundle;
    }
    
    /**
     * <p>Adds a new extended bundle to the cache of the current {@link RequestContext}. An extended bundle is a WebScript 
     * {@link ResourceBundle} that has had {@link ResourceBundle} instances merged into it from extension modules that have 
     * been applied. These can only be cached for the lifetime of the request as different modules may be applied to the same 
     * WebScript for different requests.</p>
     * 
     * @param webScriptId The id of the WebScript to cache the extended bundle against.
     * @param extensionBUndle The extended bundle to cache.
     */
    public void addExtensionBundleToCache(String webScriptId, WebScriptPropertyResourceBundle extensionBundle)
    {
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            context.addExtensionBundleToCache(webScriptId, extensionBundle);
        }
    }

    public ScriptConfigModel getExtendedScriptConfigModel(String xmlConfig)
    {
        ScriptConfigModel scriptConfigModel = null;
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            scriptConfigModel = context.getExtendedScriptConfigModel(xmlConfig);
        }
        return scriptConfigModel;
    }

    public TemplateConfigModel getExtendedTemplateConfigModel(String xmlConfig)
    {
        TemplateConfigModel templateConfigModel = null;
        RequestContext context = getRequestContext();
        if (context == null)
        {
            context = ThreadLocalRequestContext.getRequestContext();
        }
        if (context != null)
        {
            templateConfigModel = context.getExtendedTemplateConfigModel(xmlConfig);
        }
        return templateConfigModel;
    }
    
    public void addExtensibilityDirectives(Map<String, Object> freeMarkerModel, ExtensibilityModel extModel)
    {
        // No action required. All custom directives are added via the ProcessorModelHelper
    }

    boolean extensibilitySuppressed = false;
    
    public void suppressExtensibility()
    {
        this.extensibilitySuppressed = true;
    }
    
    public void unsuppressExtensibility()
    {
        this.extensibilitySuppressed = false;
    }
    
    public boolean isExtensibilitySuppressed()
    {
        return this.extensibilitySuppressed;
    }
}

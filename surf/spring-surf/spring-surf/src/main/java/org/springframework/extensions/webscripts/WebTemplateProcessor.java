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
import java.io.InputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;
import org.springframework.extensions.surf.render.AbstractProcessor;
import org.springframework.extensions.surf.render.ProcessorContext;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.util.DataUtil;

/**
 * Implementation of a renderer that executes a web template.
 * <p>
 * A web template may be a Freemarker template (out-of-the-box) but may also be an alternative template format.
 * This may include the PHP template processor that is provided as a Webscripts Addon.
 *
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class WebTemplateProcessor extends AbstractProcessor
{
    private static final Log logger = LogFactory.getLog(WebTemplateProcessor.class);
    
    public static final String PREFIX_CLASSPATH = "classpath:";
    
    private static final String SCRIPT_RESULTS = "webTemplateRendererScriptResults";
    
    private TemplateProcessorRegistry templateProcessorRegistry;
    private ScriptProcessorRegistry scriptProcessorRegistry;
    private Map<String, Object> scriptObjects;
    
    /** thread safe cache of template URIs to Script paths */
    private Map<String, String> templateScripts = new ConcurrentHashMap<String, String>(256);
    private static final String TEMPLATE_URIPATH_SENTINEL = "<none>";
    
    
    /**
     * WebFrameworkConfigElement
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * <p>This accessor method is required for Spring to set the <code>WebFrameworkConfigElement</code> required by this
     * <code>WebTemplateProcessor</code>.</p>
     * @param frameworkBean
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    /**
     * Sets the template processor registry.
     *
     * @param templateProcessorRegistry the new template processor registry
     */
    public void setTemplateProcessorRegistry(TemplateProcessorRegistry templateProcessorRegistry)
    {
        this.templateProcessorRegistry = templateProcessorRegistry;
    }

    /**
     * Gets the template processor registry.
     *
     * @return the template processor registry
     */
    public TemplateProcessorRegistry getTemplateProcessorRegistry()
    {
        return this.templateProcessorRegistry;
    }

    /**
     * Sets the script processor registry.
     *
     * @param scriptProcessorRegistry the new script processor registry
     */
    public void setScriptProcessorRegistry(ScriptProcessorRegistry scriptProcessorRegistry)
    {
        this.scriptProcessorRegistry = scriptProcessorRegistry;
    }

    /**
     * Gets the script processor registry.
     *
     * @return the script processor registry
     */
    public ScriptProcessorRegistry getScriptProcessorRegistry()
    {
        return this.scriptProcessorRegistry;
    }

    /**
     * Set any additional objects to be applied to the script model when executing any JavaScript
     * attached to the template.
     *
     * @param scriptObjects
     */
    public void setScriptObjects(Map<String, Object> scriptObjects)
    {
        this.scriptObjects = scriptObjects;
    }

    /**
     * Execute Template header
     *
     * @param pc the processor context
     *
     * @throws RendererExecutionException the renderer execution exception
     */
    public void executeHeader(ProcessorContext pc, ModelObject object)
        throws RendererExecutionException
    {
        RequestContext context = pc.getRequestContext();
        String uri = this.getProperty(pc, "uri");
        String templatePath = this.getProperty(pc, "template-path");
        
        // the current format
        String format = context.getFormatId();
        
        // Attempt to execute the templates associated .head.<ext> file, if it has one
        
        // uri to the template (switches on format)
        if (uri != null)
        {
            StringBuilder templateName = new StringBuilder(uri);
            int extensionIndex = templateName.lastIndexOf(".");
            String defaultFormat = this.webFrameworkConfigElement.getDefaultFormatId();
            if (format != null && format.length() > 0 && !format.equals(defaultFormat))
            {
                templateName.delete(extensionIndex, templateName.length());
                templateName.append(".head.");
                templateName.append(format);
            }
            else
            {
                templateName.insert(extensionIndex, ".head");
            }
            
            String validTemplatePath = getTemplateProcessorRegistry().findValidTemplatePath(templateName.toString());
            if (validTemplatePath != null)
            {
                try
                {
                    // build the model
                    Map<String, Object> model = new HashMap<String, Object>(32);
                    processorModelHelper.populateTemplateModel(context, model, object);
                    
                    TemplateProcessor templateProcessor = getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                    templateProcessor.process(validTemplatePath, model, context.getResponse().getWriter());
                }
                catch (UnsupportedEncodingException uee)
                {
                    throw new RendererExecutionException(uee);
                }
                catch (IOException ioe)
                {
                    throw new RendererExecutionException(ioe);
                }
            }
        }
        else if (templatePath != null)
        {
            String templateString = null;
            try
            {
                templateString = loadTemplateStringFromPath(templatePath);
                
                // build the model
                Map<String, Object> model = new HashMap<String, Object>(32);
                processorModelHelper.populateTemplateModel(context, model, object);
                
                // Assume this is freemarker as the default template type
                TemplateProcessor templateProcessor = getTemplateProcessorRegistry().getTemplateProcessorByExtension("ftl");
                templateProcessor.processString(templateString, model, context.getResponse().getWriter());
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RendererExecutionException(uee);
            }
            catch (IOException ioe)
            {
                throw new RendererExecutionException(ioe);
            }
        }
    }

    /**
     * <p>Executes controller scripts that have been provided to extend the template being rendered.</p>
     * 
     * @param context The current {@link RequestContext}.
     * @param scriptPath The path of the script being executed. This script does not need to exist for extensions to be processed.
     * @param scriptModel The current model for the script to use. If this argument is <code>null</code> then a new model will be built.
     * @param resultModel The result model of executing the script.
     * @param object The {@link ModelObject} being rendered, this will almost certainly be a {@link TemplateInstance}. This is only required
     * if the scriptModel argument is <code>null</code>
     */
    public void executeScriptBodyExtensions(RequestContext context, 
                                            String scriptPath, 
                                            Map<String, Object> scriptModel, 
                                            Map<String, Object> resultModel, 
                                            ModelObject object)
    {
        for (String moduleScriptPath: context.getExtendingModuleFiles(scriptPath))
        {
            String validExtScriptPath = getScriptProcessorRegistry().findValidScriptPath(moduleScriptPath);
            if (validExtScriptPath != null)
            {
                ScriptProcessor scriptProcessor = getScriptProcessorRegistry().getScriptProcessor(validExtScriptPath);
                ScriptContent scriptContent = scriptProcessor.findScript(validExtScriptPath);
                
                if (scriptModel == null)
                {
                    scriptModel = buildScriptModel(context, object, resultModel);
                }
                
                scriptProcessor.executeScript(scriptContent, scriptModel);
            }
        }
    }
    
    /**
     * <p>Builds a new script model object for use when executing template controller scripts.</p>
     * @param context The current {@link RequestContext}
     * @param object The current {@link ModelObject} being rendered (this will almost certainly be a {@link TemplateInstance} 
     * @param resultModel The result model that will be set in the context.
     * @return The newly built script model.
     */
    private Map<String, Object> buildScriptModel(RequestContext context, 
                                                 ModelObject object, 
                                                 Map<String, Object> resultModel)
    {
        Map<String, Object> scriptModel = new HashMap<String, Object>(32);
        processorModelHelper.populateScriptModel(context, scriptModel, object);
        
        // add any externally configured script objects
        if (this.scriptObjects != null)
        {
            scriptModel.putAll(this.scriptObjects);
        }
        
        // add in the model result object
        scriptModel.put("model", resultModel);
        return scriptModel;
    }
    
    /**
     * Execute template Body.
     *
     * @param pc the processor context
     *
     * @throws RendererExecutionException the renderer execution exception
     */
    @SuppressWarnings("unchecked")
    public void executeBody(ProcessorContext pc, ModelObject object)
            throws RendererExecutionException
    {
        final RequestContext context = pc.getRequestContext();
        final String uri = this.getProperty(pc, "uri");
        final String templatePath = this.getProperty(pc, "template-path");
        
        // the current format
        final String format = context.getFormatId();
        ExtensibilityModel extModel = context.openExtensibilityModel();
        extModel.addUnboundContent();
        Writer writer = null;
        
        try
        {
           writer = context.getResponse().getWriter();
            
            // the result model
            Map<String, Object> resultModel = null;
            
            if (object instanceof TemplateInstance)
            {
                if (context.hasValue(SCRIPT_RESULTS) == false)
                {
                    // Attempt to execute a .js file for this page template
                    resultModel = new HashMap<String, Object>(4, 1.0f);
                    
                    boolean scriptExecuted = false;
                    if (uri != null)
                    {
                        final String validScriptPath = findValidScriptPath(uri);
                        
                        // execute the script if we found a valid script path
                        if (validScriptPath != null)
                        {
                            // get the script content
                            ScriptProcessor scriptProcessor = getScriptProcessorRegistry().getScriptProcessor(validScriptPath);
                            ScriptContent script = scriptProcessor.findScript(validScriptPath);
                            if (script != null)
                            {
                                // build the model
                                Map<String, Object> scriptModel = buildScriptModel(context, object, resultModel);
                                
                                // execute the script
                                try
                                {
                                    scriptProcessor.executeScript(script, scriptModel);
                                    executeScriptBodyExtensions(context, validScriptPath, scriptModel, resultModel, object);
                                    scriptExecuted = true;
                                }
                                catch (WebScriptException we)
                                {
                                    throw new RendererExecutionException(we);
                                }
                            }
                        }
                        else
                        {
                            // If a "base" script isn't found, check for extensions - this allows a module to provide a controller
                            // for a template when one isn't defined in the base...
                            executeScriptBodyExtensions(context, uri, null, resultModel, object);
                            scriptExecuted = true;
                        }
                        
                        // store the result model in the request context for the next pass
                        // this removes the need to execute the script twice
                        if (scriptExecuted && context.isPassiveMode())
                        {
                            context.setValue(SCRIPT_RESULTS, (Serializable)resultModel);
                        }
                    }
                }
                else
                {
                    // Retrieve results from the request context - we already executed a pass.
                    // Note that there is no need to remove the results - this context is template local.
                    resultModel = (Map<String, Object>)context.getValue(SCRIPT_RESULTS);
                }
            }
            
            // Execute the template file itself
            final Map<String, Object> templateModel = new HashMap<String, Object>(32, 1.0f);
            processorModelHelper.populateTemplateModel(context, templateModel, object);
            
            // if were processing a template model object, then we will have computed script
            // object results for the corresponding js file.
            //
            // if these exist, merge these in, they will not exist if what is being rendered
            // is anything other than a template model object
            if (resultModel != null)
            {
                final String validScriptPath = findValidScriptPath(uri);
                if (validScriptPath != null)
                {
                    ScriptProcessor scriptProcessor = getScriptProcessorRegistry().getScriptProcessor(validScriptPath);
                    
                    // map entries
                    for (Map.Entry<String, Object> entry : resultModel.entrySet())
                    {
                        // retrieve script model value and unwrap each java object from script object
                        Object value = entry.getValue();
                        Object templateValue = scriptProcessor.unwrapValue(value);
                        templateModel.put(entry.getKey(), templateValue);
                    }
                }
            }
            
            // if we're processing by uri, find the template path name this switches on format
            TemplateProcessor templateProcessor = null;
            String validTemplatePath = null;
            
            if (uri != null)
            {
                if (format != null && format.length() != 0 && !this.webFrameworkConfigElement.getDefaultFormatId().equals(format))
                {
                    validTemplatePath = getTemplateProcessorRegistry().findValidTemplatePath(uri + "." + format);
                }
                
                if (validTemplatePath == null)
                {
                    validTemplatePath = getTemplateProcessorRegistry().findValidTemplatePath(uri);
                }
                
                if (validTemplatePath != null)
                {
                    templateProcessor = getTemplateProcessorRegistry().getTemplateProcessor(validTemplatePath);
                }
                else
                {
                    logger.warn("Unable to find a valid template path for uri: " + uri);
                }
            }
            else if (templatePath != null)
            {
                // load the template string
                validTemplatePath = loadTemplateStringFromPath(templatePath);
                
                // process the template
                // Assume this is freemarker as the default template type
                templateProcessor = getTemplateProcessorRegistry().getTemplateProcessorByExtension("ftl");
                
            }
            else
            {
                logger.warn("No valid template path or uri to resolve template!");
            }
            
            if (templateProcessor != null && validTemplatePath != null)
            {
                ModelWriter extModelWriter = extModel.getWriter();
                context.setFileBeingProcessed(validTemplatePath);
                templateProcessor.process(validTemplatePath, templateModel, extModelWriter);
                
                // Switch into extension processing (this will prevent modules writing directly into the model,
                // they will need to use actions to update the model)...
                extModel.switchToExtensionProcessing();
                
                for (String moduleTemplatePath: context.getExtendingModuleFiles(validTemplatePath))
                {
                    String modulePath = getTemplateProcessorRegistry().findValidTemplatePath(moduleTemplatePath);
                    if (modulePath != null)
                    {
                        context.setFileBeingProcessed(modulePath);
                        templateProcessor.process(modulePath, templateModel, extModelWriter);
                    }
                }
                
                
            }
        }
        catch (Exception ex)
        {
            if (ex instanceof RendererExecutionException)
            {
                throw (RendererExecutionException)ex;
            }
            else
            {
                if (uri != null)
                {
                    throw new RendererExecutionException("WebTemplateProcessor failed to process template uri: " + uri, ex);
                }
                if (templatePath != null)
                {
                    throw new RendererExecutionException("WebTemplateProcessor failed to process template path: " + templatePath, ex);
                }
            }
        }
        finally
        {
            context.closeExtensibilityModel(extModel, writer);
        }
    }

    /**
     * Return a valid script path for the given uri.
     *
     * @param uri   URI to search for a script path for
     *
     * @return script path if found, null otherwise
     */
    private String findValidScriptPath(String uri)
    {
        String validScriptPath = this.templateScripts.get(uri);
        if (validScriptPath == null)
        {
            // no cache entry for the given uri, look it up
            validScriptPath = getScriptProcessorRegistry().findValidScriptPath(uri);
            if (validScriptPath != null)
            {
                // cache uri to script path
                this.templateScripts.put(uri, validScriptPath);
            }
            else
            {
                // store sentinel value to indicate cache miss
                this.templateScripts.put(uri, TEMPLATE_URIPATH_SENTINEL);
            }
        }
        if (validScriptPath == TEMPLATE_URIPATH_SENTINEL)
        {
            validScriptPath = null;
        }
        return validScriptPath;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.Processor#exists(org.alfresco.web.framework.render.ProcessorContext)
     */
    public boolean exists(ProcessorContext pc, ModelObject object)
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();
        String uri = this.getProperty(pc, "uri");

        // the current format
        String format = context.getFormatId();

        String validTemplatePath = null;

        if (format != null && format.length() != 0 && !this.webFrameworkConfigElement.getDefaultFormatId().equals(format))
        {
            validTemplatePath = getTemplateProcessorRegistry().findValidTemplatePath(uri + "." + format);
        }

        if (validTemplatePath == null)
        {
            validTemplatePath = getTemplateProcessorRegistry().findValidTemplatePath(uri);
        }

        return (validTemplatePath != null);
    }

    protected String loadTemplateStringFromPath(String templatePath)
        throws IOException
    {
        String templateString = null;

        if (templatePath.startsWith(PREFIX_CLASSPATH))
        {
            templatePath = templatePath.substring(10);

            InputStream is = this.getClass().getClassLoader().getResourceAsStream(templatePath);
            templateString = DataUtil.copyToString(is, null, true);
        }

        return templateString;
    }
}

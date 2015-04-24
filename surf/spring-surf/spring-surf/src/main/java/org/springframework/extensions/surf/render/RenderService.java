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
package org.springframework.extensions.surf.render;

import static org.springframework.extensions.surf.WebFrameworkConstants.URI;
import static org.springframework.extensions.surf.WebFrameworkConstants.URL;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement.ErrorHandlerDescriptor;
import org.springframework.extensions.config.WebFrameworkConfigElement.SystemPageDescriptor;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.LinkBuilderFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.PageRendererExecutionException;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.exception.ResourceLoaderException;
import org.springframework.extensions.surf.render.ProcessorContext.ProcessorDescriptor;
import org.springframework.extensions.surf.render.bean.ChromeRenderer;
import org.springframework.extensions.surf.render.bean.ComponentRenderer;
import org.springframework.extensions.surf.render.bean.PageRenderer;
import org.springframework.extensions.surf.render.bean.RegionRenderer;
import org.springframework.extensions.surf.render.bean.TemplateInstanceRenderer;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.resource.ResourceProvider;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.AdvancedComponent;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.ComponentType;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.ModuleDeployment;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.types.SubComponent.RenderData;
import org.springframework.extensions.surf.types.SurfBug;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.TemplateType;
import org.springframework.extensions.surf.uri.UriUtils;

/**
 * Web Framework Render Service.
 *
 * @author kevinr
 * @author David Draper
 */
public class RenderService implements ApplicationContextAware
{
    private static final Log logger = LogFactory.getLog(RenderService.class);

    public static final String CONTEXT_VALUE_ERROR_THROWABLE = "error-throwable";
    public static final String CONTEXT_VALUE_ERROR_PAGE_ID = "error-pageId";
    public static final String CONTEXT_VALUE_ERROR_TEMPLATE_ID = "error-templateId";
    public static final String CONTEXT_VALUE_ERROR_REGION_ID = "error-regionId";
    public static final String CONTEXT_VALUE_ERROR_REGION_SCOPE_ID = "error-regionScopeId";
    public static final String CONTEXT_VALUE_ERROR_REGION_SOURCE_ID = "error-regionSourceId";
    public static final String CONTEXT_VALUE_ERROR_COMPONENT_ID = "error-componentId";

    public static final String CONTEXT_VALUE_ERROR_TITLE = "errorTitle";
    public static final String CONTEXT_VALUE_ERROR_DESCRIPTION = "errorDescription";
    public static final String CONTEXT_VALUE_STACKTRACE = "stacktrace";

    private static final String PREFIX_WEBFRAMEWORK_RENDITION_PROCESSOR = "webframework.rendition.processor.";
    private static final String COMPONENT_TYPE_WEBSCRIPT = "webscript";
    private static final String TEMPLATE_TYPE_WEBTEMPLATE = "webtemplate";

    public static final String NEWLINE = "\r\n";

    private ModelObjectService modelObjectService;
    private WebFrameworkConfigElement webFrameworkConfiguration;

    private PageRenderer pageRenderer;
    private TemplateInstanceRenderer templateRenderer;
    private RegionRenderer regionRenderer;
    private ComponentRenderer componentRenderer;
    private ChromeRenderer chromeRenderer;

    private ApplicationContext applicationContext;

    /**
     * Returns a rendition processor for the given id (i.e. jsp, webscript, etc)
     *
     * @param id
     * @return processor
     */
    public Processor getRenditionProcessorById(String id)
    {
        String processorId = PREFIX_WEBFRAMEWORK_RENDITION_PROCESSOR + id;

        return (Processor) this.applicationContext.getBean(processorId);
    }

    /**
     * Returns a rendition processor for a renderable in the default VIEW render mode
     *
     * @param renderable
     * @return
     */
    public Processor getRenditionProcessor(Renderable renderable)
    {
        return getRenditionProcessor(renderable, RenderMode.VIEW);
    }

    /**
     * Returns a rendition processor for a renderable in the given render mode
     *
     * @param renderable
     * @param renderMode
     * @return
     */
    public Processor getRenditionProcessor(Renderable renderable, RenderMode renderMode)
    {
        Processor processor = null;

        if (renderable != null)
        {
            String processorId = renderable.getProcessorId(renderMode);
            if (processorId != null)
            {
                processor = getRenditionProcessorById(processorId);
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Unable to get processor for : " + renderable);
                }
            }
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Unable to get processor - renderable was null");
            }
        }

        return processor;
    }

    /**
     * Executes the processor for the given focus on the provided renderable object.
     *
     * @param context
     * @param renderFocus
     * @param renderable
     * @throws RendererExecutionException
     */
    public void processRenderable(RequestContext context, RenderFocus renderFocus, ModelObject object, Renderable renderable)
        throws RendererExecutionException
    {
        // get the processor
        Processor processor = getRenditionProcessor(renderable);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);

            // load from renderable data
            processorContext.load(renderable);

            // execute the processor
            processor.execute(processorContext, object, renderFocus);
        }
    }
   
    /**
     * <p>The <code>SurfBug</code> instance that is associated with the running application. Only
     * one instance of a <code>SurfBug</code> object can be associated with an application at a time
     * and this is defined using the <{@code}surfbug> element in the Surf configuration. The <code>SurfBug</code>
     * element is managed by the <code>RenderService</code> and is used to provide debugging information
     * for each component. SurfBug should be enabled and disabled through the "toggle_surfbug" WebScript.</p>
     * <p>This instance variable should never be referenced directly. Instead use the <code>getSurfBug</code>
     * method which ensures that the application associated <code>SurfBug</code> is retrieved from the 
     * <code>ModelObjectService</code>.</p>
     * <p>TODO: Ideally we should attempt to populate this through an initialisation method.</p>
     */
    private SurfBug surfbug = null;
    
    /**
     * <p>Returns the <code>SurfBug</code> instance associated with the running application (which will be
     * obtained from the <code>ModelObjectService</code> if not previously obtained.
     * 
     * @return The instance of <code>SurfBug</code> associated with the running application.
     */
    public SurfBug getSurfBug()
    {
        if (this.surfbug == null)
        {
            String surfbugId = this.webFrameworkConfiguration.getSurfBug();
            this.surfbug = this.modelObjectService.getSurfBug(surfbugId);
        }
        return this.surfbug;
    }
    
    /**
     * <p>Determines whether or not the supplied <code>Component</code> should be debugged.
     * Debugging should occur if:
     * <ul>
     * <li>SurfBug is enabled (specified in the <code>WebFrameworkConfigElement</code></li>
     * <li>An instance of SurfBug is configured for the application</li>
     * <li>The supplied <code>Component</code> has not already been debugged</li>
     * </ul>
     * 
     * @param object The <code>Component</code> to decide whether to debug.
     * @return <code>true</code> if the <code>Component</code> should be debugged and <code>false</code> otherwise.
     */
    private boolean debugComponent(Component object)
    {
        boolean debug = (this.webFrameworkConfiguration.isSurfBugEnabled() && 
                         getSurfBug() != null && 
                         !getSurfBug().hasBeenDebugged(object));
        return debug;
    }
    
    
    public class SubComponentData implements Serializable,Comparable<SubComponentData>
    {
        private static final long serialVersionUID = -1415329151847980149L;
        public SubComponentData(SubComponent subComponent)
        {
            this.subComponent = subComponent;
        }
        private SubComponent subComponent = null;
        public SubComponent getSubComponent()
        {
            return subComponent;
        }
        private List<String> contributingSourcePaths = new ArrayList<String>();
        public List<String> getContributingSourcePaths()
        {
            return contributingSourcePaths;
        }
        public void addContributingSourcePath(String path)
        {
            this.contributingSourcePaths.add(path);
        }
        public int compareTo(SubComponentData o)
        {
            return this.subComponent.compareTo(o.getSubComponent());
        }
    }
    
    /**
     * <p>Retrieves all the {@link SubComponent} instances defined for the supplied {@link AdvancedComponent}. The 
     * list returned is populated from both the {@link AdvancedComponent} configuration and the {@link ModuleDeployment}
     * configurations.</p>
     * 
     * @param aComp The {@link AdvancedComponent} to retrieve the {@link SubComponent} instances for.
     * @param context The current {@link RequestContext}
     * @return A sorted list of {@link SubComponent} instances.
     */
    private List<SubComponentData> getSubComponents(AdvancedComponent aComp, RequestContext context)
    {
        // Get renderable-elements, first from the Component itself...
        List<SubComponentData> allSubComponents = new ArrayList<SubComponentData>();
        for (SubComponent s: aComp.getSubComponents())
        {
            SubComponent clone = s.clone(); // Clone the sub-component to ensure thread safety...
            SubComponentData data = new SubComponentData(clone);
            data.addContributingSourcePath(aComp.getKey().getStoragePath());
            allSubComponents.add(data);
        }
        
        // ... and the from the deployed modules...
        List<ExtensionModule> deployedModules = context.getEvaluatedModules();
        for (ExtensionModule depMod: deployedModules)
        {
            // Check to see if the module contains an extension to the AdvancedComponent and if it does 
            // retrieve it's SubComponents and add them to the list. 
            for (Entry<String, AdvancedComponent> currComp: depMod.getAdvancedComponents().entrySet())
            {
                // Replace any tokens in the key (this is so that we can handle user dashboard extensions)...
                String tokenizedKey = UriUtils.replaceTokens(currComp.getKey(), context, null, null, "");
                if (tokenizedKey.equals(aComp.getId()))
                {
                    AdvancedComponent extComp = currComp.getValue();
                    if (extComp != null)
                    {
                        List<SubComponent> extSubComponents = extComp.getSubComponents();
                        if (extSubComponents != null)
                        {
                            for (SubComponent extSubComponent: extSubComponents)
                            {
                                SubComponentData target = findSubComponent(extSubComponent, allSubComponents, context);
                                if (target != null)
                                {
                                    // If the SubComponent has already been defined then we need to merge in the 
                                    // overrides that have been declared (adding the storage path of the extension
                                    // that has updated the Sub-Component)
                                    target.getSubComponent().mergeExtension(extSubComponent);
                                    target.addContributingSourcePath(extComp.getKey().getStoragePath());
                                }
                                else
                                {
                                    // The SubComponent is new so just add it to the list (capturing storagae path of
                                    // the extension that has provided the Sub-Component
                                    SubComponentData subComponentData = new SubComponentData(extSubComponent);
                                    subComponentData.addContributingSourcePath(extComp.getKey().getStoragePath());
                                    allSubComponents.add(subComponentData);
                                }
                            }
                        }
                        else
                        {
                            if (logger.isErrorEnabled())
                            {
                                logger.error("The getRenderableElements() method of class " + extComp.getClass() + " returns null. This is an invalid implementation of the interface");
                            }
                        }
                    }
                }
            }
        }
        
        // Sort the elements into the correct order...
        Collections.sort(allSubComponents);
        return allSubComponents;
    }
    
    /**
     * <p>Finds a {@link SubComponentData} in the supplied {@link List} with an <code>id</code> that matches that of
     * the supplied {@link SubComponent}.
     * 
     * @param subComponent The {@link SubComponentData} to find a match for.
     * @param list A {@link List} of {@link SubComponentData} instances in which to find a match.
     * @return The {@link SubComponent} in the {@link List} with a matching <code>id</code> attribute of <code>null</code>
     * if one cannot be found.
     */
    private SubComponentData findSubComponent(SubComponent subComponent, List<SubComponentData> list, RequestContext context)
    {
        SubComponentData target = null;
        String targetId = subComponent.getId();
        String tokenizedTargetId = UriUtils.replaceTokens(targetId, context, null, null, "");
        if (targetId != null)
        {
            for (SubComponentData current: list)
            {
                String currId = current.getSubComponent().getId();
                String currTokenizedId = UriUtils.replaceTokens(currId, context, null, null, "");
                if (currTokenizedId.equals(tokenizedTargetId))
                {
                    target = current;
                    break;
                }
            }
        }
        return target;
    }
    
    /**
     * Executes the processor for the given render focus on the given component instance.
     *
     * @param context
     * @param renderFocus
     * @param component
     * @throws RendererExecutionException
     */
    public void processComponent(RequestContext context,
                                 RenderFocus renderFocus, 
                                 Component component,
                                 boolean chromeless)
        throws RendererExecutionException
    {
        if (component instanceof AdvancedComponent)
        {
            // If the component is and instance of AdvancedComponent then it means that the default Spring Surf
            // configuration has been updated to switch the Component object type to be and AdvancedComponent. The
            // upshot of this is that all existing configured components will be treated as advanced components.
            for (SubComponentData subComponentData: getSubComponents((AdvancedComponent) component, context))
            {
                String htmlId = RenderUtil.validHtmlId(subComponentData.getSubComponent().getId());
                context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, htmlId);
                String componentChromeId = this.webFrameworkConfiguration.getDefaultSubComponentChrome();
                if (chromeless || componentChromeId == null || componentChromeId.trim().length() == 0)
                {
                    // No sub-component chrome configured, just render the SubComponent...
                    renderSubComponent(subComponentData.getSubComponent(), context, renderFocus);
                }
                else
                {
                    // Set SurfBug (shoud be able to do this when the RequestContext is created?)
                    context.setValue(WebFrameworkConstants.RENDER_DATA_SURFBUG_ENABLED, Boolean.valueOf(this.webFrameworkConfiguration.isSurfBugEnabled()).toString());
                    Chrome chrome = this.modelObjectService.getChrome(componentChromeId);
                    if (chrome != null)
                    {
                        context.setValue(WebFrameworkConstants.CURRENT_RENDERER, WebFrameworkConstants.RENDER_SUB_COMPONENT);
                        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_CHROME, chrome);
                        context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT, component);
                        context.setValue(WebFrameworkConstants.RENDER_DATA_SUB_COMPONENT, subComponentData);
                        this.chromeRenderer.render(context, chrome, renderFocus);
                    }
                    else
                    {
                        // If sub-component chrome is specified but the referenced type cannot be found then it we
                        // will output and error and not render the component element.
                        if (logger.isErrorEnabled())
                        {
                            logger.error("<" + AdvancedComponent.SUB_COMPONENT + "> chrome '" + componentChromeId + "' cannot be found, component elements will not be rendered");
                        }
                    }
                }
            }
        }
        else
        {
            // The original implementation of Spring Surf supported the notion of components being able 
            // to override the URI defined in the ComponentType to which they are bound (which somewhat
            // negates the point of actually specifying a ComponentType)... to support backwards compatibility
            // this is being reluctantly kept.
            // Check whether or not the Component should be debugged 
            if (!chromeless && RenderFocus.BODY.equals(renderFocus) && debugComponent(component))
            {
                // NOTE: In this instance we know that it is safe to reference the surfbug instance variable
                // directly because debugComponent would not have returned true if it didn't exist.
                this.surfbug.setCurrentComponent(component);
                this.processRenderable(context, renderFocus, this.surfbug, (Renderable) this.surfbug);
            }
            else
            {
                String uri = component.getURL();
                if (uri == null)
                {
                    uri = component.getProperty(URI);
                }
                if (uri == null)
                {
                    uri = component.getProperty(URL);
                }
                Renderable objectToRender = determineObjectToRender(uri, component.getComponentTypeId(), (Renderable) component);
                process(uri, objectToRender, context, component, renderFocus);
            }
        }
    }

    /**
     * <p>Renders the supplied {@link SubComponent}.</p>
     * 
     * @param subComponent The {@link SubComponent} to render.
     * @param context The current {@link RequestContext}
     * @param renderFocus The current focus of the request.
     */
    public void renderSubComponent(SubComponent subComponent, RequestContext context, RenderFocus renderFocus)
    {
        try
        {
            RenderData data = subComponent.determineURI(context, this.applicationContext);
            if (!data.shouldRender())
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("<" + AdvancedComponent.SUB_COMPONENT + "> '" + subComponent + "' will not be rendered");
                }
            }
            else
            {
                if (logger.isDebugEnabled())
                {
                    logger.debug("Rendering <" + AdvancedComponent.SUB_COMPONENT + "> '" + subComponent + "'");
                }
                Renderable objectToRender = determineObjectToRender(data.getUri(), subComponent.getComponentTypeId(), (Renderable) subComponent);
                context.setEvaluatedProperties(data.getProperties());
                context.addSubComponentDebugData(subComponent.getId(), data);
                process(data.getUri(), objectToRender, context, subComponent, renderFocus);
            }
        }
        catch (RendererExecutionException e)
        {
            if (logger.isErrorEnabled())
            {
                logger.error("It was not possible to render <" + AdvancedComponent.SUB_COMPONENT + "> with id: '" + subComponent + "' due to the following exception", e);
            }
        }
        
    }
    
    /**
     * <p>This method determines the correct object render, the code was abstracted from the original <code>processComponent</code> method
     * which was purely aimed at handling {@link Component} objects whereas this method can handle both {@link Component} objects (to support
     * legacy configuration) and {@link SubComponent} objects to support the new configuration style.</p>
     * 
     * @param uri The URI that will get rendered. This only needs to be non-null if no <code>componentTypeId</code> is provided, or a {@link Processor}
     * cannot be derived from the supplied {@link Renderable} object.
     * @param componentTypeId The id of a {@link ComponentType} to use as the {@link Renderable}. This is only required if a {@link Processor} cannot be
     * derived from the supplied {@link Renderable}
     * @param renderableObject This object will be checked for a defined {@link Processor} to use as the {@link Renderable}.
     * @return The object that should be rendered based on the supplied objects.
     * @throws RendererExecutionException
     */
    private Renderable determineObjectToRender(String uri, String componentTypeId, Renderable renderableObject) throws RendererExecutionException
    {
        Renderable objectToRender = null;
        Processor processor = getRenditionProcessor(renderableObject);
        if (processor != null)
        {
            objectToRender = (Renderable) renderableObject;
        }
        else
        {
            if (componentTypeId == null)
            {
                // If a ComponentType id has not been provided then we will assume that the
                // the ComponentType is a WebScript as this is the default. There is a default
                // WebScript ComponentType configured that we can use. In order for this to 
                // work we will need to have a URI defined to use though.
                if (uri == null)
                {
                    throw new RendererExecutionException("Cannot resolve component URL - may be missing from the definition: " + renderableObject.toString());
                }
                else
                {
                    componentTypeId = COMPONENT_TYPE_WEBSCRIPT;
                }
            }
            
            // Get the ComponentType...
            ComponentType componentType = this.modelObjectService.getComponentType(componentTypeId);
            if (componentType != null)
            {
                objectToRender = (Renderable) componentType;
            }
            else
            {
                // This is an error
                throw new RendererExecutionException("Cannot located ComponentType '" + componentTypeId + "' defined for Component '" + renderableObject.toString() + "'");
            }
        }
        return objectToRender;
    }
    
    
    private void process(String uri,
                         Renderable objectToRender,
                         RequestContext context,
                         ModelObject object,
                         RenderFocus renderFocus) throws RendererExecutionException
    {
        // get the processor
        Processor processor = getRenditionProcessor(objectToRender);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);

            // load from renderable data
            processorContext.load(objectToRender);

            // apply any overrides from special cases
            if (uri != null)
            {
                ProcessorDescriptor viewDescriptor = processorContext.getDescriptor(RenderMode.VIEW);
                viewDescriptor.put(URI, uri);
            }

            // execute the processor
            try
            {
                processor.execute(processorContext, object, renderFocus);
                handleIEStyleSheetBug(context);
            }
            catch (RendererExecutionException ree)
            {
                logger.error("Unable to process component: " + object.getId());
                throw ree;
            }
        }
    }
    
    /**
     * This is a workaround for the Internet Explorer bug detailed in KB262161
     * "All style tags after the first 30 style tags on an HTML page are not applied in Internet Explorer"
     * http://support.microsoft.com/kb/262161
     * @throws RendererExecutionException 
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    private void handleIEStyleSheetBug(RequestContext context) throws RendererExecutionException
    {
        if (context.hasValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME))
        {
            // stylesheets to consolidate
            LinkedList<String> css = (LinkedList<String>)context.getValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
            if (css != null)
            {
                try
                {
                    Iterator iter = css.iterator();
                    HttpServletResponse response = null;
                    response = context.getResponse();
                    Writer writer = response.getWriter();
                    writer.write("   <style type=\"text/css\" media=\"screen\">\n");
                    while (iter.hasNext())
                    {
                        writer.write("      @import \"" + iter.next() + "\";\n");
                    }
                    writer.write("   </style>");
                }
                catch (IOException ioe)
                {
                    throw new RendererExecutionException(ioe);
                }
            }
            context.removeValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
        }
    }
    
    /**
     * Executes the processor for the given render focus on the given template instance.
     *
     * @param context
     * @param renderFocus
     * @param template
     * @throws RendererExecutionException
     */
    public void processTemplate(RequestContext context, RenderFocus renderFocus, TemplateInstance template)
        throws RendererExecutionException
    {
        // special case variable for freemarker
        String uri = null;

        // the template type id
        TemplateType templateType = null;
        String templateTypeId = template.getTemplateTypeId();

        // test to see if this is a valid template type
        if (templateTypeId != null)
        {
            templateType = this.modelObjectService.getTemplateType(templateTypeId);
            if (templateType == null)
            {
                // assume it is a freemarker template id
                uri = templateTypeId;

                // load the template type
                templateType = this.modelObjectService.getTemplateType(TEMPLATE_TYPE_WEBTEMPLATE);
            }
        }
        else
        {
            // template type id is null and uri is null
            // shortcut: assume template type is the same as template id
            templateTypeId = template.getId();

            // if we have a uri, assume it is freemarker
            if (templateTypeId == null)
            {
                templateTypeId = TEMPLATE_TYPE_WEBTEMPLATE;
            }

            // load the template type
            templateType = this.modelObjectService.getTemplateType(templateTypeId);
        }

        // catch issues where the URL etc. have not been defined
        if (templateType == null)
        {
            throw new RendererExecutionException("Cannot resolve template - may be missing from the definition: " +
                    template.toString());
        }

        // get the processor
        Processor processor = getRenditionProcessor((Renderable)templateType);
        if (processor != null)
        {
            // build a processor context
            ProcessorContext processorContext = new ProcessorContext(context);

            // load from renderable data
            processorContext.load((Renderable)templateType);

            // apply any overrides from special cases
            if (uri != null)
            {
                ProcessorDescriptor viewDescriptor = processorContext.getDescriptor(RenderMode.VIEW);
                viewDescriptor.put(URI, uri);
            }

            // execute the processor
            try
            {
                processor.execute(processorContext, template, renderFocus);
            }
            catch(RendererExecutionException ree)
            {
                logger.error("Unable to process template: " + template.getId());
                throw ree;
            }
        }
    }

    /**
     * Entry point for the rendering of the current page as provided
     * by the request context.
     *
     * @param context the render context
     *
     * @throws RendererExecutionException
     * @throws RequestDispatchException
     */
    public void renderPage(RequestContext context, RenderFocus renderFocus)
    {
        try
        {
            if (this.webFrameworkConfiguration.isSurfBugEnabled())
            {
                // Always clear the current Component from SurfBug when rendering a page because
                // otherwise single Component pages will only get debugged on the first viewing...
                SurfBug surfBug = getSurfBug();
                if (surfBug != null)
                {
                    surfBug.setCurrentComponent(null);
                }
            }
            
            Page page = context.getPage();
            if (page == null)
            {
                throw new PageRendererExecutionException("Unable to locate current page in request context");
            }
            else
            {
                this.pageRenderer.render(context, page, renderFocus);
            }
        }
        catch (RendererExecutionException e)
        {
            String pageId = context.getPageId();
            context.setValue(CONTEXT_VALUE_ERROR_THROWABLE, e);
            context.setValue(CONTEXT_VALUE_ERROR_PAGE_ID, pageId);
            handleRenderProblem(pageId, WebFrameworkConstants.DISPATCHER_HANDLER_PAGE_ERROR, context, e);
        }
    }

    /**
     * Entry point for the rendering of the current content item as
     * provided by the request context.
     *
     * @param parentContext the render context
     *
     * @throws RendererExecutionException
     */
    public void renderTemplate(RequestContext context, RenderFocus renderFocus)
    {
        try
        {
            TemplateInstance template = context.getTemplate();
            this.templateRenderer.render(context, template, RenderFocus.BODY);
        }
        catch (RendererExecutionException e)
        {
            String templateId = context.getTemplateId();
            context.setValue(CONTEXT_VALUE_ERROR_THROWABLE, e);
            context.setValue(CONTEXT_VALUE_ERROR_PAGE_ID, templateId);
            handleRenderProblem(templateId, WebFrameworkConstants.DISPATCHER_HANDLER_TEMPLATE_ERROR, context, e);
        }

    }

    /**
     * Entry point for the rendering a region of a given template
     *
     * @param parentContext
     * @param renderFocus
     * @param templateId
     * @param regionId
     * @param regionScopeId
     * @param overrideChromeId
     *
     * @throws RendererExecutionException
     */
    public void renderRegion(RequestContext context,
                             RenderFocus renderFocus,
                             String templateId,
                             String regionId,
                             String regionScopeId,
                             String overrideChromeId,
                             boolean chromeless)
    {
        try
        {
            String regionSourceId = RenderUtil.getSourceId(context, regionScopeId);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_ID, regionId);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID, regionScopeId);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID, regionSourceId);

            // figure out if a component is bound to this region
            // bind in the html id
            Component component = getComponentBoundToRegion(context, regionId, regionScopeId, regionSourceId);
            if (component != null)
            {
                // bind in the region's htmlid from bound component
                context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, RenderUtil.validHtmlId(component.getId()));
            }
            else
            {
                // bind in the region's htmlid by hand
                context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, "unbound-region-" + RenderUtil.validHtmlId(regionId));
            }

            String oldRegionChrome = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID); 
            if (overrideChromeId != null)
            {
                context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID, overrideChromeId);
            }
            
            // Set a context value to indicate that the region should be rendered without chrome (this is taken to mean
            // all chrome, i.e. component and sub-component chrome as well). 
            context.setValue(WebFrameworkConstants.RENDER_DATA_CHROMELESS, Boolean.valueOf(chromeless));

            // loads the "region renderer" bean and executes it
            this.regionRenderer.render(context, component, renderFocus);
            context.setValue(WebFrameworkConstants.RENDER_DATA_REGION_CHROME_ID, oldRegionChrome);
            context.setValue(WebFrameworkConstants.RENDER_DATA_CHROMELESS, Boolean.FALSE);
        }
        catch (RendererExecutionException e)
        {
            String regionSourceId = RenderUtil.getSourceId(context, regionScopeId);

            context.setValue(CONTEXT_VALUE_ERROR_THROWABLE, e);
            context.setValue(CONTEXT_VALUE_ERROR_TEMPLATE_ID, templateId);
            context.setValue(CONTEXT_VALUE_ERROR_REGION_ID, regionId);
            context.setValue(CONTEXT_VALUE_ERROR_REGION_SCOPE_ID, regionScopeId);
            context.setValue(CONTEXT_VALUE_ERROR_REGION_SOURCE_ID, regionSourceId);
            handleRenderProblem(regionId, WebFrameworkConstants.DISPATCHER_HANDLER_REGION_ERROR, context, e);
        }
    }

    /**
     * Renders the components of the region described by the render context
     * This method is generally called from the region include tag.
     *
     * @param parentContext
     * @throws RendererExecutionException
     */
    public void renderRegionComponents(RequestContext context, ModelObject object, boolean chromeless)
        throws RendererExecutionException
    {
        // values from the render context
        String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
        String regionScopeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
        String regionSourceId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID);

        // render in either one of two ways
        // if there is a component bound, then continue processing downstream
        // if not, then render a "no component" screen
        Component component = getComponentBoundToRegion(context, regionId, regionScopeId, regionSourceId);
        if (component != null)
        {
            // if we are in passive mode, then we won't bother to execute the renderer.
            // rather, we will notify the template that this component is bound to it
            if (context.isPassiveMode())
            {
                // don't render the component, just inform the current context what our component is
                context.setRenderingComponent(component);
            }
            else
            {
                // TODO: Use the setRenderingComponent as in the passive mode?
                context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID, component.getId());
                this.renderComponent(context, RenderFocus.BODY, component, null, chromeless);
            }
        }
        else
        {
            // stamp an htmlid onto the renderer context
            context.setValue(WebFrameworkConstants.RENDER_DATA_HTMLID, "unbound-region-" + regionId);

            // if we couldn't get a component, then redirect to a region "no-component" renderer
            boolean handled = renderErrorHandlerPage(context, WebFrameworkConstants.DISPATCHER_HANDLER_REGION_NO_COMPONENT);

            // TODO: do nothing or should we throw out to servlet container?
            if (!handled)
            {
                // do nothing for now
            }
        }
    }

    /**
     * Entry point for the rendering a single identified component
     * with the default chrome.
     *
     * @param context
     * @param renderFocus
     * @param componentId
     *
     * @throws RendererExecutionException
     */
    public void renderComponent(RequestContext context,
                                RenderFocus renderFocus,
                                Component component,
                                String chromeIdOverride,
                                boolean chromeless)
    {
        try
        {
            // Set the id of the current component to be rendered...
            context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID, component.getId());

            if (chromeless)
            {
                this.processComponent(context, renderFocus, component, chromeless);
            }
            else
            {
                String componentChromeId = null;
                if (chromeIdOverride != null && chromeIdOverride.length() != 0)
                {
                    componentChromeId = chromeIdOverride;
                }
                else if (component.getChrome() != null)
                {
                    componentChromeId = component.getChrome();
                }
                else
                {
                    componentChromeId = this.webFrameworkConfiguration.getDefaultComponentChrome();
                }
                Chrome chrome = this.modelObjectService.getChrome(componentChromeId);
                context.setValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_CHROME, chrome);
                this.componentRenderer.render(context, component, renderFocus);
            }
        }
        catch (RendererExecutionException e)
        {
            context.setValue(RenderService.CONTEXT_VALUE_ERROR_THROWABLE, e);
            context.setValue(RenderService.CONTEXT_VALUE_ERROR_COMPONENT_ID, component.getId());
            context.setRenderMode(RenderMode.VIEW);
            handleRenderProblem(component.getId(), WebFrameworkConstants.DISPATCHER_HANDLER_COMPONENT_ERROR, context, e);
        }
    }

    /**
     * Entry point for the rendering a component with the given chrome.
     *
     * @param parentContext
     * @param renderFocus
     * @param componentId
     * @param overrideChromeId
     * @throws RendererExecutionException
     *
     * @throws RendererExecutionException
     */
    public void renderComponent(RequestContext context,
                                RenderFocus renderFocus,
                                String componentId,
                                String overrideChromeId,
                                boolean chromeless)
    {
        Component component = this.modelObjectService.getComponent(componentId);
        renderComponent(context, renderFocus, component, overrideChromeId, chromeless);
    }


    /**
     * Returns the Chrome instance to utilize while rendering the given
     * region on the given template.
     */
    public Chrome getRegionChrome(String regionId, String chromeId)
    {
        Chrome chrome = null;
        if (chromeId != null && chromeId.length() != 0)
        {
            // We already have a chrome id, no action required.
        }
        else
        {
            // No chrome id was provided, so get the default region chrome.
            chromeId = this.webFrameworkConfiguration.getDefaultRegionChrome();
        }
        chrome = this.modelObjectService.getChrome(chromeId);
        return chrome;
    }

    /**
     * Determines the component which is bound to the given region
     * If there is no component bound, then null is returned.
     *
     * @param context
     * @param regionId
     * @param regionScopeId
     * @param regionSourceId
     *
     * @return the component
     */
    public Component getComponentBoundToRegion(RequestContext context,
            String regionId,
            String regionScopeId,
            String regionSourceId)
    {
        return this.modelObjectService.getComponent(regionScopeId, regionId, regionSourceId);
    }
    
    /**
     * Renders a default error handler page
     *
     * A system page can be configured to handle a fault state for the web framework.
     * Several fault states exist and a system page provides a way for default
     * presentation to be rendered back.
     *
     * @param context
     * @param errorHandlerPageId
     * @param defaultErrorHandlerPageRenderer
     * @return whether an error handler page could handle the fault
     */
    public boolean renderErrorHandlerPage(RequestContext context, String errorHandlerPageId) throws RendererExecutionException
    {
        boolean handled = false;
        // get the error handler descriptor from config
        ErrorHandlerDescriptor descriptor = this.webFrameworkConfiguration.getErrorHandlerDescriptor(errorHandlerPageId);
        if (descriptor != null)
        {
            // get descriptor properties and processor id
            String processorId = descriptor.getProcessorId();
            Map<String,String> descriptorProperties = descriptor.map();

            // create the processor
            Processor processor = getRenditionProcessorById(processorId);

            // load processor context
            ProcessorContext processorContext = new ProcessorContext(context);
            processorContext.addDescriptor(RenderMode.VIEW, descriptorProperties);

            // execute processor
            processor.executeBody(processorContext, null);

            // mark as successful
            handled = true;
        }
        return handled;
    }

    /**
     * Renders a default system page
     *
     * A system container is a page fragment that is rendered
     * as a container of other elements like components.
     *
     * @param context
     * @param systemPageId
     * @returns whether the system page was
     */
    public boolean renderSystemPage(RequestContext context, String systemPageId) throws RendererExecutionException
    {
        boolean handled = false;

        // get the system page descriptor from config
        SystemPageDescriptor descriptor = this.webFrameworkConfiguration.getSystemPageDescriptor(systemPageId);
        if (descriptor != null)
        {
            // get descriptor properties and processor id
            String processorId = descriptor.getProcessorId();
            Map<String,String> descriptorProperties = descriptor.map();

            // create the processor
            Processor processor = getRenditionProcessorById(processorId);

            // load processor context
            ProcessorContext processorContext = new ProcessorContext(context);
            processorContext.addDescriptor(RenderMode.VIEW, descriptorProperties);

            // execute processor
            processor.executeBody(processorContext, null);

            // mark as successful
            handled = true;
        }

        return handled;
    }

    /**
     * Generates text to be inserted into template markup head for a given
     * renderer context.  The renderer context must describe a template.
     *
     * @param rendererContext
     *
     * @return head tags render output
     */
    public String renderTemplateHeaderAsString(RequestContext context, ModelObject object)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        String headTags = "";
        if (context.isPassiveMode())
        {
            // No action required. If we're in passive mode, just return empty string
        }
        else
        {
            try
            {
                /* At this point we need to move the context into passive mode to prevent the header
                 * information being output onto the response too early. By setting passive mode in the
                 * context we will render the header output into the FakeHttpServletResponse which we
                 * are then able to retrieve via the .getContentAsString() method. This will then
                 * be stored as a value in the model which is retrieved using the ${head} property in
                 * the FreeMarker template.
                 */
                context.setPassiveMode(true);

                // execute the renderer for 'header'
                this.templateRenderer.header(context, object);

                // get head tags from captured output
                headTags = context.getContentAsString();

                if (headTags == null)
                {
                    headTags = "";
                }
            }
            catch (Exception e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("The following error occurred rendering the template header.", e);
                }
            }
            finally
            {
                // It is important that we ensure that we've switched back out of passive mode
                // so that normal processing be output onto the standard HttpServletResponse.
                context.setPassiveMode(false);
            }
        }
        return headTags;
    }

    /**
     * <p>A <code>ResourceService</code> is used to obtain URLs to resources in the <code>generateResourceURL</code>
     * method. This method was added as requirement of the SpringSurf JSP TagLib and FreeMarker directives which this
     * service is primarily intended to cater for. It does not necessarily fit with the rest of the methods in the
     * class so could potentially be moved out at a later date if necessary.</p>
     */
    private ResourceService resourceService;

    /**
     * <p>This setter method is provided so that Spring can inject a <code>ResourceService</code> into this bean.</p>
     * @param resourceService
     */
    public void setResourceService(ResourceService resourceService)
    {
        this.resourceService = resourceService;
    }

    /**
     * <p>A <p>LinkBuilderFactory</p> is required when building links to render. It has been included as
     * a property of this bean in order to service the <code>generateLink</code> and <code>generateAnchorLink</code>
     * methods. A <code>LinkBuilderFactory</code> should be configured as a property when defining this class
     * as a Spring bean.</code>
     */
    private LinkBuilderFactory linkBuilderFactory;

    /**
     * <p>This <code>LinkBuilder</code> is instantiated whenever the <code>setLinkBuilderFactory</code>
     * method is called. This is because the <code>PresentationService</code> will typically only
     * require a single <code>LinkBuilder</code> but only the factories are currently available as
     * an injectable service.</p>
     */
    private LinkBuilder linkBuilder;

    /**
     * <p>This method is provided to allow the Spring framework to inject a <code>LinkBuilderFactory</code>
     * into this class when it is instantiated as a Spring bean. As well as setting the <code>LinkBuilderFactory</code>
     * it will also use the factory to create a new <code>LinkBuilder</code>.
     * @param linkBuilderFactory
     */
    public void setLinkBuilderFactory(LinkBuilderFactory linkBuilderFactory)
    {
        this.linkBuilderFactory = linkBuilderFactory;
        this.linkBuilder = this.linkBuilderFactory.newInstance();
    }

    /**
     * <p>Convenience method provided for both the "surfbugInclude" FreeMarker directive and 
     * JSP tag to use. Currently this only defers control to the <code>processComponent</code>
     * method but in the future may be required to carry out additional processing</p>
     *  
     * @param context
     * @throws RendererExecutionException
     */
    public void renderSurfBugInclude(RequestContext context) throws RendererExecutionException
    {        
        this.processComponent(context, RenderFocus.BODY, getSurfBug().getCurrentComponent(), false);
    }
    
    /**
     * <p>Renders either a component or a region depending upon the value of <code>RENDER_TYPE</code> key set in the
     * current <code>RequestContext</code>. This means that either the <{@code}componentInclude> or <{@code}regionInclude>
     * tag can be incorrectly used within the chrome implementation without errors occurring.</p>
     *
     * @param context
     */
    public void renderChromeInclude(RequestContext context, ModelObject object) throws RequestDispatchException
    {
        Object value = context.getValue(WebFrameworkConstants.RENDER_TYPE);
        if (value != null && value instanceof String)
        {
            String renderType = (String) value;
            if (renderType.equals(WebFrameworkConstants.RENDER_COMPONENT))
            {
                // Render the associated component (as defined in the context)...
                String componentId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT_ID);
                if (componentId != null)
                {
                    // Render the component but notice that we're requesting to render the component
                    // without chrome (via the boolean argument) as we've already rendered the chrome
                    // for the component (which is why this method is being executed!)...
                    this.renderComponent(context, RenderFocus.BODY, componentId, null, true);
                }
                else
                {
                    handleError(context, "Could not find a component id to render",
                                "The id of the component was expected to be stored in the RequestContext " +
                                "as the \"component-id\" attribute.", null);
                }
            }
            else if (renderType.equals(WebFrameworkConstants.RENDER_REGION))
            {
                // Render the associated region (as defined in the context)...
                try
                {
                    this.renderRegionComponents(context, object, false);  // We can set chromeless to be false, since we can only have got here by rendering chrome
                }
                catch (Throwable t)
                {
                    String regionId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_ID);
                    String regionScopeId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SCOPE_ID);
                    String regionSourceId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_REGION_SOURCE_ID);
                    handleError(context,
                                "An error occurred attempting to render the components of the region: " + regionId +
                                ", at scope: " + regionScopeId + ", from the source: " + regionSourceId,
                                null, t);
                }
            }
            else if (renderType.equals(WebFrameworkConstants.RENDER_SUB_COMPONENT))
            {
                try
                {
                    Object subComponent = context.getValue(WebFrameworkConstants.RENDER_DATA_SUB_COMPONENT);
                    if (subComponent instanceof SubComponentData)
                    {
                        // Render the SubComponent wrapped in the sub-component chrome...
                        this.renderSubComponent(((SubComponentData) subComponent).getSubComponent(), context, RenderFocus.BODY);
                    }
                    else
                    {
                        if (logger.isErrorEnabled())
                        {
                            if (object != null)
                            {
                                logger.error("Chrome attempted to render the object: '" + subComponent + "' but it not a <" + AdvancedComponent.SUB_COMPONENT + ">");    
                            }
                            else
                            {
                                logger.error("Chrome attempted to render a null object");
                            }
                        }
                    }
                }
                catch (Throwable t)
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("The following error occurred attemping to render a sub-component", t);
                    }
                }
            }
            else
            {
                // Sanity check - an unexpected RENDER_TYPE has been set. Currently this tag is
                // only programmed to handle region or component rendering.
                logger.error("Unexpected render type set in RequestContext: " + renderType);
                handleError(context, "Unexpected render type",
                            "The RequestContext should have been set with an attribute \"RENDER_TYPE\" which" +
                            "should have been either \"" + WebFrameworkConstants.RENDER_COMPONENT + "\" or \"" +
                            WebFrameworkConstants.RENDER_REGION + "\", but the actual value was set to: \"" +
                            renderType + "\". The only place that this attribute should be set is in the " +
                            ChromeRenderer.class.getName() + " class", null);
            }
        }
        else if (value == null)
        {
            // Sanity check - somehow the RENDER_TYPE key has been set to null. This should not have
            // happened and is likely to be the result of a developer error.
            logger.error("RequestContext key has not been set");
            handleError(context, "Unexpected render type class",
                    "The RequestContext should have been set with an attribute \"RENDER_TYPE\" which" +
                    "should have been either \"" + WebFrameworkConstants.RENDER_COMPONENT + "\" or \"" +
                    WebFrameworkConstants.RENDER_REGION + "\", but it has not been set at all.", null);
        }
        else
        {
            // Sanity check - somehow the RENDER_TYPE key has been set with a non-string value,
            // this should not have happened and is likely to be the result of a developer error.
            // Therefore we will throw an exception in the hope this will get caught at development
            // time before the code is released.
            logger.error("Unexpected render type set in RequestContext: " + value.getClass().getName());
            handleError(context, "Unexpected render type class",
                        "The RequestContext should have been set with an attribute \"RENDER_TYPE\" which" +
                        "should have been either \"" + WebFrameworkConstants.RENDER_COMPONENT + "\" or \"" +
                        WebFrameworkConstants.RENDER_REGION + "\", but the actual value was set to an " +
                        "unexpected Class: " + value.getClass().getName(), null);
        }
    }

    /**
     * <p>This method wraps the <code>generateLink</code> method to place the link inside an HTML anchor tag which is then
     * returned. The anchor tag can optionally include a "target" property if one is provided.</p>
     *
     * @param context
     * @param pageTypeId
     * @param pageId
     * @param objectId
     * @param formatId
     * @param target
     * @return
     */
    public String generateAnchorLink(String pageTypeId,
                                     String pageId,
                                     String objectId,
                                     String formatId,
                                     String target)
    {
        StringBuilder sb = new StringBuilder();
        sb.append("<A href=\"");
        sb.append(generateLink(pageTypeId, pageId, objectId, formatId));
        sb.append("\"");
        if (target != null)
        {
            sb.append(" target=\"");
            sb.append(target);
            sb.append("\"");
        }
        sb.append(">");
        return sb.toString();
    }

    /**
     * <p>Uses the <code>LinkBuilder</code> created from the <code>LinkBuilderFactory</code> that should have been set as
     * a property of this class when instantiated as a Spring bean.</p>
     *
     * @param pageTypeId
     * @param pageId
     * @param objectId
     * @param formatId
     * @return
     */
    public String generateLink(String pageTypeId,
                               String pageId,
                               String objectId,
                               String formatId)
    {
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        String link = null;
        if (pageTypeId != null)
        {
            if (objectId != null)
            {
                link = this.linkBuilder.pageType(context, pageTypeId, formatId, objectId);
            }
            else
            {
                link = this.linkBuilder.pageType(context, pageTypeId, formatId);
            }
        }
        else if (pageId != null)
        {
            if (objectId != null)
            {
                link = this.linkBuilder.page(context, pageId, formatId, objectId);
            }
            else
            {
                link = this.linkBuilder.page(context, pageId, formatId);
            }
        }
        else if (objectId != null)
        {
            link = this.linkBuilder.object(context, objectId, formatId);
        }
        return link;
    }

    /**
     *
     * @param renderContext
     * @param name
     * @param id
     * @param protocol
     * @param endpoint
     * @param objectId
     * @param payload
     * @return
     */
    public String generateResourceURL(RequestContext renderContext,
                                      ModelObject object,
                                      String name,
                                      String id,
                                      String protocol,
                                      String endpoint,
                                      String objectId,
                                      String payload)
    {
        String url = null;
        if (object != null)
        {
            try
            {
                Resource resource = null;

                // named resources
                if (name != null && object instanceof ResourceProvider)
                {
                    ResourceProvider provider = (ResourceProvider) object;
                    resource = provider.getResource(name);
                }
                else if (id != null)
                {
                    // look up by resource id
                    resource = resourceService.getResource(id);
                }
                else
                {
                    // look up by resource descriptor ids
                    resource = resourceService.getResource(protocol, endpoint, objectId);
                }

                url = resource.getContentURL();
                if ("metadata".equalsIgnoreCase(payload))
                {
                    url = resource.getMetadataURL();
                }
            }
            catch (ResourceLoaderException e)
            {
                // If a ResourceLoaderException occurs then there is very little that can be done. Rather than throwing
                // the exception we will return a message indicating what has happened, but will return the error message
                // as the URL. This should allow pages to still render.
                String msg = "An exception occurred loading a resource using the following values, name=\"" + name +
                             "\", id=\"" + id + "\", protocol=\"" + protocol + "\", endpoint=\"" + endpoint +
                             "\", objectId=\"" + objectId + "\", payload=\"" + payload + "\"";
                logger.error(msg, e);
                url = msg;
            }
        }

        return url;
    }

    /**
     * <p>Adds to the (or creates a new) <code>LinkedList</code> of CSS style sheet URLs stored as the
     * <code>WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME</code> attribute in the supplied
     * <code>RequestContext</code>.</p>
     *
     * @param context
     * @param href
     */
    @SuppressWarnings("unchecked")
    public void updateStyleSheetImports(RequestContext context, String href)
    {
        LinkedList<String> css = null;
        if (context.hasValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME))
        {
            css = (LinkedList<String>)context.getValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME);
        }
        else
        {
            css = new LinkedList<String>();
            context.setValue(WebFrameworkConstants.STYLESHEET_RENDER_CONTEXT_NAME, css);
        }
        css.add(href);
    }

    /**
     * <p>This method sets up the <code>RequestContext</code> and attempts to redirect the
     * request to the general error page. It only sets the context with non-null arguments.
     * Ideally, a title, description and <code>Throwable</code> instance should be supplied
     * to provide as much debug information as possible. The description should be as verbose
     * as possible and can contain HTML elements to format the output if required.</p>
     * <p>Currently the output is not translated. In the future this should be updated to
     * accept NLS keys to make the error page consumable in other languages.</p>
     * @param context The context to add the diagnostic information to.
     * @param errorTitle The title of the error (a short explanation of what went wrong!)
     * @param errorDescription A verbose description of what has happened and what could be
     * done to address the issue.
     * @param t A <code>Throwable</code> instance from which to obtain a stack trace to
     * display.
     * @throws RequestDispatchException
     */
    protected void handleError(RequestContext context,
                               String errorTitle,
                               String errorDescription,
                               Throwable t) throws RequestDispatchException
    {
        if (errorTitle != null)
        {
            context.setValue(CONTEXT_VALUE_ERROR_TITLE, errorTitle);
        }
        else
        {
            // No title was provided for the error. No action required.
        }

        if (errorDescription != null)
        {
            context.setValue(CONTEXT_VALUE_ERROR_DESCRIPTION, errorDescription);
        }
        else
        {
            // No description was provided for the error. No action required.
        }

        if (t != null)
        {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            t.printStackTrace(pw);
            context.setValue(CONTEXT_VALUE_STACKTRACE, pw.toString());
        }
        else
        {
            // No Throwable was provided for the error. No action required.
        }
        handleRenderProblem("", WebFrameworkConstants.DISPATCHER_HANDLER_GENERAL_ERROR, context, t);
    }
    /**
     * <p>This method is invoked whenever the <code>RenderService</code> fails to render something. It
     * renders the requested error page (hopefully!) indicating the cause of the problem.</p>
     *
     * @param targetId The id of the page/template/region/component that could not be rendered
     * @param errorPageId The target error page to render
     * @param context The current <code>RequestContext</code>
     * @throws RequestDispatchException When the error page cannot be rendered.
     */
    protected void handleRenderProblem(String targetId,
                                       String errorPageId,
                                       RequestContext context,
                                       Throwable t)
    {
        logger.error("An exception occurred while rendering: " + targetId, t);
        try
        {
            // If unable to handle things gracefully, throw exception out to caller
            if (!renderErrorHandlerPage(context, errorPageId))
            {
                // TODO: Handle rendering failures.
                logger.error("An error occurred rendering error page: " + errorPageId);
            }
        }
        catch (Exception e)
        {
            // TODO: Handle unexpected errors that occur rendering the error page.
            logger.error(e);
        }
    }

    /* ************************************************
     *                                                *
     * SETTERS FOR SPRING BEAN PROPERTIES             *
     *                                                *
     **************************************************/

    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }

    public void setWebFrameworkConfiguration(WebFrameworkConfigElement webFrameworkConfiguration)
    {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
    }

    public void setPageRenderer(PageRenderer pageRenderer)
    {
        this.pageRenderer = pageRenderer;
    }

    public void setTemplateRenderer(TemplateInstanceRenderer templateRenderer)
    {
        this.templateRenderer = templateRenderer;
    }

    public void setRegionRenderer(RegionRenderer regionRenderer)
    {
        this.regionRenderer = regionRenderer;
    }

    public void setComponentRenderer(ComponentRenderer componentRenderer)
    {
        this.componentRenderer = componentRenderer;
    }
    
    public void setChromeRenderer(ChromeRenderer chromeRenderer)
    {
        this.chromeRenderer = chromeRenderer;
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }

    /**
     * <p>This variable will be set to the bean name set when defining this class as a Spring bean. It
     * is set by the <code>setBeanName</code> method which is defined by the <code>BeanNameAware</code>
     * interface that this class implements. The bean name is only used for identifying the bean in
     * log messages to assist debugging.</p>
     */
    private String beanName;

    /**
     * <p>This method is required to implement the the <code>BeanNameAware</code> interface and will be
     * invoked by the Spring framework to set the configured name of this class when defined as a Spring
     * bean.</p>
     */
    public void setBeanName(String name)
    {
        this.beanName = name;
    }

    /**
     * <p>Returns the id given to this class when it is defined as a Spring bean. If this class has not
     * bean instantiated by the Spring framework then this will return null.</p>
     * @return The "id" property given to this class when defining it as a Spring bean.
     */
    public String getBeanName()
    {
        return this.beanName;
    }
}

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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.mozilla.javascript.Scriptable;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.directives.AnchorFreeMarkerDirective;
import org.springframework.extensions.directives.AutoComponentRegionDirective;
import org.springframework.extensions.directives.ChromeIncludeFreeMarkerDirective;
import org.springframework.extensions.directives.ComponentFreemarkerTagDirective;
import org.springframework.extensions.directives.CreateComponentDirective;
import org.springframework.extensions.directives.DirectiveFactory;
import org.springframework.extensions.directives.LinkFreeMarkerDirective;
import org.springframework.extensions.directives.MessagesDependencyDirective;
import org.springframework.extensions.directives.OutputCSSDirective;
import org.springframework.extensions.directives.OutputJavaScriptDirective;
import org.springframework.extensions.directives.ProcessJsonModelDirective;
import org.springframework.extensions.directives.RegionFreemarkerTagDirective;
import org.springframework.extensions.directives.ResourceFreemarkerTagDirective;
import org.springframework.extensions.directives.StyleSheetFreeMarkerDirective;
import org.springframework.extensions.directives.SurfBugIncludeFreeMarkerDirective;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.MarkupDirective;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.render.RenderService.SubComponentData;
import org.springframework.extensions.surf.types.Chrome;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.types.SurfBug;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.util.I18NUtil;

import freemarker.ext.jsp.TaglibFactory;
import freemarker.ext.servlet.FreemarkerServlet;
import freemarker.ext.servlet.HttpRequestHashModel;
import freemarker.ext.servlet.HttpRequestParametersHashModel;
import freemarker.ext.servlet.HttpSessionHashModel;
import freemarker.ext.servlet.ServletContextHashModel;
import freemarker.template.ObjectWrapper;

/**
 * Helper to generate the model map for Script and Template execution.
 * <p>
 * The model consists of a number of context driven objects such as current
 * page and current template and a number of common helper objects such as the
 * URL and current user. 
 * 
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public final class ProcessorModelHelper implements ApplicationContextAware
{
    public static final String MODEL_CONFIG = "config";
    public static final String MODEL_HEAD = "head";
    public static final String MODEL_URL = "url";
    public static final String MODEL_USER = "user";
    public static final String MODEL_INSTANCE = "instance";
    public static final String MODEL_CONTENT = "content";
    public static final String MODEL_CONTEXT = "context";
    public static final String MODEL_SITEDATA = "sitedata";
    public static final String MODEL_WIDGET_UTILS = "widgetUtils";
    public static final String MODEL_RESOURCE_UTILS = "resourceUtils";
    public static final String MODEL_LOCALE = "locale";
    public static final String MODEL_TEMPLATE = "template";
    public static final String MODEL_PAGE = "page";
    public static final String MODEL_PROPERTIES = "properties";
    public static final String MODEL_THEME = "theme";
    public static final String MODEL_DESCRIPTION = "description";
    public static final String MODEL_DESCRIPTION_ID = "descriptionId";
    public static final String MODEL_TITLE = "title";
    public static final String MODEL_TITLE_ID = "titleId";
    public static final String MODEL_ID = "id";
    public static final String MODEL_FORM_INSTANCE = "forminstance";
    public static final String MODEL_FORMDATA = "formdata";
    public static final String MODEL_APP = "app";
    public static final String PROP_HTMLID = "htmlid";
    public static final String MODEL_MESSAGE_METHOD = "msg";
    public static final String MODEL_RESOURCE_URL_METHOD = "resourceurl";
    public static final String MODEL_SURF = "surf";
    public static final String MODEL_AUTHENTICATION = "authentication";
    
    public static final String REGION_DIRECTIVE_NAME = "region";
    public static final String COMPONENT_DIRECTIVE_NAME = "component";
    public static final String REGION_INCLUDE_DIRECTIVE_NAME = "regionInclude";
    public static final String COMPONENT_INCLUDE_DIRECTIVE_NAME = "componentInclude";
    public static final String MARKUP_DIRECTIVE_NAME = "markup";
    public static final String SURFBUG_INCLUDE_DIRECTIVE_NAME = "surfbugInclude";
    public static final String RESOURCE_DIRECTIVE_NAME = "res";
    public static final String ANCHOR_DIRECTIVE_NAME = "anchor";
    public static final String PAGE_LINK_DIRECTIVE_NAME = "pagelink";
    public static final String LINK_DIRECTIVE_NAME = "link";
    public static final String MESSAGES_DIRECTIVE_NAME = "generateMessages";
    public static final String CHECKSUM_RESOURCE_DIRECTIVE_NAME = "checksumResource";
    public static final String RELOCATE_JAVASCRIPT_DEPENDENCIES_DIRECTIVE_NAME = "relocateJavaScript";
    public static final String CREATE_WEBSCRIPT_WIDGETS_DIRECTIVE_NAME = "createWidgets";
    public static final String ADD_INLINE_JAVASCRIPT_DIRECTIVE_NAME = "inlineScript";
    public static final String CHROME_DETECTION_DIRECTIVE_NAME = "uniqueIdDiv";
    public static final String STANDALONE_WEBSCRIPT_WRAPPER_DIRECTIVE_NAME = "standalone";
    
    
    public static final String SCRIPT_DIRECTIVE_NAME = "script";
    
    private static final FreemarkerI18NMessageMethod FREEMARKER_MESSAGE_METHOD_INSTANCE =
        new FreemarkerI18NMessageMethod();
    private static final FreemarkerResourceUrlMethod FREEMARKER_RESOURCE_URL_METHOD_INSTANCE =
        new FreemarkerResourceUrlMethod();
    private static final ScriptMessageResolver SCRIPT_MESSAGE_INSTANCE =
        new ScriptMessageResolver();
    
    /*
     * Templates have the following:
     * 
     * sitedata
     * context
     * content
     * user
     * instance (current object being rendered)
     * page
     * theme
     * htmlid
     * url
     * head
     * 
     * 
     * Components have the following
     * 
     * sitedata
     * context
     * content
     * user
     * instance (current object being rendered)
     * page
     * theme
     * htmlid
     * 
     * All rendering objects have the following model elements:
     * 
     * Application (servlet context)
     * Session
     * Request
     * RequestParameters 
     * JspTaglibs
     */
    /**
     */
    private ApplicationContext applicationContext;
    
    /**
     * <p>This method is provided so that the Spring Framework can set the <code>ApplicationContext</code> required
     * 
     * @param applicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext)
    {
        this.applicationContext = applicationContext;
    }
    
    private TaglibFactory taglibFactory;

    public TaglibFactory getTaglibFactory()
    {
        return taglibFactory;
    }

    public void setTaglibFactory(TaglibFactory taglibFactory)
    {
        this.taglibFactory = taglibFactory;
    }
    
    private ServletContextHashModel servletContextHashModel;

    public ServletContextHashModel getServletContextHashModel()
    {
        return servletContextHashModel;
    }

    public void setServletContextHashModel(ServletContextHashModel servletContextHashModel)
    {
        this.servletContextHashModel = servletContextHashModel;
    }

    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    public WebFrameworkConfigElement getWebFrameworkConfigElement()
    {
        return webFrameworkConfigElement;
    }

    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    private ScriptConfigModel scriptConfigModelInstance;
    
    public void setScriptConfigModelInstance(ScriptConfigModel scriptConfigModelInstance)
    {
        this.scriptConfigModelInstance = scriptConfigModelInstance;
    }

    private TemplateConfigModel templateConfigModelInstance;
            
    public void setTemplateConfigModelInstance(TemplateConfigModel templateConfigModelInstance)
    {
        this.templateConfigModelInstance = templateConfigModelInstance;
    }

    private RenderService webFrameworkRenderService;
    
    public void setWebFrameworkRenderService(RenderService webFrameworkRenderService)
    {
        this.webFrameworkRenderService = webFrameworkRenderService;
    }

    private DirectiveFactory directiveFactory;
    
    public void setDirectiveFactory(DirectiveFactory directiveFactory)
    {
        this.directiveFactory = directiveFactory;
    }
    
    private DependencyAggregator dependencyAggregator;
    
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
    
    /**
     * A no argument constructor is required for Spring Framework to instantiate this class as a Spring Bean. 
     */
    public ProcessorModelHelper()
    {
        // No action required.        
    }
    
    /**
     * Populates the common model for all processors
     * 
     * @param context   render context
     * @param model     to populate
     */
    private void populateModel(RequestContext context, Map<String, Object> model, ModelObject object)
    {
        // merge in spring mvc model
        if (context.getModel() != null && model != null)
        {
            model.putAll(context.getModel());
        }
        
        HttpServletRequest req = ServletUtil.getRequest();
        
        // merge in servlet container objects (taglib integration and more)
        model.put(FreemarkerServlet.KEY_JSP_TAGLIBS, taglibFactory);
        model.put(FreemarkerServlet.KEY_APPLICATION, servletContextHashModel);
        if (req != null)
        {
            model.put(FreemarkerServlet.KEY_SESSION, buildSessionModel(req, context.getResponse()));
            model.put(FreemarkerServlet.KEY_REQUEST, new HttpRequestHashModel(req, context.getResponse(), ObjectWrapper.DEFAULT_WRAPPER));
            model.put(FreemarkerServlet.KEY_REQUEST_PARAMETERS, new HttpRequestParametersHashModel(req));
        }
       
        setPageProps(context, model);
        setTemplateProps(context, model);       
        
        // if we're rendering a component, then provide a "form" object
        if (object instanceof Component)
        {
            // add form
            ScriptForm formInstance = new ScriptForm(context, object);
            model.put(MODEL_FORM_INSTANCE, formInstance);
            
            if ("POST".equalsIgnoreCase(context.getRequestMethod()))
            {
                ScriptFormData formData = new ScriptFormData(context, object);
                model.put(MODEL_FORMDATA, formData);
            }
        }  
        
        // If the current ModelObject is a SurfBug object then we want to setup some information regarding
        // the Component that it is being debugged wrapping.
        if (object instanceof SurfBug)
        {            
            Component component = ((SurfBug) object).getCurrentComponent();
            if (component != null)
            {
                String path = component.getKey().getSourcePath();
                if (path == null)
                {
                    model.put("storagePath", component.getKey().getStoragePath());    
                }
                else
                {
                    model.put("storagePath", path);
                }  
                
                model.put("componentDebug", component.getModelProperties());
                model.put("componentCustomPropsDebug", component.getCustomProperties());
                
                WebScript webscript = component.getResolvedWebScript();
                if (webscript != null)
                {
                    String storePath = webscript.getDescription().getStorePath();
                    String descPath = webscript.getDescription().getDescPath();
                    model.put("resolvedWSStorePath", storePath);
                    model.put("resolvedWSDescPath", descPath);
                    model.put("resolvedWSBasePath", context.getContextPath() + "/page/script/" + webscript.toString());
                }
            }
        }
        
        if (object instanceof Chrome)
        {
            Object o = context.getValue(WebFrameworkConstants.RENDER_DATA_SUB_COMPONENT);
            if (o instanceof SubComponentData)
            {
                SubComponentData subComponentData = (SubComponentData) o;
                populateSurfBugData(context, model, subComponentData);
            }
        }
        
        // the global app theme
        model.put(MODEL_THEME, context.getThemeId());
        
        // locale for the current thread
        model.put(MODEL_LOCALE, I18NUtil.getLocale().toString());
        
        //
        // add in the root-scoped web framework script objects
        //
        ScriptSiteData scriptSiteData = new ScriptSiteData(context, applicationContext); 
        model.put(MODEL_SITEDATA, scriptSiteData);
        
        ScriptWidgetUtils scriptWidgetUtils = new ScriptWidgetUtils(); 
        model.put(MODEL_WIDGET_UTILS, scriptWidgetUtils);
        
        ScriptResourceUtils scriptResourceUtils = new ScriptResourceUtils(this.dependencyAggregator); 
        model.put(MODEL_RESOURCE_UTILS, scriptResourceUtils);
        
        ScriptRenderContext scriptRequestContext = new ScriptRenderContext(context);
        model.put(MODEL_CONTEXT, scriptRequestContext);
        
        ScriptSurf scriptSurf = new ScriptSurf(context);
        model.put(MODEL_SURF, scriptSurf);
        
        if (context.getCurrentObject() != null)
        {
            ScriptResource scriptResource = new ScriptResource(context, context.getCurrentObject());
            model.put(MODEL_CONTENT, scriptResource);
        }
        
        ScriptRenderingInstance scriptRenderer = new ScriptRenderingInstance(context, object);
        model.put(MODEL_INSTANCE, scriptRenderer);
        
        // add in the web application reference
        ScriptWebApplication scriptWebApplication = new ScriptWebApplication(context);
        model.put(MODEL_APP, scriptWebApplication);
        
        // add in the current User
        if (context.getUser() != null)
        {
            ScriptUser scriptUser = new ScriptUser(context, context.getUser());
            model.put(MODEL_USER, scriptUser);
        }        
        
        // we are also given the "rendering configuration" for the current
        // object.  usually, this is either a component or a template.
        // in either case, the configuration is set up ahead of time
        // our job here is to make sure that freemarker has everything
        // it needs for the component or template to process
        String htmlBindingId = (String) context.getValue(WebFrameworkConstants.RENDER_DATA_HTMLID);
        if (htmlBindingId != null && htmlBindingId.length() != 0)
        {
            model.put(PROP_HTMLID, htmlBindingId);
        }
    }
    
    /**
     * <p>This method should be used to populate the model with data that will be displayed when SurfBug
     * is enabled. It collects key information about both the current {@link SubComponent} and its parent
     * {@link Component}.
     * @param context The current {@link RequestContext}
     * @param model The model being populated for use by the template.
     * @param subComponentData Data about the current {@link SubComponent} being rendered.
     */
    private void populateSurfBugData(RequestContext context, 
                                     Map<String, Object> model, 
                                     SubComponentData subComponentData)
    {
        SubComponent subComponent = subComponentData.getSubComponent();
        
        model.put("surfBugPageId", context.getPageId());
        model.put("surfBugTemplateId", context.getTemplateId());
        model.put("surfBugTemplatePath", context.getTemplate().getTemplateTypeId());
        
        // What information do we need to provide about a SubComponent...
        // It's ID and it's parent ID..
        model.put(WebFrameworkConstants.RENDER_DATA_SURFBUG_ENABLED, context.getValue(WebFrameworkConstants.RENDER_DATA_SURFBUG_ENABLED));
        model.put("subComponent_id", subComponent.getId());
        model.put("subComponent_parentId", subComponent.getParentId());
        model.put("subComponent_index", subComponent.getIndex());
        model.put("subComponent_processor", subComponent.getProcessorId());
        
        // Convert the list of paths that have contributed to the sub-component (since a sub-component can 
        // either be provided or contributed to by a extending module) into a usable string...
        StringBuilder s = new StringBuilder();
        Iterator<String> i = subComponentData.getContributingSourcePaths().iterator();
        while (i.hasNext())
        {
            s.append(i.next());
            if (i.hasNext())
            {
                s.append(",");
            }
        }
        model.put("subComponent_paths", s.toString());
        
        // Get the WebScript data for the SubComponent.
        WebScript scWebscript = subComponent.getResolvedWebScript();
        if (scWebscript != null)
        {
            String storePath = scWebscript.getDescription().getStorePath();
            String descPath = scWebscript.getDescription().getDescPath();
            model.put("subComponent_resolvedWSStorePath", storePath);
            model.put("subComponent_resolvedWSDescPath", descPath);
            model.put("subComponent_resolvedWSBasePath", context.getContextPath() + "/page/script/" + scWebscript.toString());
        }
        
        // The Component should also have been stored, retrieve it and populate the model with information
        // about it...
        Object o = context.getValue(WebFrameworkConstants.RENDER_DATA_COMPONENT);
        if (o instanceof Component)
        {
            Component component = (Component) o;
            String path = component.getKey().getSourcePath();
            if (path == null)
            {
                model.put("storagePath", component.getKey().getStoragePath());
            }
            else
            {
                model.put("storagePath", path);
            }  
            
            model.put("componentDebug", component.getModelProperties());
            model.put("componentCustomPropsDebug", component.getCustomProperties());
            
            WebScript webscript = component.getResolvedWebScript();
            if (webscript != null)
            {
                String storePath = webscript.getDescription().getStorePath();
                String descPath = webscript.getDescription().getDescPath();
                model.put("resolvedWSStorePath", storePath);
                model.put("resolvedWSDescPath", descPath);
                model.put("resolvedWSBasePath", context.getContextPath() + "/page/script/" + webscript.toString());
            }
        }
    }
    
    /**
     * <p>This method updates the model to include the properties for the current page. If 
     * a page has not been set in the context then the template set in the context is used.
     * </p>
     * <p>As well as setting some default page information (such as id, description, etc) any
     * configured custom properties are also added. There are defined in the page XML configuration 
     * file using the <code><{@code}properties></code> element where each child element is the 
     * property key and the value is the property value, e.g:</p>
     * 
     * <p>
     * <code>
     * <{@code}properties><br>
     * &nbsp&nbsp<{@code}firstPropertyKey>value1<{@code}/firstPropertyKey><br>
     * &nbsp&nbsp<{@code}secondPropertyKey>value2<{@code}/secondPropertyKey><br>
     * <{@code}/properties>
     * </code>
     * </p>
     * @param context
     * @param model
     */
    @SuppressWarnings("unchecked")
    private void setPageProps(RequestContext context, Map<String, Object> model)
    {
        ModelObject pageObject = context.getPage();
        Map<String, Serializable> pageProps;
        if (pageObject != null)
        {
            pageProps = pageObject.getCustomProperties();
        }
        else
        {
            pageObject = context.getTemplate();
            pageProps = Collections.EMPTY_MAP;
        }
        
        if (pageObject != null)
        {
            Map<String, Object> pageModel = new HashMap<String, Object>(16, 1.0f);
            URLHelper urlHelper = (URLHelper) model.get(MODEL_URL);
            if (urlHelper != null)
            {
                pageModel.put(MODEL_URL, urlHelper);
            }
            
            pageModel.put(MODEL_ID, pageObject.getId());
            pageModel.put(MODEL_TITLE, pageObject.getTitle());
            pageModel.put(MODEL_TITLE_ID, pageObject.getTitleId());
            pageModel.put(MODEL_DESCRIPTION, pageObject.getDescription());
            pageModel.put(MODEL_DESCRIPTION_ID, pageObject.getDescriptionId());
            // Page objects have an authentication level that should be made available as metadata
            if (pageObject instanceof Page)
            {
                pageModel.put(MODEL_AUTHENTICATION, ((Page)pageObject).getAuthentication().toString());
            }
            
            // custom page properties - add to model
            // use ${page.properties["abc"]}
            Map<String, Serializable> customProps = new HashMap<String, Serializable>(pageProps.size()); 
            customProps.putAll(pageProps);
            pageModel.put(MODEL_PROPERTIES, customProps);
            model.put(MODEL_PAGE, pageModel);
        }
        else
        {
            // Neither a Page nor Template were set in the context. Therefore it was not possible to
            // set Page properties. No action required.
        }
    }
    
    /**
     * <p>This method updates the model to set any custom properties defined for the <code>TemplateInstance</code>.
     * These properties can then be accessed via <code>model.template.properties[<{@code}property name>]</code>
     * in the rendering file (e.g. the WebScript, FreeMarker or JSP component or template).</p> 
     * <p>Template properties are defined in the templates XML configuration file using the <code><{@code}properties></code>
     * element where each child element is the property key and the value is the property value, e.g:</p>
     * 
     * <p>
     * <code>
     * <{@code}properties><br>
     * &nbsp&nbsp<{@code}firstPropertyKey>value1<{@code}/firstPropertyKey><br>
     * &nbsp&nbsp<{@code}secondPropertyKey>value2<{@code}/secondPropertyKey><br>
     * <{@code}/properties>
     * </code>
     * </p>
     * @param context
     * @param model
     */
    private void setTemplateProps(RequestContext context, Map<String, Object> model)
    {
        TemplateInstance template = context.getTemplate();
        if (template != null)
        {
            Map<String, Object> templateModel = new HashMap<String, Object>(1, 1.0f);
            Map<String, Serializable> templateProps = template.getCustomProperties();
            if (templateProps.size() != 0)
            {
                Map<String, Serializable> customProps = new HashMap<String, Serializable>(templateProps.size());
                customProps.putAll(templateProps);
                templateModel.put(MODEL_PROPERTIES, customProps);
            }
            else
            {
                templateModel.put(MODEL_PROPERTIES, Collections.EMPTY_MAP);
            }            
            model.put(MODEL_TEMPLATE, templateModel);
        }
    }
    
    /**
     * Populate the model for script processor.
     * 
     * @param context   render context
     * @param model     to populate
     */
    public void populateScriptModel(RequestContext context, Map<String, Object> model, ModelObject object)
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        // common population
        populateModel(context, model, object);
        
        if (object instanceof TemplateInstance)
        {
            // Attempt to create a Script model object that contains additional configuration that is dynamically provided
            // by extension modules. If it cannot be created then defer to the default static configuration.
            ScriptConfigModel scriptConfigModel = context.getExtendedScriptConfigModel(null);
            if (scriptConfigModel == null)
            {
                scriptConfigModel = this.scriptConfigModelInstance;
            }
            
            // Add in the config service accessor
            model.put(MODEL_CONFIG, scriptConfigModel);
            model.put(MODEL_MESSAGE_METHOD, SCRIPT_MESSAGE_INSTANCE);
        }
    }
    
    /**
     * Populate the model for template processor.
     * 
     * @param context   render context
     * @param model     to populate
     */
    public void populateTemplateModel(RequestContext context, Map<String, Object> model, ModelObject object)
        throws RendererExecutionException, UnsupportedEncodingException
    {
        if (model == null)
        {
            throw new IllegalArgumentException("Model is mandatory.");
        }
        
        // common population
        populateModel(context, model, object);

        /**
         * We add in the "url" object if we're processing against a TemplateInstance
         * 
         * If we're processing against a Web Script, it will already be there
         */
        if (object instanceof TemplateInstance)
        {
            // provide the URL helper for the template
            URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
            if (urlHelper != null)
            {
                model.put(MODEL_URL, urlHelper);
            }

            // TODO: Update the head to include the debug info (if debug info is enabled)...
            StringBuilder head = new StringBuilder(webFrameworkRenderService.renderTemplateHeaderAsString(context, object));
            if (this.webFrameworkConfigElement.isSurfBugEnabled())
            {
                // If SurfBug is enabled then add in the CSS and JS libraries...
                head.append("<link rel=\"stylesheet\" type=\"text/css\" href=\"" + context.getContextPath() + "/res/css/surfbug.css" + "\"/>");
                head.append("<script type=\"text/javascript\" src=\"" + context.getContextPath() + "/res/js/surfbug.js" + "\"></script>");
            }
            
            // Append the dependencies generated from component WebScripts...
            head.append(context.getExtensionDependencies());
            
            // add in the ${head} tag
            model.put(MODEL_HEAD, head.toString());
            
            // Attempt to create a template model configuration object that includes configuration provided by extension 
            // modules. If this is not possible then defer to the default static configuration.
            TemplateConfigModel templateConfigModel = context.getExtendedTemplateConfigModel(null);
            if (templateConfigModel == null)
            {
                templateConfigModel = this.templateConfigModelInstance;
            }
            model.put(MODEL_CONFIG, templateConfigModel);
            
            // add in msg() method used for template I18N support - already provided by web script framework
            model.put(MODEL_MESSAGE_METHOD, FREEMARKER_MESSAGE_METHOD_INSTANCE);
            
            // add in resourceurl() method used for template resource support - already provided by web script framework
            model.put(MODEL_RESOURCE_URL_METHOD, FREEMARKER_RESOURCE_URL_METHOD_INSTANCE);
        }
        else
        {
            // provide the URL helper for the non-templates
            URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
            if (urlHelper != null)
            {
                model.put(MODEL_URL, urlHelper);
            }
        }
        
        // Components rendered in HEADER focus need to have access to URL
        if (object instanceof Component || object instanceof SubComponent)
        {
            if (model.get(MODEL_URL) == null)
            {
                // provide the URL helper for the template
                URLHelper urlHelper = (URLHelper)context.getValue(MODEL_URL);
                if (urlHelper != null)
                {
                    model.put(MODEL_URL, urlHelper);
                }
            }
            
            // add in the config service accessor
            TemplateConfigModel templateConfigModel = context.getExtendedTemplateConfigModel(null);
            if (templateConfigModel == null)
            {
                templateConfigModel = this.templateConfigModelInstance;
            }
            model.put(MODEL_CONFIG, templateConfigModel);
        }
        
        /**
         * TAGS
         */       
        
        // Add directives...
        model.put(COMPONENT_DIRECTIVE_NAME, new ComponentFreemarkerTagDirective(COMPONENT_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        model.put(COMPONENT_INCLUDE_DIRECTIVE_NAME, new ChromeIncludeFreeMarkerDirective(COMPONENT_INCLUDE_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        model.put(REGION_INCLUDE_DIRECTIVE_NAME, new ChromeIncludeFreeMarkerDirective(REGION_INCLUDE_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        model.put(SURFBUG_INCLUDE_DIRECTIVE_NAME, new SurfBugIncludeFreeMarkerDirective(SURFBUG_INCLUDE_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        
        // Extensibility directives...
        ExtensibilityModel extensibilityModel =  context.getCurrentExtensibilityModel();
        model.put(REGION_DIRECTIVE_NAME, new RegionFreemarkerTagDirective(REGION_DIRECTIVE_NAME, extensibilityModel, context, object, webFrameworkRenderService));
        model.put(MARKUP_DIRECTIVE_NAME, new MarkupDirective(MARKUP_DIRECTIVE_NAME, extensibilityModel));
        
        // add in <@resource/> directive
        model.put(RESOURCE_DIRECTIVE_NAME, new ResourceFreemarkerTagDirective(RESOURCE_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        
        // content specific
        model.put(ANCHOR_DIRECTIVE_NAME, new AnchorFreeMarkerDirective(ANCHOR_DIRECTIVE_NAME, webFrameworkRenderService));
        model.put(PAGE_LINK_DIRECTIVE_NAME, new LinkFreeMarkerDirective(PAGE_LINK_DIRECTIVE_NAME, webFrameworkRenderService));

        // Create the create component directive - this is a temporary hack to ensure that all required components exist...
        model.put(CreateComponentDirective.DIRECTIVE_NAME, this.directiveFactory.createCreateComponentDirective(CreateComponentDirective.DIRECTIVE_NAME));

        // When the useChecksumDependencies configuration value is true we need to create <@script> and <@link> directives that generate import
        // requests that include a content specific checksum. This checksum ensures that it is not possible for browsers to use cached stale
        // data following application updates (because if the resource has changed then a different checksum will be generated and the resource
        // requested will not exist in the browsers cache).
        //
        // When this value is not set we simply need to add the legacy <@link> directive that (there is no legacy <@script> directive)
        if (this.webFrameworkConfigElement.useChecksumDependencies() || this.webFrameworkConfigElement.isAggregateDependenciesEnabled())
        {
            // If the configuration indicates that either checksum or aggregate dependencies is enabled then
            // the following common directives are required...
            MessagesDependencyDirective messagesDependencyWebScript = this.directiveFactory.createMessagesDependencyDirective(MESSAGES_DIRECTIVE_NAME, object, extensibilityModel, context);
            messagesDependencyWebScript.setMessagesWebScript(this.directiveFactory.getMessagesWebScript());
            model.put(MESSAGES_DIRECTIVE_NAME, messagesDependencyWebScript);
            model.put(ADD_INLINE_JAVASCRIPT_DIRECTIVE_NAME, this.directiveFactory.createAddInlineJavaScriptDirective(ADD_INLINE_JAVASCRIPT_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(CREATE_WEBSCRIPT_WIDGETS_DIRECTIVE_NAME, this.directiveFactory.createCreateWebScriptsDirective(CREATE_WEBSCRIPT_WIDGETS_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(RELOCATE_JAVASCRIPT_DEPENDENCIES_DIRECTIVE_NAME, this.directiveFactory.createRelocateJavaScriptDirective(RELOCATE_JAVASCRIPT_DEPENDENCIES_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME, this.directiveFactory.createOutputJavaScriptDirective(OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME, this.directiveFactory.createOutputCssDirective(OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(SCRIPT_DIRECTIVE_NAME, this.directiveFactory.createJavaScriptDependencyDirective(SCRIPT_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(LINK_DIRECTIVE_NAME, this.directiveFactory.createCssDependencyDirective(LINK_DIRECTIVE_NAME, object, extensibilityModel, context));
            model.put(CHECKSUM_RESOURCE_DIRECTIVE_NAME, this.directiveFactory.createChecksumResourceDirective(CHECKSUM_RESOURCE_DIRECTIVE_NAME, object, extensibilityModel, context));
            
            // This directive is used to detect whether or not Surf Region Chrome is enabled and wraps the directives in a uniquely identified <div> element...
            model.put(CHROME_DETECTION_DIRECTIVE_NAME, this.directiveFactory.createChromeDetectionDirective(CHROME_DETECTION_DIRECTIVE_NAME, extensibilityModel, webFrameworkConfigElement, context));

            // This directive is used to allow WebScripts to be processed outside of the context of a page (i.e. during asynchronous requests)...
            model.put(STANDALONE_WEBSCRIPT_WRAPPER_DIRECTIVE_NAME, this.directiveFactory.createStandaloneWebScriptWrapperDirective(STANDALONE_WEBSCRIPT_WRAPPER_DIRECTIVE_NAME, object, extensibilityModel, context));
            
            // If Dojo support has been enabled then we'll create the createDojoPage directive...
            if (this.webFrameworkConfigElement.isDojoEnabled())
            {
                model.put(ProcessJsonModelDirective.DIRECTIVE_NAME, this.directiveFactory.createProcessJsonModelDirective(ProcessJsonModelDirective.DIRECTIVE_NAME, object, extensibilityModel, context, null));
                model.put(AutoComponentRegionDirective.DIRECTIVE_NAME, this.directiveFactory.createAutoComponentRegionDirective(AutoComponentRegionDirective.DIRECTIVE_NAME, context, this.webFrameworkRenderService));
            }
        }
        else
        {
            // This is the legacy behaviour for older applications running older versions of Surf...
            model.put(LINK_DIRECTIVE_NAME, new StyleSheetFreeMarkerDirective(LINK_DIRECTIVE_NAME, context, object, webFrameworkRenderService));
        }
    }
   
    /**
     * Build a FreeMarker {@link HttpSessionHashModel} for the given request,
     * detecting whether a session already exists and reacting accordingly.
     * @param request current HTTP request
     * @param response current servlet response
     * @return the FreeMarker HttpSessionHashModel
     */
    private static HttpSessionHashModel buildSessionModel(HttpServletRequest request, HttpServletResponse response) 
    {
        HttpSessionHashModel model = null;
        
        HttpSession session = request.getSession(false);
        if (session != null) 
        {
            model = new HttpSessionHashModel(session, ObjectWrapper.DEFAULT_WRAPPER);
        }
        else 
        {
            model = new HttpSessionHashModel(null, request, response, ObjectWrapper.DEFAULT_WRAPPER);
        }
        
        return model;
    }
    
    
    /**
     * Helper to resolve I18N messages in Template level JS controller scripts
     * 
     * @author Kevin Roast
     */
    public static class ScriptMessageResolver
    {
        /**
         * Get an I18N message
         * 
         * @param id    Message Id
         * 
         * @return resolved message
         */
        public String get(String id)
        {
            String result = null;
            
            if (id != null && id.length() != 0)
            {
                result = resolveMessage(id);
            }
            
            return (result != null ? result : "");
        }
        
        /**
         * Get an I18N message with the given message args
         * 
         * @param id    Message Id
         * @param args  Message args
         * 
         * @return resolved message
         */
        @SuppressWarnings("rawtypes")
        public String get(String id, Scriptable args)
        {
            String result = null;
            
            if (id != null && id.length() != 0)
            {
                Object params = ScriptValueConverter.unwrapValue(args);
                if (params instanceof List)
                {
                    result = resolveMessage(id, ((List)params).toArray());
                }
                else
                {
                    result = resolveMessage(id);
                }
            }
            
            return (result != null ? result : "");
        }
        
        private final String resolveMessage(final String id, final Object... args)
        {
            String result = I18NUtil.getMessage(id);
            if (args.length == 0)
            {
                // for no args, just return found msg or the id on failure
                if (result == null)
                {
                    result = id;
                }
            }
            else
            {
                // for supplied msg args, format msg or return id on failure
                if (result != null)
                {
                    result = MessageFormat.format(result, args);
                }
                else
                {
                    result = id;
                }
            }
            
            return result;
        }
    }
}

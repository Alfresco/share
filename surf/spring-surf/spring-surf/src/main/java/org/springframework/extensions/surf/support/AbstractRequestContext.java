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

package org.springframework.extensions.surf.support;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigImpl;
import org.springframework.extensions.config.ConfigSection;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.config.evaluator.Evaluator;
import org.springframework.extensions.config.xml.XMLConfigService;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;
import org.springframework.extensions.directives.CssDependencyDirective;
import org.springframework.extensions.directives.DirectiveConstants;
import org.springframework.extensions.directives.JavaScriptDependencyDirective;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.FrameworkBean;
import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.LinkBuilder;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.ExtensibilityModuleHandler;
import org.springframework.extensions.surf.extensibility.HandlesExtensibility;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityModelImpl;
import org.springframework.extensions.surf.render.RenderMode;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.site.SiteUtil;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.SubComponent.RenderData;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.surf.util.FakeHttpServletResponse;
import org.springframework.extensions.webscripts.ExtendedScriptConfigModel;
import org.springframework.extensions.webscripts.ExtendedTemplateConfigModel;
import org.springframework.extensions.webscripts.ScriptConfigModel;
import org.springframework.extensions.webscripts.TemplateConfigModel;
import org.springframework.extensions.webscripts.WebScriptPropertyResourceBundle;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.web.context.request.WebRequest;

/**
 * <p>Abstract base class for RequestContext implementations.  This
 * is provided as a convenience to developers who wish to build their
 * own custom RequestContext variations.
 * </p><p>
 * Implementations that inherit from this class will benefit from
 * having automatic thread local bindings for their custom request
 * context types.
 * </p>
 * @see ThreadLocalRequestContext
 *
 * @author muzquiano
 * @author David Draper
 */
public abstract class AbstractRequestContext extends ThreadLocalRequestContext implements HandlesExtensibility
{
    private static final Log logger = LogFactory.getLog(AbstractRequestContext.class);
    
    private static final long serialVersionUID = -3714605386235121796L;

    public static final String VALUE_HEAD_TAGS = "headTags";

    /*
     * Increments every time a request ID is required (debug)
     */
    protected static AtomicLong counter = new AtomicLong();

    protected Map<String, Serializable> valuesMap;
    protected Map<String, String> parametersMap;
    protected Map<String, Serializable> attributesMap;
    protected Map<String, String> headersMap;

    protected Map<String, String> uriTokens = Collections.emptyMap();

    protected Page rootPage;
    protected Configuration siteConfiguration;

    protected Page currentPage;
    protected TemplateInstance currentTemplate;
    protected Resource currentResource;
    protected String currentFormatId;
    protected String storeId;
    protected User user;
    protected String id;
    protected String uri;
    protected String viewName;
    protected Theme theme;

    protected Map<String, Component> components = null;

    protected Map<String, Object> model;

    protected String method;
    protected String scheme;
    protected String contextPath;

    /**
     * <p>The <code>FrameworkBean</code> is a replacement for the deprecated <code>FrameworkUtil</code> class. Instead
     * of providing static helper methods it provides instance methods which are accessible when it is is properly
     * used as a Spring bean.</p>
     * <p>The <code>AbstractRequestContext</code> class has been provided with a new constructor that will set this
     * <code>FrameworkBean</code> so that it can be used properly</p>. However, in order to retain backwards compatibility
     * the old constructor has been deprecated - if this is use then this variable will not be set so care needs to be
     * taken to avoid <code>NullPointerExceptions</code>.
     */
    private FrameworkBean frameworkUtil;

    /**
     * Constructs a new Request Context.  In general, you should not
     * have to construct these by hand.  They are constructed by
     * the framework via a RequestContextFactory.
     */
    protected AbstractRequestContext(WebFrameworkServiceRegistry serviceRegistry, FrameworkBean frameworkUtil)
    {
        super(serviceRegistry);

        this.frameworkUtil = frameworkUtil;

        // request maps
        this.valuesMap = new HashMap<String, Serializable>(4, 1.0f);
        this.parametersMap = new HashMap<String, String>(4, 1.0f);
        this.attributesMap = new HashMap<String, Serializable>(4, 1.0f);
        this.headersMap = new CaseInsensitiveHeadersMap<String, String>(16, 1.0f);

        // components map
        this.components = new LinkedHashMap<String, Component>(16, 1.0f);

        // initialize the view model
        this.model = new HashMap<String, Object>(16);
    }

    /**
     * <p>Constructs a new Request Context.  In general, you should not have to construct these by hand.  They are constructed by
     * the framework via a RequestContextFactory.</p>
     * <p>This method has been deprecated as it depends upon the deprecated static helper methods of <code>FrameworkUtil</code>
     * which have been replaced by non-static helper methods accessed by using <code>FrameworkUtil</code> as a Spring bean</p>
     *
     * @param serviceRegistry
     * @deprecated
     */
    protected AbstractRequestContext(WebFrameworkServiceRegistry serviceRegistry)
    {
        this(serviceRegistry, null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getId()
     */
    @SuppressWarnings("static-access")
    public String getId()
    {
        if (this.id == null)
        {
            this.id = Long.toString(this.counter.incrementAndGet());
        }
        return this.id;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getSiteConfiguration()
     */
    public Configuration getSiteConfiguration()
    {
        if (this.siteConfiguration == null)
        {
            this.siteConfiguration = SiteUtil.getSiteConfiguration(this);
        }

        return this.siteConfiguration;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getWebsiteTitle()
     */
    public String getWebsiteTitle()
    {
        String title = "Web Application";

        if (getSiteConfiguration() != null)
        {
            title = getSiteConfiguration().getTitle();
        }

        return title;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getPageTitle()
     */
    public String getPageTitle()
    {
        String title = "Default Page";

        if (getPage() != null)
        {
            title = getPage().getTitle();
        }

        return title;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getUri()
     */
    public String getUri()
    {
        return this.uri;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setUri(java.lang.String)
     */
    public void setUri(String uri)
    {
        this.uri = uri;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getViewName()
     */
    public String getViewName()
    {
        return this.viewName;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setViewName(java.lang.String)
     */
    public void setViewName(String viewName)
    {
        this.viewName = viewName;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getPage()
     */
    public Page getPage()
    {
        return this.currentPage;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setPage(org.springframework.extensions.surf.types.Page)
     */
    public void setPage(Page page)
    {
        this.currentPage = page;
        // clear cached variable
        this.currentTemplate = null;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getPageId()
     */
    public String getPageId()
    {
        if (getPage() != null)
        {
            return getPage().getId();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getLinkBuilder()
     */
    public abstract LinkBuilder getLinkBuilder();

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getRootPage()
     */
    public Page getRootPage()
    {
        if(this.rootPage == null)
        {
            this.rootPage = SiteUtil.getRootPage(this);
        }

        return this.rootPage;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getTemplate()
     */
    public TemplateInstance getTemplate()
    {
        if (this.currentTemplate == null)
        {
            if (getPage() != null)
            {
                this.currentTemplate = getPage().getTemplate(this);
            }
        }
        return this.currentTemplate;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setTemplate(org.springframework.extensions.surf.types.TemplateInstance)
     */
    public void setTemplate(TemplateInstance currentTemplate)
    {
        this.currentTemplate = currentTemplate;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getTemplateId()
     */
    public String getTemplateId()
    {
        if (getTemplate() != null)
        {
            return getTemplate().getId();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getCurrentObjectId()
     */
    public String getCurrentObjectId()
    {
        String id = null;
        if (getCurrentObject() != null)
        {
            id = getCurrentObject().getObjectId();
        }
        return id;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setCurrentObject(org.springframework.extensions.surf.resource.Resource)
     */
    public void setCurrentObject(Resource resource)
    {
        this.currentResource = resource;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getCurrentObject()
     */
    public Resource getCurrentObject()
    {
        return this.currentResource;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getFormatId()
     */
    public String getFormatId()
    {
        return this.currentFormatId;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setFormatId(java.lang.String)
     */
    public void setFormatId(String formatId)
    {
        this.currentFormatId = formatId;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setUser(org.springframework.extensions.webscripts.connector.User)
     */
    public void setUser(User user)
    {
        this.user = user;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getUser()
     */
    public User getUser()
    {
        return user;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getUserId()
     */
    public String getUserId()
    {
    	return (getUser() != null ? getUser().getId() : null);
    }

    /**
     * <p>Returns a <code>CredentialVault</code>. It the subclass has been instantiated using the deprecated
     * constructor then the <code>CredentialVault</code> will be retrieved using the deprecated static helper
     * method from <code>FrameworkUtil</code>.</p>
     * <p>If the subclass was instantiated using the preferred constructor that accepts a <code>FrameworkBean</code>
     * (adapted from the <code>FrameworkUtil</code> class) then the <code>FrameworkBean</code> will be used
     * instead.</p>
     * <p>Both methods effectively do the same thing, but the <code>FrameworkBean</code> makes use of properly
     * configured Spring beans so if more flexible and extensible.</p>
     *
     * @return A <code>CredentialVault</code>
     */
    public CredentialVault getCredentialVault()
    {
        CredentialVault credentialVault;
        if (frameworkUtil == null)
        {
            credentialVault = FrameworkUtil.getCredentialVault(this, this.getUserId());
        }
        else
        {
            credentialVault = frameworkUtil.getCredentialVault(this, this.getUserId());
        }
        return credentialVault;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setValue(java.lang.String, java.io.Serializable)
     */
    public void setValue(String key, Serializable value)
    {
        this.valuesMap.put(key, value);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getValue(java.lang.String)
     */
    public Serializable getValue(String key)
    {
        return this.valuesMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#removeValue(java.lang.String)
     */
    public void removeValue(String key)
    {
        this.valuesMap.remove(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#hasValue(java.lang.String)
     */
    public boolean hasValue(String key)
    {
        return (this.valuesMap.get(key) != null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getValuesMap()
     */
    public Map<String, Serializable> getValuesMap()
    {
        return this.valuesMap;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getParameter(java.lang.String)
     */
    public String getParameter(String key)
    {
        return this.parametersMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#hasParameter(java.lang.String)
     */
    public boolean hasParameter(String key)
    {
        return (this.parametersMap.get(key) != null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getParameters()
     */
    public Map<String, String> getParameters()
    {
        return this.parametersMap;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getRenderingComponents()
     */
    public Component[] getRenderingComponents()
    {
        if (this.components.size() == 0)
        {
            return null;
        }
        else
        {
            return this.components.values().toArray(new Component[this.components.size()]);
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setRenderingComponent(org.springframework.extensions.surf.types.Component)
     */
    public void setRenderingComponent(Component component)
    {
        this.components.put(component.getId(), component);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getTheme()
     */
    public Theme getTheme()
    {
        return this.theme;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setTheme(org.springframework.extensions.surf.types.Theme)
     */
    public void setTheme(Theme theme)
    {
        this.theme = theme;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getThemeId()
     */
    public String getThemeId()
    {
        String themeId = null;

        if (getTheme() != null)
        {
            themeId = getTheme().getId();
        }

        return themeId;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getRequestMethod()
     */
    public String getRequestMethod()
    {
        return this.method;
    }

    public String getRequestScheme()
    {
        return this.scheme;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.support.BaseFactoryBean#getObjectService()
     */
    public ModelObjectService getObjectService()
    {
        return getServiceRegistry().getModelObjectService();
    }

    @Override
    public String toString()
    {
        return "RequestContext-" + getId();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setModel(java.util.Map)
     */
    public void setModel(Map<String, Object> model)
    {
        this.model = model;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getModel()
     */
    public Map<String, Object> getModel()
    {
        return this.model;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getContextPath()
     */
    public String getContextPath()
    {
        return this.contextPath;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getAttribute(java.lang.String)
     */
    public Serializable getAttribute(String key)
    {
        return this.attributesMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#hasAttribute(java.lang.String)
     */
    public boolean hasAttribute(String key)
    {
        return (this.attributesMap.get(key) != null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getAttributes()
     */
    public Map<String, Serializable> getAttributes()
    {
        return this.attributesMap;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getHeader(java.lang.String)
     */
    public String getHeader(String key)
    {
        return (String) this.headersMap.get(key);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#hasHeader(java.lang.String)
     */
    public boolean hasHeader(String key)
    {
        return (this.headersMap.get(key) != null);
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getHeaders()
     */
    public Map<String, String> getHeaders()
    {
        return this.headersMap;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#setUriTokens(java.util.Map)
     */
    public void setUriTokens(Map<String,String> _uriTokens)
    {
        this.uriTokens = _uriTokens != null ? _uriTokens : Collections.<String,String>emptyMap();
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.RequestContext#getUriTokens()
     */
    public Map<String, String> getUriTokens()
    {
        return this.uriTokens;
    }

    // TODO: this method needs to be revisited in light of portlet support
    /* (non-Javadoc)
     * @see org.alfresco.web.site.RequestContext#getRequestContent()
     */
    public synchronized Content getRequestContent()
    {
        /*
        // ensure we only try to read the content once - as this method may be called several times
        // but the underlying inputstream itself can only be processed a single time
        if (content == null)
        {
            try
            {
                content = new InputStreamContent(getRequest().getInputStream(), getRequest().getContentType(), getRequest().getCharacterEncoding());
            }
            catch (IOException e)
            {
                throw new WebScriptException("Failed to retrieve request content", e);
            }
        }
        return content;
        */
        return null;
    }


    // NOTE: Copied in from AbstractRenderContext

    private ModelObject object;
    private WebRequest request;
    private HttpServletResponse response;
    private RenderMode mode = RenderMode.VIEW;
    private String renderId = null;
    private boolean passiveMode = false;

//    /**
//     * Increments every time a request id is required
//     */
//    protected static int idCounter = 0;
//
//
//    /**
//     * Constructor
//     *
//     * @param provider
//     * @param requestContext
//     */
//    public AbstractRenderContext(RequestContext requestContext)
//    {
//        super(requestContext);
//    }

    // methods from RenderContext

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#getRenderMode()
     */
    public final RenderMode getRenderMode()
    {
        return this.mode;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#setRenderMode(org.springframework.extensions.surf.render.RenderMode)
     */
    public final void setRenderMode(RenderMode mode)
    {
        this.mode = mode;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.WrappedRequestContext#getRequest()
     */
    public final WebRequest getRequest()
    {
        return this.request;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#setRequest(org.springframework.web.context.request.WebRequest)
     */
    public final void setRequest(WebRequest request)
    {
        this.request = request;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#getResponse()
     */
    public final HttpServletResponse getResponse()
    {
        HttpServletResponse responseToReturn = null;
        if (this.passiveMode)
        {
            responseToReturn = this.fakeResponse;
        }
        else 
        {
            responseToReturn = this.response;
        }
        return responseToReturn;
    }

    public String getContentAsString() throws UnsupportedEncodingException
    {
        return this.fakeResponse.getContentAsString();
    }

    private FakeHttpServletResponse fakeResponse;

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#setResponse(javax.servlet.http.HttpServletResponse)
     */
    public final void setResponse(HttpServletResponse response)
    {
        this.response = response;
        this.fakeResponse = new FakeHttpServletResponse(response);

    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#getObject()
     */
    public ModelObject getObject()
    {
        return this.object;
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.render.RenderContext#setObject(org.springframework.extensions.surf.ModelObject)
     */
    public void setObject(ModelObject object)
    {
        this.object = object;
    }

    /**
     * <p>This switches the <code>RequestContext</code> into or out of "passive mode" (depending upon
     * the value of the supplied <code>passiveMode</code> argument). In "passive mode" any output generated
     * will be written to a <code>FakeHttpServletResponse</code> rather than the genuine <code>HttpServletResponse</code>.
     * This is useful so that we can generate data that we want to "save for later" (i.e. we generate all the head
     * ouput needed without writing it to the response stream and store it in the model to be accessed at the
     * appropriate time).</p>
     */
    public final void setPassiveMode(boolean passiveMode)
    {
        this.passiveMode = passiveMode;
        if (passiveMode)
        {
            fakeResponse = new FakeHttpServletResponse(this.response);
        }
    }

    /**
     * <p>Indicates whether or not the <code>RequestContext</code> is currently in "passive mode". In "passive mode" any
     * output is written to a <code>FakeHttpServletResponse</code> rather than the genuine <code>HttpServletResponse</code>.
     * Output written to the <code>FakeHttpServletResponse</code> can be retrieved by calling the <code>getContentAsString()</code>
     * method.<p>
     */
    public final boolean isPassiveMode()
    {
        return this.passiveMode;
    }

    /**
     * <p>The context path of the servlet being used to process the request.</p>
     */
    private String servletContextPath = null;
    
    /**
     * <p>Returns the context path of the servlet being used to process the request. This path does NOT
     * include the application context, just the servlet context. This may return null if not set, although
     * this should be set by the {@link ServletRequestContextFactory} there is no guarantee that it will have been
     * set depending upon the Spring application context.</p>
     * 
     * @return The context path of the servlet.
     */
    public String getServletContextPath()
    {
        return this.servletContextPath;
    }

    /**
     * <p>This method should be used to set the context path of the servlet being used to process the request.
     * It has been provided for use by the {@link ServletRequestContextFactory} but could be used by other classes, however
     * care should be taken to ensure that it is called with the correct value.</p>
     */
    public void setServletContextPath(String path)
    {
        this.servletContextPath = path;
    }
    
    // NOTE: This is just temporary for test purposes - something better needs to be done with this...

    private ExtensibilityModel extensibilityModel;
    
    public ExtensibilityModel openExtensibilityModel()
    {
        this.extensibilityModel = new ExtensibilityModelImpl(this.extensibilityModel, this);
        return this.extensibilityModel;
    }

    public void closeExtensibilityModel(ExtensibilityModel model, Writer out)
    {
        model.flushModel(out);
        this.extensibilityModel = model.getParentModel();
        
        // After closing a model, update the parent model with the details of the extensibility directives
        // that have just been rendered. This is required because it's the Chrome of a Sub-Component that
        // renders the SurfBug debug data, but the Web Script with the extensibility directives is in 
        // a child model. This one should be filed under "hack" :-)
        if (this.extensibilityModel != null)
        {
            this.extensibilityModel.setChildDebugData(model.getDebugData());
        }
    }

    public ExtensibilityModel getCurrentExtensibilityModel()
    {
        return this.extensibilityModel;
    }
    
    private Map<String, Serializable> evaluatedProperties = new HashMap<String, Serializable>();
    
    public void setEvaluatedProperties(Map<String, Serializable> properties)
    {
        this.evaluatedProperties.clear();
        this.evaluatedProperties.putAll(properties);
    }
    
    public Map<String, Serializable> getEvaluatedProperties()
    {
        return this.evaluatedProperties;
    }
    
    private DependencyHandler dependencyHandler = null;
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    
    /**
     * <p>A {@link ExtensibilityModuleHandler} is required to evaluate which {@link ExtensionModule}
     * instances are applicable for a request. It should be set by the {@link AbstractRequestContextFactory}
     * that creates the RequestContext. Both the {@link ExtensibilityModuleHandler} and the
     * {@link AbstractRequestContextFactory} should be a Spring beans and the application context should
     * be configured for the former to be referenced by the latter.</p>
     */
    private ExtensibilityModuleHandler extensibilityModuleHandler = null;

    /**
     * <p>This method is provided for {@link AbstractRequestContextFactory} instances to set the 
     * {@link ExtensibilityModuleHandler} that is required to evaluate which {@link ExtensionModule}
     * instances are applicable for a request.</p>
     * 
     * @param extensibilityModuleHandler A {@link ExtensibilityModuleHandler} to be used by this request context.
     */
    public void setExtensibilityModuleHandler(ExtensibilityModuleHandler extensibilityModuleHandler)
    {
        this.extensibilityModuleHandler = extensibilityModuleHandler;
    }
    
    /**
     * <p>The WebFramework configuration. This should be set through the Spring application context configuration.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * <p>This method is provided for {@link AbstractRequestContextFactory} instances to set the 
     * {@link WebFrameworkConfigElement} for the application.</p>
     * 
     * @param extensibilityModuleHandler A {@link ExtensibilityModuleHandler} to be used by this request context.
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }
    
    private ConfigService configService;
    
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    
    /**
     * <p>The list of {@link ExtensionModule} instances that have been evaluated as applicable to
     * this RequestContext. This is set to <code>null</code> when during instantiation and is only
     * properly set the first time the <code>getEvaluatedModules</code> method is invoked. This ensures
     * that module evaluation only occurs once per request.</p>
     */
    private List<ExtensionModule> evaluatedModules = null;
    
    /**
     * <p>Retrieve the list of {@link ExtensionModule} instances that have been evaluated as applicable
     * for the current request. If this list has not yet been populated then use the {@link ExtensibilityModuleHandler}
     * configured in the Spring application context to evaluate them.</p>
     * 
     * @return A list of {@link ExtensionModule} instances that are applicable to the current request.
     */
    public List<ExtensionModule> getEvaluatedModules()
    {
        if (!this.webFrameworkConfigElement.isGuestPageExtensionModulesEnabled() && this.user.isGuest())
        {
            this.evaluatedModules = new ArrayList<ExtensionModule>();
        }
        else if (this.evaluatedModules == null)
        {
            if (this.extensibilityModuleHandler == null)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("No 'extensibilityModuleHandler' has been configured for this request context. Extensions cannot be processed");
                }
                this.evaluatedModules = new ArrayList<ExtensionModule>();
            }
            else
            {
                this.evaluatedModules = this.extensibilityModuleHandler.evaluateModules(this);
            }
        }
        return this.evaluatedModules;
    }
    
    public void addJSDependency(String dependency)
    {
        if (!this.requestedDependencies.contains(dependency))
        {
            this.requestedDependencies.add(dependency);
            this.extensionModuleJsDependencies.add(dependency);
        }
    }
    
    public void addCssDependency(String dependency, String media)
    {
        if (!this.requestedDependencies.contains(dependency))
        {
            this.requestedDependencies.add(dependency);
            LinkedHashSet<String> mediaSpecificDependencies = this.extensionModuleCssDependencies.get(media);
            if (mediaSpecificDependencies == null)
            {
                mediaSpecificDependencies = new LinkedHashSet<String>();
                this.extensionModuleCssDependencies.put(media, mediaSpecificDependencies);
            }
            mediaSpecificDependencies.add(dependency);
        }
    }
    
    /**
     * <p>Returns the dependencies requested by extension modules as a formatted String. The dependency requests are processed
     * by the associated {@link DependencyHandler} to take advantage of caching and optional checksum identification and
     * CSS data image generation.</p>
     * @return A String of dependency requests.
     */
    public String getExtensionDependencies()
    {
        StringBuilder dependencies = new StringBuilder();
        if (this.extensionModuleJsDependencies.isEmpty() && this.extensionModuleCssDependencies.isEmpty())
        {
            // If there no JS or CSS dependencies then there is no need to add any output. This is mostly
            // to help with the automated FV test to ensure that no comments are generated.
        }
        else
        {
            dependencies.append(DirectiveConstants.DEPENDENCIES_COMMENT);
            for (String jsDep: this.extensionModuleJsDependencies)
            {
                String checksumPath = null;
                if (this.webFrameworkConfigElement.useChecksumDependencies())
                {
                    if (jsDep.startsWith(this.dependencyHandler.getResourceControllerMapping() + "/"))
                    {
                        jsDep = jsDep.substring(this.dependencyHandler.getResourceControllerMapping().length() + 1);
                    }
                    checksumPath =  this.dependencyHandler.getChecksumPath(jsDep);
                    if (checksumPath != null)
                    {
                        dependencies.append(DirectiveConstants.OPEN_DEP_SCRIPT_TAG);
                        dependencies.append(this.getContextPath());
                        dependencies.append(this.dependencyHandler.getResourceControllerMapping());
                        dependencies.append("/");
                        dependencies.append(checksumPath);
                        dependencies.append(DirectiveConstants.CLOSE_DEP_SCRIPT_TAG);
                        dependencies.append(DirectiveConstants.NEW_LINE);
                    }
                    else
                    {
                        // Couldn't find dependency...
                        dependencies.append(DirectiveConstants.COULD_NOT_FIND_JS_COMMENT_OPEN);
                        dependencies.append(jsDep);
                        dependencies.append(DirectiveConstants.COULD_NOT_FIND_JS_COMMENT_CLOSE);
                    }
                }
                else
                {
                    // When checksum dependencies is not enabled just output the request as it was made...
                    dependencies.append(DirectiveConstants.OPEN_DEP_SCRIPT_TAG);
                    if (jsDep.startsWith("/"))
                    {
                        dependencies.append(this.getContextPath());
                    }
                    dependencies.append(jsDep);
                    dependencies.append(DirectiveConstants.CLOSE_DEP_SCRIPT_TAG);
                    dependencies.append(DirectiveConstants.NEW_LINE);
                }
            }
            
            if (!this.extensionModuleCssDependencies.isEmpty())
            {
                for (Entry<String, LinkedHashSet<String>> entry: this.extensionModuleCssDependencies.entrySet())
                {
                    // IE can only handle 31 import statements per style element so we 
                    // need to make sure that we don't create to many. We'll keep track
                    // of the number of imports added and create a new style tag once
                    // we get to 31...
                    int count = 0;
                    dependencies.append(DirectiveConstants.OPEN_CSS_1);
                    dependencies.append(entry.getKey());
                    dependencies.append(DirectiveConstants.OPEN_CSS_2);
                    for (String cssDep: entry.getValue())
                    {
                        String checksumPath = null;
                        if (this.webFrameworkConfigElement.useChecksumDependencies())
                        {
                            if (cssDep.startsWith(this.dependencyHandler.getResourceControllerMapping() + "/"))
                            {
                                cssDep = cssDep.substring(this.dependencyHandler.getResourceControllerMapping().length() + 1);
                            }
                            checksumPath = this.dependencyHandler.getChecksumPath(cssDep);
                            if (checksumPath != null)
                            {
                                count++;
                                dependencies.append(DirectiveConstants.CSS_IMPORT);
                                dependencies.append(this.getContextPath());
                                dependencies.append(this.dependencyHandler.getResourceControllerMapping());
                                dependencies.append("/");
                                dependencies.append(checksumPath);
                                dependencies.append(DirectiveConstants.DELIMIT_CSS_IMPORT);
                                if (count == 31)
                                {
                                    // We've hit 31 imports... close the current style element and start a new one to
                                    // avoid IE failing to load the files.
                                    dependencies.append(DirectiveConstants.CLOSE_CSS);
                                    dependencies.append(DirectiveConstants.OPEN_CSS_1);
                                    dependencies.append(entry.getKey());
                                    dependencies.append(DirectiveConstants.OPEN_CSS_2);
                                    count = 0;
                                }
                            }
                            else
                            {
                                dependencies.append(DirectiveConstants.COULD_NOT_FIND_CSS_COMMENT_OPEN);
                                dependencies.append(cssDep);
                                dependencies.append(DirectiveConstants.COULD_NOT_FIND_CSS_COMMENT_CLOSE);
                            }
                        }
                        else
                        {
                            dependencies.append(DirectiveConstants.CSS_IMPORT);
                            if (cssDep.startsWith("/"))
                            {
                                dependencies.append(this.getContextPath());
                            }
                            dependencies.append(cssDep);
                            dependencies.append(DirectiveConstants.DELIMIT_CSS_IMPORT);
                        }
                    }
                    dependencies.append(DirectiveConstants.CLOSE_CSS);
                }
            }
        }
        return dependencies.toString();
    }
    
    /**
     * <p>The JavaScript dependencies requested by extension modules for the current request.</p>
     */
    private LinkedHashSet<String> extensionModuleJsDependencies = new LinkedHashSet<String>();
    
    /**
     * <p>The CSS dependencies requested by extension modules for the current request.</p>
     */
    private Map<String, LinkedHashSet<String>> extensionModuleCssDependencies = new HashMap<String, LinkedHashSet<String>>();
    
    /**
     * <p>Updates the dependencies requested by extension modules for the current request.<p>
     * 
     * @param path The current path being processed.
     * @param model The current model.
     */
    public void updateExtendingModuleDependencies(String path, Map<String, Object> model)
    {
        for (ExtensionModule module: this.getEvaluatedModules())
        {
            for (String dep: this.extensibilityModuleHandler.getModuleJsDeps(module, path))
            {
                if (!this.requestedDependencies.contains(dep))
                {
                    extensionModuleJsDependencies.add(dep);
                    this.requestedDependencies.add(dep);
                }
            }
        }
        for (ExtensionModule module: this.getEvaluatedModules())
        {
            for (Entry<String, LinkedHashSet<String>> entry: this.extensibilityModuleHandler.getModuleCssDeps(module, path).entrySet())
            {
                for (String dep: entry.getValue())
                {
                    if (!this.requestedDependencies.contains(dep))
                    {
                        LinkedHashSet<String> mediaSpecificDependencies = this.extensionModuleCssDependencies.get(entry.getKey());
                        if (mediaSpecificDependencies == null)
                        {
                            mediaSpecificDependencies = new LinkedHashSet<String>();
                            this.extensionModuleCssDependencies.put(entry.getKey(), mediaSpecificDependencies);
                        }
                        mediaSpecificDependencies.add(dep);
                        this.requestedDependencies.add(dep);
                    }
                }
            }
        }
    }
    
    /**
     * <p>Retrieves a list of files that should be provided by the evaluated modules.</p>
     */
    public List<String> getExtendingModuleFiles(String pathBeingProcessed)
    {
        List<String> extendingModuleFiles = new ArrayList<String>();
        for (ExtensionModule module: this.getEvaluatedModules())
        {
            extendingModuleFiles.addAll(this.extensibilityModuleHandler.getExtendingModuleFiles(module, pathBeingProcessed));
        }
        return extendingModuleFiles;
    }
    
    /**
     * <p>This {@link Set} is used to keep track of all the dependency resources that have been requested. It
     * is used to check that dependencies are not requested more than once.</p>
     */
    private Set<String> requestedDependencies = new HashSet<String>();
    
    /**
     * <p>Checks whether or not the supplied dependency has already been requested.</p>
     * 
     * @param dep The path to the dependency to check.
     * @return <code>true</code> if the dependency has already been requested and <code>false</code> otherwise.
     */
    public boolean dependencyAlreadyRequested(String dep)
    {
        return this.requestedDependencies.contains(dep);
    }
        
    /**
     * <p>This method can be used to indicate that the supplied dependency has been requested by other means.
     * This is provided to ensure that dependencies requested directly on the output stream (e.g. via the 
     * {@link JavaScriptDependencyDirective}, {@link CssDependencyDirective}, etc.)</p>
     * 
     * @param dep The path to the dependency to mark as requested.
     */
    public void markDependencyAsRequested(String dep)
    {
        this.requestedDependencies.add(dep);
    }
    
    private String fileBeingProcessed = null;
    
    public String getFileBeingProcessed()
    {
        return this.fileBeingProcessed;
    }

    public void setFileBeingProcessed(String file)
    {
        this.fileBeingProcessed = file;
    }
    
    
    private Map<String, RenderData> subComponentDebugData = new HashMap<String, RenderData>();
    
    public RenderData getSubComponentDebugData(String id)
    {
        RenderData data = this.subComponentDebugData.get(id);
        return data;
    }
    
    public void addSubComponentDebugData(String id, RenderData data)
    {
        this.subComponentDebugData.put(id, data);
    }
    
    /**
     * <p>A cache of extended {@link ResourceBundle} instances for the current request.</p>
     */
    private Map<String, WebScriptPropertyResourceBundle> extendedBundleCache = new HashMap<String, WebScriptPropertyResourceBundle>(); 
    
    /**
     * <p>Checks the cache to see if it has cached an extended bundle (that is a basic {@link ResourceBundle} that
     * has had extension modules applied to it. Extended bundles can only be safely cached once per request as the modules
     * applied can vary for each request.</p>
     * 
     * @param webScriptId The id of the WebScript to retrieve the extended bundle for.
     * @return A cached bundle or <code>null</code> if the bundle has not previously been cached.
     */
    public ResourceBundle getCachedExtendedBundle(String webScriptId)
    {
        return this.extendedBundleCache.get(webScriptId);
    }
    
    /**
     * <p>Adds a new extended bundle to the cache. An extended bundle is a WebScript {@link ResourceBundle} that has had 
     * {@link ResourceBundle} instances merged into it from extension modules that have been applied. These can only be cached 
     * for the lifetime of the request as different modules may be applied to the same WebScript for different requests.</p>
     * 
     * @param webScriptId The id of the WebScript to cache the extended bundle against.
     * @param extensionBUndle The extended bundle to cache.
     */
    public void addExtensionBundleToCache(String webScriptId, WebScriptPropertyResourceBundle extensionBundle)
    {
        this.extendedBundleCache.put(webScriptId, extensionBundle);
    }
    
    /**
     * <p>Creates a new {@link ExtendedScriptConfigModel} instance using the local configuration generated for this request.
     * If configuration for the request will be generated if it does not yet exist. It is likely that this method will be
     * called multiple times within the context of a single request and although the configuration containers will always
     * be the same a new {@link ExtendedScriptConfigModel} instance will always be created as the the supplied <code>xmlConfig</code>
     * string could be different for each call (because each WebScript invoked in the request will supply different
     * configuration.</p>
     */
    public ScriptConfigModel getExtendedScriptConfigModel(String xmlConfig)
    {
        if (this.globalConfig == null && this.sectionsByArea == null && this.sections == null)
        {
            this.getConfigExtensions();
        }
        return new ExtendedScriptConfigModel(this.configService, xmlConfig, this.globalConfig, this.sectionsByArea, this.sections);
    }
    
    /**
     * <p>Creates a new {@link TemplateConfigModel} instance using the local configuration generated for this request.
     * If configuration for the request will be generated if it does not yet exist. It is likely that this method will be
     * called multiple times within the context of a single request and although the configuration containers will always
     * be the same a new {@link TemplateConfigModel} instance will always be created as the the supplied <code>xmlConfig</code>
     * string could be different for each call (because each WebScript invoked in the request will supply different
     * configuration.</p>
     */
    public TemplateConfigModel getExtendedTemplateConfigModel(String xmlConfig)
    {
        if (this.globalConfig == null && this.sectionsByArea == null && this.sections == null)
        {
            this.getConfigExtensions();
        }
        return new ExtendedTemplateConfigModel(this.configService, xmlConfig, this.globalConfig, this.sectionsByArea, this.sections);
        
    }
    
    /**
     * <p>This is a local {@link ConfigImpl} instance that will only be used when extension modules are employed. It will
     * initially be populated with the default "static" global configuration taken from the {@link ConfigService} associated
     * with this {@link RequestContext} but then updated to include global configuration provided by extension modules that
     * have been evaluated to be applied to the current request.</p>
     */
    private ConfigImpl globalConfig = null;
    
    /**
     * <p>This map represents {@link ConfigSection} instances mapped by area. It  will only be used when extension modules are 
     * employed. It will initially be populated with the default "static" configuration taken from the {@link ConfigService} associated
     * with this {@link RequestContext} but then updated to include configuration provided by extension modules that have been evaluated 
     * to be applied to the current request.</p>
     */
    private Map<String, List<ConfigSection>> sectionsByArea = null;
    
    /**
     * <p>A list of {@link ConfigSection} instances that are only applicable to the current request. It  will only be used when extension modules are 
     * employed. It will initially be populated with the default "static" configuration taken from the {@link ConfigService} associated
     * with this {@link RequestContext} but then updated to include configuration provided by extension modules that have been evaluated 
     * to be applied to the current request.</p>
     */
    private List<ConfigSection> sections = null;
    
    /**
     * <p>Creates and populates the request specific configuration container objects (<code>globalConfig</code>, <code>sectionsByArea</code> & 
     * <code>sections</code> with a combination of the default static configuration (taken from files accessed by the {@link ConfigService}) and
     * dynamic configuration taken from extension modules evaluated for the current request. </p>  
     */
    private void getConfigExtensions()
    {
        // Extended configuration is only possible if config service is an XMLConfigService...
        // 
        // ...also, it's only necessary to populate the configuration containers if they have not already been populated. This test should also
        // be carried out by the two methods ("getExtendedTemplateConfigModel" & "getExtendedTemplateConfigModel") to prevent duplication
        // of effort... but in case other methods attempt to access it we will make these additional tests. 
        if (this.configService instanceof XMLConfigService && this.globalConfig == null && this.sectionsByArea == null && this.sections == null)
        {
            // Cast the config service for ease of access
            XMLConfigService xmlConfigService = (XMLConfigService) this.configService;
            
            // Get the current configuration from the ConfigService - we don't want to permanently pollute
            // the standard configuration with additions from the modules...
            this.globalConfig = new ConfigImpl((ConfigImpl)xmlConfigService.getGlobalConfig()); // Make a copy of the current global config
            
            // Initialise these with the config service values...
            this.sectionsByArea = new HashMap<String, List<ConfigSection>>(xmlConfigService.getSectionsByArea()); 
            this.sections = new ArrayList<ConfigSection>(xmlConfigService.getSections());
            
            // Check to see if there are any modules that we need to apply...
            List<ExtensionModule> evaluatedModules = this.getEvaluatedModules();
            if (evaluatedModules != null && !evaluatedModules.isEmpty())
            {
                for (ExtensionModule currModule: evaluatedModules)
                {
                    for (Element currentConfigElement: currModule.getConfigurations())
                    {
                        // Set up containers for our request specific configuration - this will contain data taken from the evaluated modules...
                        Map<String, ConfigElementReader> parsedElementReaders = new HashMap<String, ConfigElementReader>();
                        Map<String, Evaluator> parsedEvaluators = new HashMap<String, Evaluator>();
                        List<ConfigSection> parsedConfigSections = new ArrayList<ConfigSection>();
                        
                        // Parse and process the parses configuration...
                        String currentArea = xmlConfigService.parseFragment(currentConfigElement, parsedElementReaders, parsedEvaluators, parsedConfigSections);
                        for (Map.Entry<String, Evaluator> entry : parsedEvaluators.entrySet())
                        {
                            // add the evaluators to the config service
                            parsedEvaluators.put(entry.getKey(), entry.getValue());
                        }
                        for (Map.Entry<String, ConfigElementReader> entry : parsedElementReaders.entrySet())
                        {
                            // add the element readers to the config service
                            parsedElementReaders.put(entry.getKey(), entry.getValue());
                        }
                        for (ConfigSection section : parsedConfigSections)
                        {
                            // Update local configuration with our updated data...
                            xmlConfigService.addConfigSection(section, currentArea, globalConfig, sectionsByArea, sections);
                        }
                    }
                }
            }
        }
    }

    public void addExtensibilityDirectives(Map<String, Object> freeMarkerModel, ExtensibilityModel extModel)
    {
        // No action required. All custom directives are added via the ProcessorModelHelper
    }
    
    
    /**
     * Case insensitive map for browser headers.
     * 
     * @author Kevin Roast
     */
    private static class CaseInsensitiveHeadersMap<K, V> extends HashMap<K, V>
    {
        public CaseInsensitiveHeadersMap()
        {
            super();
        }

        public CaseInsensitiveHeadersMap(int initialCapacity)
        {
            super(initialCapacity);
        }

        public CaseInsensitiveHeadersMap(Map<? extends K, ? extends V> m)
        {
            super(m);
        }

        public CaseInsensitiveHeadersMap(int i, float f)
        {
            super(i, f);
        }

        @Override
        public boolean containsKey(Object key)
        {
            return super.containsKey(key.toString().toLowerCase());
        }

        @Override
        public V get(Object key)
        {
            return super.get(key.toString().toLowerCase());
        }

        @Override
        public V put(K key, V value)
        {
            return super.put((K)key.toString().toLowerCase(), value);
        }
    }
}

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

package org.springframework.extensions.surf.mvc;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.RequestContextUtil;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.util.EncodingUtil;
import org.springframework.extensions.webscripts.DefaultURLHelper;
import org.springframework.extensions.webscripts.ProcessorModelHelper;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.URLHelper;
import org.springframework.extensions.webscripts.URLHelperFactory;
import org.springframework.web.servlet.view.AbstractUrlBasedView;
import org.springframework.web.util.WebUtils;

/**
 * <p>Abstract Spring MVC implementation of a view resolver for Surf.
 * </p><p>
 * Developers who wish to implement custom Spring views may wish to
 * extend their own view implementations from this class so as to gain
 * access to member convenience functions for working with Surf services.
 * </p>
 *
 * @author Dave Draper
 * @author muzquiano
 * @author kevinr
 */
public abstract class AbstractWebFrameworkView extends AbstractUrlBasedView
{
    public static final String MIMETYPE_HTML = "text/html;charset=utf-8";

    /**
     * Constructor
     *
     * @param webFrameworkServiceRegistry   WebFrameworkServiceRegistry
     */
    public AbstractWebFrameworkView(WebFrameworkConfigElement webFrameworkConfiguration,
                                    ModelObjectService modelObjectService,
                                    ResourceService resourceService,
                                    RenderService renderService,
                                    TemplatesContainer templatesContainer)
    {
        this.webFrameworkConfiguration = webFrameworkConfiguration;
        this.modelObjectService = modelObjectService;
        this.resourceService = resourceService;
        this.templatesContainer = templatesContainer;
        this.renderService = renderService;
    }

    /**
     * <p>This constructor is temporarily retained to support legacy applications that may depend on it. However, it has since
     * been deprecated and should no longer be used for new implementations. The reason for the change is that Spring Surf was
     * originally coded to have many dependencies upon static helper methods rather than using properly configured Spring beans.
     * This is a problem because it inhibits flexibility and extensibility.
     * </p>
     *
     * @param webFrameworkServiceRegistry   WebFrameworkServiceRegistry
     * @deprecated Due to dependencies upon static helper methods rather than Spring configuration.
     */
    public AbstractWebFrameworkView(WebFrameworkServiceRegistry webFrameworkServiceRegistry)
    {
        // Check that a WebFrameworkServiceRegistry has been supplied (i.e. not null) to avoid NPE.
        if (webFrameworkServiceRegistry != null)
        {
            this.renderService = webFrameworkServiceRegistry.getRenderService();
            this.templatesContainer = webFrameworkServiceRegistry.getTemplatesContainer();
            this.webFrameworkConfiguration = webFrameworkServiceRegistry.getWebFrameworkConfiguration();
            this.modelObjectService = webFrameworkServiceRegistry.getModelObjectService();
            this.resourceService = webFrameworkServiceRegistry.getResourceService();
        }
        else
        {
            // TODO: It would be nice to throw an exception here, but we can't without changing the constructor signature. Log an error?
        }
    }

    /**
     * 
     */
    private URLHelperFactory urlHelperFactory = null;
    
    public URLHelperFactory getUrlHelperFactory()
    {
        return urlHelperFactory;
    }

    public void setUrlHelperFactory(URLHelperFactory urlHelper)
    {
        this.urlHelperFactory = urlHelper;
    }

    private WebFrameworkConfigElement webFrameworkConfiguration;

    /**
     * <p>Gets a <code>WebFrameworkConfigElement</code>.</p>
     *
     * @return The <code>WebFrameworkConfigElement</code> stored in the <code>WebFrameworkServiceRegistry</code> (if this
     * view was instantiated using the deprecated constructor) or the <code>WebFrameworkConfigElement</code> supplied to
     * the non-deprecated constructor</code>
     */
    public WebFrameworkConfigElement getWebFrameworkConfiguration()
    {
        return this.webFrameworkConfiguration;
    }

    private ModelObjectService modelObjectService;

    /**
     * <p>Gets a <code>ModelObjectService</code>.</p>
     *
     * @return The <code>ModelObjectService</code> stored in the <code>WebFrameworkServiceRegistry</code> (if this
     * view was instantiated using the deprecated constructor) or the <code>ModelObjectService</code> supplied to
     * the non-deprecated constructor</code>
     */
    public ModelObjectService getObjectService()
    {
        return this.modelObjectService;
    }

    private ResourceService resourceService;

    /**
     * <p>Gets a <code>ResourceService</code>.</p>
     *
     * @return The <code>ResourceService</code> stored in the <code>WebFrameworkServiceRegistry</code> (if this
     * view was instantiated using the deprecated constructor) or the <code>ResourceService</code> supplied to
     * the non-deprecated constructor</code>
     */
    public ResourceService getResourceService()
    {
        return this.resourceService;
    }

    /**
     * TODO: Add JavaDoc describing what a TemplatesContainer is!
     */
    private TemplatesContainer templatesContainer;

    /**
     * TODO: Add JavaDoc describing what a TemplatesContainer is!
     * <p>Returns the <code>TemplatesContainer</code> to be used by the view. In legacy mode this will always be the <code>TemplatesContainer</code>
     * stored in the <code>WebFrameworkServiceRegistry</code> (supplied in the constructor). However, if this view has been constructed
     * using the non-deprecated constructor then the <code>TemplatesContainer</code> returned will be the one supplied in the
     * constructor.</p>
     */
    public TemplatesContainer getTemplatesContainer()
    {
        return this.templatesContainer;
    }

    /**
     * TODO: Add JavaDoc to say what a RenderService is
     */
    private RenderService renderService;

    /**
     * TODO: Add JavaDoc describing what a RenderService is!
     * <p>Returns the RenderService to be used by the view. In legacy mode this will always be the <code>RenderService</code>
     * stored in the <code>WebFrameworkServiceRegistry</code> (supplied in the constructor). However, if this view has been constructed
     * using the non-deprecated constructor then the <code>RenderService</code> returned will be the one supplied in the
     * constructor.</p>
     * @return the render service
     */
    public RenderService getRenderService()
    {
        return this.renderService;
    }

    /**
     * <p>This is a reference to the <code>Map</code> of URI tokens for the current view. This will be set
     * by any view resolver that resolves a view that supports URI tokens (such as <code>ComponentViewResolver</code>
     * and <code>RegionViewResolver</code>. This data needs to be persisted in the view as it gets cached once it
     * has been initially resolved so needs to be available on subsequent reloads</p>.
     */
    private Map<String,String> uriTokens = null;

    /**
     * <p>Get the URI tokens for the view</p>
     *
     * @return A Map of name/value pairs derived from the URI associated with the view.
     */
    public Map<String, String> getUriTokens()
    {
        return uriTokens;
    }

    /**
     * <p>Set the URI tokens associates with the view</p>
     *
     * @param uriTokens     A Map of name/value pairs derived from the URI associated with the view.
     */
    public void setUriTokens(Map<String, String> uriTokens)
    {
        this.uriTokens = uriTokens;
    }

    /**
     * <p>This method must be implemented to actually render the view. One of the key functions of
     * this implementation is to retrieve the <code>RequestContext</code> instantiated by the
     * <code>RequestContextInterceptor</code> and complete it's initialisation.</p>
     */
    @Override
    protected void renderMergedOutputModel(Map<String, Object> mvcModel,
                                           HttpServletRequest request,
                                           HttpServletResponse response) throws Exception
    {
        // store request into the request attributes
        ServletUtil.setRequest(request);
        
        // setup the request context
        setupRequestContext(mvcModel, request);
        
        // Retrieve the RequestContext. This should have been instantiated using a RequestContextFactory in the
        // RequestContextInterceptor. It is imperative that we set the response because the HttpServletResponse
        // was not available when the interceptor was invoked.
        RequestContext context = ThreadLocalRequestContext.getRequestContext();
        response.setContentType(MIMETYPE_HTML);
        context.setResponse(response);
        
        // Set url and the URI tokens for the view in the request.
        context.setViewName(getUrl());
        if (uriTokens != null) context.setUriTokens(uriTokens);
        
        // bind the model into the request context
        Map<String, Object> model = context.getModel();
        if (model == null)
        {
            model = new HashMap<String, Object>(8);
            context.setModel(model);
        }
        model.putAll(mvcModel);
        
        // Bind url helper into model (if not already bound)
        if (model.get(ProcessorModelHelper.MODEL_URL) == null)
        {
            // Build a URLHelper object one time - it is immutable and can be reused
            URLHelper urlHelper;
            if (this.urlHelperFactory != null)
            {
                // If a factory has been provided then use that to create the URLHelper, this
                // allows Surf to be extended to customise the URLHelper implementation, the
                // primary reason for this would be to modify the url context returned...
                urlHelper = this.urlHelperFactory.createUrlHelper(context, uriTokens);
            }
            else
            {
                // If no factory has been provided then just create the default URLHelper...
                urlHelper = new DefaultURLHelper(context, uriTokens);
                
            }
            model.put(ProcessorModelHelper.MODEL_URL, urlHelper);
        }
        
        // standard population of the request context
        populateRequestContext(context, request);
        
        // allow for customization of request context set up
        validateRequestContext(context, request);
        
        // Expose the model object as request attributes.
        // Expose forward request attributes
        exposeModelAsRequestAttributes(model, request);
        exposeForwardRequestAttributes(request);
        
        // default character encoding
        String encoding = request.getCharacterEncoding();
        if (encoding == null)
        {
            try
            {
                request.setCharacterEncoding(EncodingUtil.DEFAULT_ENCODING);
            }
            catch (UnsupportedEncodingException uee)
            {
                throw new RendererExecutionException("Unable to set encoding to default: " + EncodingUtil.DEFAULT_ENCODING, uee);
            }
        }
        
        try
        {
            renderView(context);
        }
        catch (Throwable t)
        {
            // This catch block is intended to catch unchecked exceptions that might be thrown further up the stack.
            // It is intended to assist in diagnosing unexpected errors that occur during development. These should
            // be properly fixed before code is committed.
            t.printStackTrace();
            throw new Exception(t);
        }
        finally
        {
            // When the RequestContext was instantiated it was added to a ThreadLocal instance so that
            // the thread of execution could retrieve its associated rendering information. Once the
            // view has been rendered (regardless of whether or not it was successful) it is necessary
            // to call the release() method which removes the RequestContext from the ThreadLocal. This
            // is the only place that the .release() method should be called - if it is called deeper
            // in the stack then it may cause a NullPointerException.
            context.release();
        }
    }

    /**
     * To be implemented by view classes to provide render dispatch
     *
     * @param context the render context
     * @throws Exception
     */
    protected abstract void renderView(RequestContext context)
        throws Exception;

    /**
     * Expose forward request attributes.
     *
     * @param request the request
     */
    protected void exposeForwardRequestAttributes(HttpServletRequest request)
    {
        WebUtils.exposeForwardRequestAttributes(request);
    }

    /**
     * Implementation classes should extend this method to provide for any initial setup
     * of the request context.  This may include setting its internal state.
     *
     * @param mvcModel
     * @param request
     */
    protected void setupRequestContext(Map<String, Object> mvcModel, HttpServletRequest request)
        throws Exception
    {
    }

    /**
     * Performs default population of the request context.
     *
     * @param context
     * @param request
     *
     * @throws Exception
     */
    protected void populateRequestContext(RequestContext context, HttpServletRequest request)
        throws Exception
    {
        RequestContextUtil.populateRequestContext(context, request);
    }

    /**
     * Extension point for performing any validation of the request context state
     *
     * If the request context state is invalid, it can either be adjusted or an exception
     * can be raised.
     *
     * By default, no validation is performed.
     *
     * @param context
     * @param request
     */
    protected void validateRequestContext(RequestContext context, HttpServletRequest request)
        throws Exception
    {
    }

    /**
     * Retrieves the page object with the given page id.
     *
     * If a page object doesn't exist, this supports the short-cut method of wiring directly to a template file on disk.
     * If a template file exists on disk with the same id as the page, we create in-memory disposal objects to represent
     * the page and the template instance required to arrive to that template.
     *
     * This provides a quick way to get started building templated sites.
     *
     * @param pageId
     *
     * @return page object
     */
    protected Page lookupPage(String pageId)
    {
        Page page = getObjectService().getPage(pageId);
        if (page == null)
        {
            // if a page object doesn't exist, we support a short-cut
            // if a template (ftl or php) exists on disk, we'll create disposable objects to represent the page and template

            TemplateProcessorRegistry templateProcessorRegistry = this.templatesContainer.getTemplateProcessorRegistry();
            String validTemplatePath = templateProcessorRegistry.findValidTemplatePath(pageId);
            if (validTemplatePath != null)
            {
                TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessor(validTemplatePath);
                if (templateProcessor != null)
                {
                    // we have both discovered a template as well as a template processor that can handle this path
                    // as such, let's cheat and auto-wire a dummy template instance up so that we can punch through
                    // to the template without the need to code up all those objects

                    TemplateInstance templateInstance = this.modelObjectService.newTemplate(pageId);
                    templateInstance.setTemplateTypeId(pageId);

                    page = this.modelObjectService.newPage(pageId);
                    page.setTemplateId(pageId);
                }
            }
        }

        return page;
    }
}

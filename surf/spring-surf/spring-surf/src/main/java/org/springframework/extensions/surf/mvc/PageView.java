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

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.TemplatesContainer;
import org.springframework.extensions.surf.WebFrameworkConstants;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderFocus;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.surf.resource.ResourceService;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.PageType;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.surf.util.URLDecoder;
import org.springframework.extensions.webscripts.TemplateProcessor;
import org.springframework.extensions.webscripts.TemplateProcessorRegistry;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>Default view implementation for Surf pages.</p>
 *
 * @author muzquiano
 * @author kevinr
 * @author David Draper
 */
public class PageView extends AbstractWebFrameworkView
{
    private static Log logger = LogFactory.getLog(PageView.class);

    private static final String ALF_REDIRECT_URL   = "alfRedirectUrl";
    private static final String ALF_LAST_USERNAME = "alfLastUsername";

    /**
     * <p>This is a local copy of the <code>Page</code> ID that this view will render. This will be set when the view
     * is first built by the <code>PageViewResolver</code> or <code>PageTypeViewResolver</code> before the view
     * instance gets cached.</p>
     * <p>However, this alone is not enough to ensure that the page gets displayed. The rendering code relies on the
     * the instance of the <code>Page</code> that is stored in the <code>RequestContext</code> so we use this reference
     * to set the value in the <code>RequestContext</code> once the view has been cached. This is not the optimum way
     * in which to accommodate the Spring view caching but is sufficient for the time being.</p>
     */
    private String pageId = null;

    /**
     * <p>This is the preferred constructor to use for instantiating a new <code>PageView</code> because it allows
     * complete flexibility when rendering the view. An <code>AbstractWebFrameworkView</code> is typically instantiated from
     * within a <code>AbstractWebFrameworkViewResolver</code> and all the arguments in the constructor signature should be
     * supplied to the <code>AbstractWebFrameworkViewResolver</code> as beans via the Spring configuration.</p> 
     * 
     * @param webFrameworkServiceRegistry
     * @param webFrameworkConfiguration
     * @param modelObjectService
     * @param resourceService
     * @param presentationService
     * @param renderService
     * @param templatesContainer
     */
    public PageView(WebFrameworkConfigElement webFrameworkConfiguration,
                    ModelObjectService modelObjectService,
                    ResourceService resourceService,
                    RenderService renderService,
                    TemplatesContainer templatesContainer)
    {
        super(webFrameworkConfiguration, 
              modelObjectService, 
              resourceService, 
              renderService, 
              templatesContainer);
    }
    
    /**
     * <p>This constructor should be avoided if possible because it relies on the supplied <code>WebFrameworkServiceRegistry</code>
     * argument to provide all the other Spring beans required to render the view. This means that there is no flexibility via
     * configuration to adapt different views to use different beans.</p>
     * 
     * @param serviceRegistry
     * @deprecated
     */
    public PageView(WebFrameworkServiceRegistry serviceRegistry)
    {
        super(serviceRegistry);
    }

    /**
     * Set the <code>Page</code> to be rendered by this view.
     * @param page
     */
    public void setPage(Page page)
    {
        this.pageId = page.getId();
    }

    /**
     * Initial setup of the request context.
     * 
     * @param mvcModel
     * @param request
     */
    @Override
    protected void setupRequestContext(Map<String, Object> mvcModel, HttpServletRequest request) throws Exception
    {
        // This line of code is a hangover from the old system of re-parsing all of the URL information each time
        // a cached PageView is rendered. Currently, all the rendering relies upon the Page object being stored on
        // the current RequestContext so in order to ensure that rendering is successful we need to set this value.
        // In the future the rendering should not rely so heavily on the contents of the current RequestContext but
        // for now it must. However, it is possible that other view resolvers could be provided by applications that
        // wish to render a PageView so we must log a warning message if those view resolvers fail to set the PageView
        // object up correctly.
        if (this.pageId != null)
        {
            ThreadLocalRequestContext.getRequestContext().setPage(getObjectService().getPage(this.pageId));
        }
        else
        {
            logger.error("The current PageView does not have its \"page\" property set. Please ensure that the associated view resolver is calling the setPage() method when building the view!");
        }
    }

    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.mvc.AbstractWebFrameworkView#validateRequestContext(org.springframework.extensions.surf.RequestContext, javax.servlet.http.HttpServletRequest)
     */
    @Override
    protected void validateRequestContext(RequestContext context, HttpServletRequest request)
        throws Exception
    {
        // if we have absolutely nothing to dispatch to, then check to
        // see if there is a root-page declared to which we can go
        if (context.getPage() == null && context.getCurrentObjectId() == null)
        {
            // if the site configuration exists...
            if (context.getSiteConfiguration() != null)
            {
                // check if a root page exists to which we can forward
                Page rootPage = context.getRootPage();
                if (rootPage != null)
                {
                    context.setPage(rootPage);
                }
            }
        }
    }

    protected String buildLoginRedirectURL(HttpServletRequest request)
    {
        String redirectUrl = request.getRequestURI() + (request.getQueryString() != null ? ("?" + request.getQueryString()) : "");
        return redirectUrl;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.extensions.surf.mvc.AbstractWebFrameworkView#renderView(org.springframework.extensions.surf.render.RenderContext)
     */
    @SuppressWarnings("unchecked")
    protected void renderView(RequestContext context)
        throws Exception
    {
        String formatId = context.getFormatId();
        String objectId = context.getCurrentObjectId();
        String pageId = context.getPageId();
        Page page = context.getPage();

        HttpServletRequest request = ServletUtil.getRequest();

        if (page != null)
        {
            // redirect to login based on page authentication required
            if (loginRequiredForPage(context, request, page))
            {
                String loginPageId = null;

                // Consider the theme first - which can override common page types
                String themeId = (String) context.getThemeId();
                if (themeId != null)
                {
                    Theme theme = getObjectService().getTheme(themeId);
                    if (theme != null)
                    {
                        loginPageId = theme.getPageId(PageType.PAGETYPE_LOGIN);
                    }
                }

                // Consider whether a system default has been set up
                if (loginPageId == null)
                {
                    loginPageId = this.getWebFrameworkConfiguration().getDefaultPageTypeInstanceId(PageType.PAGETYPE_LOGIN);
                }

                Page loginPage = null;
                if (loginPageId != null)
                {
                    loginPage = this.lookupPage(loginPageId);
                    if (loginPage != null)
                    {
                        // get URL arguments as a map ready for rebuilding the request params
                        Map<String, String> args = new HashMap<String, String>(
                                request.getParameterMap().size(), 1.0f);
                        Enumeration names = request.getParameterNames();
                        while (names.hasMoreElements())
                        {
                            String name = (String)names.nextElement();
                            args.put(name, request.getParameter(name));
                        }

                        // set redirect url for use on login page template
                        context.setValue(ALF_REDIRECT_URL, buildLoginRedirectURL(request));

                        // set last username if any
                        Cookie cookie = AuthenticationUtil.getUsernameCookie(request);
                        if (cookie != null)
                        {
                            context.setValue(ALF_LAST_USERNAME, URLDecoder.decode(cookie.getValue()));
                        }

                        // dispatch to the login page
                        context.setPage(loginPage);
                        dispatchPage(context, loginPage.getId(), formatId);

                        // no need to process further as we have dispatched
                        return;
                    }
                }

                // if we get here then no login page was found - the webapp is not configured correctly
                if (loginPageId == null || loginPage == null)
                {
                    throw new PlatformRuntimeException("No 'login' page type configured - but page auth required it.");
                }
            }
        }

        if (logger.isDebugEnabled())
        {
            debug(context, "Current Page ID: " + pageId);
            debug(context, "Current Format ID: " + formatId);
            debug(context, "Current Object ID: " + objectId);
        }

        // if at this point there really is nothing to view...
        if (page == null && objectId == null)
        {
            if (logger.isDebugEnabled())
                debug(context, "No Page or Object determined");

            // Go to the getting started page
            try
            {                
                if (!getRenderService().renderSystemPage(context, WebFrameworkConstants.SYSTEM_PAGE_GETTING_STARTED))
                {
                    throw new RequestDispatchException("Unable to discover a page to be dispatched - no target page or root page specified and a getting started page was not configured.");
                }
            }
            catch (RendererExecutionException ree)
            {
                throw new RequestDispatchException(ree);
            }
        }
        else
        {
            // we know we're dispatching to something...
            // if we have a page specified, then we'll go there
            if (pageId != null)
            {
                if (logger.isDebugEnabled())
                    debug(context, "Dispatching to Page: " + pageId);

                // if there happens to be a content item specified as well,
                // it will just become part of the context
                // i.e. if the content item doesn't determine the
                // destination page if the destination page is specified

                // we're dispatching to the current page
                dispatchPage(context, pageId, formatId);
            }
        }
    }

    /**
     * Return if login page should be display for the given page.
     * <p>
     * Default implementation of this method will retrieve the current User and test to see
     * if a guest, non-guest or admin user id is present as appropriate for the authentication
     * level specified on the supplied page object.
     * 
     * @param context   RequestContext
     * @param request   HttpServletRequest
     * @param page      Page to test authentication for
     * 
     * @return true if login is required for given page, false otherwise
     */
    protected boolean loginRequiredForPage(RequestContext context, HttpServletRequest request, Page page)
    {
        boolean login = false;
        User user = context.getUser();
        switch (page.getAuthentication())
        {
            case guest:
            {
                login = (user == null);
                break;
            }
            
            case user:
            {
                login = (user == null || AuthenticationUtil.isGuest(user.getId()));
                break;
            }
            
            case admin:
            {
                login = (user == null || !user.isAdmin());
                if (login)
                {
                    // special case for admin - need to clear user context before
                    // we can login again to "upgrade" our user authentication level
                    AuthenticationUtil.clearUserContext(request);
                }
                break;
            }
        }
        return login;
    }

    /**
     * Dispatches a given page in a given format.
     *
     * @param context
     * @param pageId
     * @param formatId
     * @throws RequestDispatchException
     */
    public void dispatchPage(RequestContext context, String pageId, String formatId)
        throws RequestDispatchException
    {
        Page page = context.getPage();
        if (page == null || !page.getId().equals(pageId))
        {
            // load the page and set onto context
            page = lookupPage(pageId);
            context.setPage(page);
        }

        if (logger.isDebugEnabled())
            debug(context, "Template ID: " + page.getTemplateId());

        TemplateInstance currentTemplate = page.getTemplate(context);
        if (currentTemplate == null)
        {
            // if no template instance exists, we can provide a
            // fast workaround where we can check to see if, by chance,
            // a template exists with the same name as the page.

            TemplateProcessorRegistry templateProcessorRegistry = getTemplatesContainer().getTemplateProcessorRegistry();
            String validTemplatePath = templateProcessorRegistry.findValidTemplatePath(pageId);
            if (validTemplatePath != null)
            {
                TemplateProcessor templateProcessor = templateProcessorRegistry.getTemplateProcessor(validTemplatePath);
                if (templateProcessor != null)
                {
                    // we have both discovered a template as well as a template processor that can handle this path
                    // as such, let's cheat and auto-wire a dummy template instance up so that we can punch through
                    // to the template without the need to code up all those objects

                    // create a dummy template instance (does not persist)
                    currentTemplate = getObjectService().newTemplate(pageId);
                    currentTemplate.setTemplateTypeId(pageId);

                    // bind to context
                    context.setTemplate(currentTemplate);
                }
            }
        }
        if (currentTemplate != null)
        {
            if (logger.isDebugEnabled())
                debug(context, "Rendering Page with template: " + currentTemplate.getId());
            
            getRenderService().renderPage(context, RenderFocus.BODY);
        }
        else
        {
            if (logger.isDebugEnabled())
                debug(context, "Unable to render Page - template was not found");
            
            try
            {
                if (!getRenderService().renderSystemPage(context, WebFrameworkConstants.SYSTEM_PAGE_UNCONFIGURED))
                {
                    throw new RequestDispatchException("The page '" + pageId + "' exists but a template association could not be determined.");
                }
            }
            catch (RendererExecutionException ree)
            {
                throw new RequestDispatchException(ree);
            }
        }
    }

    /**
     * Debug logger helper function.
     *
     * @param context
     * @param value
     */
    protected static void debug(RequestContext context, String value)
    {
        logger.debug("[" + context.getId() + "] " + value);
    }
}

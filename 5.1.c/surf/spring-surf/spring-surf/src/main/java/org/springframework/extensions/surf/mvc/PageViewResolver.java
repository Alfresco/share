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

import java.util.Locale;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * Resolver for web framework Page views
 *
 * This allows for page ids to utilise uri templates by default.
 *
 * @author kevinr
 * @author muzquiano
 * @author David Draper
 */
public class PageViewResolver extends AbstractWebFrameworkViewResolver
{
    /** Lock to provide protection around the uri template list index */
    private ReadWriteLock uriIndexListLock = new ReentrantReadWriteLock();
    
    /*package*/ static final String URI_PAGEID = "pageid";
    
    
    /**
     * Constructor
     */
    public PageViewResolver()
    {
        super();
        
        // do not cache null Page lookups - otherwise simply entering the url of a page that does not
        // yet exist will effectively block it from access in the future if it is created.
        this.useNullSentinel = false;
    }

    /**
     * <p>Determines whether or not this view resolver can build a view to be displayed. This method will
     * return <code>true</code> providing one of the following criteria are met:</p>
     * <ul>
     * <li>No view name has been specified (indicating that the root page will be loaded</li>
     * <li>The view name directly matches a page id</li>
     * <li>The view name maps to a template that contains a page id token</li>
     * </ul>
     * <p>If a page id is found then it will be used to attempt to load a page. If a page can be loaded it
     * will be stored in the current <code>RequestContext</code> object to that the associated view can
     * retrieve it. Finally, if the view name maps to a template uri then the tokens in the uri will also
     * be saved into the current <code>RequestContext</code>.</p>
     *
     * @param viewName The view name to check.
     * @param locale The current locale.
     */
    @Override
    protected boolean canHandle(String viewName, Locale locale)
    {
        boolean canHandle = false;

        Page page = null;
        RequestContext currentRequestContext = ThreadLocalRequestContext.getRequestContext();
        
        /* The following section of code makes it possible to use the root page defined in the site configuration.
         * In order to achieve this we need to retrieve the ServletContextPath (which should be set to the 
         * Spring MVC RequestDispatcher) and remove this from the requested view. If no page has been appended
         * to the view then we will default to use the site root page.
         */
        String pageRequested = viewName;
        String servletContextPath = currentRequestContext.getServletContextPath();
        if (servletContextPath != null)
        {
            if (!viewName.startsWith("/"))
            {
                servletContextPath = servletContextPath.substring(1); // Remove the leading slash if the page view does not start with a slash...
            }
            
            // Strip the request dispatcher context path from the view requested to get the pure page request...
            // If no page has been requested then we will use the default page...
            if (viewName.startsWith(servletContextPath))
            {
                pageRequested = viewName.substring(servletContextPath.length());
            }
        }
        
        if (pageRequested.length() == 0 || (pageRequested.length() == 1 && pageRequested.charAt(0) == '/'))
        {
            // assume root page
            page = currentRequestContext.getRootPage();
            currentRequestContext.setPage(page);
            
            // Set the tokens found for the configured root page...
            currentRequestContext.setUriTokens(getTokens(page.getId()));
            canHandle = true;
        }
        else
        {
            // Typically, the viewName will actually be the ID of the page to load, so initially set
            // the pageId to be the view name...
            String pageId = viewName;
            
            // ...however, it is also possible to set one or more uri-templates in the application configuration.
            // If the view name matches one of these templates then the tokens generated from the template should
            // be stored so that they can be made available as properties in the view. If a "pageid" token is present
            // then this should override the view name and become the target page id...
            Map<String,String> tokens = getTokens(viewName);
            if (tokens != null)
            {
                // Use the "pageid" token as the pageId if one is found...
                if (tokens.containsKey(URI_PAGEID))
                {
                    pageId = tokens.get(URI_PAGEID);
                }
                
                // Set the tokens in the current request. If this method returns anything other than null
                // (which won't happen if this code block gets executed) then these tokens will be needed
                // in the associated view. Therefore they should be stored now to prevent them needed to be
                // generated again.
                currentRequestContext.setUriTokens(tokens);
            }
            
            // retrieve the page
            page = lookupPage(pageId);
            if (page != null)
            {
                // If a page has been found then store it on the current RequestContext object to save
                // looking it up later.
                currentRequestContext.setPage(page);
                
                // If we've found a page then this view resolver can build the page!
                canHandle = true;
            }
        }
        return canHandle;
    }

    /**
     * <p>Retrieves the tokens matched against the configured URI templates for the supplied view name.</p>
     * @param viewName The view name to map tokens from.
     * @return A map of tokens generated from the view name
     */
    @SuppressWarnings("deprecation")
    protected Map<String,String> getTokens(String viewName)
    {
        Map<String,String> tokens = null;
        this.uriIndexListLock.readLock().lock();
        try
        {
            if (getUriTemplateListIndex() == null)
            {
                this.uriIndexListLock.readLock().unlock();
                this.uriIndexListLock.writeLock().lock();
                try
                {
                    // check again as multiple threads could have been waiting on the write lock
                    if (getUriTemplateListIndex() == null)
                    {
                        setUriTemplateIndex(generateUriTemplateListIndexFromConfig(
                                getWebFrameworkServiceRegistry(), "uri-templates"));
                    }
                }
                finally
                {
                    this.uriIndexListLock.readLock().lock();
                    this.uriIndexListLock.writeLock().unlock();
                }
            }
            
            // perform the uri template match to retrieve the tokens if any
            tokens = matchUriTemplate("/" + viewName);
        }
        finally
        {
            this.uriIndexListLock.readLock().unlock();
        }
        return tokens;
    }
   
    /**
     * Constructs a new <code>PageView</code> object using and sets it's URL to the current view name
     * providing that a <code>Page</code> object is stored on the current <code>RequestContext</code>
     * object.
     *
     * @param viewName The name of the view to build.
     */
    @Override
    protected AbstractUrlBasedView buildView(String viewName)
    {
        PageView view = null;
        Page page = ThreadLocalRequestContext.getRequestContext().getPage();
        if (page != null)
        {
            view = new PageView(getWebframeworkConfigElement(), 
                                getModelObjectService(), 
                                getWebFrameworkResourceService(), 
                                getWebFrameworkRenderService(),
                                getTemplatesContainer());
            view.setUrl(viewName);
            view.setPage(page);
            view.setUrlHelperFactory(getUrlHelperFactory()); // It doesn't matter if this is null, the result will be the DefaultURLHelper gets created
            view.setUriTokens(ThreadLocalRequestContext.getRequestContext().getUriTokens());
        }

        return view;
    }
}

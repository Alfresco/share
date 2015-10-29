/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.exceptionresolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.Section;
import org.alfresco.wcm.client.exception.PageNotFoundException;
import org.alfresco.wcm.client.exception.RepositoryUnavailableException;
import org.alfresco.wcm.client.interceptor.ModelDecorator;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.mvc.PageView;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.SimpleMappingExceptionResolver;

/**
 * This class attempts to find an error page for a http status code within the
 * repository when an exception occurs in a controller. eg 404page.html If one
 * is not found then it looks for a specific surf page. eg 404page If still not
 * found then it reverts to the behaviour of the SimpleMappingExceptionResolver
 * and so uses a default catch-all error page.
 * 
 * @author Chris Lack
 */
public class RepositoryExceptionResolver extends SimpleMappingExceptionResolver
{
    private static Log log = LogFactory.getLog(RepositoryExceptionResolver.class);

    /** The web framework service registry. */
    private WebFrameworkServiceRegistry webFrameworkServiceRegistry;

    private String errorPageSuffix;
    private ModelDecorator modelDecorator;

    @Override
    protected ModelAndView doResolveException(HttpServletRequest request, HttpServletResponse response, Object handler,
            Exception ex)
    {
        // Log the exception
        if (!(ex instanceof RepositoryUnavailableException))
        {
            // Don't bother to log these for every request. It will be logged 
            // by GuestSessionFactoryImpl. 
            log.error(ex, ex);
        }

        // Determine the http status code from the exception
        Integer statusCode;
        if (ex instanceof PageNotFoundException)
        {
            statusCode = HttpStatus.NOT_FOUND.value();
        }
        else
        {
            statusCode = HttpStatus.INTERNAL_SERVER_ERROR.value();
        }

        // Get the current website from the request
        RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
        if (requestContext != null)
        {
            Section rootSection = (Section) requestContext.getValue("rootSection");
            if (rootSection != null)
            {
                // Determine the error page asset name and fetch it from the
                // repository
                String errorPage = statusCode + errorPageSuffix + ".html";
                
                Asset errorAsset = rootSection.getAsset(errorPage); 

                String template = null;
                if (errorAsset != null)
                {
                    // A generic Surf error page will be used with the
                    // repository asset html inserted within it.
                    template = "errorpage";
                }
                else
                {
                    // If no asset exists in the repository for the status code
                    // then look for a specific Surf page.
                    String pageName = statusCode + errorPageSuffix;
                    if (lookupPage(pageName) != null)
                    {
                        template = pageName;
                    }
                }

                // If there is an editorially configured error page or a
                // specific Surf one then use it
                if (template != null)
                {
                    // Apply HTTP status code for error views.
                    // Only apply it if we're processing a top-level request.
                    applyStatusCodeIfPossible(request, response, statusCode);

                    PageView view = new PageView(requestContext.getServiceRegistry());
                    view.setPage(lookupPage(template));
                    view.setUrl(template);

                    ModelAndView mv = new ModelAndView();
                    mv.setView(view);

                    // Store website, section and asset on spring model too for
                    // use in page meta data
                    // When exceptions are encountered a new model is created by
                    // Spring so any data loaded
                    // by the the controller interceptors is lost.
                    try
                    {
                        modelDecorator.populate(request, mv);
                    }
                    catch (Exception e)
                    {
                        // ignore any errors on trying to populate the model
                    }

                    // Store error details on model
                    mv.addObject("exception", ex);
                    mv.addObject("errorAsset", errorAsset);
                    return mv;
                }
            }
        }

        // If we couldn't determine an editorially configured error page or a
        // specific Surf one
        // then use a static page
        return super.doResolveException(request, response, handler, ex);
    }

    /**
     * Retrieves the page object with the given page id from Surf.
     * 
     * @param pageId
     * @return Page object or null if not found
     */
    private Page lookupPage(String pageId)
    {
        return webFrameworkServiceRegistry.getModelObjectService().getPage(pageId);
    }

    public void setErrorPageSuffix(String errorPageSuffix)
    {
        this.errorPageSuffix = errorPageSuffix;
    }

    public void setModelDecorator(ModelDecorator modelDecorator)
    {
        this.modelDecorator = modelDecorator;
    }

    public void setServiceRegistry(WebFrameworkServiceRegistry webFrameworkServiceRegistry)
    {
        this.webFrameworkServiceRegistry = webFrameworkServiceRegistry;
    }

}

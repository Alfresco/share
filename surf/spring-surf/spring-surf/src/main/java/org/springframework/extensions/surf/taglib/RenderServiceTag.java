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
package org.springframework.extensions.surf.taglib;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.exception.RequestDispatchException;
import org.springframework.extensions.surf.render.RenderContextRequest;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.tags.RequestContextAwareTag;

/**
 * <p>An abstract <code>RequestContextAwareTag</code> intended to be sub-classed by custom JSP tags that
 * require access to a <code>RenderService</code> for rendering Spring Surf objects (such as pages,
 * templates, components, etc). It extends the Spring provided <code>RequestContextAwareTag</code> class
 * in order to access the <code>WebApplicationContext</code> from which to retrieve a <code>RenderService</code>
 * interface implementing bean. If no bean is found then the JSP will render an error message indicating
 * the cause of the problem (ideally this will be detected during development rather than in product release). If
 * more than one <code>RenderService</code> is found then it will use the first that is a
 * <code>RenderServiceImpl</code> instance, and if one is not found will simply use the first bean
 * retrieved.</p>
 * <p>Although the Spring Surf defines a <code>RenderServiceImpl</code> with a known bean id, it is not
 * directly looked up because this would prevent alternative services being set through custom configuration.
 * To ensure that an alternative RenderService from the default Spring Surf configured bean is used you 
 * must override the default bean (that is re-use the bean id: "webframework.service.render").</p>
 *    
 * @author David Draper
 */
public abstract class RenderServiceTag extends RequestContextAwareTag
{
    private static final long serialVersionUID = 8558352373760130809L;

    private static Log logger = LogFactory.getLog(RenderServiceTag.class);

    /**
     * <p>The life-cycle of a custom JSP tag is that it is created the first time it is required and then
     * the same instance is re-used for all other invocations. Because it could be expensive to look up 
     * a <code>RenderService</code> from the Spring application context for each invocation of the
     * the custom tag we will store the instance found on the first invocation as a form of bootstrapping.
     * All subsequent invocations will re-use the same service. 
     */
    private RenderService renderService = null;
    
    /**
     * <p>Attempts to find a <code>RenderService</code> bean to provide to subclasses to use for rendering.
     * If no <code>RenderService</code> bean is defined in then an error message will be rendered without
     * invoking any subclass code. If more than one <code>RenderService</code> is found then it will attempt
     * to use the default Spring Surf implementation. Failing that it will use the first <code>RenderService</code>
     * bean found.
     */
    @Override
    protected final int doStartTagInternal() throws Exception
    {
        int returnCode = SKIP_BODY;
        
        // Get the RequestContext of the request as it will be required by the RenderService
        HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();        
        RequestContext renderContext = (RequestContext) request.getAttribute(RenderContextRequest.ATTRIB_RENDER_CONTEXT);
        ModelObject object = (ModelObject) request.getAttribute(RenderContextRequest.ATTRIB_MODEL_OBJECT);

        if (this.renderService != null)
        {
            // If the RenderService instance variable is not null then it means that the subclass
            // has already been instantiated and used. There is no point in looking up the RenderService
            // for each method invocation because it will not have changed (and we are not supporting 
            // application context refreshes). Therefore we can just call the invokeRenderService() method
            // with the previously saved instance.
            returnCode = invokeRenderService(this.renderService, renderContext, object);
        }
        else
        {
            try
            {
                // Get all the beans that implement the RenderService interface...
                WebApplicationContext applicationContext = getRequestContext().getWebApplicationContext(); 
                String[] renderServices = applicationContext.getBeanNamesForType(RenderService.class);
                if (renderServices.length == 0)
                {            
                    // If no RenderService beans are available then we will log an error, but rather than
                    // throwing an exception we will render the error on the page to assist debugging the problem...
                    logger.error("A RenderService bean has not been defined in the Spring application context. It is not possible to render the custom JSP implemented by the class: " + this.getClass().getName());
                    this.pageContext.getOut().write("Cannot render " + getId() + " because a RenderService bean is not configured in the Spring application context");
                    returnCode = SKIP_BODY;
                }
                else if (renderServices.length == 1)
                {
                    // There is only a single RenderService configured in the application context so retrieve
                    // the bean and call the invokeRenderService method which will have been implemented by 
                    // the subclass to call the appropriate service...
                    this.renderService = (RenderService) applicationContext.getBean(renderServices[0]);
                    returnCode = invokeRenderService(this.renderService, renderContext, object);
                }
                else
                {
                    // Multiple instances of RenderService have been defined so we need to choose which one we
                    // want to invoke.           
                    logger.info("Multiple RenderService beans have been configured, searching for instance of " + RenderService.class.getName()); 
                   
                    // Declare variables to hold both the preferred and first render service. The preferred render
                    // service will be set to the first bean that is an instance of the default Spring Surf render service
                    // (RenderServiceImpl) and the first render service will be set to the first bean found in the 
                    // array (we do this to save retrieving it from the application context twice)...
                    RenderService preferredRenderService = null;
                    RenderService firstRenderService = null;
                   
                    // Iterate through the bean names, retrieving each one to try and find an instance of RenderServiceImpl
                    FindRenderServiceImpl: for (String currRenderServiceName: renderServices)
                    {
                        RenderService currRenderService = (RenderService) applicationContext.getBean(currRenderServiceName);
                        if (currRenderService instanceof RenderService)
                        {
                            // We've found an instance of RenderServiceImpl so we can break out of the loop early
                            preferredRenderService = currRenderService;
                            break FindRenderServiceImpl;
                        }
                        else if (firstRenderService == null)
                        {
                            // If the firstRenderService hasn't been stored yet (i.e, we're on the first iteration of
                            // the loop) then save it so that we don't need to retrieve it from the application context a second
                            // time if we can't find our preferred bean.
                            firstRenderService = currRenderService;
                        }
                    }
                   
                    // Invoke the required service using the preferred service if available (and the first service found if not)...
                    if (preferredRenderService != null)
                    {
                        logger.info("Using RenderService bean: \"" + preferredRenderService.getBeanName() + "\" as it is an instance of " + RenderService.class.getName());
                        this.renderService = preferredRenderService;
                        returnCode = invokeRenderService(preferredRenderService, renderContext, object); 
                    }
                    else
                    {
                        logger.info("Could not find an instance of " + RenderService.class.getName() + ", using bean: \"" + firstRenderService.getBeanName() + "\"");
                        this.renderService = firstRenderService;
                        returnCode = invokeRenderService(firstRenderService, renderContext, object);
                    }
                }
            }
            catch (RequestDispatchException e)
            {
                // It's possible that a RequestDispatchException will be thrown if the RenderService fails to render first
                // the requested object and then the resultant error page. If this is the case then we should output some form of
                // useful message rather than just throwing on the exception to display a stack trace.
                String msg = "A RequestDispatchException has been thrown whilst rendering " + getId() + 
                             " this is most likely because an error occurred during the rendering of an error page.";
                logger.error(msg, e);
                pageContext.getOut().write(msg);
            }            
        }

        return returnCode;
    }
    
    /**
     * <p>This method should be implemented to invoke the required method of the <code>RenderService</code>
     * that the subclass needs to render its output</p>
     *   
     * @param renderService
     * @param renderContext
     * @return The appropriate return code to pass on (e.g. SKIP_BODY, EVAL_BODY_INCLUDE, etc). 
     */
    protected abstract int invokeRenderService(RenderService renderService, RequestContext renderContext, ModelObject object) throws Exception;

}

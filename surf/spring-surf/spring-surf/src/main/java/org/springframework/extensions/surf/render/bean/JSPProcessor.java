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

package org.springframework.extensions.surf.render.bean;

import static org.springframework.extensions.surf.WebFrameworkConstants.URI;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.RendererExecutionException;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.ExtensibilityHttpResponse;
import org.springframework.extensions.surf.render.AbstractProcessor;
import org.springframework.extensions.surf.render.ProcessorContext;
import org.springframework.extensions.surf.render.RenderContextRequest;
import org.springframework.extensions.surf.site.RequestUtil;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.web.context.ServletContextAware;

/**
 * The JSP processor is a delegating processor in that it allows you to
 * pass control of render processing to a specific JSP page.
 *
 * @author muzquiano
 * @author David Draper
 */
public class JSPProcessor extends AbstractProcessor implements ServletContextAware
{
    private static final Log logger = LogFactory.getLog(JSPProcessor.class);

    private static final String JSP_FILE_URI = "jsp-file-uri";
    private static final String JSP_PATH_URI = "jsp-path-uri";

    /**
     * Constant for "jsp-path". This is the name of the element that identifies the location of the
     * JSP to process.
     */
    private static final String _JSP_PATH = "jsp-path";

    protected ServletContext servletContext;

    /* (non-Javadoc)
     * @see org.springframework.web.context.ServletContextAware#setServletContext(javax.servlet.ServletContext)
     */
    public void setServletContext(ServletContext servletContext)
    {
        this.servletContext = servletContext;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeHeader(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeHeader(ProcessorContext pc, ModelObject object) throws RendererExecutionException
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();

        String jspPath = getJspPath(pc);

        try
        {
            if (jspPath != null)
            {
                int x = jspPath.lastIndexOf('.');
                if (x != -1)
                {
                    jspPath = jspPath.substring(0,x) + ".head." + jspPath.substring(x+1, jspPath.length());
                }

                // check whether the file exists
                URL resource = servletContext.getResource(jspPath);

                // if it exists, execute it
                if (resource != null)
                {
                    HttpServletResponse response = null;
                    ExtensibilityModel extModel = context.getCurrentExtensibilityModel();
                    if (extModel == null)
                    {
                        response = context.getResponse();
                    }
                    else
                    {
                        response = new ExtensibilityHttpResponse(context.getResponse(), extModel);
                    }
                    
                    RequestUtil.include(ServletUtil.getRequest(),
                            response, jspPath);
                }
            }
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException("Unable to execute 'header' JSP Include: " + jspPath, ex);
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.render.AbstractProcessor#executeBody(org.alfresco.web.framework.render.ProcessorContext)
     */
    public void executeBody(ProcessorContext pc, ModelObject object) throws RendererExecutionException
    {
        // get render context and processor properties
        RequestContext context = pc.getRequestContext();
        String jspPath = getJspPath(pc);

        try
        {
            // Place the JSP file path onto the render data.
            // This allows it to be retrieved within the JSP page.
            context.setValue(JSP_FILE_URI, jspPath);

            // Place the JSP file's parent folder path onto the render data.
            // This allows it to be retrieved within the JSP page.
            int x = jspPath.lastIndexOf('/');
            if (x != -1)
            {
                String pathUri = jspPath.substring(0, x);
                context.setValue(JSP_PATH_URI, pathUri);
            }
            else
            {
                context.setValue(JSP_PATH_URI, "/");
            }

            doInclude(context, object, jspPath);
        }
        catch (Exception ex)
        {
            throw new RendererExecutionException("Unable to execute 'body' JSP include: " + jspPath, ex);
        }
    }

    /**
     * <p>Attempts to retrieve the value of a "uri" property in the supplied <code>ProcessorContext</code> which should be the location
     * of a JSP that this processor will render.</p>
     *
     * @param pc The <code>ProcessorContext</code> to search for the "uri" property.
     * @return The value of the "uri" property or <code>null</code> if one is not present.
     */
    private String getJspPath(ProcessorContext pc)
    {
        // Previously...
        // ...when configuring a JSP component type, the location of the JSP to render was specified by the element <jsp-path> (as
        // opposed to <uri> for FreeMarker components and nothing(!) for WebScript components). This inconsistency made little sense
        // since it made specifying the id of the processor to use pointless (since it could be derived from the element the processor
        // was looking for!). The code has been updated to look for the <uri> property FIRST (but then fall back to look for <jsp-path>
        // for backwards compatibility)...
        String jspPath = getProperty(pc, URI);
        if (jspPath == null)
        {
            jspPath = getProperty(pc, _JSP_PATH);
        }

        // If the JSP path is still null then log an error.
        if (jspPath == null)
        {
            logger.error("Could not retrieve a URI for the JSP to render. Please ensure that the property \"uri\" is set in the ProcessorContext");
        }
        return jspPath;
    }

    /**
     * <p>Checks whether or not the JSP as defined by the "uri" property in the supplied ProcessorContext actually exists.</p>
     *
     * @param pc The ProcessorContext to search for a "uri" property defining the location of the JSP
     * @return <code>true</code> if the JSP could be found and <code>false</code> otherwise.
     */
    public boolean exists(ProcessorContext pc, ModelObject object)
    {
        boolean exists = false;

        // get render context and processor properties
        String jspPath = getJspPath(pc);
        try
        {
            URL resource = this.servletContext.getResource(jspPath);
            exists = resource != null;
        }
        catch (MalformedURLException e)
        {
            logger.error("The URI for the JSP was badly formed: " + jspPath);
        }

        return exists;
    }

    protected void doInclude(RequestContext context, ModelObject object, String jspPath) throws ServletException, IOException
    {
        HttpServletRequest httpServletRequest = ServletUtil.getRequest();
        RenderContextRequest request = new RenderContextRequest(context, object, httpServletRequest);

        RequestDispatcher dispatcher = request.getRequestDispatcher(jspPath);

        HttpServletResponse response = null;
        ExtensibilityModel extModel = context.getCurrentExtensibilityModel();
        if (extModel == null)
        {
            response = context.getResponse();
        }
        else
        {
            response = new ExtensibilityHttpResponse(context.getResponse(), extModel);
        }
        
        // if we're dispatching a template, we'll do a forward
        if (object != null && object instanceof TemplateInstance)
        {
            if (!response.isCommitted())
            {
                dispatcher.forward(request, response);
            }
            else
            {
                logger.warn("Unable to forward to '" + jspPath + "' as response already committed.");
            }
        }
        else
        {
            // otherwise, we'll do an include
            dispatcher.include(request, response);
        }
    }
}

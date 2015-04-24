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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.DependencyResource;
import org.springframework.extensions.surf.DojoDependencyHandler;
import org.springframework.extensions.surf.ServletUtil;

/**
 * <p>Overrides the {@link VirtualizedResourceController} (which in itself overrides the WebScript 
 * {@link org.springframework.extensions.webscripts.servlet.mvc.ResourceController} to use the 
 * {@link DependencyHandler} to process requests for resources that contain content based checksums.</p> 
 * @author David Draper
 */
public class ResourceController extends VirtualizedResourceController
{
    public static final String HTTP_HEADER_EXPIRES = "Expires";
    public static final String HTTP_HEADER_FAR_FUTURE_EXPIRES_VALUE = "Sun, 17-Jan-2038 19:14:07 GMT";
    
    /**
     * <p>A {@link DependencyHandler} is used to lookup resource dependencies including those that are 
     * referenced with a checksum based on their contents. This variable should be set by the Spring
     * application context configuration.</p>
     */
    private DependencyHandler dependencyHandler;

    /**
     * <p>Sets the {@link DependencyHandler} to be used when looking up resources.</p>
     * 
     * @param dependencyHandler
     */
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    
    private DependencyAggregator dependencyAggregator;
    
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
    
    private DojoDependencyHandler dojoDependencyHandler;

    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }

    /**
     * <p>The {@link WebFrameworkConfigElement} is used to determine whether or not preview is enabled
     * as this will determine how resources should be looked up.</p>
     */
    private WebFrameworkConfigElement webframeworkConfigElement;

    public void setWebframeworkConfigElement(WebFrameworkConfigElement webframeworkConfigElement)
    {
        this.webframeworkConfigElement = webframeworkConfigElement;
    }

    /**
     * <p>Overrides the default method to use the {@link DependencyHandler} to find resources before
     * falling back on the default WebScript library implementation.</p>
     */
    @Override
    public boolean dispatchResource(String path,
                                    HttpServletRequest request,
                                    HttpServletResponse response) throws ServletException, IOException
    {
        boolean resolved = false;
        
        if (!isAllowedResourcePath(path))
        {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            return true;
        }
        
        // Check the VirtualizedResourceController first for backwards compatibility with 
        // applications built for previous Surf verions...
        if (this.webframeworkConfigElement.isPreviewEnabled())
        {
            // see if we can resolve and serve back the remote resource
            String endpointId = (String) request.getParameter("e");
            String storeId = (String) request.getParameter("s");
            String webappId = (String) request.getParameter("w");
            resolved = retrieveRemoteResource(request, response, path, endpointId, storeId, webappId);
        }
        
        // ...check the DependencyAggregator...
        DependencyResource resource = this.dependencyAggregator.getCachedDependencyResource(path);
        if (resource != null)
        {
            // TODO: Need to handle IE6/7/8 lack of support for CSS data images.
            byte[] bytes = resource.getContent().getBytes(this.dependencyHandler.getCharset());
            applyHeaders(path, response, bytes.length, 0L);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            copyStream(in, response.getOutputStream());
            resolved = true;
        }
        
        if (!resolved && this.webframeworkConfigElement.isDojoEnabled())
        {
            // ...check the Dojo Dependency handler (if enabled in the Surf configuration)...
            String aggregatedDojoResource = this.dojoDependencyHandler.getCachedResource(path);
            if (aggregatedDojoResource != null)
            {
                byte[] bytes = aggregatedDojoResource.getBytes(this.dependencyHandler.getCharset());
                applyHeaders(path, response, bytes.length, 0L);
                ByteArrayInputStream in = new ByteArrayInputStream(bytes);
                copyStream(in, response.getOutputStream());
                resolved = true;
            }
        }

        // ...now check the DependencyHandler...
        if (!resolved)
        {
            InputStream in = null;
            Float ieVersion = ServletUtil.getInternetExplorerVersion(request);
            if (ieVersion != null && ieVersion < 8)
            {
                // The request has come from a version of IE less than 8, this means that we cannot
                // support CSS data images. Therefore we need to ensure that they are not generated.
                // The DataHandler may have previously generated and cached resources that contain
                // data images so we need to ensure that they are NOT used. Therefore we need to obtain
                // the unmodified InputStream to ensure that no modifications will have occurred.
                in = this.dependencyHandler.getUnmodifiedResourceInputStream(path);
            }
            else
            {
                // For IE version 8 onwards (and all other browsers) get the regular input stream...
                in = this.dependencyHandler.getResourceInputStream(path);
            }
            
            if (in != null)
            {
                applyHeaders(path, response, in.available(), 0L);
                copyStream(in, response.getOutputStream());
                resolved = true;
            }
        }
        
        // ...Finally drop through again to the VirtualizedResourceController (this is ultimately
        // not the most efficient implementation but should satisfy most cases.
        if (!resolved)
        {
            resolved = super.dispatchResource(path, request, response);
        }
        return resolved;
    }
    
    public String getPath(String sourcePath, String dependencyPath)
    {
        String pathPrefix = sourcePath.substring(0, sourcePath.lastIndexOf(FORWARD_SLASH));
        
        // Remove opening and closing quotes...
        if (dependencyPath.startsWith(DOUBLE_QUOTES) || dependencyPath.startsWith(SINGLE_QUOTE))
        {
            dependencyPath = dependencyPath.substring(1);
        }
        if (dependencyPath.endsWith(DOUBLE_QUOTES) || dependencyPath.endsWith(SINGLE_QUOTE))
        {
            dependencyPath = dependencyPath.substring(0, dependencyPath.length() -1);
        }
        
        // Clear any pointless current location markers...
        if (dependencyPath.startsWith(FULL_STOP) && !dependencyPath.startsWith(DOUBLE_FULL_STOP))
        {
            dependencyPath = dependencyPath.substring(1);
        }
        
        StringBuilder sb = new StringBuilder(pathPrefix);
        if (!dependencyPath.startsWith(FORWARD_SLASH))
        {
            sb.append(FORWARD_SLASH);
        }
        sb.append(dependencyPath);
        return sb.toString();
    }
    
    /**
     * Checks whether resource path provided is allowed by web framework configuration. 
     * 
     * @param pathToCheck resource path to check
     * @return <code>true</code> if path is allowed to be viewed, otherwise <code>false</code>
     */
    public boolean isAllowedResourcePath(String pathToCheck)
    {
        if (!pathToCheck.startsWith(FORWARD_SLASH))
        {
            pathToCheck = FORWARD_SLASH + pathToCheck;
        }
        for (Pattern pattern : this.webframeworkConfigElement.getResourcesDeniedPaths())
        {
            Matcher matcher = pattern.matcher(pathToCheck);
            if (matcher.matches())
            {
                // this path is configured as denied
                return false;
            }
        }
        return true;
    }
    
    /**
     * <p>Constant for the forward slash "/"</p>
     */
    public static final String FORWARD_SLASH = "/";
    
    /**
     * <p>Constant for the full stop "." (or period as it is known in the US). In this context it is used to indicate the current location
     * in paths.</p>
     */
    public static final String FULL_STOP = ".";
    
    /**
     * <p>Constant for double full stop ".." (or period as it is known in the US). In this context it is used to indicate the part folder of 
     * the current location.</p>
     */
    public static final String DOUBLE_FULL_STOP = "..";
    
    /**
     * <p>Constant for the double quote '"'.</p>
     */
    public static final String DOUBLE_QUOTES = "\"";
    
    /**
     * <p>Constant for the single quote "'"</p>
     */
    public static final String SINGLE_QUOTE = "'";
}

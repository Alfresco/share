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
package org.springframework.extensions.directives;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.AbstractExtensibilityDirective;
import org.springframework.extensions.surf.mvc.ResourceController;

/**
 * <p>An abstract directive implementation that provides additional methods for handling dependencies. These
 * methods explicitly deal with manipulating dependency URLs to take application context and servlet context
 * mappings into consideration.</p> 
 * 
 * @author David Draper
 */
public abstract class AbstractDependencyExtensibilityDirective extends AbstractExtensibilityDirective
{
    public AbstractDependencyExtensibilityDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }

    /**
     * <p>The default resource controller mapping. This is the one that is used by Alfresco Share 
     * out of the box.</p>
     */
    private static final String RES = "/res/";
    
    /**
     * <p>Returns a String that is used to map requests to the {@link ResourceController}. This defaults to
     * "/res/" but has been abstracted to a method so that this can be overridden.</p>
     * @return
     */
    protected String getResourceControllerMapping()
    {
        return RES;
    }
    
    private WebFrameworkConfigElement webFrameworkConfig;
    
    public WebFrameworkConfigElement getWebFrameworkConfig()
    {
        return webFrameworkConfig;
    }

    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }

    /**
     * <p>A {@link DependencyHandler} is used to handle locating and producing checksums for resource files. This variable
     * should be set through the Spring application context.</p>
     */
    protected DependencyHandler dependencyHandler;
    
    /**
     * <p>Sets the {@link DependencyHandler}</p>
     * 
     * @param dependencyHandler
     */
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    
    protected DependencyAggregator dependencyAggregator;
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }

    /**
     * <p>{@link RequestContext} instances stored per thread. 
     */
    private RequestContext requestContext;;
    
    /**
     * <p>Sets the {@link RequestContext} for the current thread.</p>
     * @param requestContext
     */
    public void setRequestContext(RequestContext requestContext)
    {
        this.requestContext = requestContext;
    }
 
    /**
     * <p>Retrieves the {@link RequestContext} for the current thread.</p>
     * @return
     */
    public RequestContext getRequestContext()
    {
        return this.requestContext;
    }
    
    /**
     * The {@link ModelObject} currently being processed by the current thread.</p>
     */
    private ModelObject modelObject;;
    
    /**
     * <p>Sets the {@link ModelObject} for the current thread.</p>
     * @param object
     */
    public void setModelObject(ModelObject object)
    {
        this.modelObject = object;
    }
    
    /**
     * <p>Gets the {@linkm ModelObject} for the current thread.</p>
     * @return
     */
    public ModelObject getModelObject()
    {
        return this.modelObject;
    }

    /**
     * <p>Examines the supplied dependency source and returns a {@link ProcessedDependency} instance
     * containing the updated source (to be passed to the {@link DependencyHandler} as well as the
     * prefix that should be added back to the updated source when outputting the source in the 
     * HTML element.</p>
     * @param src
     * @return
     */
    public ProcessedDependency processDependency(String src)
    {
        // This directive has been implemented with the intention of replacing the <@script> macro defined
        // in the Alfresco Share application. In order to minimise the impact of introducing this directive
        // the following section of code attempts to handle the use of "~/res". Ideally this directive should 
        // be used without the application context, servlet mapping, controller mapping or URL rewrite mapped 
        // path - but the following code will compensate for it being used...
        String toInsert = "";
        int resIndex = src.indexOf(getResourceControllerMapping());
        if (resIndex != -1)
        {
            String toRemove = src.substring(0, resIndex + getResourceControllerMapping().length());
            src = src.substring(toRemove.length());
            toInsert = toRemove;
            if (src.startsWith("/"))
            {
                src = src.substring(1);
            }
        }
        return new ProcessedDependency(src, toInsert);
    }
    
    /**
     * <p>Retrieves the modified source URL from the {@link ProcessedDependency} object. This method simply
     * wraps that provided by {@link ProcessedDependency} but does so to allow overrides to customise behaviour.</p>
     * @param pd
     * @return
     */
    protected String getUpdatedSrc(ProcessedDependency pd)
    {
        return pd.getUpdatedSrc();
    }
    
    /**
     * <p>Retrieves the prefix to insert of the URL from the {@link ProcessedDependency} object. This method simply
     * wraps that provided by {@link ProcessedDependency} but does so to allow overrides to customise behaviour.</p>
     * @param pd
     * @return
     */
    protected String getToInsert(ProcessedDependency pd)
    {
        return pd.getToInsert();
    }
    
    /**
     * <p>Inner class that is provided to allow explicit processed dependency information to be passed. This
     * The <code>processDependency</code> method could have just returned a String array but this would have
     * been prone to error. Whilst more verbose this at least leaves to margin for misunderstanding and 
     * reduces potential errors.</p> 
     * @author David Draper
     */
    public class ProcessedDependency
    {
        private String updatedSrc = "";
        private String toInsert = "";
        protected ProcessedDependency(String updatedSrc, String toInsert)
        {
            this.updatedSrc = updatedSrc;
            this.toInsert = toInsert;
        }
        public String getUpdatedSrc()
        {
            return updatedSrc;
        }
        public String getToInsert()
        {
            return toInsert;
        }
        @Override
        public String toString()
        {
            return "ProcessedDependency [updatedSrc=" + updatedSrc + ", toInsert=" + toInsert + "]";
        }
    }
}

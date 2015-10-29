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

import java.io.IOException;

import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentSourceModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

public class DependencyDirectiveData extends DefaultExtensibilityDirectiveData
{
    public DependencyDirectiveData(String id, 
                                   String action, 
                                   String target, 
                                   String directiveName,
                                   TemplateDirectiveBody body, 
                                   Environment env,
                                   String dependency,
                                   String group,
                                   boolean aggregate,
                                   DeferredContentTargetModelElement targetElement)
    {
        super(id, action, target, directiveName, body, env);
        this.dependency = dependency;
        this.group = group;
        this.aggregate = aggregate;
        this.targetElement = targetElement;
    }

    /**
     * <p>The dependency requested by the directive.</p>
     */
    protected String dependency;
    
    /**
     * <p>The group that the dependency should be added to (as requested by the directive)</p>
     */
    protected String group;
    
    /**
     * <p>This is the {@link DeferredContentSourceModelElement} that will get created when the <code>createContentModelElement</code>
     * method is called. A reference to it is required so that when the <code>render</code> method is called it can be passed to its
     * associated {@link DeferredContentTargetModelElement} instance.</p>
     */
    protected DeferredContentSourceModelElement modelElement;
    
    /**
     * <p>This is required for backwards compatibility to support WebScripts that are still using *.head.ftl files to add dependencies.
     * When a "head" file is processed the main model (and therefore any target {@link DeferredContentTargetModelElement}) will not yet
     * exist. In order to ensure that the dependency is not lost we need to add the dependency via this {@link RequestContext}.</p>
     */
    protected RequestContext context;
    
    /**
     * Indicates whether or not to aggregate the dependency.
     */
    protected boolean aggregate = false;
    
    /**
     * <p>The {@link DeferredContentTargetModelElement} to associate any created {@link DeferredContentSourceModelElement} with.</p>
     */
    protected DeferredContentTargetModelElement targetElement;
    
    @Override
    public ContentModelElement createContentModelElement()
    {
        this.modelElement = new DependencyDeferredContentSourceModelElement(getId(), getDirectiveName(), this.dependency, this.group, this.aggregate, this.targetElement); 
        return this.modelElement; 
    }

    @Override
    public void render(ModelWriter writer) throws TemplateException, IOException
    {
        // This will get called during merge, before, after and replace operations...
        // This is when we need to find the targeted DeferredContentTargetModelElement and register ourselves
        // as content to be included...
        if (this.targetElement != null)
        {
            this.targetElement.registerDeferredSourceElement(this.modelElement);
        }
    }
}

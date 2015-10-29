/*
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

import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.impl.ModelWriter;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

public class CssDependencyDirectiveData extends DependencyDirectiveData
{
    public CssDependencyDirectiveData(String id, 
                                      String action, 
                                      String target, 
                                      String directiveName,
                                      TemplateDirectiveBody body, 
                                      Environment env, 
                                      String dependency, 
                                      String group,
                                      String media,
                                      boolean aggregate,
                                      DeferredContentTargetModelElement targetElement)
    {
        super(id, action, target, directiveName, body, env, dependency, group, aggregate, targetElement);
        this.media = media;
    }

    /**
     * <p>The media type specified for the CSS dependency request.</p>
     */
    private String media;
    
    
    @Override
    public ContentModelElement createContentModelElement()
    {
        this.modelElement = new CssDependencyContentModelElement(getId(), getDirectiveName(), this.dependency, this.group, this.media, this.aggregate, this.targetElement); 
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
        else
        {
            this.context.addCssDependency(this.dependency, this.media);
        }
    }
}

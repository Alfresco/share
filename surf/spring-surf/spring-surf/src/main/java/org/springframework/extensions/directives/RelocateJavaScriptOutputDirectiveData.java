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

import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.impl.DiscardUnboundContentModelElementImpl;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;

/**
 * <p>This is referenced by the {@link RelocateJavaScriptOutputDirective} but instead of providing its own
 * {@link ContentModelElement} instance for output data to be written to, it uses an existing 
 * {@link OutputJavaScriptContentModelElement} so that the existing JavaScript content is moved to the location
 * in the model where the {@link RelocateJavaScriptOutputDirective} has been used.</p>
 * 
 * @author David Draper
 */
public class RelocateJavaScriptOutputDirectiveData extends DefaultExtensibilityDirectiveData
{
    public RelocateJavaScriptOutputDirectiveData(String id,
                                                 String action, 
                                                 String target, 
                                                 TemplateDirectiveBody body, 
                                                 Environment env,
                                                 OutputJavaScriptContentModelElement javaScriptContent)
    {
        super(id, action, target, "javascript", body, env);
        this.javaScriptContent = javaScriptContent;
    }
    
    /**
     * <p>A reference to the {@link OutputJavaScriptContentModelElement} that should be relocated. This will be
     * set by the constructor.</p>
     */
    private OutputJavaScriptContentModelElement javaScriptContent;
    
    /**
     * <p>Overrides the default implementation to return a <code>JavaScriptContentModelElement</code>
     * which will outputs an instructions to instantiate a widget with merged configuration data.</p>
     */
    @Override
    public ContentModelElement createContentModelElement()
    {
        ContentModelElement content = null;
        if (this.javaScriptContent != null)
        {
            content = this.javaScriptContent;
        }
        else
        {
            // If the JavaScript content model element was not supplied then replace it with a 
            // and element that gets ignored when the model is flushed to the output stream.
            content = new DiscardUnboundContentModelElementImpl();
        }
        return content;
    }
}

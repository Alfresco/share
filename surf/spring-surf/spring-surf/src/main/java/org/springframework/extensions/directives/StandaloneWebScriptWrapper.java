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

import java.util.Map;

import org.springframework.extensions.surf.extensibility.CloseModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.OpenModelElement;
import org.springframework.extensions.surf.extensibility.impl.CloseModelElementImpl;
import org.springframework.extensions.surf.extensibility.impl.OpenModelElementImpl;
import org.springframework.extensions.surf.types.Page;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>This FreeMarker Directive can be used as the outer-wrapper in a WebScript FreeMarker template to add the the 
 * {@link OutputCSSContentModelElement} and {@link OutputJavaScriptContentModelElement} instances into the {@link ExtensibilityModel}
 * when the WebScript is not processed within the context of a {@link Page}. This ensures that any dependency files are 
 * loaded into the page.</p>
 * <p>TODO: Currently this only outputs the JS and CSS deferred content model elements. This could be further enhanced to add
 * additional content elements that set up the structure of a page</p> 
 * 
 * @author David Draper
 */
public class StandaloneWebScriptWrapper extends AbstractDependencyExtensibilityDirective
{
    public StandaloneWebScriptWrapper(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public ExtensibilityDirectiveData createExtensibilityDirectiveData(String id, 
                                                                       String action, 
                                                                       String target,
                                                                       Map params, 
                                                                       TemplateDirectiveBody body, 
                                                                       Environment env) throws TemplateException
    {
        if (getRequestContext().getPage() == null)
        {
            // Add the JavaScript target content model element...
            OpenModelElement jsOpen = new OpenModelElementImpl(OutputJavaScriptDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                               OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME);
            OutputJavaScriptContentModelElement jsTarget = 
                new OutputJavaScriptContentModelElement(id, 
                                                        OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME, 
                                                        this.dependencyAggregator, 
                                                        getWebFrameworkConfig());
            CloseModelElement jsClose = new CloseModelElementImpl(OutputJavaScriptDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                                  OutputJavaScriptDirective.OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME);
            this.getModel().insertDeferredContentTarget(1, jsOpen, jsTarget, jsClose);
            
            // Add the CSS target content model element...
            OpenModelElement cssOpen = new OpenModelElementImpl(OutputCSSDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                                OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME);
            OutputCSSContentModelElement cssTarget = 
                new OutputCSSContentModelElement(id, 
                                                 OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME, 
                                                 this.dependencyAggregator);
            CloseModelElement cssClose = new CloseModelElementImpl(OutputCSSDirective.OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                                   OutputCSSDirective.OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME);
            this.getModel().insertDeferredContentTarget(1, cssOpen, cssTarget, cssClose);
        }
        return super.createExtensibilityDirectiveData(id, action, target, params, body, env);
    }
}

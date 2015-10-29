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
import java.util.Map;

import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>Directive for outputting JavaScript into the FreeMarker template. This directive acts as a placeholder in
 * the extensibility model for other directives (such as {@link JavaScriptDependencyDirective} to use to request
 * resources to be included earlier in the template. This directive should typically be placed in the <{@code}head>
 * element of the page and the {@link RelocateJavaScriptOutputDirective} can then be used to move all JavaScript to
 * the end of the HTML page if required.</p>
 * @author David Draper
 */
public class OutputJavaScriptDirective extends AbstractDependencyExtensibilityDirective
{
    public static final String OUTPUT_DEPENDENCY_DIRECTIVE_ID = "outputJavaScriptDirective";
    public static final String OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME = "outputJavaScript";
    
    public OutputJavaScriptDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env,
                        Map params,
                        TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        ExtensibilityDirectiveData directiveData = new OutputJavaScriptDirectiveData(OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                                                     null, 
                                                                                     null, 
                                                                                     OUTPUT_JS_DEPENDENCIES_DIRECTIVE_NAME, 
                                                                                     params, 
                                                                                     body, 
                                                                                     env,
                                                                                     this.dependencyAggregator,
                                                                                     getWebFrameworkConfig());
        getModel().merge(directiveData);
    }
}

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

import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityDirectiveData;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>This directive is used for outputting the CSS dependencies that have been requested by other directives onto the 
 * output stream. This directive relies on a {@link DeferredContentTargetModelElement} approach to add a place holder into the
 * {@link ExtensibilityModel} which can then be located and added to by subsequently processed directives.</p>
 * 
 * @author David Draper
 */
public class OutputCSSDirective extends AbstractDependencyExtensibilityDirective
{
    public static final String OUTPUT_DEPENDENCY_DIRECTIVE_ID = "outputCSSDependenciesDirective";
    public static final String OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME = "outputCSS";
    
    public OutputCSSDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }

    private DependencyAggregator dependencyAggregator;
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public void execute(Environment env,
                        Map params,
                        TemplateModel[] loopVars,
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        ExtensibilityDirectiveData directiveData = new OutputCSSDirectiveData(OUTPUT_DEPENDENCY_DIRECTIVE_ID, 
                                                                              null, 
                                                                              null, 
                                                                              OUTPUT_CSS_DEPENDENCIES_DIRECTIVE_NAME, 
                                                                              params, 
                                                                              body, 
                                                                              env,
                                                                              dependencyAggregator);
        getModel().merge(directiveData);
    }
}

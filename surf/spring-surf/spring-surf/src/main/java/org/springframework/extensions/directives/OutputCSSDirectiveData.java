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

import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.DeferredContentTargetModelElement;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.extensibility.impl.DefaultContentModelElement;
import org.springframework.extensions.surf.extensibility.impl.DefaultExtensibilityDirectiveData;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;

/**
 * <p>This directive is provided to allow nested model directives to update the model outside of the
 * scope that they have been used in. Specifically this has been provided as a means of allowing WebScripts
 * to add JavaScript and CSS dependencies into the <@{code}head> element of the HTML page. This directive
 * should be placed within the <{@code}head> element in the HTML page and the {@link DefaultContentModelElement}
 * it adds to the {@link ExtensibilityModel} will be updated through the use of the following directives:
 * <ul>
 * <li>{@link AggregateCssDependencyDirective}</li>
 * <li>{@link AggregateIeCssDependencyDirective}</li>
 * <li>{@link AggregateJavaScriptDependencyDirective}</li>
 * </ul>
 * </p>
 * 
 * @author David Draper
 */
public class OutputCSSDirectiveData extends DefaultExtensibilityDirectiveData
{
    private DependencyAggregator dependencyAggregator;
    public OutputCSSDirectiveData(String id, 
                                  String action, 
                                  String target,
                                  String directiveName,
                                  Map<String, Object> params,
                                  TemplateDirectiveBody body, 
                                  Environment env, 
                                  DependencyAggregator dependencyAggregator) throws TemplateException
    {
        super(id, action, target, directiveName, body, env);
        this.dependencyAggregator = dependencyAggregator;
    }

    /**
     * <p>Creates and returns a {@link OutputCSSContentModelElement} which is a type 
     * of {@link DeferredContentTargetModelElement}. This will be added into the {@link ExtensibilityModel}
     * and will allow other directives to add content to it - specifically they will be able to add
     * new JavaScript and CSS dependencies.</p>
     */
    @Override
    public ContentModelElement createContentModelElement()
    {
        return new OutputCSSContentModelElement(getId(),
                                                getDirectiveName(),
                                                this.dependencyAggregator);
    }
}

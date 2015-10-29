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
package org.springframework.extensions.surf.types;

import java.util.List;

import org.dom4j.Element;

/**
 * <p>Extends {@link Component} to provide some additional extensibility function. Overriding the default Spring Surf configuration
 * for the Component type to use an implementation of this interface will enable all existing configured components to be treated
 * as AdvancedComponents.</p>
 * <p>An AdvancedComponent is a Component that contains elements that can be individually rendered. This means that extending modules
 * can add additional renderable elements into them, or update the evaluators that determine the WebScript that renders them.</p>
 * 
 * @author David Draper
 */
public interface AdvancedComponent extends Component
{
    public static final String SUB_COMPONENTS = "sub-components";
    public static final String SUB_COMPONENT = "sub-component";
    public static final String COMPONENT = "component";
    public static final String INDEX = "index";
    public static final String ID = "id";
    public static final String EVALUATIONS = "evaluations";
    public static final String EVALUATION = "evaluation";
    public static final String EVALUATORS = "evaluators";
    public static final String EVALUATOR = "evaluator";
    public static final String NEGATE = "negate";
    public static final String URI = "uri";
    public static final String URL = "url";
    public static final String TYPE = "type";
    public static final String PARAMS = "params";
    public static final String PARAM = "param";
    public static final String NAME = "name";
    public static final String VALUE = "value";
    public static final String RENDER_IF_EVALUATED = "render";
    public static final String PROPERTIES = "properties";
    
    /**
     * @return A {@link List} of {@link SubComponent} instances directly configured (i.e. not defined in an {@link ExtensionModule}).
     */
    public List<SubComponent> getSubComponents();
    
    public void setSubComponents(List<SubComponent> renderableElements);
    
    public boolean isAdvancedConfig();
    
    public void applyConfig(Element componentEl);
}

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

import freemarker.template.TemplateBooleanModel;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import freemarker.template.TemplateScalarModel;

public class DirectiveUtils
{
    /**
     * <p>Attempts to retrieve a String property from the map of parameters supplied using the key
     * provided.
     * <ul>
     * <li>If a property is found but it is not a String then an exception will be thrown.</li>
     * <li>If a property is not found and not required then <code>null</code> will be returned.</li>
     * <li>If a property is not found and is required then an exception will be thrown</li>
     * </ul>
     * </p>
     *   
     * @param params A map of parameters (this is supplied to the <code>execute</code> method that must
     * be implemented by subclasses.
     * @param targetProp The name of the property to attempt to retrieve.
     * @param required Indicates whether or not the property is required for the directive to process successfully.
     * @return A String value for the property name supplied (or <code>null</code> if it could not be found)
     * @throws TemplateModelException When a property matching the supplied name is found but is not a String.
     */
    public static String getStringProperty(Map<String, Object> params, 
                                           String targetProp,
                                           String directiveName,
                                           boolean required) throws TemplateModelException
    {
        String str = null;
        TemplateModel value = (TemplateModel)params.get(targetProp);
        if (value != null)
        {
            if (value instanceof TemplateScalarModel)
            {
                str = ((TemplateScalarModel)value).getAsString();
            }
            else
            {
                throw new TemplateModelException("The \"" + targetProp + "\" parameter to the \"" + directiveName + "\" directive must be a string.");                
            }
        }
        else if (required)
        {
            throw new TemplateModelException("The \"" + targetProp + "\" parameter to the \"" + directiveName + "\" directive must be provided.");
        }
        return str;
    }
    
    /**
     * <p>Attempts to retrieve a boolean property from the map of parameters supplied using the key
     * provided.
     * <ul>
     * <li>If a property is found but it is not a boolean then an exception will be thrown.</li>
     * <li>If a property is not found and not required then <code>false</code> will be returned.</li>
     * <li>If a property is not found and is required then an exception will be thrown</li>
     * </ul>
     * </p>   
     * @param params A map of parameters (this is supplied to the <code>execute</code> method that must
     * be implemented by subclasses.
     * @param targetProp The name of the property to attempt to retrieve.
     * @param required Indicates whether or not the property is required for the directive to process successfully.
     * @return A boolean value for the property name supplied (or <code>false</code> if it could not be found)
     * @throws TemplateModelException When a property matching the supplied name is found but is not a boolean.
     */
    public static boolean getBooleanProperty(Map<String, Object> params, 
                                             String targetProp, 
                                             String directiveName,
                                             boolean required)  throws TemplateModelException
    {
        boolean bool = false;
        TemplateModel value = (TemplateModel)params.get(targetProp);
        if (value != null)
        {
           if (value instanceof TemplateBooleanModel)
           {
               bool = ((TemplateBooleanModel)value).getAsBoolean();
           }
           else if (value instanceof TemplateScalarModel)
           {
               bool = Boolean.parseBoolean(((TemplateScalarModel) value).getAsString());
           }
           else
           {
               throw new TemplateModelException("The \"" + targetProp + "\" parameter to the \"" + directiveName + "\" directive must be a boolean.");
           }
        }
        else if (required)
        {
            throw new TemplateModelException("The \"" + targetProp + "\" parameter to the \"" + directiveName + "\" directive must be provided.");
        }
        return bool;
    }
}

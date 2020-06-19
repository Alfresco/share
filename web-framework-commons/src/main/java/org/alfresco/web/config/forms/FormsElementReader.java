/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.web.config.forms;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;forms&gt; (plural) elements. &lt;form&gt; (singular) elements have their own
 * reader class which is delegated to by this one.
 * 
 * @author Neil McErlean.
 * @see ConstraintHandlersElementReader
 * @see DefaultControlsElementReader
 * @see DependenciesElementReader
 * @see FormElementReader
 */
public class FormsElementReader implements ConfigElementReader
{
    public static final String ATTR_NAME_ID = "id";
    public static final String ELEMENT_FORMS = "forms";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element formsElement)
    {
        FormsConfigElement result = null;
        if (formsElement == null)
        {
            return null;
        }

        String name = formsElement.getName();
        if (!name.equals(ELEMENT_FORMS))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_FORMS
                    + " elements, the element passed was '" + name + "'");
        }

        result = new FormsConfigElement();
        
        // Go through each of the <form> tags under <forms>
        for (Object obj : formsElement.selectNodes("./form")) {
            Element formElement = (Element)obj;
            
            FormElementReader formReader = new FormElementReader();
            FormConfigElement form = (FormConfigElement)formReader.parse(formElement);

            if (form.getId() == null)
            {
                result.setDefaultForm(form);
            }
            else
            {
                result.addFormById(form, form.getId());
            }
        }
        
        // Go through each of the <default-controls> tags under <forms>
        for (Object obj : formsElement.selectNodes("./default-controls")) {
            Element defltCtrlsElement = (Element)obj;
            
            DefaultControlsElementReader defltCtrlsReader = new DefaultControlsElementReader();
            DefaultControlsConfigElement defltCtrlsCE = (DefaultControlsConfigElement)defltCtrlsReader.parse(defltCtrlsElement);

            result.setDefaultControls(defltCtrlsCE);
        }
        
        // Go through each of the <constraint-handlers> tags under <forms>
        for (Object obj : formsElement.selectNodes("./constraint-handlers")) {
            Element constraintHandlersElement = (Element)obj;
            
            ConstraintHandlersElementReader constraintHandlersReader = new ConstraintHandlersElementReader();
            ConstraintHandlersConfigElement constraintHandlersCE = (ConstraintHandlersConfigElement)constraintHandlersReader.parse(constraintHandlersElement);

            result.setConstraintHandlers(constraintHandlersCE);
        }

        // Go through each of the <dependencies> tags under <forms>
        for (Object obj : formsElement.selectNodes("./dependencies")) {
            Element depsElement = (Element)obj;
            
            DependenciesElementReader depsReader = new DependenciesElementReader();
            DependenciesConfigElement depsCE = (DependenciesConfigElement)depsReader.parse(depsElement);

            result.setDependencies(depsCE);
        }
        
        return result;
    }
}

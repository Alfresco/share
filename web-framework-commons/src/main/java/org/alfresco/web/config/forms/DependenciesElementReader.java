/*
 * #%L
 * Alfresco Web Framework common libraries
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;dependencies&gt; elements.
 * 
 * @author Neil McErlean.
 */
public class DependenciesElementReader implements ConfigElementReader
{
    public static final String ELEMENT_DEPENDENCIES = "dependencies";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element dependenciesElem)
    {
        DependenciesConfigElement result = null;
        if (dependenciesElem == null)
        {
            return null;
        }

        String name = dependenciesElem.getName();
        if (!name.equals(ELEMENT_DEPENDENCIES))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_DEPENDENCIES
                    + " elements, the element passed was '" + name + "'");
        }

        result = new DependenciesConfigElement();

        List<String> cssDependencies = getSrcDependencies(dependenciesElem, "./css");
        List<String> jsDependencies = getSrcDependencies(dependenciesElem, "./js");

        result.addCssDependencies(cssDependencies);
        result.addJsDependencies(jsDependencies);

        return result;
    }

    /**
     * This method takes the specified xml node, finds children matching the specified
     * xpath expression and returns a List<String> containing the values of the "src"
     * attribute on each of those child nodes.
     * 
     * @param typeNode Element
     * @param xpathExpression String
     * @return List<String>
     */
    @SuppressWarnings("unchecked")
    private List<String> getSrcDependencies(Element typeNode, final String xpathExpression)
    {
        List<String> result = new ArrayList<String>();
        
        for (Object cssObj : typeNode.selectNodes(xpathExpression))
        {
            Element cssElem = (Element)cssObj;
            List<Attribute> cssAttributes = cssElem.selectNodes("./@*");
            for (Attribute nextAttr : cssAttributes)
            {
                String nextAttrName = nextAttr.getName();
                if (nextAttrName.equals("src"))
                {
                    String nextAttrValue = nextAttr.getValue();
                    result.add(nextAttrValue);
                }
                // Ignore attributes not called "src".
            }
        }
        
        return result;
    }
}

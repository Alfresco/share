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

import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents &lt;dependencies&gt; values for the
 * client.
 * 
 * @author Neil McErlean.
 */
public class DependenciesConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = -8573715101320883067L;

    public static final String CONFIG_ELEMENT_ID = "dependencies";
    private final List<String> cssDependencies = new ArrayList<String>();
    private final List<String> jsDependencies = new ArrayList<String>();

    /**
     * This constructor creates an instance with the default name.
     */
    public DependenciesConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public DependenciesConfigElement(String name)
    {
        super(name);
    }
    
    /**
     * This method returns the css dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return String[]
     */
    public String[] getCss()
    {
        if (this.cssDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.cssDependencies.toArray(new String[0]);
        }
    }
    
    /**
     * This method returns the JavaScript dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return String[]
     */
    public String[] getJs()
    {
        if (this.jsDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.jsDependencies.toArray(new String[0]);
        }
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the default-controls config via the generic interfaces is not supported");
    }

    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        if (configElement == null)
        {
            return this;
        }

        DependenciesConfigElement otherDepsElement = (DependenciesConfigElement) configElement;
        DependenciesConfigElement result = new DependenciesConfigElement();

        // combine all the dependencies
        if (this.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(this.cssDependencies);
        }
        
        if (otherDepsElement.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(otherDepsElement.cssDependencies);
        }

        if (this.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(this.jsDependencies);
        }
        
        if (otherDepsElement.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(otherDepsElement.jsDependencies);
        }

        return result;
    }

    void addCssDependencies(List<String> cssDeps)
    {
        if (cssDeps == null)
        {
            return;
        }
        this.cssDependencies.addAll(cssDeps);
    }

    void addJsDependencies(List<String> jsDeps)
    {
        if (jsDeps == null)
        {
            return;
        }
        this.jsDependencies.addAll(jsDeps);
    }
}

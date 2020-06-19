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
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents &lt;default-controls&gt; values for the
 * client.
 * 
 * @author Neil McErlean.
 */
public class DefaultControlsConfigElement extends ConfigElementAdapter
{
    public static final String CONFIG_ELEMENT_ID = "default-controls";
    private static final long serialVersionUID = -6758804774427314050L;

    private final Map<String, Control> datatypeDefCtrlMappings = new LinkedHashMap<String, Control>();

    /**
     * This constructor creates an instance with the default name.
     */
    public DefaultControlsConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public DefaultControlsConfigElement(String name)
    {
        super(name);
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
        // There is an assumption here that it is only like-with-like combinations
        // that are allowed. i.e. Only an instance of a DefaultControlsConfigElement
        // can be combined with this.
        DefaultControlsConfigElement otherDCCElement = (DefaultControlsConfigElement) configElement;

        DefaultControlsConfigElement result = new DefaultControlsConfigElement();

        for (String nextDataType : datatypeDefCtrlMappings.keySet())
        {
            String nextTemplate = getTemplateFor(nextDataType);
            Control nextDefaultControls = datatypeDefCtrlMappings
                    .get(nextDataType);
            List<ControlParam> nextControlParams = null;
            if (nextDefaultControls != null)
            {
                nextControlParams = nextDefaultControls.getParamsAsList();
            }
            
            result.addDataMapping(nextDataType, nextTemplate,
                            nextControlParams);
        }

        for (String nextDataType : otherDCCElement.datatypeDefCtrlMappings.keySet())
        {
            String nextTemplate = otherDCCElement.getTemplateFor(nextDataType);
            Control nextDefaultControls = otherDCCElement.datatypeDefCtrlMappings
                    .get(nextDataType);
            List<ControlParam> nextControlParams = null;
            if (nextDefaultControls != null)
            {
                nextControlParams = nextDefaultControls.getParamsAsList();
            }
            
            result.addDataMapping(nextDataType, nextTemplate,
                            nextControlParams);
        }

        return result;
    }

    /* package */void addDataMapping(String dataType, String template,
            List<ControlParam> parameters)
    {
        if (parameters == null)
        {
            parameters = Collections.emptyList();
        }
        Control newControl = new Control(template);
        for (ControlParam p : parameters)
        {
            newControl.addControlParam(p);
        }
        this.datatypeDefCtrlMappings.put(dataType, newControl);
    }

    public String[] getItemNames()
    {
        return this.getItemNamesAsList().toArray(new String[0]);
    }
    
    public List<String> getItemNamesAsList()
    {
        Set<String> result = datatypeDefCtrlMappings.keySet();
        List<String> resultList = new ArrayList<String>(result);
    	return Collections.unmodifiableList(resultList);
    }
    
    // This is fine.
    public Map<String, Control> getItems()
    {
    	return Collections.unmodifiableMap(datatypeDefCtrlMappings);
    }

    /**
     * This method returns a String representing the path of the template associated
     * with the given dataType.
     * 
     * @param dataType the dataType for which a template is required.
     * @return the path of the associated template. <code>null</code> if the specified
     *         dataType is <code>null</code> or if there is no registered template.
     */
    public String getTemplateFor(String dataType)
    {
        Control ctrl = datatypeDefCtrlMappings.get(dataType);
        if (ctrl == null)
        {
            return null;
        }
        else
        {
            return ctrl.getTemplate();
        }
    }

    public ControlParam[] getControlParamsFor(String dataType)
    {
        return this.getControlParamsAsListFor(dataType).toArray(new ControlParam[0]);
    }

    /**
     * This method returns an unmodifiable List of <code>ControlParam</code> objects
     * associated with the specified dataType.
     * 
     * @param dataType the dataType for which control-params are required.
     * @return an unmodifiable List of the associated <code>ControlParam</code> objects.
     * 
     * @see ControlParam
     */
    public List<ControlParam> getControlParamsAsListFor(String dataType)
    {
        return Collections.unmodifiableList(datatypeDefCtrlMappings.get(
                dataType).getParamsAsList());
    }
}

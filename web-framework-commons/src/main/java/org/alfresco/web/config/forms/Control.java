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

/**
 * This class represents a single control configuration item. These items can exist
 * within a group of &lt;default-controls&gt; or underneath a &lt;field&gt;.
 * 
 * @author Neil McErlean.
 */
public class Control
{
    private String template;
    private final Map<String, ControlParam> controlParams = new LinkedHashMap<String, ControlParam>();

    /**
     * Constructs a Control object with a null template.
     */
    public Control()
    {
        this(null);
    }
    
    /**
     * Constructs a Control object with the specified template.
     * 
     * @param template String
     */
    public Control(String template)
    {
        this.template = template;
    }

    void addControlParam(String cpName, String cpValue)
    {
        ControlParam cp = new ControlParam(cpName, cpValue);
        this.addControlParam(cp);
    }

    void addControlParam(ControlParam param)
    {
        controlParams.put(param.getName(), param);
    }

    /**
     * This method returns the template path of this Control.
     * @return the template path.
     */
    public String getTemplate()
    {
        return template;
    }
    
    void setTemplate(String newTemplate)
    {
        this.template = newTemplate;
    }

    public ControlParam[] getParams()
    {
        return this.getParamsAsList().toArray(new ControlParam[0]);
    }
    
    /**
     * This method returns an unmodifiable List of <code>ControlParam</code>
     * objects that are associated with this Control.
     * @return an unmodifiable List of ControlParam references.
     */
    public List<ControlParam> getParamsAsList()
    {
        List<ControlParam> result = new ArrayList<ControlParam>(controlParams.size());
        for (Map.Entry<String, ControlParam> entry : controlParams.entrySet())
        {
            result.add(entry.getValue());
        }
        return Collections.unmodifiableList(result);
    }
    
    public Control combine(Control otherControl)
    {
        String combinedTemplate = otherControl.template == null ? this.template : otherControl.template;
        Control result = new Control(combinedTemplate);
        
        for (Map.Entry<String, ControlParam> thisEntry : this.controlParams.entrySet())
        {
            ControlParam thisCP = thisEntry.getValue();
            result.controlParams.put(thisCP.getName(), thisCP);
        }
        for (Map.Entry<String, ControlParam> otherEntry : otherControl.controlParams.entrySet())
        {
            ControlParam otherCP = otherEntry.getValue();
            // This call to 'put' will replace any cp entries with the same name
            // that are already in the map.
            result.controlParams.put(otherCP.getName(), otherCP);
        }
        
        return result;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(template);
        result.append(controlParams);
        return result.toString();
    }
}
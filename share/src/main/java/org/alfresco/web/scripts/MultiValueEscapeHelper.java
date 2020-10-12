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
package org.alfresco.web.scripts;

import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.webscripts.processor.BaseProcessorExtension;

/**
 * Template helper for splitting and unescaping multi-value fields.
 * @author Frederik Heremans
 */
public class MultiValueEscapeHelper extends BaseProcessorExtension
{
    /**
     * Returns the individual values of a multi-valued property, which has been properly
     * escaped by the {@link org.alfresco.repo.forms.processor.workflow.ExtendedFieldBuilder}.
     * 
     * @param escapedString the string containing the escaped, comma-seperated values.
     * @return the values split up and unescaped.
     */
    public List<String> getUnescapedValues(String escapedString)
    {
        List<String> elements = new ArrayList<String>();
        StringBuffer currentElement = new StringBuffer();
        
        char currentChar;
        boolean isEscaped = false;
        for(int i = 0; i < escapedString.length(); i++)
        {
            currentChar = escapedString.charAt(i);
            
            if(isEscaped) 
            {
                isEscaped = false;
                currentElement.append(currentChar);
            }
            else if(currentChar == '\\')
            {
                // Escape character encountered
                isEscaped = true;
            }
            else if(currentChar == ',')
            {
                // New element encounterd
                elements.add(currentElement.toString());
                currentElement.delete(0, currentElement.length());
            }
            else
            {
                // Plain character, push to current value
                currentElement.append(currentChar);
            }
        }
        
        if(currentElement.length() > 0)
        {
            elements.add(currentElement.toString());
        }
        return elements;
    }
}

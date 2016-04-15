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

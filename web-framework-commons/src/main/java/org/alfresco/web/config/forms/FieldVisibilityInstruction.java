package org.alfresco.web.config.forms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * This class represents an instruction (within the forms config) to show or hide a
 * field. The instruction may only apply in certain modes.
 * 
 * @see Mode
 * 
 * @author Neil McErlean
 */
class FieldVisibilityInstruction
{
    private final Visibility showOrHide;
    private final String fieldId;
    private final List<Mode> forModes;
    
    /**
     * 
     * @param showOrHide
     * @param fieldId
     * @param modesString
     */
    public FieldVisibilityInstruction(String showOrHide, String fieldId, String modesString)
    {
        this.showOrHide = Visibility.visibilityFromString(showOrHide);
        this.fieldId = fieldId;
        if (modesString == null || modesString.length() == 0)
        {
            this.forModes = Arrays.asList(Mode.values());
        }
        else
        {
            this.forModes = Mode.modesFromString(modesString);
        }
    }

    public Visibility getShowOrHide()
    {
        return showOrHide;
    }
    
    public String getFieldId()
    {
        return fieldId;
    }
    
    public List<Mode> getModes()
    {
        return Collections.unmodifiableList(forModes);
    }
    
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(this.showOrHide)
            .append(" ")
            .append(fieldId)
            .append(" ")
            .append(forModes);
        return result.toString();
    }
}
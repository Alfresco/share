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
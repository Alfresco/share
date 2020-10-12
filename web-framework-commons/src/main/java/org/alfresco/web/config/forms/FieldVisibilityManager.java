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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This class is responsible for implementing the algorithm that defines field
 * visibility. This is governed by the particular formulation of &lt;show&gt; and
 * &lt;hide&gt; tags under the &lt;field-visibility&gt; tag.
 * <P/>
 * The algorithm for determining visibility works as follows
 * <ul>
 *   <li>If there is no field-visibility configuration (show or hide tags) then
 *       all fields are visible in all modes.</li>
 *   <li>If there are one or more hide tags then the specified fields will be hidden
 *       in the specified modes. All other fields remain visible as before.</li>
 *   <li>However, as soon as a single show tag appears in the config xml, this is
 *       taken as a signal that all field visibility is to be manually configured.
 *       At that point, all fields default to hidden and only those explicitly
 *       configured to be shown (with a show tag) will actually be shown.</li>
 *   <li>Show and hide rules will be applied in sequence with later rules
 *       potentially invalidating previous rules.</li>
 *   <li>Show or hide rules which only apply for specified modes have an implicit
 *       element. e.g. <show id="name" for-mode="view"/> would show the name field
 *       in view mode and by implication, hide it in other modes.</li>
 * </ul>
 * 
 * @author Neil McErlean
 */
class FieldVisibilityManager
{
    // Implementation note
    // The show/hide instructions are read in sequentially during the configuration
    // read. There may be instructions spread across multiple <config> elements and
    // multiple configuration files.
    // At any time, a <show> tag will change the meaning of all previous instructions.
    //
    // Two approaches to this class have been attempted.
    // 1. Read in the show/hide instructions and produce the lists of what is visible
    //    across modes, applying the above algorithm during this read.
    //    The individual instructions may then be discarded as they are read.
    //    When a call to isFieldVisible is made, look up the lists for the answer.
    // 2. Read in the show/hide instructions and store this sequence of instructions.
    //    When a call to isFieldVisible is made, apply the algorithm to the stored
    //    sequence of instructions and return the result.
    //
    // The former leads to more complicated code, but less runtime computation.
    // The latter leads to more readable code, but requires the application of the
    // algorithm on each call to isFieldVisible.
    //
    // The latter has been adopted for maintainability, extensibility reasons.
    //
    // Later note: The latter approach is actually required as the show/hide instructions
    //      must not be cached - in order to support changes at runtime.
    
    private static Log logger = LogFactory.getLog(FieldVisibilityManager.class);

    // We can't store 3 separate Lists for each of the 3 modes as the presence
    // of a <show> tag in any mode has repercussions for field-visibility in all modes.
    //TODO Is this correct?
    private List<FieldVisibilityInstruction> visibilityInstructions = new ArrayList<FieldVisibilityInstruction>();

    /**
     * 
     * @param showOrHide
     * @param fieldId
     * @param modesString
     */
    void addInstruction(String showOrHide, String fieldId, String modesString)
    {
        this.visibilityInstructions.add(new FieldVisibilityInstruction(showOrHide, fieldId, modesString));
    }
    
    /**
     * This method combines the specified otherFVM with the current instance and returns
     * a new instance of FieldVisibilityManager which represents the combination of
     * these two.
     * 
     * @param otherFVM
     * @return a new instance of FieldVisibilityManager.
     */
    public FieldVisibilityManager combine(FieldVisibilityManager otherFVM)
    {
        if (otherFVM == this)
        {
            return this;
        }
        FieldVisibilityManager result = new FieldVisibilityManager();
        
        result.visibilityInstructions.addAll(this.visibilityInstructions);
        result.visibilityInstructions.addAll(otherFVM.visibilityInstructions);

        return result;
    }
    
    /**
     * This method checks whether the specified field is visible in the specified mode.
     * 
     * @param fieldId
     * @param m the mode
     * @return true if the field is visible in the specified mode, else false.
     */
    public boolean isFieldVisible(String fieldId, Mode m)
    {
        // First attempt: the brute force simple impl.
        // TODO Could refactor later to have less loop iteration.

        if (this.visibilityInstructions.isEmpty())
        {
            return true;
        }
        
        // The most important thing to understand is whether there are *any* <show>
        // tags present as this changes everything.
        int indexOfFirstShow = getIndexOfFirstShow();
        
        if (indexOfFirstShow != -1)
        {
            // There is at least one "show" tag.
            
            // We need to ignore all those instructions that precede the first 'show'.
            List<FieldVisibilityInstruction> relevantInstructions
                    = visibilityInstructions.subList(indexOfFirstShow, visibilityInstructions.size());
            
            // With any show tag present, show/hide is explicitly config managed,
            // so we default to HIDE.
            boolean showCurrentField = false;
            
            for (FieldVisibilityInstruction fvi : relevantInstructions)
            {
                if (fvi.getFieldId().equals(fieldId)
                        && fvi.getModes().contains(m))
                {
                    // We override the show/hide as we go.
                    showCurrentField = fvi.getShowOrHide().equals(Visibility.SHOW);
                }
            }

            return showCurrentField;
        }
        else
        {
            // There are no "show" tags, only hides.
            for (FieldVisibilityInstruction fvi : visibilityInstructions)
            {
                if (fvi.getFieldId().equals(fieldId)
                        && fvi.getShowOrHide().equals(Visibility.HIDE) // Always true.
                        && fvi.getModes().contains(m))
                {
                    return false;
                }
            }
            return true;
        }
    }
    
    /**
     * This method checks whether the specified field is hidden in the specified mode.
     * 
     * @param fieldId
     * @param m the mode
     * @return true if the field is hidden in the specified mode, else false.
     */
    public boolean isFieldHidden(String fieldId, Mode m)
    {
        // iterate through instructions, looking for hide instructions
        // for the field and the requested mode
        for (FieldVisibilityInstruction fvi : visibilityInstructions)
        {
            if (fvi.getFieldId().equals(fieldId) && 
                fvi.getShowOrHide().equals(Visibility.HIDE) && 
                fvi.getModes().contains(m))
            {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Finds the index of the first "show" instruction.
     * 
     * @return an int index of the first "show" instruction, or -1 if none exists.
     */
    public int getIndexOfFirstShow()
    {
        for (int i = 0; i < visibilityInstructions.size(); i++)
        {
            if (visibilityInstructions.get(i).getShowOrHide().equals(Visibility.SHOW)) return i;
        }
        return -1;
    }

    /**
     * Returns true if managing hidden fields, false if managing shown fields.
     * @return
     */
    public boolean isManagingHiddenFields()
    {
        return this.getIndexOfFirstShow() != -1;
    }

    /**
     * This method attempts to return a List of the fieldIDs of the fields which are
     * visible in the specified Mode. Such a request only makes sense if this
     * class is managing 'shown' fields, in other words, if there has been at least
     * one show tag.
     * 
     * @param mode the Mode.
     * @return the list of fields visible in the specified mode if this is knowable,
     * else <code>null</code>.
     */
    public List<String> getFieldNamesVisibleInMode(Mode mode)
    {
        int indexOfFirstShow = getIndexOfFirstShow();
        if (indexOfFirstShow == -1)
        {
            // Visible fields for any mode are not knowable at this point.
            return null;
        }
        else
        {
            Set<String> result = new LinkedHashSet<String>();

            List<FieldVisibilityInstruction> relevantInstructions
                   = visibilityInstructions.subList(indexOfFirstShow, visibilityInstructions.size());
            
            for (FieldVisibilityInstruction fvi : relevantInstructions)
            {
                if (fvi.getModes().contains(mode))
                {
                    if (fvi.getShowOrHide().equals(Visibility.SHOW))
                    {
                        result.add(fvi.getFieldId());
                    }
                    else if (fvi.getShowOrHide().equals(Visibility.HIDE))
                    {
                        result.remove(fvi.getFieldId());
                    }
                }
            }
            
            return Collections.unmodifiableList(new ArrayList<String>(result));
        }
    }

    /**
     * This method attempts to return a List of the fieldIDs of the fields which are
     * hidden in the specified Mode. Such a request only makes sense if this
     * class not managing 'shown' fields, in other words, if there has not been any
     * show tags.
     * 
     * @param mode the Mode.
     * @return the list of fields hidden in the specified mode if this is knowable,
     * else <code>null</code>.
     */
    public List<String> getFieldNamesHiddenInMode(Mode mode)
    {
        int indexOfFirstShow = getIndexOfFirstShow();
        if (indexOfFirstShow != -1)
        {
            // Hidden fields for any mode are not knowable at this point.
            return null;
        }
        else
        {
            Set<String> result = new LinkedHashSet<String>();

            for (FieldVisibilityInstruction fvi : visibilityInstructions)
            {
                if (fvi.getModes().contains(mode))
                {
                    // There should only be 'hide' instructions at this point so
                    // perhaps the below 'if' clause is unnecessary, but...
                    if (fvi.getShowOrHide().equals(Visibility.HIDE))
                    {
                        result.add(fvi.getFieldId());
                    }
                }
            }
            
            if (result.size() == 0)
            {
                return null;
            }
            else
            {
                return Collections.unmodifiableList(new ArrayList<String>(result));
            }
        }
    }
}
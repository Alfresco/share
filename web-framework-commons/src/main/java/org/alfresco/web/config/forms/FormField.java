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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigException;

/**
 * This class represents a &lt;field&gt; element within the &lt;appearance&gt; tag in
 * the config xml.
 * 
 * @author Neil McErlean.
 */
public class FormField
{
    private static final String ATTR_LABEL_ID = "label-id";
    private static final String ATTR_LABEL = "label";
    private static final String ATTR_DESCRIPTION_ID = "description-id";
    private static final String ATTR_DESCRIPTION = "description";
    private static final String ATTR_HELP_TEXT_ID = "help-id";
	private static final String ATTR_HELP_TEXT = "help";
    private static final String ATTR_HELP_ENCODE_HTML = "help-encode-html";
	private static final String ATTR_SET = "set";
    private static final String ATTR_READ_ONLY = "read-only";
    private static final String ATTR_MANDATORY = "mandatory";
    private static final String ATTR_SORTED = "sorted";

	private static Log logger = LogFactory.getLog(FormField.class);
    
    private final String id;
    private final Map<String, String> attributes;
    private Control associatedControl = new Control();
    private final List<ConstraintHandlerDefinition> constraintDefns = new ArrayList<ConstraintHandlerDefinition>();
    
    /**
     * 
     * @param id the id of the field. This cannot be null.
     * @param attributes
     * @throws ConfigException if id is null.
     */
    public FormField(String id, Map<String, String> attributes)
    {
        if (id == null)
        {
            String msg = "Illegal null field id";
            if (logger.isWarnEnabled())
            {
                logger.warn(msg);
            }
            throw new ConfigException(msg);
        }
        this.id = id;
        if (attributes == null)
        {
            attributes = Collections.emptyMap();
        }
        this.attributes = attributes;
    }
    
    public Control getControl()
    {
        return this.associatedControl;
    }

    void addConstraintDefinition(String type, String message, String messageId,
    		String validationHandler, String event)
    {
    	for (ConstraintHandlerDefinition constraint : this.constraintDefns)
    	{
    		if (constraint.getType().equals(type))
    		{
    			// The value for this constraint is being overridden.
    			constraint.setMessage(message);
    			constraint.setMessageId(messageId);
    			constraint.setValidationHandler(validationHandler);
    			constraint.setEvent(event);
    			return;
    		}
    	}
    	this.constraintDefns.add(new ConstraintHandlerDefinition(type, validationHandler, message, messageId, event));
    }
    
    public Map<String, String> getAttributes()
    {
        return Collections.unmodifiableMap(attributes);
    }
    
    public String getId()
    {
        return this.id;
    }
    
    // The following are convenience accessor methods for certain known attributes.
    public String getLabel()
    {
    	return attributes.get(ATTR_LABEL);
    }
    
    public String getLabelId()
    {
    	return attributes.get(ATTR_LABEL_ID);
    }
    
    public String getDescription()
    {
        return attributes.get(ATTR_DESCRIPTION);
    }
    
    public String getDescriptionId()
    {
        return attributes.get(ATTR_DESCRIPTION_ID);
    }
    
    public boolean isReadOnly()
    {
        Object disabledValue = attributes.get(ATTR_READ_ONLY);
        return disabledValue instanceof String
            && "true".equalsIgnoreCase((String)disabledValue);
    }
    
    public boolean isMandatory()
    {
        Object mandatoryValue = attributes.get(ATTR_MANDATORY);
        return mandatoryValue instanceof String
            && "true".equalsIgnoreCase((String)mandatoryValue);
    }
    
    public boolean isSorted() 
    {
        Object sortedValue = attributes.get(ATTR_SORTED);
        return sortedValue instanceof String
            && "true".equalsIgnoreCase((String) sortedValue);
    }
    
    public String getSet()
    {
    	final String setId = attributes.get(ATTR_SET);
    	if (setId != null)
    	{
    	    return setId;
    	}
    	else
    	{
    	    return FormConfigElement.DEFAULT_SET_ID;
    	}
    }
    
    public String getHelpText()
    {
    	return attributes.get(ATTR_HELP_TEXT);
    }
    
    public String getHelpTextId()
    {
    	return attributes.get(ATTR_HELP_TEXT_ID);
    }
    
    public String getHelpEncodeHtml()
    {
        return attributes.get(ATTR_HELP_ENCODE_HTML);
    }
    // End of convenience accessor methods.
        
    public Map<String, ConstraintHandlerDefinition> getConstraintDefinitionMap()
    {
        Map<String, ConstraintHandlerDefinition> defns = new LinkedHashMap<String, ConstraintHandlerDefinition>(4);
        
        for (ConstraintHandlerDefinition defn : this.constraintDefns)
        {
            defns.put(defn.getType(), defn);
        }
        
        return Collections.unmodifiableMap(defns);
    }
    
    public FormField combine(FormField otherField)
    {
        if (logger.isDebugEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("Combining instances of ").append(this);
            logger.debug(msg.toString());
        }
        
        // It doesn't make sense to combine two fields with different IDs.
        if (!this.id.equals(otherField.id))
        {
            if (logger.isWarnEnabled())
            {
                StringBuilder msg = new StringBuilder();
                msg.append("Illegal attempt to combine two FormFields with different IDs: ")
                    .append(this.id)
                    .append(", ")
                    .append(otherField.id);
                logger.warn(msg.toString());
            }
            return this;
        }

        // Combine the xml attributes of the <field> tag.
        Map<String, String> combinedAttributes = new LinkedHashMap<String, String>();
        combinedAttributes.putAll(this.attributes);
        combinedAttributes.putAll(otherField.attributes);
        
        FormField result = new FormField(this.id, combinedAttributes);
        
        Control combinedControl = this.associatedControl.combine(otherField.associatedControl);
        result.associatedControl = combinedControl;
        
        // Combine constraint data
        for (ConstraintHandlerDefinition constraint : this.constraintDefns)
        {
        	result.addConstraintDefinition(constraint.getType(), constraint.getMessage(),
        			constraint.getMessageId(), constraint.getValidationHandler(), constraint.getEvent());
        }
        for (ConstraintHandlerDefinition constraint : otherField.constraintDefns)
        {
        	result.addConstraintDefinition(constraint.getType(), constraint.getMessage(),
        			constraint.getMessageId(), constraint.getValidationHandler(), constraint.getEvent());
        }

        return result;
    }

    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append("FormField:")
            .append(this.id);
        return result.toString();
    }
}
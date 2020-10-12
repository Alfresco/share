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

public class ConstraintHandlersConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 2266042608444782740L;
    public static final String CONFIG_ELEMENT_ID = "constraint-handlers";
    
    /*
     * LinkedHashMap is an extension of HashMap which maintains insertion order of the
     * entries. We are interested here in the insertion order of key/value pairs and
     * later in this class, we are traversing the keys with the assumption that they
     * will be iterated in insertion order.
     * Sun's javadoc is interesting here as it makes it clear that the values will have
     * their order maintained.
     * The insertion order of keys is observed to be maintained in Apple's 1.5 VM,
     * but it's not clear that this is a contractual obligation w.r.t. the class API.
     */
    private Map<String, ConstraintHandlerDefinition> items = new LinkedHashMap<String, ConstraintHandlerDefinition>();

    /**
     * This constructor creates an instance with the default name.
     */
    public ConstraintHandlersConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public ConstraintHandlersConfigElement(String name)
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
                "Reading the constraint-handlers config via the generic interfaces is not supported");
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
        // that are allowed. i.e. Only an instance of a ConstraintHandlersConfigElement
        // can be combined with this.
        ConstraintHandlersConfigElement otherCHCElement = (ConstraintHandlersConfigElement) configElement;

        ConstraintHandlersConfigElement result = new ConstraintHandlersConfigElement();

        for (String nextType : items.keySet())
        {
            String nextValidationHandler = getValidationHandlerFor(nextType);
            String nextMessage = getMessageFor(nextType);
            String nextMessageId = getMessageIdFor(nextType);
            String nextEvent = getEventFor(nextType);
            result.addDataMapping(nextType, nextValidationHandler, nextMessage,
                    nextMessageId, nextEvent);
        }

        for (String nextType : otherCHCElement.items.keySet())
        {
            String nextValidationHandler = otherCHCElement
                    .getValidationHandlerFor(nextType);
            String nextMessage = otherCHCElement.getMessageFor(nextType);
            String nextMessageId = otherCHCElement.getMessageIdFor(nextType);
            String nextEvent = otherCHCElement.getEventFor(nextType);
            result.addDataMapping(nextType, nextValidationHandler, nextMessage,
                    nextMessageId, nextEvent);
        }

        return result;
    }

    /* package */void addDataMapping(String type, String validationHandler,
            String message, String messageID, String event)
    {
        items.put(type, new ConstraintHandlerDefinition(type, validationHandler, message, messageID, event));
    }

    /**
     * This method returns the registered constraint types.
     * @return a String[] of the constraint types.
     */
    String[] getConstraintTypes()
    {
        return this.getConstraintTypesAsList().toArray(new String[0]);
    }

    /**
     * This method returns the registered constraint types.
     * @return an unmodifiable List of the constraint types.
     */
    List<String> getConstraintTypesAsList()
    {
        Set<String> result = items.keySet();
        // See the comment above on ordering in LinkedHashMaps' keys.
        List<String> listResult = new ArrayList<String>(result);
        return Collections.unmodifiableList(listResult);
    }

    /**
     * This method returns a String identifier for the validation-handler
     * associated with the specified constraint type.
     * 
     * @param type the constraint type.
     * @return a String identifier for the validation-handler.
     */
    String getValidationHandlerFor(String type)
    {
        return items.get(type).getValidationHandler();
    }

    /**
     * This method returns a message String  associated with the specified constraint
     * type.
     * 
     * @param type the constraint type.
     * @return the message String for the validation-handler.
     */
    String getMessageFor(String type)
    {
    	return items.get(type).getMessage();
    }

    /**
     * This method returns a message-id String  associated with the specified constraint
     * type.
     * 
     * @param type the constraint type.
     * @return the message-id String for the validation-handler.
     */
    String getMessageIdFor(String type)
    {
    	return items.get(type).getMessageId();
    }
    
    /**
     * This method returns an event String associated with the specified constraint
     * type.
     * 
     * @param type the constraint type.
     * @return the event when the constraint will be triggered
     */
    String getEventFor(String type)
    {
        return items.get(type).getEvent();
    }

    public String[] getItemNames()
    {
    	return this.getItemNamesAsList().toArray(new String[0]);
    }
    
    public List<String> getItemNamesAsList()
    {
        return this.getConstraintTypesAsList();
    }

    public Map<String, ConstraintHandlerDefinition> getItems()
    {
    	return Collections.unmodifiableMap(items);
    }
}

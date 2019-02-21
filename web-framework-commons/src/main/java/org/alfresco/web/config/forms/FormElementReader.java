/*
 * #%L
 * Alfresco Web Framework common libraries
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;form&gt; elements.
 * 
 * @author Neil McErlean.
 */
class FormElementReader implements ConfigElementReader
{
	public static final String ATTR_APPEARANCE = "appearance";
	public static final String ATTR_LABEL = "label";
	public static final String ATTR_LABEL_ID = "label-id";
    public static final String ATTR_FOR_MODE = "for-mode";
    public static final String ATTR_FORM_ID = "id";
    public static final String ATTR_MESSAGE = "message";
    public static final String ATTR_MESSAGE_ID = "message-id";
    public static final String ATTR_EVENT = "event";
    public static final String ATTR_VALIDATION_HANDLER = "validation-handler";
    public static final String ATTR_NAME = "name";
    public static final String ATTR_NAME_ID = "id";
    public static final String ATTR_PARENT = "parent";
    public static final String ATTR_SUBMISSION_URL = "submission-url";
    public static final String ATTR_TEMPLATE = "template";
    public static final String ATTR_TYPE = "type";
    public static final String ATTR_FORCE = "force";
    public static final String ELEMENT_FORM = "form";
    public static final String ELEMENT_HIDE = "hide";
    public static final String ELEMENT_SHOW = "show";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element formElement)
    {
        FormConfigElement result = null;
        if (formElement == null)
        {
            return null;
        }

        String name = formElement.getName();
        if (!name.equals(ELEMENT_FORM))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_FORM
                    + " elements, the element passed was '" + name + "'");
        }

        result = new FormConfigElement();
        
        parseFormId(formElement, result);
        
        parseSubmissionURL(formElement, result);
        
        parseFormTag(formElement, result);

        parseFieldVisibilityTag(formElement, result);

        parseAppearanceTag(formElement, result);

        return result;
    }

    private void parseAppearanceTag(Element formElement, FormConfigElement result)
    {
        parseSetTags(formElement, result);

        parseFieldTags(formElement, result);
    }

    @SuppressWarnings("unchecked")
    private void parseFieldTags(Element formElement, FormConfigElement result)
    {
        // xpath expressions.
        for (Object fieldObj : formElement.selectNodes("./appearance/field")) {
            Element fieldElem = (Element)fieldObj;
            // List<Attribute> fieldAttributes = fieldElem.selectNodes("./@*");
            List<Attribute> fieldAttributes = new ArrayList<Attribute>();

            for (Object obj : fieldElem.selectNodes("./@*")) {
                fieldAttributes.add((Attribute) obj);
            }
            
            List<String> fieldAttributeNames = new ArrayList<String>();
            List<String> fieldAttributeValues = new ArrayList<String>();
            
            // With special handling for the mandatory "id" attribute.
            String fieldIdValue = null;
            for (Attribute nextAttr : fieldAttributes) {
                String nextAttributeName = nextAttr.getName();
                String nextAttributeValue = nextAttr.getValue();
                
                if (nextAttributeName.equals(ATTR_NAME_ID))
                {
                    fieldIdValue = nextAttributeValue;
                }
                else
                {
                    fieldAttributeNames.add(nextAttributeName);
                    fieldAttributeValues.add(nextAttributeValue);
                }
            }

            if (fieldIdValue == null)
            {
                throw new ConfigException("<field> node missing mandatory id attribute.");
            }
            result.addField(fieldIdValue, fieldAttributeNames, fieldAttributeValues);

            // List<Element> controlObjs = fieldElem.selectNodes("./control");
            List<Element> controlObjs = new ArrayList<Element>();

            for (Object obj : fieldElem.selectNodes("./control")) {
                controlObjs.add((Element) obj);
            }

            if (!controlObjs.isEmpty())
            {
                // We are assuming that there is only one <control> child element
                Element controlElem = controlObjs.get(0);
            
                String templateValue = controlElem.attributeValue(ATTR_TEMPLATE);
                List<String> controlParamNames = new ArrayList<String>();
                List<String> controlParamValues = new ArrayList<String>();
                for (Object paramObj : controlElem
                        .selectNodes("./control-param"))
                {
                    Element paramElem = (Element) paramObj;
                    controlParamNames.add(paramElem.attributeValue(ATTR_NAME));
                    controlParamValues.add(paramElem.getTextTrim());
                }

                result.addControlForField(fieldIdValue, templateValue, controlParamNames,
                        controlParamValues);
            }
            
            // Delegate the reading of the <constraint-handlers> tag(s) to the reader.
            ConstraintHandlersElementReader constraintHandlersElementReader = new ConstraintHandlersElementReader();
            for (Object constraintHandlerObj : fieldElem.selectNodes("./constraint-handlers")) {
                // There need only be one <constraint-handlers> element, but there is nothing
                // to prevent the use of multiple such elements.
                
                Element constraintHandlers = (Element)constraintHandlerObj;
                ConfigElement confElem = constraintHandlersElementReader.parse(constraintHandlers);
                ConstraintHandlersConfigElement constraintHandlerCE = (ConstraintHandlersConfigElement)confElem;
                // This ConstraintHandlersConfigElement contains the config data for all
                // <constraint> elements under the current <constraint-handlers> element.
                
                Map<String, ConstraintHandlerDefinition> constraintItems = constraintHandlerCE.getItems();
                for (String key : constraintItems.keySet())
                {
                    ConstraintHandlerDefinition defn = constraintItems.get(key);
                    result.addConstraintForField(fieldIdValue, defn.getType(), defn.getMessage(),
                            defn.getMessageId(), defn.getValidationHandler(), defn.getEvent());
                }
            }
        }
    }

    private void parseSetTags(Element formElement, FormConfigElement result)
    {
        for (Object setObj : formElement.selectNodes("./appearance/set")) {
            Element setElem = (Element)setObj;
            String setId = setElem.attributeValue(ATTR_NAME_ID);
            String parentSetId = setElem.attributeValue(ATTR_PARENT);
            String appearance = setElem.attributeValue(ATTR_APPEARANCE);
            String label = setElem.attributeValue(ATTR_LABEL);
            String labelId = setElem.attributeValue(ATTR_LABEL_ID);
            String template = setElem.attributeValue(ATTR_TEMPLATE);
            
            result.addSet(setId, parentSetId, appearance, label, labelId, template);
        }
    }

    private void parseFieldVisibilityTag(Element formElement,
            FormConfigElement result)
    {
        for (Object obj : formElement.selectNodes("./field-visibility/show|./field-visibility/hide")) {
            Element showOrHideElem = (Element)obj;
            String nodeName = showOrHideElem.getName();
            String fieldId = showOrHideElem.attributeValue(ATTR_NAME_ID);
            String mode = showOrHideElem.attributeValue(ATTR_FOR_MODE);
            String forceString = showOrHideElem.attributeValue(ATTR_FORCE);
            
            result.addFieldVisibility(nodeName, fieldId, mode, forceString);
        }
    }

    private void parseFormTag(Element formElement, FormConfigElement result)
    {
        for (Object obj : formElement.selectNodes("./edit-form|./view-form|./create-form")) {
            Element editOrViewOrCreateFormElem = (Element)obj;
            String nodeName = editOrViewOrCreateFormElem.getName();
            String template = editOrViewOrCreateFormElem.attributeValue(ATTR_TEMPLATE);
            
            result.setFormTemplate(nodeName, template);
        }
    }

    private void parseSubmissionURL(Element formElement,
            FormConfigElement result)
    {
        String submissionURL = formElement.attributeValue(ATTR_SUBMISSION_URL);
        result.setSubmissionURL(submissionURL);
    }

    private void parseFormId(Element formElement,
            FormConfigElement result)
    {
        String formId = formElement.attributeValue(ATTR_FORM_ID);
        result.setFormId(formId);
    }
}

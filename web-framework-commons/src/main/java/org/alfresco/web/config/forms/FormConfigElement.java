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
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * This config element represents form values for the client. Note the distinction
 * between 'form' and 'forms' elements in the config xml. Form elements are contained
 * by forms elements.
 * 
 * @author Neil McErlean.
 */
public class FormConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = -7008510360503886308L;
    private static Log logger = LogFactory.getLog(FormConfigElement.class);
    
    public static final String FORM_NAME_ID = "form";
    public static final String DEFAULT_SET_ID = "";
    
    private String formId;
    private String submissionURL;
    
    private String createTemplate;
    private String editTemplate;
    private String viewTemplate;
    
    FieldVisibilityManager fieldVisibilityManager = new FieldVisibilityManager();
    private final Map<String, FormSet> sets = new LinkedHashMap<String, FormSet>(4);
    private Map<String, FormField> fields = new LinkedHashMap<String, FormField>(8);
    private List<String> forcedFields = new ArrayList<String>(4);
    
    public FormConfigElement()
    {
        this(FORM_NAME_ID);
    }

    public FormConfigElement(String name)
    {
        super(name);
        // There should always be a 'default set' for those fields which do not declare
        // explicit membership of any set.
        FormSet defaultSet = new FormSet(DEFAULT_SET_ID);
        this.sets.put(DEFAULT_SET_ID, defaultSet);
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the form config via the generic interfaces is not supported");
    }

    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement otherConfigElement)
    {
        if (otherConfigElement == null)
        {
            return this;
        }
        
        FormConfigElement otherFormElem = (FormConfigElement)otherConfigElement;
        FormConfigElement result = new FormConfigElement();
        
        combineSubmissionURL(otherFormElem, result);
        
        combineTemplates(otherFormElem, result);
        
        combineFieldVisibilities(otherFormElem, result);
        
        combineSets(otherFormElem, result);
        
        combineFields(otherFormElem, result);
        
        return result;
    }

    private void combineFields(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        Map<String, FormField> newFields = new LinkedHashMap<String, FormField>();
        for (String nextFieldId : this.fields.keySet())
        {
            FormField nextFieldFromThis = this.fields.get(nextFieldId);
            if (otherFormElem.getFields().containsKey(nextFieldId))
            {
                FormField combinedField = nextFieldFromThis
                    .combine(otherFormElem.getFields().get(nextFieldId));
                newFields.put(nextFieldId, combinedField);
            }
            else
            {
                newFields.put(nextFieldId, nextFieldFromThis);
            }
        }

        for (String nextFieldId : otherFormElem.fields.keySet())
        {
            if (!this.fields.containsKey(nextFieldId))
            {
                newFields.put(nextFieldId, otherFormElem.fields.get(nextFieldId));
            }
            else
            {
                // handled by above loop.
            }
        }
        result.setFields(newFields);
        
        // Combine the lists of 'forced' fields.
        result.forcedFields.addAll(this.forcedFields);
        for (String fieldName : otherFormElem.forcedFields)
        {
            if (result.forcedFields.contains(fieldName) == false)
            {
                result.forcedFields.add(fieldName);
            }
        }
    }

    private void combineSets(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        for (String nextOldSet : sets.keySet())
        {
            FormSet nextOldSetData = sets.get(nextOldSet);
            String setId = nextOldSetData.getSetId();
            String parentId = nextOldSetData.getParentId();
            String appearance = nextOldSetData.getAppearance();
            String label = nextOldSetData.getLabel();
            String labelId = nextOldSetData.getLabelId();
            String template = nextOldSetData.getTemplate();
            result.addSet(setId, parentId, appearance, label, labelId, template);
        }
        for (String nextNewSet : otherFormElem.sets.keySet())
        {
            FormSet nextNewSetData = otherFormElem.sets.get(nextNewSet);
            String setId = nextNewSetData.getSetId();
            String parentId = nextNewSetData.getParentId();
            String appearance = nextNewSetData.getAppearance();
            String label = nextNewSetData.getLabel();
            String labelId = nextNewSetData.getLabelId();
            String template = nextNewSetData.getTemplate();
            result.addSet(setId, parentId, appearance, label, labelId, template);
        }
    }

    private void combineFieldVisibilities(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        FieldVisibilityManager combinedManager
                = this.fieldVisibilityManager.combine(otherFormElem.fieldVisibilityManager);
        result.fieldVisibilityManager = combinedManager;
    }

    private void combineTemplates(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        result.setFormTemplate("create-form", 
                    otherFormElem.createTemplate == null ? this.createTemplate : otherFormElem.createTemplate);
        
        result.setFormTemplate("edit-form", 
                    otherFormElem.editTemplate == null ? this.editTemplate : otherFormElem.editTemplate);
        
        result.setFormTemplate("view-form", 
                    otherFormElem.viewTemplate == null ? this.viewTemplate : otherFormElem.viewTemplate);
    }

    private void combineSubmissionURL(FormConfigElement otherFormElem,
            FormConfigElement result)
    {
        String otherSubmissionURL = otherFormElem.getSubmissionURL();
        result.setSubmissionURL(otherSubmissionURL == null ? this.submissionURL : otherSubmissionURL);
    }
    
    public String getId()
    {
    	return this.formId;
    }

    public String getSubmissionURL()
    {
        return this.submissionURL;
    }
    
    public Map<String, FormSet> getSets()
    {
        return Collections.unmodifiableMap(this.sets);
    }

    public String[] getSetIDs()
    {
        return this.getSetIDsAsList().toArray(new String[0]);
    }
    
    public List<String> getSetIDsAsList()
    {
        Set<String> keySet = sets.keySet();
        
        List<String> result = new ArrayList<String>(keySet.size());
        result.addAll(keySet);
        
        return Collections.unmodifiableList(result);
    }

    public FormSet[] getRootSets()
    {
        return this.getRootSetsAsList().toArray(new FormSet[0]);
    }

    /**
     * This method returns a Map of those &lt;set&gt;s which have no declared parentID
     * i&#46;e&#46; those that are 'roots' in the tree of sets. Note that this map will
     * always have at least one member; the default set.
     * 
     * @return List<FormSet>
     * @see #DEFAULT_SET_ID
     */
    public List<FormSet> getRootSetsAsList()
    {
        List<FormSet> result = new ArrayList<FormSet>(sets.size());
        for (Iterator<String> iter = sets.keySet().iterator(); iter.hasNext(); )
        {
            String nextKey = iter.next();
            FormSet nextSet = sets.get(nextKey);
            String nextParentID = nextSet.getParentId();
            if (nextParentID == null)
            {
                result.add(nextSet);
            }
        }
        return result;
    }
    
    /**
     * This method returns a Map of field names mentioned in the forms config, with any
     * associated config data as the associated value.
     * This will include any fields specified under the &lt;appearance&gt; tag in
     * the config as well as those mentioned under &lt;field-visibility&gt;. The former
     * will have associated config data whilst the latter will not.
     * 
     * @return a mapping of field names to their associated FormField data objects.
     */
    public Map<String, FormField> getFields()
    {
        // In the case where we have <field-visibility> but no <appearance> i.e. no <fields>
        // this method should include FormField objects for each 'show'n field.
        // These objects will have no associated metadata.
        
        // Get field names that are visible in any mode.
        Set<String> fieldsVisibleInAnyMode = new LinkedHashSet<String>();
        for (Mode m : Mode.values())
        {
            List<String> newFields = fieldVisibilityManager.getFieldNamesVisibleInMode(m);
            
            // Insertion order is not affected if an element is re-inserted into the LinkedHashSet
            if (newFields != null)
            {
                fieldsVisibleInAnyMode.addAll(newFields);
            }
        }
        
        // Put any fields from the <appearance> block into the result.
        // These fields will have config data.
        Map<String, FormField> result = new LinkedHashMap<String, FormField>();
        result.putAll(this.fields);

        // Go through those fields marked as to-be-shown in <field-visibility>
        // and if they are not already included, add them to the result.
        // These fields will not have associated data.
        for (String s : fieldsVisibleInAnyMode)
        {
            if (result.containsKey(s) == false) 
            {
                result.put(s, new FormField(s, null));
            }
        }

        return Collections.unmodifiableMap(result);
    }
    
    public String[] getHiddenCreateFieldNames()
    {
        List<String> names = this.getHiddenCreateFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }

    public String[] getHiddenEditFieldNames()
    {
        List<String> names = this.getHiddenEditFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }
    
    public String[] getHiddenViewFieldNames()
    {
        List<String> names = this.getHiddenViewFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }
    
    public String[] getVisibleCreateFieldNames()
    {
        List<String> names = this.getVisibleCreateFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }

    public String[] getVisibleEditFieldNames()
    {
        List<String> names = this.getVisibleEditFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }
    
    public String[] getVisibleViewFieldNames()
    {
        List<String> names = this.getVisibleViewFieldNamesAsList();
        if (names != null)
        {
            return names.toArray(new String[0]);
        }
        else
        {
            return null;
        }
    }
    
    /**
     * @see FieldVisibilityManager#getFieldNamesHiddenInMode(Mode)
     */
    public List<String> getHiddenCreateFieldNamesAsList()
    {
        return getFieldNamesHiddenInMode(Mode.CREATE);
    }
    
    /**
     * @see FieldVisibilityManager#getFieldNamesHiddenInMode(Mode)
     */
    public List<String> getHiddenEditFieldNamesAsList()
    {
        return getFieldNamesHiddenInMode(Mode.EDIT);
    }
    
    /**
     * @see FieldVisibilityManager#getFieldNamesHiddenInMode(Mode)
     */
    public List<String> getHiddenViewFieldNamesAsList()
    {
        return getFieldNamesHiddenInMode(Mode.VIEW);
    }

    /**
     * @see FieldVisibilityManager#getFieldNamesVisibleInMode(Mode)
     */
    public List<String> getVisibleCreateFieldNamesAsList()
    {
        return getFieldNamesVisibleInMode(Mode.CREATE);
    }
    
    /**
     * @see FieldVisibilityManager#getFieldNamesVisibleInMode(Mode)
     */
    public List<String> getVisibleEditFieldNamesAsList()
    {
        return getFieldNamesVisibleInMode(Mode.EDIT);
    }
    
    /**
     * @see FieldVisibilityManager#getFieldNamesVisibleInMode(Mode)
     */
    public List<String> getVisibleViewFieldNamesAsList()
    {
        return getFieldNamesVisibleInMode(Mode.VIEW);
    }

    public List<String> getVisibleCreateFieldNamesForSetAsList(String setId)
    {
        return getVisibleFieldNamesFor(setId, Mode.CREATE);
    }
    
    /**
     * This method returns an array of fieldNames for those fields which are
     * visible in Create mode and which are also members of the specified set.
     * 
     * @param setId String
     * @return the array of fieldNames (if there are any).
     *         <code>null</code> will be returned if the specified setId is not
     *         recognised or if the visible fields are not known.
     */
    public String[] getVisibleCreateFieldNamesForSet(String setId)
    {
        List <String> result = getVisibleCreateFieldNamesForSetAsList(setId);
        if (result == null)
        {
            return null;
        }
        return result.toArray(new String[0]);
    }

    public List<String> getVisibleEditFieldNamesForSetAsList(String setId)
    {
        return getVisibleFieldNamesFor(setId, Mode.EDIT);
    }
    
    public String[] getVisibleEditFieldNamesForSet(String setId)
    {
        List <String> result = getVisibleEditFieldNamesForSetAsList(setId);
        if (result == null)
        {
            return null;
        }
        return result.toArray(new String[0]);
    }

    public List<String> getVisibleViewFieldNamesForSetAsList(String setId)
    {
        return getVisibleFieldNamesFor(setId, Mode.VIEW);
    }
    
    public String[] getVisibleViewFieldNamesForSet(String setId)
    {
        List <String> result = getVisibleViewFieldNamesForSetAsList(setId);
        if (result == null)
        {
            return null;
        }
        return result.toArray(new String[0]);
    }

    private List<String> getVisibleFieldNamesFor(String setId, Mode mode)
    {
        List <String> result = new ArrayList<String>();

        // Does the requested setId exist?
        FormSet specifiedSet = getSets().get(setId);
        if (specifiedSet == null)
        {
            return null;
        }

        // Get all fields visible in the specified mode - irrespective of set membership.
        final List<String> visibleFields = getFieldNamesVisibleInMode(mode);
        if (visibleFields == null)
        {
            return null;
        }
        
        // Now go through the fields and filter out those that are not members of
        // the specified set
        for (String fieldName : visibleFields)
        {
            // get field defined in <appearance> block
            final FormField appearanceField = this.fields.get(fieldName);
            // if field defined only in <field-visibility>, create it without associated data.
            final FormField formField = (appearanceField != null) ? appearanceField : new FormField(fieldName, null);
            
            String set = null;
            if (formField != null)
            {
                set = formField.getSet();
            }
            
            if (set == null)
            {
                // All fields without an explicit set are in the default set.
                set = FormConfigElement.DEFAULT_SET_ID;
            }
            
            // if set of field matches requested one add to results
            if (set.equals(setId))
            {
                result.add(fieldName);
            }
        }
        return result;
    }

    public String getCreateTemplate()
    {
        return this.createTemplate;
    }
    
    public String getEditTemplate()
    {
        return this.editTemplate;
    }
    
    public String getViewTemplate()
    {
        return this.viewTemplate;
    }
    
    void setFormId(String formId)
    {
    	this.formId = formId;
    }
    
    /**
     * 
     * @param m Mode
     * @return <code>null</code> if there is no template available for the specified mode.
     */
    public String getFormTemplate(Mode m)
    {
        switch (m)
        {
        case CREATE: return getCreateTemplate();
        case EDIT: return getEditTemplate();
        case VIEW: return getViewTemplate();
        default: return null;
        }
    }
    
    /**
     * This method checks whether the specified field is visible in the specified mode.
     * 
     * @param fieldId the id of the field
     * @param m a mode.
     * @return true if the field is configured to be visible
     */
    public boolean isFieldVisible(String fieldId, Mode m)
    {
        return fieldVisibilityManager.isFieldVisible(fieldId, m);
    }
    
    /**
     * This method checks whether the specified field is specifically hidden in the specified mode.
     * 
     * @param fieldId the id of the field
     * @param m a mode.
     * @return true if the field is configured to be hidden
     */
    public boolean isFieldHidden(String fieldId, Mode m)
    {
        return fieldVisibilityManager.isFieldHidden(fieldId, m);
    }

    /**
     * This method checks whether the specified field is visible in the specified mode.
     * This is added as a convenience for JavaScript clients.
     * 
     * @param fieldId String
     * @param modeString String
     * @return boolean
     * @see #isFieldVisible(String, Mode)
     */
    public boolean isFieldVisibleInMode(String fieldId, String modeString)
    {
        // Note. We cannot have the same method name as isFieldVisible(String, Mode)
        // as this is intended for use by JavaScript clients where method overloading
        // is not supported.
        Mode m = Mode.modeFromString(modeString);
        return this.isFieldVisible(fieldId, m);
    }
    
    /**
     * This method checks whether the specified field is specifically hidden in the specified mode.
     * This is added as a convenience for JavaScript clients.
     * 
     * @param fieldId String
     * @param modeString String
     * @return boolean
     */
    public boolean isFieldHiddenInMode(String fieldId, String modeString)
    {
        // Note. We cannot have the same method name as isFieldHidden(String, Mode)
        // as this is intended for use by JavaScript clients where method overloading
        // is not supported.
        Mode m = Mode.modeFromString(modeString);
        return this.isFieldHidden(fieldId, m);
    }
    
    /**
     * Determines whether the given fieldId has been configured as 'force'd
     * 
     * @param fieldId The field id to check
     * @return true if the field is being forced to be visible
     */
    public boolean isFieldForced(String fieldId)
    {
        return this.forcedFields.contains(fieldId);
    }
    
    public String[] getForcedFields()
    {
        return this.getForcedFieldsAsList().toArray(new String[0]);
    }
    
    /**
     * Returns the list of fields that have been forced to be visible
     * 
     * @return List of field ids
     */
    public List<String> getForcedFieldsAsList()
    {
        return this.forcedFields;
    }

    private List<String> getFieldNamesHiddenInMode(Mode mode)
    {
        List<String> result = fieldVisibilityManager.getFieldNamesHiddenInMode(mode);
        return result;
    }

    private List<String> getFieldNamesVisibleInMode(Mode mode)
    {
        List<String> result = fieldVisibilityManager.getFieldNamesVisibleInMode(mode);
        return result;
    }

    /* package */void setSubmissionURL(String newURL)
    {
        this.submissionURL = newURL;
    }
    
    /* package */void setFormTemplate(String nodeName, String newTemplate)
    {
        if (nodeName.equals("create-form"))
        {
            createTemplate = newTemplate;
        }
        else if (nodeName.equals("edit-form"))
        {
            editTemplate = newTemplate;
        }
        else if (nodeName.equals("view-form"))
        {
            viewTemplate = newTemplate;
        }
        else
        {
            if (logger.isWarnEnabled())
            {
                logger.warn("Unrecognised mode: " + nodeName);
            }
            return;
        }
    }
    
    /* package */void addFieldVisibility(String showOrHide, String fieldId,
            String mode, String forceString)
    {
        fieldVisibilityManager.addInstruction(showOrHide, fieldId, mode);
        
        boolean isForced = new Boolean(forceString);
        if (isForced && (this.forcedFields.contains(fieldId) == false))
        {
            this.forcedFields.add(fieldId);
        }
    }
    
    /* package */void addSet(String setId, String parentSetId, String appearance,
             String label, String labelId)
    {
        this.addSet(setId, parentSetId, appearance, label, labelId, null);
    }
    
    /* package */void addSet(String setId, String parentSetId, String appearance,
                             String label, String labelId, String template)
    {
        FormSet newFormSetObject = new FormSet(setId, parentSetId, appearance, 
                    label, labelId, template);
        
        // We disallow the declaration of sets whose parents do not already exist.
        // The reason for this is to ensure that cycles within the parent structure
        // are not possible.
        if (parentSetId != null &&
                !sets.containsKey(parentSetId))
        {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Set [").append(setId).append("] has undefined parent [")
            .append(parentSetId).append("].");
            throw new ConfigException(errorMsg.toString());
        }
        
        // We disallow the creation of sets whose id is that of the default set and
        // who have parents. In other words, the default set must be a root set.
        if (setId.equals(DEFAULT_SET_ID) && parentSetId != null)
        {
            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("Default set cannot have any parent set. Parent specified was: [")
                .append(parentSetId).append("].");
            throw new ConfigException(errorMsg.toString());
        }
        
        sets.put(setId, newFormSetObject);
        
        // Set parent/child references
        if (parentSetId != null)
        {
            FormSet parentObject = sets.get(parentSetId);
            
            newFormSetObject.setParent(parentObject);
            parentObject.addChild(newFormSetObject);
        }
    }
    
    /* package */void addField(String fieldId, List<String> attributeNames,
            List<String> attributeValues)
    {
        if (attributeNames == null)
        {
            attributeNames = Collections.emptyList();
        }
        if (attributeValues == null)
        {
            attributeValues = Collections.emptyList();
        }
        if (attributeNames.size() < attributeValues.size()
                && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("field ")
            .append(fieldId)
            .append(" has ")
            .append(attributeNames.size())
            .append(" xml attribute names and ")
            .append(attributeValues.size())
            .append(" xml attribute values. The trailing extra data will be ignored.");
            logger.warn(msg.toString());
        }
        
        Map<String, String> attrs = new LinkedHashMap<String, String>();
        for (int i = 0; i < attributeNames.size(); i++)
        {
            attrs.put(attributeNames.get(i), attributeValues.get(i));
        }
        fields.put(fieldId, new FormField(fieldId, attrs));
    }
    
    /* package */ void setFields(Map<String, FormField> newFieldsMap)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("Setting new fields map " + newFieldsMap);
        }
        this.fields = newFieldsMap;
    }
    
    /* package */ void addControlForField(String fieldId, String template,
            List<String> controlParamNames, List<String> controlParamValues)
    {
        if (controlParamNames == null)
        {
            controlParamNames = Collections.emptyList();
        }
        if (controlParamValues == null)
        {
            controlParamValues = Collections.emptyList();
        }
        if (controlParamNames.size() < controlParamValues.size()
                && logger.isWarnEnabled())
        {
            StringBuilder msg = new StringBuilder();
            msg.append("field ")
            .append(fieldId)
            .append(" has ")
            .append(controlParamNames.size())
            .append(" control-param names and ")
            .append(controlParamValues.size())
            .append(" control-param values. The trailing extra data will be ignored.");
            logger.warn(msg.toString());
        }
        
        FormField field = fields.get(fieldId);
        field.getControl().setTemplate(template);
        for (int i = 0; i < controlParamNames.size(); i++)
        {
            ControlParam cp = new ControlParam(controlParamNames.get(i), controlParamValues.get(i));
            field.getControl().addControlParam(cp);
        }
    }
    
    /* package */void addConstraintForField(String fieldId, String type,
            String message, String messageId, String validationHandler, String event)
    {
        FormField field = fields.get(fieldId);
        field.addConstraintDefinition(type, message, messageId, validationHandler, event);
    }
}
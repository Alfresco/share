/*
 * #%L
 * Alfresco Web Framework common libraries
 * %%
 * Copyright (C) 2005 - 2021 Alfresco Software Limited
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
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.web.config.util.BaseTest;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * JUnit tests to exercise the forms-related capabilities in the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class FormConfigBasicTest extends BaseTest
{
    private static final String TEST_CONFIG_FORMS_BASIC_XML = "test-config-forms-basic.xml";
	protected XMLConfigService configService;
    protected Config globalConfig;
    protected ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormsConfigElement myExampleFormsConfigElement;
    protected FormConfigElement myExampleDefaultForm;
    protected FormsConfigElement noAppearanceFormsConfigElement;
    protected FormConfigElement noAppearanceDefaultForm;
    protected FormsConfigElement noVisibilityFormsConfigElement;
    protected FormConfigElement noVisibilityDefaultForm;
    protected FormsConfigElement hiddenFieldsFormsConfigElement;
    protected FormConfigElement hiddenFieldsDefaultForm;
    protected DefaultControlsConfigElement defltCtrlsConfElement;

    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add(TEST_CONFIG_FORMS_BASIC_XML);
        return result;
    }
    
    @Override
	public String getResourcesDir() {
		return "classpath:";
	}
    
    protected String getExpectedMessageForNumericConstraint()
    {
        return "Test Message";
    }
    
    protected List<ControlParam> getExpectedControlParamsForDText()
    {
        return Arrays.asList(new ControlParam("size", "50"));
    }
    
    protected List<ControlParam> getExpectedControlParamsForDTest()
    {
        return Arrays.asList(new ControlParam("a", "Hello"), new ControlParam("b", null));
    }
    
    /**
     * This method returns a List<String> containing the 3 expected templates for
     * respectively view, edit and create mode.
     * @return
     */
    protected List<String> getExpectedTemplatesForNoAppearanceDefaultForm()
    {
        return Arrays.asList(new String[]{"/view/template", "/edit/template", "/create/template"});
    }
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        configService = initXMLConfigService(getConfigFiles());
        assertNotNull("configService was null.", configService);
    
        Config myExampleConfigObj = configService.getConfig("my:example");
        assertNotNull(myExampleConfigObj);
    
        ConfigElement myExampleFormsConfigObj = myExampleConfigObj.getConfigElement("forms");
        assertNotNull(myExampleFormsConfigObj);
        assertTrue("formsConfigObj should be instanceof FormsConfigElement.",
                myExampleFormsConfigObj instanceof FormsConfigElement);
        myExampleFormsConfigElement = (FormsConfigElement) myExampleFormsConfigObj;
        myExampleDefaultForm = myExampleFormsConfigElement.getDefaultForm();
        assertNotNull(myExampleDefaultForm);
        
        Config noAppearanceConfigObj = configService.getConfig("no-appearance");
        assertNotNull(noAppearanceConfigObj);
    
        ConfigElement noAppearanceFormsConfigObj = noAppearanceConfigObj.getConfigElement("forms");
        assertNotNull(noAppearanceFormsConfigObj);
        assertTrue("noAppearanceFormsConfigObj should be instanceof FormsConfigElement.",
        		noAppearanceFormsConfigObj instanceof FormsConfigElement);
        noAppearanceFormsConfigElement = (FormsConfigElement) noAppearanceFormsConfigObj;
        noAppearanceDefaultForm = noAppearanceFormsConfigElement.getDefaultForm();
        assertNotNull(noAppearanceDefaultForm);
        
        Config noVisibilityConfigObj = configService.getConfig("no-visibility");
        assertNotNull(noVisibilityConfigObj);
    
        ConfigElement noVisibilityFormsConfigObj = noVisibilityConfigObj.getConfigElement("forms");
        assertNotNull(noVisibilityFormsConfigObj);
        assertTrue("noVisibilityFormsConfigObj should be instanceof FormsConfigElement.",
        		noVisibilityFormsConfigObj instanceof FormsConfigElement);
        noVisibilityFormsConfigElement = (FormsConfigElement) noVisibilityFormsConfigObj;
        noVisibilityDefaultForm = noVisibilityFormsConfigElement.getDefaultForm();
        assertNotNull(noVisibilityDefaultForm);
        
        Config hiddenFieldsConfigObj = configService.getConfig("hidden-fields");
        assertNotNull(hiddenFieldsConfigObj);
    
        ConfigElement hiddenFieldsFormsConfigObj = hiddenFieldsConfigObj.getConfigElement("forms");
        assertNotNull(hiddenFieldsFormsConfigObj);
        assertTrue("hiddenFieldsFormsConfigObj should be instanceof FormsConfigElement.",
                    hiddenFieldsFormsConfigObj instanceof FormsConfigElement);
        hiddenFieldsFormsConfigElement = (FormsConfigElement) hiddenFieldsFormsConfigObj;
        hiddenFieldsDefaultForm = hiddenFieldsFormsConfigElement.getDefaultForm();
        assertNotNull(hiddenFieldsDefaultForm);
        
        globalConfig = configService.getGlobalConfig();
    
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
        globalDefaultControls = globalForms.getDefaultControls();
        assertNotNull("global default-controls element should not be null",
                globalDefaultControls);
        assertTrue("config element should be an instance of DefaultControlsConfigElement",
                (globalDefaultControls instanceof DefaultControlsConfigElement));
        defltCtrlsConfElement = (DefaultControlsConfigElement) globalDefaultControls;
    
        globalConstraintHandlers = globalForms.getConstraintHandlers();
        assertNotNull("global constraint-handlers element should not be null",
                globalConstraintHandlers);
        assertTrue(
                "config element should be an instance of ConstraintHandlersConfigElement",
                (globalConstraintHandlers instanceof ConstraintHandlersConfigElement));
    }
    
    public void testGetDefaultFormElement() throws Exception
    {
    	FormConfigElement defaultFormCE = myExampleFormsConfigElement.getDefaultForm();
    	assertNotNull(defaultFormCE);
    	
    	assertEquals("submit/default/form", defaultFormCE.getSubmissionURL());
    	assertNull(defaultFormCE.getId());
    }

    public void testGetNonexistentDefaultFormElement() throws Exception
    {
        Config noDefaultConfigObj = configService.getConfig("no-default-form");
        assertNotNull(noDefaultConfigObj);
    
        ConfigElement noDefaultFormsConfigObj = noDefaultConfigObj.getConfigElement("forms");
        assertNotNull(noDefaultFormsConfigObj);
        assertTrue("noDefaultFormsConfigObj should be instanceof FormsConfigElement.",
                noDefaultFormsConfigObj instanceof FormsConfigElement);
        FormsConfigElement noDefaultFormsConfigElement = (FormsConfigElement) noDefaultFormsConfigObj;
        FormConfigElement noDefaultForm = noDefaultFormsConfigElement.getDefaultForm();
        assertNull(noDefaultForm);
    }

    public void testGetFormElementById() throws Exception
    {
    	FormConfigElement formCE = myExampleFormsConfigElement.getForm("id");
    	assertNotNull(formCE);

    	assertEquals("submit/id/form", formCE.getSubmissionURL());
    	assertEquals("id", formCE.getId());
    }

    public void testGetNonexistentFormElementById() throws Exception
    {
    	FormConfigElement noSuchFormCE = myExampleFormsConfigElement.getForm("rubbish");
    	assertNull(noSuchFormCE);
    }

    public void testFormSubmissionUrl()
    {
        assertEquals("Submission URL was incorrect.", "submit/default/form",
                myExampleDefaultForm.getSubmissionURL());
    }
    
    public void testGetFormTemplatesForViewEditCreate() throws Exception
    {
        FormConfigElement testForm = noAppearanceFormsConfigElement.getDefaultForm();
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(0), testForm.getViewTemplate());
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(1), testForm.getEditTemplate());
        assertEquals(getExpectedTemplatesForNoAppearanceDefaultForm().get(2), testForm.getCreateTemplate());
    }
    
    public void testGlobalConstraintHandlers()
    {
        assertEquals(ConstraintHandlersConfigElement.class, globalConstraintHandlers.getClass());
        ConstraintHandlersConfigElement constraintHandlers
                = (ConstraintHandlersConfigElement)globalConstraintHandlers;
        
        Map<String, ConstraintHandlerDefinition> constraintItems = constraintHandlers.getItems();
        assertEquals("Incorrect count for global constraint-handlers.",
                3, constraintItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("MANDATORY");
        expectedTypeNames.add("REGEX");
        expectedTypeNames.add("NUMERIC");
        assertEquals("Incorrect global constraint-handler types.", expectedTypeNames,
                constraintItems.keySet());
        
        ConstraintHandlerDefinition mandatoryItem = constraintItems.get("MANDATORY");
        assertNotNull(mandatoryItem);
        ConstraintHandlerDefinition regexItem = constraintItems.get("REGEX");
        assertNotNull(regexItem);
        ConstraintHandlerDefinition numericItem = constraintItems.get("NUMERIC");
        assertNotNull(numericItem);
        
        assertEquals("Alfresco.forms.validation.mandatory", mandatoryItem.getValidationHandler());
        assertEquals("Alfresco.forms.validation.regexMatch", regexItem.getValidationHandler());
        assertEquals("Alfresco.forms.validation.numericMatch", numericItem.getValidationHandler());
        
        assertEquals("blur", mandatoryItem.getEvent());
        assertEquals(null, regexItem.getEvent());
        assertEquals(null, numericItem.getEvent());

        assertEquals(null, mandatoryItem.getMessage());
        assertEquals(null, regexItem.getMessage());
        assertEquals(getExpectedMessageForNumericConstraint(), numericItem.getMessage());

        assertEquals(null, mandatoryItem.getMessageId());
        assertEquals(null, regexItem.getMessageId());
        assertEquals("regex_error", numericItem.getMessageId());
    }

    public void testGlobalDefaultControls()
    {
        assertEquals(DefaultControlsConfigElement.class, globalDefaultControls.getClass());

        DefaultControlsConfigElement defaultControls
                = (DefaultControlsConfigElement)globalDefaultControls;
        
        Map<String, Control> defCtrlItems = defaultControls.getItems();
        assertEquals("Incorrect count for global default-controls.",
                6, defCtrlItems.size());
        
        Set<String> expectedTypeNames = new HashSet<String>();
        expectedTypeNames.add("d:long");
        expectedTypeNames.add("d:text");
        expectedTypeNames.add("d:test");
        expectedTypeNames.add("d:boolean");
        expectedTypeNames.add("association");
        expectedTypeNames.add("abc");
        assertEquals("Incorrect global default-control types.", expectedTypeNames,
                defCtrlItems.keySet());
        
        Control longItem = defCtrlItems.get("d:long");
        assertNotNull(longItem);
        Control textItem = defCtrlItems.get("d:text");
        assertNotNull(textItem);
        Control testItem = defCtrlItems.get("d:test");
        assertNotNull(testItem);
        
        assertEquals("/form-controls/mytextfield.ftl", longItem.getTemplate());
        assertEquals("/form-controls/mytextfield.ftl", textItem.getTemplate());
        assertEquals("/form-controls/test.ftl", testItem.getTemplate());
        
        assertEquals(Collections.emptyList(), longItem.getParamsAsList());
        assertEquals(getExpectedControlParamsForDText(),
                textItem.getParamsAsList());
        assertEquals(getExpectedControlParamsForDTest(),
                testItem.getParamsAsList());
    }

    /*
     * The datatypes and idioms used to access control-params at the global default-control
     * level and at the individual field level should be consistent.
     */
    public void testControlParamsAreConsistentBetweenGlobalAndFieldLevel()
    {
        DefaultControlsConfigElement defaultControls
                = (DefaultControlsConfigElement)globalDefaultControls;
        
        Map<String, Control> defCtrlItems = defaultControls.getItems();
        List<ControlParam> controlParamsGlobal = defCtrlItems.get("d:test").getParamsAsList();
        
        List<ControlParam> controlParamsField = myExampleDefaultForm.getFields().get("my:text").getControl().getParamsAsList();
        
        // The simple fact that the above code compiles and runs is enough to ensure
        // that the APIs are consistent. But here's an assert to dissuade changes.
        assertEquals(controlParamsGlobal.getClass(), controlParamsField.getClass());
    }
    
    public void testFormConfigElementShouldHaveNoChildren()
    {
        try
        {
            myExampleDefaultForm.getChildren();
            fail("getChildren() did not throw an exception.");
        } catch (ConfigException expectedException)
        {
            // intentionally empty
        }
    }
    
    public void testEmptyConstraintsMsgs()
    {
        // check the messages on the cm:name field
        FormField field = myExampleDefaultForm.getFields().get("cm:name");
        assertNotNull("Expecting cm:name to be present", field);
        Map<String, ConstraintHandlerDefinition> constraints = field.getConstraintDefinitionMap();
        assertNotNull(constraints);
        ConstraintHandlerDefinition constraint = constraints.get("REGEX");
        assertNotNull(constraint);
        assertNull(constraint.getMessageId());
        assertEquals("You can't have these characters in a name: /*", constraint.getMessage());
        
        // check the messages on the cm:text field
        field = myExampleDefaultForm.getFields().get("my:text");
        assertNotNull("Expecting cm:text to be present", field);
        constraints = field.getConstraintDefinitionMap();
        assertNotNull(constraints);
        constraint = constraints.get("REGEX");
        assertNotNull(constraint);
        assertNull(constraint.getMessage());
        assertEquals("custom_msg", constraint.getMessageId());
    }
    
    public void testFieldsVisibleInViewModeShouldStillBeVisibleWithNoAppearanceTag()
    {
        List<String> fieldNames = noAppearanceDefaultForm.getVisibleViewFieldNamesAsList();
        
        // The order specified in the config XML should also be preserved.
        List<String> expectedFieldNames = new ArrayList<String>();
        expectedFieldNames.add("cm:name");
        expectedFieldNames.add("cm:title");
        expectedFieldNames.add("cm:description");
        expectedFieldNames.add("cm:content");
        expectedFieldNames.add("my:text");
        expectedFieldNames.add("my:mltext");
        expectedFieldNames.add("my:date");
        
        assertEquals("Visible fields wrong.", expectedFieldNames, fieldNames);
    }

    public void testGetFormFieldVisibilitiesForModes()
    {
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.CREATE));
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.CREATE));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.CREATE));

        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.EDIT));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.EDIT));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.EDIT));

        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:name", Mode.VIEW));
        assertTrue("Field should be visible.", noAppearanceDefaultForm
                .isFieldVisible("cm:title", Mode.VIEW));
        assertFalse("Field should be invisible.", noAppearanceDefaultForm
                .isFieldVisible("rubbish", Mode.VIEW));
    }

    public void testGetForcedFields()
    {
        List<String> forcedFields = noAppearanceDefaultForm.getForcedFieldsAsList();
        assertEquals("Expecting one forced field", 1, forcedFields.size());

        assertTrue("Expected cm:name to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:name"));
        assertFalse("Expected cm:title not to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:title"));
    }

    public void testGetVisibleFieldsForFormWithoutFieldVisibilityReturnsNull()
    {
        assertEquals(null, noVisibilityDefaultForm.getVisibleCreateFieldNamesAsList());
        assertEquals(null, noVisibilityDefaultForm.getVisibleEditFieldNamesAsList());
        assertEquals(null, noVisibilityDefaultForm.getVisibleViewFieldNamesAsList());
    }
    
    public void testFieldVisibilityForTwoCombinedFormTags()
    {
        FormConfigElement combinedConfig = (FormConfigElement)myExampleDefaultForm.combine(noVisibilityFormsConfigElement.getDefaultForm());
        
        Set<String> expectedFields = new LinkedHashSet<String>();
        expectedFields.add("cm:name");
        expectedFields.add("my:text");
        expectedFields.add("my:mltext");
        expectedFields.add("my:date");
        expectedFields.add("my:duplicate");
        expectedFields.add("my:int");
        expectedFields.add("my:broken");
        
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleCreateFieldNamesAsList());
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleEditFieldNamesAsList());
        assertEquals(new ArrayList<String>(expectedFields), myExampleDefaultForm.getVisibleViewFieldNamesAsList());

        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleCreateFieldNamesAsList());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleEditFieldNamesAsList());
        assertEquals(new ArrayList<String>(expectedFields), combinedConfig.getVisibleViewFieldNamesAsList());
    }
    
    /**
     * This test case should test the overriding of a constraint handler from the default
     * (or form) level, to a single field and on to an overridden field.
     * 
     * @throws Exception
     */
    public void testConstraintHandlerOnField() throws Exception
    {
    	// The default or form level constraint handler
    	ConstraintHandlersConfigElement defaultConstraintHandlers
    	    = (ConstraintHandlersConfigElement)globalConstraintHandlers;
    	Map<String, ConstraintHandlerDefinition> handlers = defaultConstraintHandlers.getItems();
    	
    	ConstraintHandlerDefinition regexConstraintHandler = handlers.get("REGEX");
    	assertNotNull(regexConstraintHandler);
    	
    	assertEquals("REGEX", regexConstraintHandler.getType());
    	assertEquals("Alfresco.forms.validation.regexMatch", regexConstraintHandler.getValidationHandler());
    	assertNull(regexConstraintHandler.getMessage());
    	assertNull(regexConstraintHandler.getMessageId());
    	assertNull(regexConstraintHandler.getEvent());
    	
    	//TODO Currently if we define a constraint-handler on a field which overrides 
    	// the default (form-level) constraint, the properties of the constraint-handler
    	// are only those of the field-level constraint. The form-level default one is
    	// not inherited.

    	ConstraintHandlerDefinition regexFieldConstr
    	    = myExampleDefaultForm.getFields().get("cm:name").getConstraintDefinitionMap().get("REGEX");
    	assertNotNull(regexFieldConstr);
    	assertEquals("REGEX", regexFieldConstr.getType());
    	assertEquals("Alfresco.forms.validation.regexMatch", regexFieldConstr.getValidationHandler());
    	assertEquals("You can't have these characters in a name: /*", regexFieldConstr.getMessage());
    	assertNull(regexFieldConstr.getMessageId());
    	assertNull(regexFieldConstr.getEvent());
    	
    	// We also need to support multiple constraint-handlers on a single field.
    	ConstraintHandlerDefinition numericFieldConstr
    	    = myExampleDefaultForm.getFields().get("cm:name").getConstraintDefinitionMap().get("NUMERIC");
    	assertNotNull(numericFieldConstr);
    }

    /**
     * This test checks that the expected JS and CSS resources are available.
     */
    public void testGetDependencies() throws Exception
    {
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
        
        DependenciesConfigElement depsCE = globalForms.getDependencies();
        assertNotNull(depsCE);

        // We want the dependencies as arrays as these are more JS-friendly than
        // Lists, but I'll compare the expected values as Lists.
        String[] expectedCssDependencies = new String[]{"/css/path/1", "/css/path/2"};
        String[] expectedJsDependencies = new String[]{"/js/path/1", "/js/path/2"};

        assertEquals(Arrays.asList(expectedCssDependencies), Arrays.asList(depsCE.getCss()));
        assertEquals(Arrays.asList(expectedJsDependencies), Arrays.asList(depsCE.getJs()));
    }
    
    /**
     * This test checks that appearance config can be accessed when there is no visibility element. 
     */
    public void testNoVisibility() throws Exception
    {
        // test that appearance data can be retrieved
        FormField nameField = noVisibilityDefaultForm.getFields().get("cm:name");
        assertNotNull(nameField);
        assertEquals("Name From Config", nameField.getLabel());
        assertNull(nameField.getDescription());
        
        FormField xxField = noVisibilityDefaultForm.getFields().get("xx:irrelevant");
        assertNotNull(xxField);
        assertEquals("label-text", xxField.getLabel());
        assertEquals("optional", xxField.getSet());
        assertNull(xxField.getDescription());
        
        // make sure that get visible field methods return null
        assertNull(noVisibilityDefaultForm.getVisibleCreateFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getVisibleCreateFieldNames());
        assertNull(noVisibilityDefaultForm.getVisibleEditFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getVisibleEditFieldNames());
        assertNull(noVisibilityDefaultForm.getVisibleViewFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getVisibleViewFieldNames());
        
        // make sure that get hidden field methods return null
        assertNull(noVisibilityDefaultForm.getHiddenCreateFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getHiddenCreateFieldNames());
        assertNull(noVisibilityDefaultForm.getHiddenEditFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getHiddenEditFieldNames());
        assertNull(noVisibilityDefaultForm.getHiddenViewFieldNamesAsList());
        assertNull(noVisibilityDefaultForm.getHiddenViewFieldNames());
    }

    /**
     * This test checks the lookup of explicitly listed hidden fields
     */
    public void testHiddenFields() throws Exception
    {
        // make sure that get hidden field methods do NOT return null
        assertNotNull(hiddenFieldsDefaultForm.getHiddenCreateFieldNamesAsList());
        assertNotNull(hiddenFieldsDefaultForm.getHiddenCreateFieldNames());
        assertNotNull(hiddenFieldsDefaultForm.getHiddenEditFieldNamesAsList());
        assertNotNull(hiddenFieldsDefaultForm.getHiddenEditFieldNames());
        assertNotNull(hiddenFieldsDefaultForm.getHiddenViewFieldNamesAsList());
        assertNotNull(hiddenFieldsDefaultForm.getHiddenViewFieldNames());
        
        // make sure that the correct fields are hidden for view mode
        List<String> hiddenViewFields = hiddenFieldsDefaultForm.getHiddenViewFieldNamesAsList();
        String[] hiddenViewFieldsArray = hiddenFieldsDefaultForm.getHiddenViewFieldNames();
        assertEquals(2, hiddenViewFieldsArray.length);
        assertEquals(2, hiddenViewFields.size());
        assertEquals("custom:first", hiddenViewFields.get(0));
        assertEquals("custom:second", hiddenViewFields.get(1));
        
        // make sure that the correct fields are hidden for edit mode
        List<String> hiddenEditFields = hiddenFieldsDefaultForm.getHiddenEditFieldNamesAsList();
        String[] hiddenEditFieldsArray = hiddenFieldsDefaultForm.getHiddenEditFieldNames();
        assertEquals(3, hiddenEditFieldsArray.length);
        assertEquals(3, hiddenEditFields.size());
        assertEquals("custom:first", hiddenEditFields.get(0));
        assertEquals("custom:second", hiddenEditFields.get(1));
        assertEquals("sys:dbid", hiddenEditFields.get(2));
        
        // make sure that the correct fields are hidden for edit mode
        List<String> hiddenCreateFields = hiddenFieldsDefaultForm.getHiddenCreateFieldNamesAsList();
        String[] hiddenCreateFieldsArray = hiddenFieldsDefaultForm.getHiddenCreateFieldNames();
        assertEquals(3, hiddenCreateFieldsArray.length);
        assertEquals(3, hiddenCreateFields.size());
        assertEquals("custom:first", hiddenCreateFields.get(0));
        assertEquals("custom:second", hiddenCreateFields.get(1));
        assertEquals("sys:dbid", hiddenCreateFields.get(2));
        
        // check that the appaearance data can be retrieved
        Map<String, FormField> fields = hiddenFieldsDefaultForm.getFields();
        assertNotNull(fields);
        FormField nameField = fields.get("custom:name");
        assertNotNull(nameField);
        assertEquals("main", nameField.getSet());
        
        // check that the hidden status for fields are correct
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:first", Mode.VIEW));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:first", Mode.EDIT));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:first", Mode.CREATE));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:second", Mode.VIEW));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:second", Mode.EDIT));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("custom:second", Mode.CREATE));
        assertFalse(hiddenFieldsDefaultForm.isFieldHidden("sys:dbid", Mode.VIEW));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("sys:dbid", Mode.EDIT));
        assertTrue(hiddenFieldsDefaultForm.isFieldHidden("sys:dbid", Mode.CREATE));
        
        // check the string version 
        assertFalse(hiddenFieldsDefaultForm.isFieldHiddenInMode("sys:dbid", "view"));
        assertTrue(hiddenFieldsDefaultForm.isFieldHiddenInMode("sys:dbid", "edit"));
        assertTrue(hiddenFieldsDefaultForm.isFieldHiddenInMode("sys:dbid", "create"));
        
        // check the status for non-existent fields
        assertFalse(hiddenFieldsDefaultForm.isFieldHidden("non:existent", Mode.VIEW));
        assertFalse(hiddenFieldsDefaultForm.isFieldHidden("non:existent", Mode.EDIT));
        assertFalse(hiddenFieldsDefaultForm.isFieldHidden("non:existent", Mode.CREATE));
        
    }
    
    /**
     * Tests the 'mandatory' attribute
     */
    public void testMandatoryField() throws Exception
    {
        // make sure the default for a field without mandatory attribute is false
        FormField duplicateField = myExampleDefaultForm.getFields().get("my:duplicate");
        assertNotNull(duplicateField);
        assertFalse(duplicateField.isMandatory());
        
        FormField mandatoryField = myExampleDefaultForm.getFields().get("my:mandatory");
        assertNotNull(mandatoryField);
        assertTrue(mandatoryField.isMandatory());
    }
}

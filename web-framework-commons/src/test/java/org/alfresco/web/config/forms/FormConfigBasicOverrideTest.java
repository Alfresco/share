package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;

public class FormConfigBasicOverrideTest extends FormConfigBasicTest
{
    // This class inherits all of its test messages from the superclass and simply
    // overrides a number of changed properties - mimicking the changes in the xml.
    @Override
    protected List<String> getConfigFiles()
    {
        List<String> result = new ArrayList<String>(1);
        result.add("test-config-forms-basic.xml");
        result.add("test-config-forms-basic-override.xml");
        return result;
    }
    
    @Override
	public String getResourcesDir() {
		return "classpath:";
	}

    @Override
    protected String getExpectedMessageForNumericConstraint()
    {
        return "Test Message override";
    }

    @Override
    protected List<ControlParam> getExpectedControlParamsForDText()
    {
        return Arrays.asList(new ControlParam("size", "999"));
    }
    
    @Override
    protected List<ControlParam> getExpectedControlParamsForDTest()
    {
        return Arrays.asList(new ControlParam("a", "Goodbye"),
                new ControlParam("b", null),
                new ControlParam("c", "This is new"));
    }

    @Override
    protected List<String> getExpectedTemplatesForNoAppearanceDefaultForm()
    {
        return Arrays.asList(new String[]{"/view/template/override",
                "/edit/template/override", "/create/template/override"});
    }
    
    @Override
    public void testGetForcedFields()
    {
        List<String> forcedFields = noAppearanceDefaultForm.getForcedFieldsAsList();
        assertEquals("Wrong forced fields count", 2, forcedFields.size());

        assertTrue("Expected cm:name to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:name"));
        assertTrue("Expected cm:description to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:description"));
        assertFalse("Expected cm:title not to be forced", noAppearanceDefaultForm
                .isFieldForced("cm:title"));
    }

    /**
     * This test checks that the expected JS and CSS resources are available.
     */
    @Override
    public void testGetDependencies() throws Exception
    {
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");

        DependenciesConfigElement depsCE = globalForms.getDependencies();
        assertNotNull(depsCE);
        
        // We want the dependencies as arrays as these are more JS-friendly than
        // Lists, but I'll compare the expected values as Lists.
        String[] expectedCssDependencies = new String[]{"/css/path/1", "/css/path/2", "/css/path/3"};
        String[] expectedJsDependencies = new String[]{"/js/path/1", "/js/path/2", "/js/path/3"};

        assertEquals(Arrays.asList(expectedCssDependencies), Arrays.asList(depsCE.getCss()));
        assertEquals(Arrays.asList(expectedJsDependencies), Arrays.asList(depsCE.getJs()));
    }
    
    public void testCombiningFormsAcrossXmlFiles() throws Exception
    {
        Config testConfigObj = configService.getConfig("combiningFormsAcrossXmlFiles");
        assertNotNull(testConfigObj);

        ConfigElement testFormsConfigObj = testConfigObj.getConfigElement("forms");
        assertNotNull(testFormsConfigObj);
        FormsConfigElement testFormsConfigElement = (FormsConfigElement) testFormsConfigObj;
        FormConfigElement testDefaultForm = testFormsConfigElement.getDefaultForm();
        assertNotNull(testDefaultForm);
        
        FormConfigElement formWithId = testFormsConfigElement.getForm("testing");
        assertNotNull(formWithId);
    }
    
    public void testCustomFormTemplatesOverride() throws Exception
    {
        Config testConfigObj = configService.getConfig("custom-form-templates");
        assertNotNull(testConfigObj);
        
        ConfigElement testFormsConfigObj = testConfigObj.getConfigElement("forms");
        assertNotNull(testFormsConfigObj);
        FormsConfigElement testFormsConfigElement = (FormsConfigElement) testFormsConfigObj;
        FormConfigElement testDefaultForm = testFormsConfigElement.getDefaultForm();
        assertNotNull(testDefaultForm);
        
        String viewTemplate = testDefaultForm.getViewTemplate();
        assertEquals("/path/to/my/template.ftl", viewTemplate);
        
        String createTemplate = testDefaultForm.getCreateTemplate();
        assertEquals("/path/to/my/template.ftl", createTemplate);
        
        String editTemplate = testDefaultForm.getEditTemplate();
        assertEquals("/path/to/my/template.ftl", editTemplate);
    }
}

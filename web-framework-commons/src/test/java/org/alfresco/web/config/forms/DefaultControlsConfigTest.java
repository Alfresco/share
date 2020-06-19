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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.alfresco.util.BaseTest;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service. These tests only include those that require a single config
 * xml file. Override-related tests, which use multiple config xml files, are
 * located in peer classes in this package.
 * 
 * @author Neil McErlean
 */
public class DefaultControlsConfigTest extends BaseTest
{
    protected XMLConfigService configService;
    protected Config globalConfig;
    protected ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormsConfigElement formsConfigElement;
    protected FormConfigElement defaultFormConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;

    protected String getConfigXmlFile()
    {
        return "test-config-forms-basic.xml";
    }
    
    @Override
	public String getResourcesDir() {
		return "classpath:";
	}
    
    @SuppressWarnings("unchecked")
	public void testDefaultControls_MappingNameToTemplate()
    {
        // Test that the default-control types are read from the config file
        Map<String, String> expectedDataMappings = new HashMap<String, String>();
        expectedDataMappings.put("d:long", "/form-controls/mytextfield.ftl");
        expectedDataMappings.put("d:text", "/form-controls/mytextfield.ftl");
        expectedDataMappings.put("d:test", "/form-controls/test.ftl");
        expectedDataMappings.put("d:boolean", "/form-controls/checkbox.ftl");
        expectedDataMappings.put("association", "/form-controls/association-picker.ftl");
        expectedDataMappings.put("abc", "/form-controls/abc.ftl");

        List<String> actualNames = defltCtrlsConfElement.getItemNamesAsList();
        assertEquals("Incorrect name count, expected "
                + expectedDataMappings.size(), expectedDataMappings.size(),
                actualNames.size());

        assertEquals(expectedDataMappings.keySet(), new HashSet(actualNames));
        
        // Test that the datatypes map to the expected template.
        for (String nextKey : expectedDataMappings.keySet())
        {
            String nextExpectedValue = expectedDataMappings.get(nextKey);
            String nextActualValue = defltCtrlsConfElement.getTemplateFor(nextKey);
            assertTrue("Incorrect template for " + nextKey + ": "
                    + nextActualValue, nextExpectedValue
                    .equals(nextActualValue));
        }
    }

    @SuppressWarnings("unchecked")
    public void testControlParams()
    {
        Map<String, List<ControlParam>> expectedControlParams = new HashMap<String, List<ControlParam>>();

        List<ControlParam> textParams = new ArrayList<ControlParam>();
        textParams.add(new ControlParam("size", "50"));

        List<ControlParam> abcParams = new ArrayList<ControlParam>();
        abcParams.add(new ControlParam("a", "1"));
        abcParams.add(new ControlParam("b", "Hello"));
        abcParams.add(new ControlParam("c", "For ever and ever."));
        abcParams.add(new ControlParam("d", ""));

        expectedControlParams.put("d:text", textParams);
        expectedControlParams.put("d:boolean", Collections.EMPTY_LIST);
        expectedControlParams.put("association", Collections.EMPTY_LIST);
        expectedControlParams.put("abc", abcParams);

        for (String name : expectedControlParams.keySet())
        {
            List<ControlParam> actualControlParams = defltCtrlsConfElement
                    .getControlParamsAsListFor(name);
            assertEquals("Incorrect params for " + name, expectedControlParams
                    .get(name), actualControlParams);
        }
    }

    public void testDefaultControlsConfigElementShouldHaveNoChildren()
    {
        try
        {
            defltCtrlsConfElement.getChildren();
            fail("getChildren() did not throw an exception");
        } catch (ConfigException ce)
        {
            // expected exception
        }
    }

    /**
     * Tests the combination of a DefaultControlsConfigElement with another that
     * contains additional data.
     */
    public void testCombineDefaultControlsWithAddedParam()
    {
        DefaultControlsConfigElement basicElement = new DefaultControlsConfigElement();
        basicElement.addDataMapping("text", "path/textbox.ftl", null);

        // This element is the same as the above, but adds a control-param.
        DefaultControlsConfigElement parameterisedElement = new DefaultControlsConfigElement();
        List<ControlParam> testParams = new ArrayList<ControlParam>();
        testParams.add(new ControlParam("A", "1"));
        parameterisedElement.addDataMapping("text", "path/textbox.ftl",
                testParams);

        DefaultControlsConfigElement combinedElem
                = (DefaultControlsConfigElement)basicElement.combine(parameterisedElement);
        
        assertEquals("path/textbox.ftl", combinedElem.getItems().get("text").getTemplate());
        List<ControlParam> actualControlParams = combinedElem.getItems().get("text").getParamsAsList();
        assertEquals("Wrong count of control-params", 1, actualControlParams.size());
        assertEquals("A", actualControlParams.get(0).getName());
        assertEquals("1", actualControlParams.get(0).getValue());
    }

    /**
     * Tests the combination of a DefaultControlsConfigElement with another that
     * contains modified data.
     */
    public void testCombineDefaultControlsWithModifiedParam()
    {
        DefaultControlsConfigElement initialElement = new DefaultControlsConfigElement();
        List<ControlParam> testParams = new ArrayList<ControlParam>();
        testParams.add(new ControlParam("A", "1"));
        initialElement.addDataMapping("text", "path/textbox.ftl", testParams);

        // This element is the same as the above, but modifies the
        // control-param.
        DefaultControlsConfigElement modifiedElement = new DefaultControlsConfigElement();
        List<ControlParam> modifiedTestParams = new ArrayList<ControlParam>();
        modifiedTestParams.add(new ControlParam("A", "5"));
        modifiedElement.addDataMapping("text", "path/textbox.ftl",
                modifiedTestParams);

        DefaultControlsConfigElement combinedElem
               = (DefaultControlsConfigElement)initialElement.combine(modifiedElement);

        assertEquals("path/textbox.ftl", combinedElem.getItems().get("text").getTemplate());
        List<ControlParam> actualControlParams = combinedElem.getItems().get("text").getParamsAsList();
        assertEquals("Wrong count of control-params", 1, actualControlParams.size());
        assertEquals("A", actualControlParams.get(0).getName());
        assertEquals("5", actualControlParams.get(0).getValue());
    }

    /**
     * Tests the combination of a DefaultControlsConfigElement with another that
     * contains deleted data.
     */
    public void testCombineDefaultControlsWithDeletedParam()
    {
        DefaultControlsConfigElement initialElement = new DefaultControlsConfigElement();
        List<ControlParam> testParams = new ArrayList<ControlParam>();
        testParams.add(new ControlParam("A", "1"));
        initialElement.addDataMapping("text", "path/textbox.ftl", testParams);

        // This element is the same as the above, but deletes the
        // control-param.
        DefaultControlsConfigElement modifiedElement = new DefaultControlsConfigElement();
        modifiedElement.addDataMapping("text", "path/textbox.ftl", null);

        DefaultControlsConfigElement combinedElem
               = (DefaultControlsConfigElement)initialElement.combine(modifiedElement);

        assertEquals("path/textbox.ftl", combinedElem.getItems().get("text").getTemplate());
        List<ControlParam> actualControlParams = combinedElem.getItems().get("text").getParamsAsList();
        assertEquals("Wrong count of control-params", 0, actualControlParams.size());
    }
    
    /**
     * Tests the combination of a DefaultControlsConfigElement with another
     * with only new data types.
     */
    public void testCombineDefaultControlsWithNewDataType()
    {
        DefaultControlsConfigElement initialElement = new DefaultControlsConfigElement();
        List<ControlParam> testParams = new ArrayList<ControlParam>();
        testParams.add(new ControlParam("A", "1"));
        initialElement.addDataMapping("text", "path/textbox.ftl", testParams);

        // This element is unrelated to the above, just adding another data type
        DefaultControlsConfigElement newElement = new DefaultControlsConfigElement();
        newElement.addDataMapping("text4444", "path/textbox.ftl", null);

        DefaultControlsConfigElement combinedElem
               = (DefaultControlsConfigElement)initialElement.combine(newElement);

        assertEquals("path/textbox.ftl", combinedElem.getItems().get("text").getTemplate());
        List<ControlParam> actualControlParams = combinedElem.getItems().get("text").getParamsAsList();
        assertEquals("Wrong count of control-params", 1, actualControlParams.size());
    }

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
        configService = initXMLConfigService(getConfigXmlFile());
        assertNotNull("configService was null.", configService);
    
        Config myExampleConfig = configService.getConfig("my:example");
        assertNotNull("myExampleConfig was null.", myExampleConfig);
    
        ConfigElement confElement = myExampleConfig.getConfigElement("forms");
        assertNotNull("confElement was null.", confElement);
        assertTrue("confElement should be instanceof FormsConfigElement.",
                confElement instanceof FormsConfigElement);
        formsConfigElement = (FormsConfigElement) confElement;
        defaultFormConfigElement = formsConfigElement.getDefaultForm();
    
        globalConfig = configService.getGlobalConfig();
        ConfigElement globalFormsCE = globalConfig.getConfigElement("forms");
        final FormsConfigElement globalForms = (FormsConfigElement)globalFormsCE;

        this.defltCtrlsConfElement = globalForms.getDefaultControls();
        assertNotNull("global default-controls element should not be null",
                defltCtrlsConfElement);
    
        assertNotNull("global constraint-handlers element should not be null",
                globalForms.getConstraintHandlers());
    }
}

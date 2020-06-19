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
public class ConstraintHandlersConfigTest extends BaseTest
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
	public void testReadConstraintHandlersFromConfigXml()
    {
        // Test that the constraint-handlers' constraints are read from the
        // config file
        Map<String, String> expectedValidationHandlers = new HashMap<String, String>();
        expectedValidationHandlers.put("MANDATORY",
                "Alfresco.forms.validation.mandatory");
        expectedValidationHandlers.put("REGEX",
                "Alfresco.forms.validation.regexMatch");
        expectedValidationHandlers.put("NUMERIC",
                "Alfresco.forms.validation.numericMatch");

        ConstraintHandlersConfigElement chConfigElement
            = (ConstraintHandlersConfigElement) globalConstraintHandlers;
        List<String> actualTypes = chConfigElement.getConstraintTypesAsList();
        assertEquals("Incorrect type count.",
                expectedValidationHandlers.size(), actualTypes.size());
        
        assertEquals(expectedValidationHandlers.keySet(), new HashSet(actualTypes));

        // Test that the types map to the expected validation handler.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedValidationHandlers.get(nextKey);
            String nextActualValue = chConfigElement
                    .getValidationHandlerFor(nextKey);
            assertTrue("Incorrect handler for " + nextKey + ": "
                    + nextActualValue, nextExpectedValue
                    .equals(nextActualValue));
        }

        // Test that the constraint-handlers' messages are read from the config
        // file
        Map<String, String> expectedMessages = new HashMap<String, String>();
        expectedMessages.put("MANDATORY", null);
        expectedMessages.put("REGEX", null);
        expectedMessages.put("NUMERIC", "Test Message");

        // Test that the types map to the expected message.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedMessages.get(nextKey);
            String nextActualValue = chConfigElement.getMessageFor(nextKey);
            assertEquals("Incorrect message for " + nextKey + ".",
                    nextExpectedValue, nextActualValue);
        }

        // Test that the constraint-handlers' message-ids are read from the
        // config file
        Map<String, String> expectedMessageIDs = new HashMap<String, String>();
        expectedMessageIDs.put("MANDATORY", null);
        expectedMessageIDs.put("REGEX", null);
        expectedMessageIDs.put("NUMERIC", "regex_error");

        // Test that the types map to the expected message-id.
        for (String nextKey : expectedValidationHandlers.keySet())
        {
            String nextExpectedValue = expectedMessageIDs.get(nextKey);
            String nextActualValue = chConfigElement.getMessageIdFor(nextKey);
            assertEquals("Incorrect message-id for " + nextKey + ".",
                    nextExpectedValue, nextActualValue);
        }
        
        // Test that the MANDATORY constraint has the correct event
        assertEquals("Incorrect event for MANDATORY constraint", "blur", 
                    chConfigElement.getEventFor("MANDATORY"));
    }

    public void testConstraintHandlerElementShouldHaveNoChildren()
    {
        try
        {
            ConstraintHandlersConfigElement chConfigElement = (ConstraintHandlersConfigElement) globalConstraintHandlers;
            chConfigElement.getChildren();
            fail("getChildren() did not throw an exception");
        } catch (ConfigException ce)
        {
            // expected exception
        }

    }

    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains additional data.
     */
    public void testCombineConstraintHandlersWithAddedParam()
    {
        ConstraintHandlersConfigElement basicElement = new ConstraintHandlersConfigElement();
        basicElement.addDataMapping("REGEX", "foo.regex", null, null, null);

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement elementWithAdditions = new ConstraintHandlersConfigElement();
        elementWithAdditions.addDataMapping("REGEX", "foo.regex", "msg", "msg-id", null);

        ConstraintHandlersConfigElement combinedElem
                = (ConstraintHandlersConfigElement)basicElement.combine(elementWithAdditions);
        
        assertEquals("foo.regex", combinedElem.getItems().get("REGEX").getValidationHandler());
        assertEquals("msg", combinedElem.getItems().get("REGEX").getMessage());
        assertEquals("msg-id", combinedElem.getItems().get("REGEX").getMessageId());
        assertEquals(null, combinedElem.getItems().get("REGEX").getEvent());
    }

    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains modified data.
     */
    public void testCombineConstraintHandlersWithModifiedParam()
    {
        ConstraintHandlersConfigElement initialElement = new ConstraintHandlersConfigElement();
        initialElement.addDataMapping("REGEX", "foo.regex", null, null, null);

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement modifiedElement = new ConstraintHandlersConfigElement();
        modifiedElement.addDataMapping("REGEX", "bar.regex", "msg", "msg-id", null);

        ConstraintHandlersConfigElement combinedElem
                = (ConstraintHandlersConfigElement)initialElement.combine(modifiedElement);

        assertEquals("bar.regex", combinedElem.getItems().get("REGEX").getValidationHandler());
        assertEquals("msg", combinedElem.getItems().get("REGEX").getMessage());
        assertEquals("msg-id", combinedElem.getItems().get("REGEX").getMessageId());
        assertEquals(null, combinedElem.getItems().get("REGEX").getEvent());
    }
    
    /**
     * Tests the combination of a ConstraintHandlersConfigElement with another that
     * contains deleted data.
     */
    public void testCombineConstraintHandlersWithDeletedParam()
    {
        ConstraintHandlersConfigElement initialElement = new ConstraintHandlersConfigElement();
        initialElement.addDataMapping("REGEX", "bar.regex", "msg", "msg-id", null);

        // This element is the same as the above, but adds message & message-id.
        ConstraintHandlersConfigElement modifiedElement = new ConstraintHandlersConfigElement();
        modifiedElement.addDataMapping("REGEX", "bar.regex", null, null, null);

        ConstraintHandlersConfigElement combinedElem
                = (ConstraintHandlersConfigElement)initialElement.combine(modifiedElement);

        assertEquals("bar.regex", combinedElem.getItems().get("REGEX").getValidationHandler());
        assertEquals(null, combinedElem.getItems().get("REGEX").getMessage());
        assertEquals(null, combinedElem.getItems().get("REGEX").getMessageId());
        assertEquals(null, combinedElem.getItems().get("REGEX").getEvent());
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
    
        Config contentConfig = configService.getConfig("my:example");
        assertNotNull("contentConfig was null.", contentConfig);
    
        ConfigElement confElement = contentConfig.getConfigElement("forms");
        assertNotNull("confElement was null.", confElement);
        assertTrue("confElement should be instanceof FormsConfigElement.",
                confElement instanceof FormsConfigElement);
        formsConfigElement = (FormsConfigElement) confElement;
        defaultFormConfigElement = formsConfigElement.getDefaultForm();
    
        globalConfig = configService.getGlobalConfig();
    
        FormsConfigElement globalForms = (FormsConfigElement)globalConfig.getConfigElement("forms");
        
        globalDefaultControls = globalForms.getDefaultControls();
        assertNotNull("global default-controls element should not be null",
                globalDefaultControls);
        assertTrue(
                "config element should be an instance of DefaultControlsConfigElement",
                (globalDefaultControls instanceof DefaultControlsConfigElement));
        defltCtrlsConfElement = (DefaultControlsConfigElement) globalDefaultControls;
    
        globalConstraintHandlers = globalForms.getConstraintHandlers();
        assertNotNull("global constraint-handlers element should not be null",
                globalConstraintHandlers);
        assertTrue(
                "config element should be an instance of ConstraintHandlersConfigElement",
                (globalConstraintHandlers instanceof ConstraintHandlersConfigElement));
    }
}

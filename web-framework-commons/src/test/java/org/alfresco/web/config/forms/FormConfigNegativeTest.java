package org.alfresco.web.config.forms;

import org.alfresco.util.BaseTest;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.XMLConfigService;

/**
 * JUnit tests to exercise the forms-related capabilities in to the web client
 * config service.
 * 
 * @author Neil McErlean
 */
public class FormConfigNegativeTest extends BaseTest
{
    private static final String TEST_CONFIG_FORMS_NEGATIVE_XML = "test-config-forms-negative.xml";
	private XMLConfigService configService;
    private Config globalConfig;
    private ConfigElement globalDefaultControls;
    protected ConfigElement globalConstraintHandlers;
    protected FormConfigElement formConfigElement;
    protected DefaultControlsConfigElement defltCtrlsConfElement;
    
    @Override
	public String getResourcesDir() {
		return "classpath:";
	}
    
    public void testInvalidConfigXmlShouldProduceNullConfigElements()
    {
        configService = initXMLConfigService(TEST_CONFIG_FORMS_NEGATIVE_XML);
        assertNotNull("configService was null.", configService);

        Config contentConfig = configService.getConfig("content");
        assertNotNull("contentConfig was null.", contentConfig);

        ConfigElement confElement = contentConfig.getConfigElement("form");
        assertNull("confElement should be null.", confElement);

        globalConfig = configService.getGlobalConfig();

        globalDefaultControls = globalConfig
                .getConfigElement("default-controls");
        assertNull("global default-controls element should be null",
                globalDefaultControls);

        globalConstraintHandlers = globalConfig
                .getConfigElement("constraint-handlers");
        assertNull("global constraint-handlers element should be null",
                globalConstraintHandlers);
     }
}
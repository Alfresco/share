package org.alfresco.module.org_alfresco_module_wcmquickstart;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.TemporaryMultilingualAspectTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebRootModelTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.rendition.RenditionTest;
import org.alfresco.module.org_alfresco_module_wcmquickstart.webscript.WebscriptTest;

/**
 * WCM QS Module Test Suite
 * 
 * @author Roy Wetherall
 */
public class WCMQuickStartModuleTestSuite extends TestSuite
{
    /**
     * Creates the test suite
     * 
     * @return  the test suite
     */
    public static Test suite() 
    {
        TestSuite suite = new TestSuite();
        suite.addTestSuite(WebRootModelTest.class);
        suite.addTestSuite(TemporaryMultilingualAspectTest.class);
        suite.addTestSuite(WebscriptTest.class);
        suite.addTestSuite(RenditionTest.class);
        return suite;
    }
}

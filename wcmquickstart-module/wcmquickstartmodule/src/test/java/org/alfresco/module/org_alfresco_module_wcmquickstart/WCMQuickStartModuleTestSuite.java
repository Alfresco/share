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

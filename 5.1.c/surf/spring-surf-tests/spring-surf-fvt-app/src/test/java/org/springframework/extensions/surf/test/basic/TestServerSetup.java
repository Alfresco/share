/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.surf.test.basic;

import org.springframework.extensions.surf.test.AbstractTestServerSetup;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * <p>Required to exist at the test package level.</p>
 * @author David Draper
 *
 */
public class TestServerSetup extends AbstractTestServerSetup
{
    @Parameters({"configFolder"})
    @BeforeSuite
    public static void setupJettyServer(String configFolder) throws Exception
    {
        AbstractTestServerSetup.setupJettyServer(configFolder);
    }
    
    @AfterSuite
    public static void tearDownJettyServer() throws Exception
    {
        AbstractTestServerSetup.tearDownJettyServer();
    }

}

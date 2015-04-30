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

import org.springframework.extensions.surf.test.AbstractJettyTest;
import org.testng.annotations.Test;

/**
 * <p>It's possible to create parent/child relationship associations between pages for the purposes of navigation.
 * These associations don't do anything by themselves but are available in WebScript JavaScript code to be used to
 * create navigation artifacts on the screen. This means that providing that the component you use for navigation
 * is rendered by a WebScript (i.e. it's not just part of a template) and is coded to make use of this data then
 * you can automatically add to it through configuration.</p>
 *
 * <p>This class should be used to check that associations are correctly populated and made available to WebScripts</p>
 *
 * TODO: Write tests for checking associations.
 *
 * @author David Draper
 */
public class PageAssociationsTest extends AbstractJettyTest
{
    @Test
    public void testPageAssociations()
    {

    }
}

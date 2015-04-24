/*
 * Copyright (C) 2005-2011 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.alfresco.model.ContentModel.ASSOC_CATEGORIES;

import javax.annotation.Resource;

import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.util.ApplicationContextHelper;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;

/**
 * @author Nick Smith
 * @since 4.0
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:alfresco/module/org_alfresco_module_wcmquickstart/benchmark/website-generator-context.xml"})
@TransactionConfiguration(defaultRollback=true, transactionManager="transactionManager")
public class WebSiteRandomGeneratorTest
{
    private static final String SITE_NAME = "testSite";

    @Resource(name="WebSiteGenerator")
    WebSiteGenerator generator;

    @Resource(name="NodeService")
    NodeService nodeService;
    
    @Resource(name="repositoryHelper")
    Repository repositoryHelper;
    
    @Test
    public void testGenerateWebSite() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        NodeRef site = generator.generateWebSite(SITE_NAME);
        assertNotNull(site);
        NodeRef companyHome = repositoryHelper.getCompanyHome();
        NodeRef webHome = nodeService.getChildByName(companyHome, ASSOC_CATEGORIES, WebSiteRandomGenerator.WEB_SITE_HOME_NAME);
        assertNotNull(webHome);
        NodeRef actualSite= nodeService.getChildByName(webHome, ASSOC_CATEGORIES, SITE_NAME);
        assertEquals(site, actualSite);
    }
    
}

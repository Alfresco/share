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

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.site.SiteInfo;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * Parent class for all WCM QuickStart tests
 * 
 * @author Nick Burch
 */
public abstract class WCMQuickStartTest extends TestCase
{
    protected ApplicationContext appContext;
    protected AuthenticationComponent authenticationComponent;
    protected TransactionService transactionService;
    protected FileFolderService fileFolderService;
    protected NodeService nodeService;
    protected Repository repository;
    protected ContentService contentService;
    protected MutableAuthenticationService authenticationService;
    protected PersonService personService;
    protected PermissionService permissionService;
    private SiteService siteService;
    
    protected NodeRef companyHome;
    protected String testUserName;
    
    private String testSiteName = "WCM_QS_TEST_SITE";
    protected SiteInfo site;
    protected NodeRef editorialSite;
    protected NodeRef editorialSiteRoot;
    protected NodeRef liveSite;
    protected NodeRef liveSiteRoot;
    
    @Override
    protected void setUp() throws Exception 
    {
        appContext = ApplicationContextHelper.getApplicationContext();
        authenticationComponent = (AuthenticationComponent)appContext.getBean("authenticationComponent");
        transactionService = (TransactionService)appContext.getBean("transactionService");
        fileFolderService = (FileFolderService)appContext.getBean("fileFolderService");
        nodeService = (NodeService)appContext.getBean("nodeService");
        repository = (Repository)appContext.getBean("repositoryHelper");
        contentService = (ContentService)appContext.getBean("contentService");
        authenticationService = (MutableAuthenticationService)appContext.getBean("authenticationService");
        personService = (PersonService)appContext.getBean("personService");
        permissionService = (PermissionService)appContext.getBean("permissionService");
        siteService = (SiteService)appContext.getBean("siteService");
        
        // Set authentication       
        authenticationComponent.setCurrentUser("admin");        
        
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        // Create the test site to work on
        companyHome = repository.getCompanyHome();
        
        // Setup the site
        site = siteService.getSite(testSiteName);
        if(site != null)
        {
            siteService.deleteSite(testSiteName);
        }
        site = siteService.createSite(
                testSiteName, testSiteName, testSiteName, testSiteName,
                SiteVisibility.PUBLIC
        );
        assertNotNull(site);
        
        NodeRef docLib = nodeService.createNode(
                site.getNodeRef(), ContentModel.ASSOC_CONTAINS, 
                QName.createQName("documentLibrary"), ContentModel.TYPE_FOLDER
        ).getChildRef();
        
        liveSite = nodeService.createNode(
                docLib, ContentModel.ASSOC_CONTAINS,
                QName.createQName("live"), WebSiteModel.TYPE_WEB_SITE
        ).getChildRef();
        liveSiteRoot = nodeService.createNode(
                liveSite, ContentModel.ASSOC_CONTAINS,
                QName.createQName("root"), WebSiteModel.TYPE_WEB_ROOT
        ).getChildRef();
        
        editorialSite = nodeService.createNode(
                docLib, ContentModel.ASSOC_CONTAINS,
                QName.createQName("editorial"), WebSiteModel.TYPE_WEB_SITE
        ).getChildRef();
        editorialSiteRoot = nodeService.createNode(
                editorialSite, ContentModel.ASSOC_CONTAINS,
                QName.createQName("root"), WebSiteModel.TYPE_WEB_ROOT
        ).getChildRef();
        
        userTransaction.commit();
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        AuthenticationUtil.setFullyAuthenticatedUser(AuthenticationUtil.getAdminUserName());
        // Remove the test site
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        siteService.deleteSite(testSiteName);
        site = null;
        userTransaction.commit();
    }
}

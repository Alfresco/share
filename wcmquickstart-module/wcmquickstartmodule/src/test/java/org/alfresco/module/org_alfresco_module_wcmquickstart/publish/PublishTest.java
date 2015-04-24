/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.List;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.security.MutableAuthenticationService;
import org.alfresco.service.cmr.security.PersonService;
import org.alfresco.service.cmr.transfer.TransferService2;
import org.alfresco.service.cmr.transfer.TransferTarget;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.BaseApplicationContextHelper;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyMap;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;

/**
 * @author Brian Remmington
 */
public class PublishTest extends TestCase implements WebSiteModel
{
    private final static Log log = LogFactory.getLog(PublishTest.class);
    
	private ApplicationContext appContext;
	private AuthenticationComponent authenticationComponent;
	private TransactionService transactionService;
	private FileFolderService fileFolderService;
	private NodeService nodeService;
	private Repository repository;
	private ContentService contentService;
	private MutableAuthenticationService authenticationService;
	private PersonService personService;
	
	NodeRef companyHome;
	String testUserName;
    private TransferService2 transferService;

    private PublishService publishService;
	
	@Override
	protected void setUp() throws Exception 
	{
		appContext = BaseApplicationContextHelper.getApplicationContext(new String[] {"classpath:alfresco/application-context.xml",
		        "classpath:alfresco/module/org_alfresco_module_wcmquickstart/module-context.xml"});
		authenticationComponent = (AuthenticationComponent)appContext.getBean("authenticationComponent");
		transactionService = (TransactionService)appContext.getBean("transactionService");
		fileFolderService = (FileFolderService)appContext.getBean("fileFolderService");
		nodeService = (NodeService)appContext.getBean("nodeService");
		repository = (Repository)appContext.getBean("repositoryHelper");
		contentService = (ContentService)appContext.getBean("contentService");
		authenticationService = (MutableAuthenticationService)appContext.getBean("authenticationService");
		personService = (PersonService)appContext.getBean("personService");
		transferService = (TransferService2)appContext.getBean("org_alfresco_module_wcmquickstart_transferService");
		publishService = (PublishService)appContext.getBean("org_alfresco_module_wcmquickstart_publishingService");
		
		// Set authentication		
		authenticationComponent.setCurrentUser("admin");		
	}
	
	private void createUser(String userName)
    {
        if (authenticationService.authenticationExists(userName) == false)
        {
        	authenticationService.createAuthentication(userName, "PWD".toCharArray());
            
            PropertyMap ppOne = new PropertyMap(4);
            ppOne.put(ContentModel.PROP_USERNAME, userName);
            ppOne.put(ContentModel.PROP_FIRSTNAME, "firstName");
            ppOne.put(ContentModel.PROP_LASTNAME, "lastName");
            ppOne.put(ContentModel.PROP_EMAIL, "email@email.com");
            ppOne.put(ContentModel.PROP_JOBTITLE, "jobTitle");
            
            personService.createPerson(ppOne);
        }        
    }
	
	public void test()
	{
	    
	}
	
	public void xtestWebSiteHierarchy() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Get company home
		companyHome = repository.getCompanyHome();
		
		// Create webroot (downcasting to check default properties are set)
        NodeRef editorialWebroot = fileFolderService.create(companyHome, "editorial" + GUID.generate(), ContentModel.TYPE_FOLDER).getNodeRef();
        assertNotNull(editorialWebroot);
        nodeService.setType(editorialWebroot, TYPE_WEB_SITE);

        NodeRef liveWebroot = fileFolderService.create(companyHome, "live" + GUID.generate(), ContentModel.TYPE_FOLDER).getNodeRef();
        assertNotNull(liveWebroot);
        nodeService.setType(liveWebroot, TYPE_WEB_SITE);

		nodeService.createAssociation(editorialWebroot, liveWebroot, WebSiteModel.ASSOC_PUBLISH_TARGET);
		
		// Create child folder
		NodeRef section = fileFolderService.create(editorialWebroot, "section", WebSiteModel.TYPE_WEB_ROOT).getNodeRef();
		assertNotNull(section);
		
		// Create child folder of section
		NodeRef sectionChild = fileFolderService.create(section, "childSection", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(sectionChild);
		assertEquals(TYPE_SECTION, nodeService.getType(sectionChild));
		
		// Create content in child section
		NodeRef page = fileFolderService.create(sectionChild, "myFile.txt", ContentModel.TYPE_CONTENT).getNodeRef();
		ContentWriter writer = contentService.getWriter(page, ContentModel.PROP_CONTENT, true);
		writer.setEncoding("UTF-8");
		writer.setMimetype("text/html");
		writer.putContent("<html><head><title>Hello</title></head><body></body></html>");
		NodeRef nonpage = fileFolderService.create(sectionChild, "myFile.bob", ContentModel.TYPE_CONTENT).getNodeRef();
		writer = contentService.getWriter(nonpage, ContentModel.PROP_CONTENT, true);
		writer.setEncoding("UTF-8");
		writer.setMimetype("text/plain");
		writer.putContent("Some asset only text.");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check all assets have been marked up correctly
		assertTrue("Page does not have webasset aspect applied.", nodeService.hasAspect(page, ASPECT_WEBASSET));
		assertTrue("Non-page does not have webasset aspect applied.", nodeService.hasAspect(nonpage, ASPECT_WEBASSET));
		
		String targetName = "test" + GUID.generate();
		TransferTarget transferTarget = transferService.createTransferTarget(targetName);
        transferTarget.setUsername("user");
		transferTarget.setPassword("hello".toCharArray());
        transferTarget.setEndpointHost("host");
        transferTarget.setEndpointProtocol("http");
        transferTarget.setEndpointPort(80);
		transferService.saveTransferTarget(transferTarget);
        userTransaction.commit();       

        //A little breath to give time for the page's parent sections to be populated.
        Thread.sleep(200);
        
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

//        nodeFactory.addPathMapping(new Pair<Path, Path>(nodeService.getPath(editorialWebroot), nodeService.getPath(liveWebroot)));
//        TransferDefinition def = new TransferDefinition();
//		def.setNodes(section, sectionChild, page);
//		transferService.transfer(targetName, def);
		
        publishService.enqueuePublishedNodes(section,sectionChild,page);

        long start = System.currentTimeMillis();

        publishService.publishQueue(editorialWebroot);
        
        userTransaction.commit();		
		log.debug("Transfer took " + (System.currentTimeMillis() - start) + "ms");
		
		userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        assertFalse(nodeService.getChildAssocs(liveWebroot).isEmpty());
        
        NodeRef liveSection = fileFolderService.searchSimple(liveWebroot, "section");
        assertNotNull(liveSection);
        assertTrue(TYPE_SECTION.equals(nodeService.getType(liveSection)));
        
        NodeRef liveChildSection = fileFolderService.searchSimple(liveSection, "childSection");
        assertNotNull(liveChildSection);
        assertTrue(TYPE_SECTION.equals(nodeService.getType(liveChildSection)));

        NodeRef livePage = fileFolderService.searchSimple(liveChildSection, "myFile.txt");
        assertNotNull(livePage);
        assertNotNull(nodeService.getProperty(livePage, PROP_PARENT_SECTIONS));
        assertTrue(((List)nodeService.getProperty(livePage, PROP_PARENT_SECTIONS)).contains(liveChildSection));
        
        userTransaction.commit();       
	}
}

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
package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.transaction.UserTransaction;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.WCMQuickStartTest;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.cmr.security.PermissionService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;
import org.alfresco.util.PropertyMap;

/**
 * @author Roy Wetherall
 */
public class WebRootModelTest extends WCMQuickStartTest implements WebSiteModel
{
	@Override
	protected void setUp() throws Exception 
	{
	    super.setUp();
		
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
	
	public void testWebSiteHierarchy() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Create child folder
		NodeRef section = fileFolderService.create(liveSiteRoot, "section", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(section);		
		assertEquals(TYPE_SECTION, nodeService.getType(section));
		
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
		writer.setMimetype("bob/frank");
		writer.putContent("Some asset only text.");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check all assets have been marked up correctly
		assertTrue("Page does not have webasset aspect applied.", nodeService.hasAspect(page, ASPECT_WEBASSET));
		assertTrue("Non-page does not have webasset aspect applied.", nodeService.hasAspect(nonpage, ASPECT_WEBASSET));
		
		// Check the page is an article
		assertEquals(TYPE_ARTICLE, nodeService.getType(page));
		assertEquals(ContentModel.TYPE_CONTENT, nodeService.getType(nonpage));
		
		// Check that the index pages of the sections have been created
		List<FileInfo> files = fileFolderService.listFiles(section);
		assertEquals(1, files.size());
		NodeRef index = files.get(0).getNodeRef();
		assertEquals(TYPE_INDEX_PAGE, nodeService.getType(index));
		assertTrue(nodeService.hasAspect(index, ASPECT_WEBASSET));
		assertEquals("index.html", (String)nodeService.getProperty(index, ContentModel.PROP_NAME));
		
		userTransaction.commit();		
	}
		
	public void testCollections() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Create the test user
		testUserName = "testUser" + GUID.generate();
		createUser(testUserName);
		
		// Get company home
		companyHome = repository.getCompanyHome();
		
		// Create webroot and section
		NodeRef webroot = fileFolderService.create(companyHome, "webroottest" + GUID.generate(), TYPE_WEB_ROOT).getNodeRef();
		NodeRef nonAsset = fileFolderService.create(companyHome, "nonasset" + GUID.generate()+ ".txt", ContentModel.TYPE_CONTENT).getNodeRef();
		
		// Set the permission and change user
		permissionService.setPermission(webroot, testUserName, PermissionService.COORDINATOR, true);
		authenticationComponent.setCurrentUser(testUserName);
		
		// Create the section
		NodeRef section = fileFolderService.create(webroot, "sectionOne", ContentModel.TYPE_FOLDER).getNodeRef();
		
		// Create a number of resources in a section
		NodeRef webassetOne = fileFolderService.create(section, "one.txt", ContentModel.TYPE_CONTENT).getNodeRef();
		fileFolderService.create(section, "two.txt", ContentModel.TYPE_CONTENT).getNodeRef();
		fileFolderService.create(section, "three.txt", ContentModel.TYPE_CONTENT).getNodeRef();
		fileFolderService.create(section, "four.txt", TYPE_ARTICLE).getNodeRef();
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		List<FileInfo> children = fileFolderService.listFolders(section);
		assertEquals(1, children.size());
		NodeRef collectionsFolder = children.get(0).getNodeRef();
		assertNotNull(collectionsFolder);
		assertEquals(TYPE_WEBASSET_COLLECTION_FOLDER, nodeService.getType(collectionsFolder));
		
		// Add a folder to the collections section and check it is a collection
		NodeRef myCollection = fileFolderService.create(collectionsFolder, "myCollection", ContentModel.TYPE_FOLDER).getNodeRef();
		assertEquals(TYPE_WEBASSET_COLLECTION, nodeService.getType(myCollection));
		assertNull(nodeService.getProperty(myCollection, PROP_QUERY));
		assertFalse((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
	
		// Add an asset to the collection
		nodeService.createAssociation(myCollection, webassetOne, ASSOC_WEBASSETS);
		
		assertFalse((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check that can not add a child to a collection
		try
		{
			fileFolderService.create(myCollection, "anthing.txt", ContentModel.TYPE_CONTENT);
			fail("Shouldn't be able to create a child with in the collection");
		}
		catch (AlfrescoRuntimeException e)
		{
			// Expected
		}
		
		userTransaction.rollback();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check can not add non web asset as a member of a collection
		try
		{
			nodeService.createAssociation(myCollection, nonAsset, ASSOC_WEBASSETS);
			fail("Shouldn't be able to add a non-webasset to a collection");
		}
		catch (AlfrescoRuntimeException e)
		{
			// Expected
		}		
		
		userTransaction.rollback();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Set a query on the collection
		String query = "select d.*, w.* from cmis:document as d join ws:webasset as w on d.cmis:objectId = w.cmis:objectId";
		nodeService.setProperty(myCollection, PROP_QUERY_RESULTS_MAX_SIZE, 3);
		nodeService.setProperty(myCollection, PROP_QUERY, query);

		assertFalse((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();

		assertTrue((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		checkRefreshAfterNow(myCollection);
		
		List<AssociationRef> assocs = nodeService.getTargetAssocs(myCollection, ASSOC_WEBASSETS);
		assertNotNull(assocs);
		assertEquals(3, assocs.size());
		
		query = "TYPE:\"" + TYPE_ARTICLE.toString() + "\"";
		nodeService.setProperty(myCollection, PROP_QUERY_LANGUAGE, SearchService.LANGUAGE_LUCENE);
		nodeService.setProperty(myCollection, PROP_QUERY, query);		
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assertTrue((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		checkRefreshAfterNow(myCollection);
		
		assocs = nodeService.getTargetAssocs(myCollection, ASSOC_WEBASSETS);
		assertNotNull(assocs);
		assertTrue(assocs.size() > 0);
		
		nodeService.setProperty(myCollection, PROP_QUERY, "");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assocs = nodeService.getTargetAssocs(myCollection, ASSOC_WEBASSETS);
		assertNotNull(assocs);
		assertEquals(0, assocs.size());
		assertFalse((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		
		// Test the ${sectionid}
		query = "select d.*, w.* " +
				"from cmis:document as d join ws:webasset as w on d.cmis:objectId = w.cmis:objectId " +
				"where in_folder(d, '${sectionid}')";
		nodeService.setProperty(myCollection, PROP_QUERY_LANGUAGE, SearchService.LANGUAGE_CMIS_ALFRESCO);
		nodeService.setProperty(myCollection, PROP_QUERY, query);
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assocs = nodeService.getTargetAssocs(myCollection, ASSOC_WEBASSETS);
		assertNotNull(assocs);
		assertEquals(3, assocs.size());
		assertTrue((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		checkRefreshAfterNow(myCollection);
		
		// Test the ${siteid}
		query = "select d.*, w.* " +
				"from cmis:document as d join ws:webasset as w on d.cmis:objectId = w.cmis:objectId " +
				"where in_tree(d, '${siteid}')";
		nodeService.setProperty(myCollection, PROP_QUERY_LANGUAGE, SearchService.LANGUAGE_CMIS_ALFRESCO);
		nodeService.setProperty(myCollection, PROP_QUERY, query);
		nodeService.setProperty(myCollection, PROP_QUERY_RESULTS_MAX_SIZE, 2);
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assocs = nodeService.getTargetAssocs(myCollection, ASSOC_WEBASSETS);
		assertNotNull(assocs);
		assertEquals(2, assocs.size());
		assertTrue((Boolean)nodeService.getProperty(myCollection, PROP_IS_DYNAMIC));
		checkRefreshAfterNow(myCollection);
		
		userTransaction.commit();
	}
	
	private void checkRefreshAfterNow(NodeRef collection)
	{
		Calendar now = Calendar.getInstance();
		Date refreshAt = (Date)nodeService.getProperty(collection, PROP_REFRESH_AT);
		assertNotNull(refreshAt);
		Calendar temp = Calendar.getInstance();
		temp.setTime(refreshAt);
		assertTrue(now.before(temp));
	}
	
	public void testPDFWebassets() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assertNotNull(liveSiteRoot);
		
		// Create child folder
		NodeRef section = fileFolderService.create(liveSiteRoot, "section", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(section);		
		assertEquals(TYPE_SECTION, nodeService.getType(section));
		List<String> renditionConfig = Collections.singletonList(MimetypeMap.MIMETYPE_WORD + "=ws:pdfWebasset");
		
		// Set up rendition config
		nodeService.setProperty(section, PROP_RENDITION_CONFIG, (Serializable)renditionConfig);
		
		NodeRef doc = createContent(section, "testDoc.doc", MimetypeMap.MIMETYPE_WORD, "doc");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		assertTrue(nodeService.hasAspect(doc, ASPECT_WEBASSET));
		
		NodeRef docPdf = fileFolderService.searchSimple(section, "testDoc.pdf");
		assertNotNull(docPdf);
		assertTrue(nodeService.hasAspect(docPdf, ASPECT_WEBASSET));
		
		userTransaction.commit();		
	}
	
	private NodeRef createContent(NodeRef section, String name, String mimetype, String ext) throws IOException
	{
		NodeRef content = fileFolderService.create(section, name, ContentModel.TYPE_CONTENT).getNodeRef();
		ContentWriter writer = contentService.getWriter(content, ContentModel.PROP_CONTENT, true);
		writer.setEncoding("UTF-8");
		writer.setMimetype(mimetype);
		File origFile = AbstractContentTransformerTest.loadQuickTestFile(ext);
		writer.putContent(origFile);
		return content;
	}
    
    @SuppressWarnings("unchecked")
    public void testSectionTracking() throws Exception
    {
        //Test that a web asset's record of which section(s) it's in keeps up to date
        
        // Start transaction
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        // Create the test user
        testUserName = "testUser" + GUID.generate();
        createUser(testUserName);
        
        // Set the permission and change user
        permissionService.setPermission(liveSiteRoot, testUserName, PermissionService.COORDINATOR, true);
        authenticationComponent.setCurrentUser(testUserName);
        
        // Create the sections
        NodeRef section1 = fileFolderService.create(liveSiteRoot, "sectionOne", ContentModel.TYPE_FOLDER).getNodeRef();
        NodeRef section2 = fileFolderService.create(liveSiteRoot, "sectionTwo", ContentModel.TYPE_FOLDER).getNodeRef();
        
        // Create a number of resources in a section
        NodeRef webassetOne = fileFolderService.create(section1, "one.txt", ContentModel.TYPE_CONTENT).getNodeRef();
        NodeRef webassetTwo = fileFolderService.create(section1, "two.txt", ContentModel.TYPE_CONTENT).getNodeRef();
        NodeRef webassetThree = fileFolderService.create(section1, "three.txt", ContentModel.TYPE_CONTENT).getNodeRef();
        NodeRef webassetFour = fileFolderService.create(section1, "four.txt", ContentModel.TYPE_CONTENT).getNodeRef();
        
        
        userTransaction.commit();

        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        List<NodeRef> parentSections;

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetOne, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetTwo, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));
        
//        parentSections = (List<NodeRef>)nodeService.getProperty(webassetThree, PROP_PARENT_SECTIONS);
//        assertNotNull(parentSections);
//        assertEquals(1, parentSections.size());
//        assertEquals(section1, parentSections.get(0));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetFour, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));

        //Test that moving web assets causes the parent sections to be updated 
        fileFolderService.move(webassetOne, section2, null);
        fileFolderService.move(webassetTwo, section2, null);
        
        
        userTransaction.commit();

        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetOne, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetTwo, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetThree, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetFour, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));

        //Test that placing a web asset in two sections behaves as expected
        nodeService.addChild(section2, webassetThree, ContentModel.ASSOC_CONTAINS, QName.createQName("three.txt"));
        nodeService.addChild(section2, webassetFour, ContentModel.ASSOC_CONTAINS, QName.createQName("four.txt"));

        userTransaction.commit();

        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetOne, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetTwo, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetThree, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(2, parentSections.size());
        assertTrue(parentSections.contains(section1));
        assertTrue(parentSections.contains(section2));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetFour, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(2, parentSections.size());
        assertTrue(parentSections.contains(section1));
        assertTrue(parentSections.contains(section2));

        //Test that copying a web asset to a different section behaves as expected
        NodeRef copyOfWebAssetOne = fileFolderService.copy(webassetOne, section1, null).getNodeRef();
        
        userTransaction.commit();

        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetOne, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));

        parentSections = (List<NodeRef>)nodeService.getProperty(copyOfWebAssetOne, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section1, parentSections.get(0));

        parentSections = (List<NodeRef>)nodeService.getProperty(webassetTwo, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(1, parentSections.size());
        assertEquals(section2, parentSections.get(0));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetThree, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(2, parentSections.size());
        assertTrue(parentSections.contains(section1));
        assertTrue(parentSections.contains(section2));
        
        parentSections = (List<NodeRef>)nodeService.getProperty(webassetFour, PROP_PARENT_SECTIONS);
        assertNotNull(parentSections);
        assertEquals(2, parentSections.size());
        assertTrue(parentSections.contains(section1));
        assertTrue(parentSections.contains(section2));
        userTransaction.commit();
    }

    //TODO Re-write this test to reflect the actual Visitor Feedback functionality!
    public void todoTestVisitorFeedback() throws Exception
    {
        //Test that the "relevant article" links are being kept up to date
        
        // Start transaction
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
        // Create the test user
        testUserName = "testUser" + GUID.generate();
        createUser(testUserName);
        
        // Set the permission and change user
        permissionService.setPermission(companyHome, testUserName, PermissionService.COORDINATOR, true);
        authenticationComponent.setCurrentUser(testUserName);
        
        nodeService.setProperty(liveSite, PROP_FEEDBACK_CONFIG, VisitorFeedbackType.CONTACT_REQUEST_TYPE + "=${websiteowner}");
        
        // Create the section
        NodeRef section = fileFolderService.create(liveSiteRoot, "section" + GUID.generate(), TYPE_SECTION).getNodeRef();

        // Create the article
        NodeRef article1 = fileFolderService.create(section, "article" + GUID.generate(), TYPE_ARTICLE).getNodeRef();

        String name = "feedback" + GUID.generate();
        Map<QName, Serializable> props = new HashMap<QName, Serializable>();
        props.put(WebSiteModel.PROP_RELEVANT_ASSET, article1);
        props.put(ContentModel.PROP_NAME, name);
        props.put(PROP_FEEDBACK_TYPE, VisitorFeedbackType.CONTACT_REQUEST_TYPE);
        NodeRef feedback2 = nodeService.createNode(liveSiteRoot, ContentModel.ASSOC_CONTAINS, QName.createQName(name), TYPE_VISITOR_FEEDBACK, props).getChildRef();
                
        userTransaction.commit();
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        assertEquals(article1, nodeService.getProperty(feedback2, PROP_RELEVANT_ASSET));
        assertFalse(nodeService.getTargetAssocs(feedback2, ASSOC_RELEVANT_ASSET).isEmpty());
        
//        // Does the user have a pending task
//        List<WorkflowTask> tasks = workflowService.getAssignedTasks(testUserName, WorkflowTaskState.IN_PROGRESS);
//        assertNotNull(tasks);
//        assertEquals(1, tasks.size());
//        WorkflowTask task = tasks.get(0);
//        
//        // Does the workflow package contain the feedback object
//        List<NodeRef> taskPackageContents = workflowService.getPackageContents(task.id);
//        assertNotNull(taskPackageContents);
//        assertEquals(1, taskPackageContents.size());
//        assertEquals(feedback2, taskPackageContents.get(0));
//        
//        // End the task
//        workflowService.endTask(task.id, null);
        
        //test what happens when the article is deleted (should cause the feedback to be deleted too)
        nodeService.deleteNode(article1);

        userTransaction.commit();
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();

        assertFalse(nodeService.exists(feedback2));

        userTransaction.commit();
    }       
}

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
package org.alfresco.module.org_alfresco_module_wcmquickstart.rendition;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.transaction.UserTransaction;

import junit.framework.TestCase;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.repo.content.transform.AbstractContentTransformerTest;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.GUID;
import org.springframework.context.ApplicationContext;

/**
 * Tests WCM quick start rendition features
 * 
 * @author Roy Wetherall
 */
public class RenditionTest extends TestCase
                           implements WebSiteModel
{
	private ApplicationContext appContext;
	private AuthenticationComponent authenticationComponent;
	private TransactionService transactionService;
	private FileFolderService fileFolderService;
	private NodeService nodeService;
	private Repository repository;
	private ContentService contentService;
	private RenditionService renditionService;
	
	NodeRef companyHome;
	String testUserName;
	
	/**
	 * @see junit.framework.TestCase#setUp()
	 */
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
		renditionService = (RenditionService)appContext.getBean("renditionService");
		
		// Set authentication		
		authenticationComponent.setCurrentUser("admin");		
	}
	
	/**
	 * Test renditions
	 * @throws Exception
	 */
	public void testRenditionsMapedToMimetype() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Get company home
		companyHome = repository.getCompanyHome();
		
		// Create webroot (downcasting to check default properties are set)
		NodeRef webroot = fileFolderService.create(companyHome, "webroottest" + GUID.generate(), ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(webroot);
		nodeService.setType(webroot, TYPE_WEB_ROOT);
		
		// Check default hostname has been set
		// Create child folder
		NodeRef section = fileFolderService.create(webroot, "section", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(section);
		assertEquals(TYPE_SECTION, nodeService.getType(section));
		List<String> values = new ArrayList<String>(2);
		values.add(MimetypeMap.MIMETYPE_IMAGE_JPEG + "=ws:smallThumbnail");
		values.add(MimetypeMap.MIMETYPE_PDF + "=ws:smallThumbnail");
		nodeService.setProperty(section, PROP_RENDITION_CONFIG, (Serializable)values);
		
		// Add content to section
		NodeRef jpg = createContent(section, "test.jpg", MimetypeMap.MIMETYPE_IMAGE_JPEG, "jpg");
		NodeRef pdf = createContent(section, "test.pdf", MimetypeMap.MIMETYPE_PDF, "pdf");
		NodeRef text = createContent(section, "test.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "txt");
		NodeRef doc = createContent(section, "testDoc.doc", MimetypeMap.MIMETYPE_WORD, "doc");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check has been made image
		assertEquals(TYPE_IMAGE, nodeService.getType(jpg));
		assertEquals(TYPE_ARTICLE, nodeService.getType(text));
		assertEquals(ContentModel.TYPE_CONTENT, nodeService.getType(pdf));
		assertEquals(ContentModel.TYPE_CONTENT, nodeService.getType(doc));
		
		// Check that the web asset aspect has been applied
		assertTrue(nodeService.hasAspect(jpg, ASPECT_WEBASSET));
		assertTrue(nodeService.hasAspect(text, ASPECT_WEBASSET));
		assertTrue(nodeService.hasAspect(pdf, ASPECT_WEBASSET));
		assertFalse(nodeService.hasAspect(doc, ASPECT_WEBASSET));
		
		// See if the renditions can be found
		ChildAssociationRef jpgSmall = renditionService.getRenditionByName(jpg, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNotNull(jpgSmall);
		ChildAssociationRef jpgSmall2 = renditionService.getRenditionByName(pdf, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNotNull(jpgSmall2);
		
		// Check that the PDF rendition has been auto created
		NodeRef pdfDoc = fileFolderService.searchSimple(section, "testDoc.pdf");
		assertNotNull(pdfDoc);
		ChildAssociationRef jpgSmall3 = renditionService.getRenditionByName(pdfDoc, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNotNull(jpgSmall3);
		
		userTransaction.commit();
		
	}
	
	public void testRenditionsMapedToType() throws Exception
	{
		// Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Get company home
		companyHome = repository.getCompanyHome();
		
		// Create webroot (downcasting to check default properties are set)
		NodeRef webroot = fileFolderService.create(companyHome, "webroottest" + GUID.generate(), ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(webroot);
		nodeService.setType(webroot, TYPE_WEB_ROOT);
		
		// Check default hostname has been set
		// Create child folder
		NodeRef section = fileFolderService.create(webroot, "section", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(section);
		assertEquals(TYPE_SECTION, nodeService.getType(section));
		List<String> values = new ArrayList<String>(2);
		values.add("ws:image=ws:smallThumbnail");
		values.add("cmis:document=ws:imagePreview");
		nodeService.setProperty(section, PROP_RENDITION_CONFIG, (Serializable)values);
		
		// Add content to section
		NodeRef jpg = createContent(section, "test.jpg", MimetypeMap.MIMETYPE_IMAGE_JPEG, "jpg");
		NodeRef pdf = createContent(section, "test.pdf", MimetypeMap.MIMETYPE_PDF, "pdf");
		NodeRef text = createContent(section, "test.txt", MimetypeMap.MIMETYPE_TEXT_PLAIN, "txt");
		NodeRef doc = createContent(section, "testDoc.doc", MimetypeMap.MIMETYPE_WORD, "doc");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Check has been made image
		assertEquals(TYPE_IMAGE, nodeService.getType(jpg));
		assertEquals(TYPE_ARTICLE, nodeService.getType(text));
		assertEquals(ContentModel.TYPE_CONTENT, nodeService.getType(pdf));
		assertEquals(ContentModel.TYPE_CONTENT, nodeService.getType(doc));
		
		// Check that the web asset aspect has been applied
		assertTrue(nodeService.hasAspect(jpg, ASPECT_WEBASSET));
		assertTrue(nodeService.hasAspect(text, ASPECT_WEBASSET));
		assertTrue(nodeService.hasAspect(pdf, ASPECT_WEBASSET));
		assertFalse(nodeService.hasAspect(doc, ASPECT_WEBASSET));
		
		// See if the renditions can be found
		ChildAssociationRef rend1 = renditionService.getRenditionByName(jpg, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNotNull(rend1);
		ChildAssociationRef rend2 = renditionService.getRenditionByName(pdf, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNull(rend2);
		NodeRef pdfDoc = fileFolderService.searchSimple(section, "testDoc.pdf");
		assertNotNull(pdfDoc);
		ChildAssociationRef rend3 = renditionService.getRenditionByName(pdfDoc, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNull(rend3);
		ChildAssociationRef rend3a = renditionService.getRenditionByName(text, QName.createQName(NAMESPACE, "smallThumbnail"));
		assertNull(rend3a);
		
		ChildAssociationRef rend4 = renditionService.getRenditionByName(jpg, QName.createQName(NAMESPACE, "imagePreview"));
		assertNotNull(rend4);
		ChildAssociationRef rend5 = renditionService.getRenditionByName(pdf, QName.createQName(NAMESPACE, "imagePreview"));
		assertNotNull(rend5);
		ChildAssociationRef rend6 = renditionService.getRenditionByName(pdfDoc, QName.createQName(NAMESPACE, "imagePreview"));
		assertNotNull(rend6);
		ChildAssociationRef rend7 = renditionService.getRenditionByName(text, QName.createQName(NAMESPACE, "imagePreview"));
		assertNotNull(rend7);
		
		userTransaction.commit();
		
	}
	
	/**
	 * Create test content 
	 * 
	 * @param section
	 * @param name
	 * @param mimetype
	 * @param ext
	 * @return
	 * @throws IOException
	 */
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

}

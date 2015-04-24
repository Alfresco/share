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
package org.alfresco.module.org_alfresco_module_wcmquickstart.webscript;

import java.text.MessageFormat;
import java.util.List;

import javax.transaction.UserTransaction;

import org.alfresco.model.ContentModel;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.repo.security.authentication.AuthenticationComponent;
import org.alfresco.repo.web.scripts.BaseWebScriptTest;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.site.SiteService;
import org.alfresco.service.cmr.site.SiteVisibility;
import org.alfresco.service.transaction.TransactionService;
import org.alfresco.util.GUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.webscripts.TestWebScriptServer.GetRequest;
import org.springframework.extensions.webscripts.TestWebScriptServer.Response;

/**
 * WCM Quick Start web script test
 * 
 * @author Roy Wetherall
 */
public final class WebscriptTest extends BaseWebScriptTest implements WebSiteModel
{
    private final static String GET_TRANSLATION_DETAILS = "/api/webassettranslations?nodeRef={0}"; 
	private final static String GET_ASSET_COLLECTION = "/api/assetcollections/{0}?sectionid={1}";
	private final static String GET_LOADWEBSITEDATA = "/api/loadwebsitedata?site={0}";
	
	private AuthenticationComponent authenticationComponent;
	private TransactionService transactionService;
	private FileFolderService fileFolderService;
	private NodeService nodeService;
	private Repository repository;
	private ContentService contentService;
	private SiteService siteService;
	
	private NodeRef sectionChild;
    private NodeRef englishHome;
	private NodeRef collection;
	private String siteName;

	@Override
	protected void setUp() throws Exception
	{
	    super.setUp();
	    
	    ApplicationContext appContext =  getServer().getApplicationContext();
		authenticationComponent = (AuthenticationComponent)appContext.getBean("authenticationComponent");
		transactionService = (TransactionService)appContext.getBean("transactionService");
		fileFolderService = (FileFolderService)appContext.getBean("fileFolderService");
		nodeService = (NodeService)appContext.getBean("nodeService");
		repository = (Repository)appContext.getBean("repositoryHelper");
		contentService = (ContentService)appContext.getBean("contentService");
		siteService = (SiteService)appContext.getBean("siteService");
		
		// Set authentication		
		authenticationComponent.setCurrentUser("admin");	
	    
	    // Start transaction
		UserTransaction userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Get company home
		NodeRef companyHome = repository.getCompanyHome();
		
		// Create the website
		NodeRef website = fileFolderService.create(companyHome, "websitetest" + GUID.generate(), WebSiteModel.TYPE_WEB_SITE).getNodeRef();
		
		// Create webroot (downcasting to check default properties are set)
		NodeRef webroot = fileFolderService.create(website, "webroottest" + GUID.generate(), ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(webroot);
		nodeService.setType(webroot, TYPE_WEB_ROOT);
		
		// Create child folder
		NodeRef section = fileFolderService.create(webroot, "section", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(section);
		assertEquals(TYPE_SECTION, nodeService.getType(section));
		
		// Create child folder of section
		sectionChild = fileFolderService.create(section, "childSection", ContentModel.TYPE_FOLDER).getNodeRef();
		assertNotNull(sectionChild);
		assertEquals(TYPE_SECTION, nodeService.getType(sectionChild));
		
		// Create content in child section
		NodeRef page1 = createContent(sectionChild, "file1.html");
		NodeRef page2 = createContent(sectionChild, "file2.html");
		NodeRef page3 = createContent(sectionChild, "file3.html");
		
		userTransaction.commit();
		userTransaction = transactionService.getUserTransaction();
		userTransaction.begin();
		
		// Populate a collection
		NodeRef collections = fileFolderService.searchSimple(sectionChild, "collections");
		assertNotNull(collections);
		collection = fileFolderService.create(collections, "myCollection", ContentModel.TYPE_FOLDER).getNodeRef();
		nodeService.setProperty(collection, ContentModel.PROP_TITLE, "My Collection");
		nodeService.setProperty(collection, ContentModel.PROP_DESCRIPTION, "My web assect collection");
		nodeService.createAssociation(collection, page1, ASSOC_WEBASSETS);
		nodeService.createAssociation(collection, page2, ASSOC_WEBASSETS);
		nodeService.createAssociation(collection, page3, ASSOC_WEBASSETS);
		
		// Create a folder which will be translated
		englishHome = fileFolderService.create(webroot, "English", WebSiteModel.TYPE_SECTION).getNodeRef();
		
		// Create a test site
		siteName = "testSite" + GUID.generate();
		siteService.createSite("sitePreset", siteName, "siteTitle", "siteDescription", SiteVisibility.PUBLIC);
		
		userTransaction.commit();
	}
	
	private NodeRef createContent(NodeRef section, String name)
	{
		NodeRef page = fileFolderService.create(section, name, ContentModel.TYPE_CONTENT).getNodeRef();
		ContentWriter writer = contentService.getWriter(page, ContentModel.PROP_CONTENT, true);
		writer.setEncoding("UTF-8");
		writer.setMimetype("text/html");
		writer.putContent("<html><head><title>Hello</title></head><body></body></html>");
		return page;
	}
	
	/**
	 * TODO Fix this unit test
	 * The webscript used to return JSON, but has now been changed to be
	 *  hard coded to returning XML via a serialiser. This unit test
	 *  needs to be updated to test the new data
	 */
	public void testGetAssestCollection() throws Exception
	{
		String url = MessageFormat.format(GET_ASSET_COLLECTION, new Object[]{"myCollection", sectionChild.toString()});
		Response rsp = sendRequest(new GetRequest(url), 200);
		String xmlStr = rsp.getContentAsString();
		
		fail("This test assumes a JSON response, but the webscript now returns XML");
		
		JSONObject jsonRsp = new JSONObject(new JSONTokener(xmlStr));
		
		JSONObject data = jsonRsp.getJSONObject("data");
		assertNotNull(data);
		assertEquals(collection.toString(), data.getString("id"));
		assertEquals("myCollection", data.getString("name"));
		assertEquals("My Collection", data.getString("title"));
		assertEquals("My web assect collection", data.getString("description"));
		JSONArray assets = data.getJSONArray("assets");
		assertNotNull(assets);
		assertEquals(3, assets.length());		
		
		System.out.println(rsp.getContentAsString());
	}
    
    public void testGetTranslationDetails() throws Exception
    {
        String url = MessageFormat.format(GET_TRANSLATION_DETAILS, new Object[]{englishHome.toString()});
        Response rsp = sendRequest(new GetRequest(url), 200);
        String jsonStr = rsp.getContentAsString();
        JSONObject jsonRsp = null;
        
        try 
        {
            jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        }
        catch(JSONException e)
        {
            System.err.println(jsonStr);
            fail("Invalid JSON received: " + e.getMessage() + "\n" + jsonStr);
        }
        System.out.println(jsonStr);
        
        // Check it came back as expected for an untranslated section
        JSONObject data = jsonRsp.getJSONObject("data");
        assertNotNull(data);
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals(JSONObject.NULL, data.get("locale"));
        assertEquals(false, data.getBoolean("translationEnabled"));
        assertEquals(0, data.getJSONObject("translations").length());
        assertEquals(0, data.getJSONObject("parents").length());
        
        
        // Mark the English section as a translation, and add
        //  a child for it (not translated yet)
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        nodeService.addAspect(englishHome, WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL, null);
        nodeService.setProperty(englishHome, WebSiteModel.PROP_LANGUAGE, "en");
        
        NodeRef doc = fileFolderService.create(englishHome, "Document", WebSiteModel.TYPE_ARTICLE).getNodeRef();
        userTransaction.commit();
        
        
        // Check both
        // Firstly, the english section
        rsp = sendRequest(new GetRequest(url), 200);
        jsonStr = rsp.getContentAsString();
        jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        data = jsonRsp.getJSONObject("data");
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals("en", data.getString("locale"));
        assertEquals(true, data.getBoolean("translationEnabled"));
        assertEquals(1, data.getJSONObject("translations").length()); // Self
        assertEquals(0, data.getJSONObject("parents").length());

        
        // And the document within it
        String url2 = MessageFormat.format(GET_TRANSLATION_DETAILS, new Object[]{doc.toString()});
        rsp = sendRequest(new GetRequest(url2), 200);
        jsonStr = rsp.getContentAsString();
        jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        data = jsonRsp.getJSONObject("data");
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals("en", data.getString("locale"));
        assertEquals(false, data.getBoolean("translationEnabled"));
        assertEquals(0, data.getJSONObject("translations").length());
        
        assertEquals(1, data.getJSONObject("parents").length());
        assertEquals("en", data.getJSONObject("parents").names().get(0));
        assertEquals(englishHome.toString(), data.getJSONObject("parents").getJSONObject("en").getString("nodeRef"));
        assertEquals(true, data.getJSONObject("parents").getJSONObject("en").getBoolean("allPresent"));
        
        
        // Now add a French translation of the English document
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        nodeService.addAspect(doc, WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL, null);
        userTransaction.commit();
        
        userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        NodeRef french = fileFolderService.create(englishHome, "FrDocument", WebSiteModel.TYPE_ARTICLE).getNodeRef();
        nodeService.addAspect(french, WebSiteModel.ASPECT_TEMPORARY_MULTILINGUAL, null);
        nodeService.setProperty(french, WebSiteModel.PROP_LANGUAGE, "fr");
        nodeService.setProperty(french, WebSiteModel.PROP_TRANSLATION_OF, doc);
        userTransaction.commit();

        
        // And check everything
        rsp = sendRequest(new GetRequest(url), 200);
        jsonStr = rsp.getContentAsString();
        jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        data = jsonRsp.getJSONObject("data");
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals("en", data.getString("locale"));
        assertEquals(true, data.getBoolean("translationEnabled"));
        assertEquals(1, data.getJSONObject("translations").length()); // Self
        assertEquals(0, data.getJSONObject("parents").length());
        
        
        rsp = sendRequest(new GetRequest(url2), 200);
        jsonStr = rsp.getContentAsString();
        System.out.println(jsonStr);        
        jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        data = jsonRsp.getJSONObject("data");
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals("en", data.getString("locale"));
        assertEquals(true, data.getBoolean("translationEnabled"));
        
        assertEquals(2, data.getJSONObject("translations").length());
        assertEquals(doc.toString(), data.getJSONObject("translations").getJSONObject("en").getString("nodeRef"));
        assertEquals(french.toString(), data.getJSONObject("translations").getJSONObject("fr").getString("nodeRef"));
        
        assertEquals(1, data.getJSONObject("parents").length());
        assertEquals("en", data.getJSONObject("parents").names().get(0));
        assertEquals(englishHome.toString(), data.getJSONObject("parents").getJSONObject("en").getString("nodeRef"));
        assertEquals(true, data.getJSONObject("parents").getJSONObject("en").getBoolean("allPresent"));
        
        
        String url3 = MessageFormat.format(GET_TRANSLATION_DETAILS, new Object[]{french.toString()});
        rsp = sendRequest(new GetRequest(url3), 200);
        jsonStr = rsp.getContentAsString();
        System.out.println(jsonStr);
        jsonRsp = new JSONObject(new JSONTokener(jsonStr));
        data = jsonRsp.getJSONObject("data");
        
        assertTrue(data.getJSONArray("locales").length() > 4);
        assertEquals("en", data.getJSONArray("locales").getJSONObject(0).getString("id"));
        
        assertEquals("fr", data.getString("locale"));
        assertEquals(true, data.getBoolean("translationEnabled"));
        
        assertEquals(2, data.getJSONObject("translations").length());
        assertEquals(doc.toString(), data.getJSONObject("translations").getJSONObject("en").getString("nodeRef"));
        assertEquals(french.toString(), data.getJSONObject("translations").getJSONObject("fr").getString("nodeRef"));
        
        assertEquals(1, data.getJSONObject("parents").length());
        assertEquals("en", data.getJSONObject("parents").names().get(0));
        assertEquals(englishHome.toString(), data.getJSONObject("parents").getJSONObject("en").getString("nodeRef"));
        assertEquals(true, data.getJSONObject("parents").getJSONObject("en").getBoolean("allPresent"));
    }
	
	public void testGetImportWebSiteData() throws Exception
	{
        // Start transaction
        UserTransaction userTransaction = transactionService.getUserTransaction();
        userTransaction.begin();
        
	    String url = MessageFormat.format(GET_LOADWEBSITEDATA, new Object[]{siteName});
        Response resp = sendRequest(new GetRequest(url + "&preview=true"), 200);	    
        System.out.println(resp.getContentAsString());
		
        resp = sendRequest(new GetRequest(url), 200);
        System.out.println(resp.getContentAsString());
	
		NodeRef nodeRef = siteService.getContainer(siteName, "documentLibrary");
		assertNotNull(nodeRef);
		
		List<FileInfo> infos = fileFolderService.listFolders(nodeRef);
		assertNotNull(infos);
		assertEquals(1, infos.size());
		
		NodeRef root = infos.get(0).getNodeRef();
		assertNotNull(root);
		assertEquals(ContentModel.TYPE_FOLDER, nodeService.getType(root));
		
        infos = fileFolderService.listFolders(root);
        assertNotNull(infos);
        assertEquals(2, infos.size());
        NodeRef website = infos.get(0).getNodeRef();
        assertNotNull(website);
        assertEquals(TYPE_WEB_SITE, nodeService.getType(website));
        website = infos.get(1).getNodeRef();
        assertNotNull(website);
        assertEquals(TYPE_WEB_SITE, nodeService.getType(website));
        
		userTransaction.commit();
	}
}

///*
// * Copyright (C) 2005-2012 Alfresco Software Limited.
// *
// * This file is part of Alfresco
// *
// * Alfresco is free software: you can redistribute it and/or modify
// * it under the terms of the GNU Lesser General Public License as published by
// * the Free Software Foundation, either version 3 of the License, or
// * (at your option) any later version.
// *
// * Alfresco is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// * GNU Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public License
// * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
// */
//package org.alfresco.webdrone.share.dashlet;
//
//import java.util.List;
//import java.util.concurrent.TimeUnit;
//
//import org.alfresco.webdrone.PageException;
//import org.alfresco.webdrone.RenderTime;
//import org.alfresco.webdrone.share.ShareLink;
//import org.alfresco.webdrone.share.site.document.DocumentDetailsPage;
//import org.testng.Assert;
//import org.testng.annotations.BeforeClass;
//import org.testng.annotations.BeforeTest;
//import org.testng.annotations.Test;
//
///**
// * Integration test to verify My Documents dashlet page elements are in place.
// * 
// * @author Michael Suzuki
// * @since 1.3
// */
//public class MyDocumentsDashletTest extends AbstractDashletTest
//{
//    
//    @BeforeTest
//    public void prepare() throws Exception
//    {
//        siteName = "MyDocumentsDashletTests" + System.currentTimeMillis();
//    }
//    
//    @BeforeClass
//    public void setup() throws Exception
//    {
//         uploadDocument();
//    }
//    
//    @Test
//    public void instantiateMyDocumentsDashlet()
//    {
//        MySitesDashlet dashlet = new MySitesDashlet(drone);
//        Assert.assertNotNull(dashlet);
//    }
//    
//    /**
//     * Test process of accessing my documents
//     * dashlet from the dash board view.
//     * @throws Exception 
//     */
//    @Test
//    public void selectMyDocumentDashlet() throws Exception
//    {
//        MyDocumentsDashlet dashlet = dashBoard.getDashlet("my-documents").render();
//        final String title = dashlet.getDashletTitle();
//        Assert.assertEquals("My Documents",title);
//    }
//    
//    @Test
//    public void getDocumentsAndSelectDocument() throws Exception
//    {
//    	//My Site dashlet takes 15s to update
//    	boolean hasResult = dashletRenderedWithResult();
//    	if(!hasResult){saveScreenShot(drone, "getDocumentsAndSelectDocument");}
//    	Assert.assertTrue(hasResult);
//    	MyDocumentsDashlet dashlet = dashBoard.getDashlet("my-documents").render();
//		List<ShareLink> documents = dashlet.getDocuments();
//		Assert.assertNotNull(documents);
//		Assert.assertEquals(false, documents.isEmpty());
//    	DocumentDetailsPage page = dashlet.selectDocument(fileName).click().render();
//        
//        Assert.assertNotNull(page);
//        Assert.assertEquals(true, page.isDocumentDetailsPage());
//    }
//    
//    @Test(expectedExceptions = PageException.class)
//    public void selectFake() throws Exception
//    {
//        MyDocumentsDashlet dashlet = dashBoard.getDashlet("my-documents").render();
//        dashlet.selectDocument("bla");
//    }
//    
//    private synchronized boolean dashletRenderedWithResult()
//    {
//    	RenderTime timer = new RenderTime(70,TimeUnit.SECONDS);
//    	try
//    	{
//    		while(true)
//    		{
//    		    synchronized(this)
//    		    {
//    		        try{ this.wait(100L); } catch (InterruptedException e) {}
//    		    }
//    			try
//    			{
//    				timer.start();
//    				MyDocumentsDashlet dashlet = dashBoard.getDashlet("my-documents").render();
//    				if(!dashlet.getDocuments().isEmpty())
//    				{
//    					for(ShareLink link : dashlet.getDocuments())
//    					{
//    						if(link.getDescription().equalsIgnoreCase(fileName))
//    						{
//    							return true;
//    						}
//    					}
//    				}
//    				drone.refresh();
//    			}
//    			finally
//    			{
//    				timer.end();
//    			}
//    		}
//    	}
//    	catch (Exception e) 
//    	{
//    		return false;
//    	}
//    }
//}

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

package org.springframework.extensions.surf.test;

import java.io.File;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;

import org.springframework.context.ApplicationContext;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.mock.web.MockServletConfig;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * <p>This Class can be used by the TestNG test configuration files (by including the class or containing package
 * as a test target) to setup an environment for testing the Spring Surf module code.</p> 
 * 
 * @author muzquiano
 * @author David Draper
 */
public class TestCaseSetup
{	
    /**
     * <p>This constant defines the root folder in which temporary test data will be created.</p>
     */
    private static File _testDataFolder = new File("target", "test-data");
    
    /**
     * <p>This method creates a Spring framework in which to load Spring Surf to that it can be tested</p>
     * 
     * @param configLocations This should map to a parameter declared in in the TestNG configuration file.
     * @throws ServletException
     */
    @Parameters({"configLocations"})
	@BeforeSuite
	public static void setUp(String configLocations) throws ServletException
	{
        // Setup the folder structure for temporary test data...
		initTestDataFolder();
		
		// Set up the test environment...
		servletConfig = new MockServletConfig(new SurfServletContext(_testDataFolder), "root");
		
		MockServletConfig surfConfig = new MockServletConfig(servletConfig.getServletContext(), "surf");
		surfConfig.addInitParameter("publishContext", "false");
		surfConfig.addInitParameter("class", "notWritable");
		surfConfig.addInitParameter("unknownParam", "someValue");
		
		dispatcherServlet = new DispatcherServlet();
		dispatcherServlet.setContextClass(XmlWebApplicationContext.class);
		dispatcherServlet.setContextConfigLocation(configLocations);
		dispatcherServlet.init(surfConfig);
	}
	
    /**
     * <p>This method is called once a TestNG suite has completed and performs cleanup by deleting the 
     * temporary data directory (including all it's sub-directories)</p>
     * @throws ServletException
     */
	@AfterSuite
	public static void tearDown() throws ServletException
	{
	    deleteDirectory(_testDataFolder);
	}
	
    protected static MockServletConfig servletConfig;
    
    public static MockServletConfig getServletConfig()
    {
        return servletConfig;
    }

    protected static DispatcherServlet dispatcherServlet;
    
    public static DispatcherServlet getDispatcherServlet()
    {
        return dispatcherServlet;
    }

	public static ServletContext getServletContext() 
	{
		return servletConfig.getServletContext();
	}
	
	public static ApplicationContext getApplicationContext()
	{
		return dispatcherServlet.getWebApplicationContext();
	}
	
	public static WebFrameworkServiceRegistry getServiceRegistry()
	{
		return (WebFrameworkServiceRegistry) getApplicationContext().getBean("webframework.service.registry");
	}
	
	public static ModelObjectService getObjectService()
	{
	    return getServiceRegistry().getModelObjectService();
	}
		
	/**
	 * <p>Creates all the empty directoreis in which test data will get created. These directories will
	 * be deleted by the <code>tearDown</code> method.</p>
	 */
	public static void initTestDataFolder() 
	{		
		deleteDirectory(_testDataFolder);
		_testDataFolder.mkdirs();
		
		File site = new File(_testDataFolder, "site");
		site.mkdirs();
		new File(site, "chrome").mkdirs();
		new File(site, "components").mkdirs();
		new File(site, "component-types").mkdirs();
		new File(site, "configurations").mkdirs();
		new File(site, "content-associations").mkdirs();
		new File(site, "page-associations").mkdirs();
		new File(site, "pages").mkdirs();
		new File(site, "page-types").mkdirs();
		new File(site, "template-instances").mkdirs();
		new File(site, "template-types").mkdirs();
		new File(site, "themes").mkdirs();		
		new File(_testDataFolder, "webscripts").mkdirs();		
		new File(_testDataFolder, "templates").mkdirs();
	}
	
	/**
	 * <p>Standard recursive delete method for deleting directory and all it's contents. This is necessary
	 * because Java doesn't provide a "delete all" style method on <code>java.io.File</code></p>
	 * @param path The current file to delete.
	 * @return <code>true</code> if the deletion was successful and <code>false</code> otherwise.
	 */
	public static boolean deleteDirectory(File path) 
    {
        if( path.exists() )
        {
            File[] files = path.listFiles();
            for(int i=0; i<files.length; i++) 
            {
                if(files[i].isDirectory()) 
                {
                    deleteDirectory(files[i]);
                }
                else 
                {
                    files[i].delete();
                }
            }
        }
        return(path.delete());
    }
}

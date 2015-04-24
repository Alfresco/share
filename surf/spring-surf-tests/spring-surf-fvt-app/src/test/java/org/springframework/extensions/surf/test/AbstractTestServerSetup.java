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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.mortbay.jetty.Server;
import org.mortbay.jetty.webapp.WebAppContext;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Parameters;

/**
 * <p>This class provides the setup and tear down of the Jetty server that hosts the FVT application. The <code>setupJettyServer</code>
 * is called before a test suite is run through the use of the <code>@BeforeSuite</code> annotation and the <code>tearDownJettyServer</code>
 * is called one all the tests in the suite have completed through the use of the <code>@AfterSuite</code> annotation.</p>
 * <p>Test suites are configured by adding <{@code}suiteXmlFile> elements to the <{@code}suiteXmlFiles> element in the project <code>
 * pom.xml</code> file, where each element defines a TestNG configuration file for a test suite. Each TestNG configuration file should
 * reference the same package to test (<code>org.springframework.extensions.surf.test</code>) but supply a different value for the
 * suite scope <code>configFolder</code> parameter. The value of this parameter should be the name of a sub-folder of
 * <code>src/test/testSuiteConfigurations</code> that should contain the configuration files to be used for the test suite.</p>
 * <p>Essentially this means that its possible to run the same suite of tests against different configurations. This was first implemented
 * so that we could test with caching enabled and disabled to ensure that the results were the same.</p>
 *
 * @author David Draper
 */
public class AbstractTestServerSetup
{
    /**
     * This will be instantiated with a Jetty server to perform testing on. Currently this is private but it may be more sensible to open up access if this
     * becomes an abstract class.
     */
    private static Server _jettyServer = null;

    /**
     * This constant contains the relative path the to web.xml file in the source tree. This can be used when running tests against source rather than the
     * packaged build.
     */
    public static final String PATH_TO_WEB_XML = "src/main/webapp/WEB-INF/web.xml";

    /**
     * This constant contains the relateive path to the web application content (i.e. the "webapp" directory. This can be used when running tests against the
     * source rather than the packaged build.
     */
    public static final String PATH_TO_RESOURCE_BASE = "src/main/webapp";

    /**
     * This constant defines the port to use for the Jetty server.
     */
    private static final int _SERVER_PORT = 8083;

    /**
     * This constant defines the context root to use for the web application running on the Jetty server.
     */
    private static final String _CONTEXT_ROOT = "";

    /**
     * This constant defines the URL prefix to use when requesting resources from the Jetty server. It is constructed using the <code>_SERVER_PORT</code> and
     * <code>_CONTEXT_ROOT</code> objects.
     */
    public static final String _URL_PREFIX = "http://localhost:" + _SERVER_PORT + _CONTEXT_ROOT + "/";

    /**
     * <p>This constant defines the folder in which to look for sub-folders defined by the <code>configFolder</code>
     * TestNG configuration parameter. When the <code>setupJettyServer</code> method is run this property will
     * be used to help locate the configuration files to use for the current test suite</p>
     */
    private static final String _ROOT_TEST_SUITE_CONFIG_FOLDER = "src/test/testSuiteConfigurations/";

    /**
     * <p>This constant defines the folder in which to copy test suite specific configuration files. This is the location
     * that has been chosen to host all Spring Surf configuration files for the FVT application. When <code>setupJettyServer</code>
     * runs it will delete the contents of this directory and copy all the files from the specified test suite configuration
     * folder into it.</p>
     */
    private static final String _WEB_APP_CONFIG_FOLDER = "src/main/webapp/WEB-INF/config/";

    /**
     * <p>This constant defines the location of the Surf configuration file in the FVT application. This is the location to which
     * test suite surf configuration files will be copied.</p>
     */
    private static final String _WEB_APP_SURF_CONFIG_FILE = "src/main/webapp/WEB-INF/surf.xml";

    /**
     * <p>This constant defines the folder created by SVN to control the source code. It is referenced when determining whether or not
     * to delete a directory/file (SVN folders should NOT be deleted!)</p>
     */
    private static final String _SVN_DIRECTORY_NAME = ".svn";

    private static void deleteFile(File f) throws Exception
    {
        System.out.println(">>> Deleting file: " + f.getAbsolutePath());
        boolean deleted = f.delete();
        if (!deleted)
        {
            // If it was not possible to delete a configuration file from a previous test suite then
            // we need to throw an exception, because old configuration files could corrupt execution
            // of the current test suite.
            throw new Exception("Could not delete old configuration file: " + f.getAbsolutePath());
        }
        else
        {
            // No action required. The configuration file was successfully deleted.
        }
    }

    /**
     * <p>This method is annotated with <code>@BeforeSuite</code> which means that TestNG will run it before
     * executing any of the tests found in the test suite. The purpose of this method is to copy across the
     * desired Spring Surf configuration from the <code>testSuiteConfigurations</code> folder into the
     * <code>webapp/WEB-INF/config</code> folder and start up a Jetty server that will make use of that configuration.</p>
     *
     * @param configFolder This parameter must be configured in the TestNG configuration file as it defines the
     * sub-folder of <code>testSuiteConfigurations</code> from which to retrieve the configuration files.
     * @throws Exception
     */
    @Parameters({"configFolder"})
    @BeforeSuite
    public static void setupJettyServer(String configFolder) throws Exception
    {
        System.out.println("Setting up");

        // Create a new Jetty server with a port that's unlikely to be in use.
        _jettyServer = new Server(_SERVER_PORT);

        WebAppContext webapp = new WebAppContext();    // Create a new WebAppContext for our packaged WAR
        webapp.setContextPath(_CONTEXT_ROOT);          // Set the context root
        webapp.setDescriptor(PATH_TO_WEB_XML);         // (for option 2) create a
        webapp.setResourceBase(PATH_TO_RESOURCE_BASE); // WebAppConext to the source

        // Copy across the configuration files for the current test suite...
        if (configFolder == null)
        {
            throw new Exception("\"configFolder\" parameter not defined in TestNG configuration file");
        }
        else if (configFolder.equals(""))
        {
            throw new Exception("\"configFolder\" parameter defined in TestNG configuration file must not be set to \"\"");
        }
        else
        {
            // Copy the surf.xml configuration file from the test suite configuration directory if it exists...
            File surfConfig = new File(_ROOT_TEST_SUITE_CONFIG_FOLDER + configFolder + "/surf/surf.xml");
            if (surfConfig.exists())
            {
                // Delete the old Surf config...
                File oldSurfConfig = new File(_WEB_APP_SURF_CONFIG_FILE);
                if (oldSurfConfig.exists())
                {
                    deleteFile(oldSurfConfig);
                }

                // Copy across the test suite version...
                copy(surfConfig, oldSurfConfig);
            }

            // Delete all the previous configuration files (if any) that may be left over from the last test suite to run...
            File webAppConfigDirectory = new File(_WEB_APP_CONFIG_FOLDER);
            if (webAppConfigDirectory.exists() && webAppConfigDirectory.isDirectory())
            {
                for (File f: webAppConfigDirectory.listFiles())
                {
                    if (f.getName().equals(_SVN_DIRECTORY_NAME))
                    {
                        // No action required. Don't try and delete the .svn folder.
                    }
                    else
                    {
                        deleteFile(f);
                    }
                }
            }
            else
            {
                // SANITY CHECK: If web app configuration file doesn't exist or is not a folder, then something has gone
                // seriously wrong. We'll throw an exception just to identify the cause of the problem. We could choose to
                // actually re-create the folder so that it can be populated, but just in case the folder has been renamed
                // or moved we won't (because it could be that there is another folder with Spring Surf configuration files
                // that may corrupt the test!)
                throw new Exception("The desigated FVT web app configuration folder (" + _WEB_APP_CONFIG_FOLDER + ") does not exist!");
            }

            // Copy across the configuration files for the current test suite...
            File configDirectory = new File(_ROOT_TEST_SUITE_CONFIG_FOLDER + configFolder + "/spring");
            if (configDirectory.exists() && configDirectory.isDirectory())
            {
                for (File f: configDirectory.listFiles())
                {
                    if (f.getName().equals(_SVN_DIRECTORY_NAME))
                    {
                        // No action required. Don't copy the SVN folder.
                    }
                    else
                    {
                        File target = new File(_WEB_APP_CONFIG_FOLDER + f.getName());
                        copy(f, target);
                    }
                }
            }
            else
            {
                throw new Exception("Could not find folder: " + configDirectory.getAbsolutePath());
            }
        }

        // This is a rather crude hack to get around the problem of Jetty not being able to find compile
        // JSPs that use the Surf TagLibs. Without this code the JSP chrome related tests will fail. Jetty
        // is somehow able to find the classes referenced by the TLD but not the TLD itself so in order to
        // make it available we manually copy it across (because we don't want to maintain a separate copy
        // as this would require keeping changes synchronised). It's also necessary to delete the file even
        // if we find one because we must ensure we have the latest version. This section of code is not
        // actually necessary for all test classes, but is being kept in the abstract class just in case
        // any extending classes tests make use of the tag lib. It was not possible to perform the copy
        // using Maven as this only copies to the target directory and is generally to complex to configure
        // any plugins to do this!
        File destTld = new File("src/main/webapp/WEB-INF/surf.tld");
        if (destTld.exists()) destTld.delete();
        File tld = new File("../../spring-surf/spring-surf/src/main/resources/META-INF/tlds/surf.tld");
        copy(tld, destTld);
        _jettyServer.setHandler(webapp); // Tell Jetty to use the WAR
        _jettyServer.start();
    }

    /**
     * Copy a source file to a destination.
     *
     * @param src
     * @param dst
     * @throws IOException
     */
    private static void copy(File src, File dst) throws IOException
    {
        System.out.println(">>> Copying: " + src.getAbsolutePath() + ", to: " + dst.getAbsolutePath());
        InputStream in = new FileInputStream(src);
        OutputStream out = new FileOutputStream(dst);
        byte[] buf = new byte[1024];
        int len;
        while ((len = in.read(buf)) > 0)
        {
            out.write(buf, 0, len);
        }
        in.close();
        out.close();
    }

    @AfterSuite
    public static void tearDownJettyServer() throws Exception
    {
        System.out.println("Tearing down");
        _jettyServer.stop();
        System.out.println("Stopped Jetty server");
    }

}

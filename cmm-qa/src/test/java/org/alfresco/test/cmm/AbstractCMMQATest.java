/*
 * #%L
 * Alfresco CMM Automation QA
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.test.cmm;

import java.io.File;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.cmm.AbstractTestCMM;
import org.alfresco.po.share.util.PageUtils;
import org.alfresco.po.share.util.ShareTestProperty;
import org.alfresco.rest.api.tests.client.HttpClientProvider;
import org.alfresco.test.properties.QATestSettings;
import org.alfresco.test.utils.api.AlfrescoApiProperties;
import org.alfresco.test.utils.api.AlfrescoUserProperties;
import org.alfresco.test.utils.api.ApiClient;
import org.alfresco.test.utils.api.CmisApiUtils;
import org.alfresco.test.utils.api.OAuthProperties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
@ContextConfiguration("classpath*:cmm-qa-testContext.xml")
/**
 * Class includes: Abstract test holds all common methods, These will be used from within the ShareUser utils or tests.
 * 
 * @author Meenal Bhave
 * @since version 1.0
 */
public abstract class AbstractCMMQATest extends AbstractTestCMM
{
    private static final Log logger = LogFactory.getLog(AbstractCMMQATest.class);
    
    // Test Context File
    protected static String contextFileName = "cmm-qa-testContext.xml";

    // Test Related Folders
    public static final String SLASH = File.separator;

    private static final String SRC_ROOT = System.getProperty("user.dir") + SLASH;

    protected static final String DATA_FOLDER = SRC_ROOT + "testdata" + SLASH;
    

    protected String testName;

    public static long maxWaitTime;

    // Test Run and Users Info: This is now a part of test.properties
    @Autowired private ApplicationContext ctx;
    
    protected static String ADMIN_USERNAME;

    protected static String ADMIN_PASSWORD;

    protected static String DOMAIN_FREE;

    protected static String DOMAIN_PREMIUM;

    protected static String DOMAIN_HYBRID;
        
    protected static long SOLR_WAIT_TIME;
    
    protected static int SOLR_RETRY_COUNT;

    protected static String DEFAULT_USER;    

    protected static String DEFAULT_FREENET_USER;

    protected static String DEFAULT_PREMIUMNET_USER;

    protected static String DEFAULT_PASSWORD;

    protected static final String DEFAULT_LASTNAME = "LName";

    protected static String UNIQUE_TESTRUN_NAME;

    protected static boolean oAuthEnabled;

    protected static String apiKey;

    protected static String apiSecretKey;

    protected static Map<String, WebDriver> driverMap = new HashMap<String, WebDriver>();

    protected static Map<WebDriver, ShareTestProperty> driverMapPO = new HashMap<WebDriver, ShareTestProperty>();

    protected static Map<WebDriver, QATestSettings> driverMapQA = new HashMap<WebDriver, QATestSettings>();

    protected static Map<WebDriver, AlfrescoApiProperties> driverMapAPI = new HashMap<WebDriver, AlfrescoApiProperties>();

    protected QATestSettings testProperties;

    @Autowired protected AlfrescoApiProperties api;

    @Autowired protected HttpClientProvider httpClientProvider;
    protected OAuthProperties oAuthProps;
    
    protected AlfrescoUserProperties alfUsers;
    
    protected AlfrescoUserProperties alfUsersHybrid;

    // Hybrid Related
    protected ShareTestProperty hybridShareTestProperties;

    protected QATestSettings testPropertiesHybrid;

    protected AlfrescoApiProperties apiPropsHybrid;

    protected HttpClientProvider httpClientProviderHybrid;

    protected OAuthProperties oAuthPropsHybrid;
    
    protected ApiClient apiClient;
    protected CmisApiUtils cmisApiClient;
    @Value("${cmm.model.admin.group}") protected String modelAdmin = "ALFRESCO_MODEL_ADMINISTRATORS";
    @Value("${cmm.property.datatype.int}") protected String datatypei;
    @Value("${cmm.property.datatype.float}") protected String datatypef;
    @Value("${cmm.property.datatype.date}") protected String datatypedate;
    @Value("${cmm.property.datatype.boolean}") protected String datatypeb;
    @Value("${cmm.property.datatype.content}") protected String datatypec; 
    @Value("${cmm.property.datatype.double}") protected String datatyped;
    @Value("${cmm.property.datatype.datetime}") protected String datatypedt;
    @Value("${cmm.property.datatype.text}") protected String datatypet;
    @Value("${cmm.property.mandatory}") protected String mandatory;
    @Value("${cmm.property.optional}") protected String optional;
    @Value("${cmm.property.datatype.long}")protected String datatypel;
    @Value("${cmm.property.datatype.datetime}")protected String datatypeDateTime;
    @Value("${cmm.model.status.draft}") protected String modelStatusDraft = "DRAFT";
    @Value("${cmm.model.status.active}")protected String modelStatusActive = "ACTIVE";
    @Value("${property.value.empty}")protected String propertyEmpty = "(None)";
    
    protected static final String dateValue = "2100-01-01";
    protected static final String dateEntry ="01/01/2100";
    
    @BeforeClass(alwaysRun = true)
    public void setupTestContext() throws Exception
    {        
        logger.debug("Starting CMM QA Setup");
        // Alfresco Instance 1
        this.testProperties = (QATestSettings) ctx.getBean("testSettings");
        this.api = (AlfrescoApiProperties) ctx.getBean("api");
        this.httpClientProvider = (HttpClientProvider) ctx.getBean("httpClientProvider");
        this.oAuthProps = (OAuthProperties) ctx.getBean("authProperties");
        this.alfUsers = (AlfrescoUserProperties) ctx.getBean("alfUsers");

        // Alfresco Instance 2: Hybrid?
//        this.hybridShareTestProperties = (ShareTestProperty) ctx.getBean("shareHybridTestProperties");
//        this.testPropertiesHybrid = (QATestSettings) ctx.getBean("testSettings");
//        this.apiPropsHybrid = (AlfrescoApiProperties) ctx.getBean("apiHybrid");
//        this.httpClientProviderHybrid = (HttpClientProvider) ctx.getBean("httpClientProviderHybrid");
//        this.oAuthPropsHybrid = (OAuthProperties) ctx.getBean("authPropertiesHybrid");
//        this.alfUsersHybrid = (AlfrescoUserProperties) ctx.getBean("alfUsersHybrid");

        UNIQUE_TESTRUN_NAME = testProperties.getUniqueTestRunName();

        ADMIN_USERNAME = testProperties.getAdminUsername();
        ADMIN_PASSWORD = testProperties.getAdminPassword();
        
        DEFAULT_USER = testProperties.getDefaultUser();
        DEFAULT_PASSWORD = testProperties.getDefaultPassword();
        
        DOMAIN_FREE = testProperties.getDomainFree();
        DOMAIN_PREMIUM = testProperties.getDomainPremium();
        DOMAIN_HYBRID = testProperties.getDomainHybrid();        
        
        SOLR_WAIT_TIME = testProperties.getSolrWaitTime();
        SOLR_RETRY_COUNT = testProperties.getSolrRetryCount();

        oAuthEnabled = oAuthProps.isOAuthEnabled();
        apiKey = oAuthProps.getApiKey();
        apiSecretKey = oAuthProps.getApiSecretKey();

        DEFAULT_FREENET_USER = DEFAULT_USER + "@" + DOMAIN_FREE;
        DEFAULT_PREMIUMNET_USER = DEFAULT_USER + "@" + DOMAIN_PREMIUM;

        logger.info("Target URL: " + shareUrl);
    }
    
    private ApiClient getAPIClient()
    {
        return new ApiClient(contextFileName); 
    }
    
    public void setupCmis() throws Exception
    {
        setup();
        apiClient = getAPIClient();
        cmisApiClient = apiClient.getCmisApiClient();
    }
    
    public void setup() throws Exception
    {
        if (driver == null)
        {
            super.getWebDriver();          
        }
        driverMap.put("std_driver", driver);
        //driverMapPO.put(driver, shareTestProperties);
        driverMapQA.put(driver, testProperties);
        driverMapAPI.put(driver, api);
        maxWaitTime = maxPageWaitTime;
        logger.info("WebDriver instance opened");
    }

//    public void setupHybriddriver() throws Exception
//    {
//        if (hybriddriver == null)
//        {
//            hybriddriver = (WebDriver) ctx.getBean("hybriddriver");
//        }
//        
//        // Alfresco Instance 2: Or Hybrid        
//        this.hybridShareTestProperties = (ShareTestProperty) ctx.getBean("shareHybridTestProperties");
//        this.testPropertiesHybrid = (QATestSettings) ctx.getBean("testSettings");
//        this.apiPropsHybrid = (AlfrescoApiProperties) ctx.getBean("apiHybrid");
//        this.httpClientProviderHybrid = (HttpClientProvider) ctx.getBean("httpClientProviderHybrid");
//        this.oAuthPropsHybrid = (OAuthProperties) ctx.getBean("authenticationHybrid");
//        this.alfUsersHybrid = (AlfrescoUserProperties) ctx.getBean("alfUsersHybrid");
//        
//        driverMap.put("hybriddriver", hybriddriver);
//        driverMapPO.put(hybriddriver, hybridShareTestProperties);
//        driverMapQA.put(hybriddriver, testPropertiesHybrid);
//        driverMapAPI.put(hybriddriver, apiPropsHybrid);
//        maxWaitTime = ((WebDriverImpl) hybriddriver).getMaxPageRenderWaitTime();
//    }

    @AfterClass(alwaysRun = true)
    public void tearDown()
    {
        if (logger.isTraceEnabled())
        {
            logger.trace("shutting web driver");
        }
        // Close the browser
        for (Map.Entry<String, WebDriver> entry : driverMap.entrySet())
        {
            try
            {
                if (entry.getValue() != null)
                {
                    try
                    {
                        shareUtil.logout(entry.getValue());
                    }
                    catch (Exception e)
                    {
                        logger.error("If it's tests associated with admin-console-summary-page. it's normal. If not - we have a problem.", e);
                    }
                    entry.getValue().quit();
                    logger.info(entry.getKey() + " closed");
                    logger.info("[Suite ] : End of Tests in: " + this.getClass().getSimpleName());
                }
            }
            catch (Exception e)
            {
                logger.error("Failed to close previous instance of browser:" + entry.getKey(), e);
            }
        }
    }

    /**
     * Helper to log a user into alfresco.
     * 
     * @param driver
     * @param userInfo
     * @return DashBoardPage
     * @throws Exception if error
     */
    public DashBoardPage loginAs(WebDriver driver, String... userInfo) throws Exception
    {
        if (userInfo.length < 2)
        {
            userInfo = getAuthDetails(userInfo[0]);
        }        
        return shareUtil.loginWithPost(driver, shareUrl, userInfo[0], userInfo[1]).render();
    }

    @BeforeMethod
    protected String getMethodName(Method method)
    {
        String methodName = method.getName();
        logger.info("[Test: " + methodName + " ]: START");
        return methodName;
    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st step of the test. Common Test code can later be introduced here.
     * 
     * @return String testcaseName
     */
    public String getUniqueTestName()
    {
        String testID = Thread.currentThread().getStackTrace()[2].getMethodName();
        return getTestDataRef(testID);
    }
    
    /**
     * Helper returns the test / methodname.
     * 
     * @return String testcaseName
     */
    public String getTestName()
    {
        return Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st step of the test. Common Test code can later be introduced here.
     * 
     * @return String testcaseName
     */
    public String getTestName(String testID)
    {
        return testID.substring(testID.lastIndexOf("_")).replace("_", alfrescoVersion + "-");
    }

    /**
     * Helper returns the test / methodname. This needs to be called as the 1st step of the test. Common Test code can later be introduced here.
     * 
     * @return String testcaseName
     */
    public String getTestDataRef(String testID)
    {
    	if (testID.length() > 13) 
    	{
    		testID = testID.substring(4,13);
    	}
    	else if(testID.length() > 9)
    	{
    		testID = testID.substring(4);
    	}
    	
    	String testLongName = "_" + System.currentTimeMillis();
        return testID + testLongName.substring(7);
    }

    /**
     * Helper to consistently get the username in the free domain, in the desired format.
     * 
     * @param testID String Name of the test for uniquely identifying / mapping test data with the test
     * @return String username
     */
    public String getUserNameFreeDomain(String testID)
    {
        return String.format("user%s@%s", testID, DOMAIN_FREE);
    }

    /**
     * Helper to consistently get the username in the premium domain, in the desired format.
     * 
     * @param testID String Name of the test for uniquely identifying / mapping test data with the test
     * @return String username
     */
    protected String getUserNamePremiumDomain(String testID)
    {
        return String.format("user%s@%s", testID, DOMAIN_PREMIUM);
    }

    /**
     * Helper to consistently get the userName in the specified domain, in the desired format.
     * 
     * @param testID String Name of the test for uniquely identifying / mapping test data with the test
     * @return String userName
     */
    protected String getUserNameForDomain(String testID, String domainName)
    {
        if (domainName.isEmpty())
        {
            domainName = DOMAIN_FREE;
        }
        return String.format("user%s@%s", testID, domainName).toLowerCase();
    }

    /**
     * Helper to consistently get the DomainName based on the specified domain, in the desired format.
     * 
     * @param domainID String to be prefixed to DOMAIN_FREE
     * @return String Domain
     */
    protected String getDomainName(String domainID)
    {
        if ((domainID == null) || (domainID.isEmpty())) { return DOMAIN_FREE; }
        return domainID + DOMAIN_FREE;
    }

    /**
     * Helper to consistently get the Site Name.
     * 
     * @param testID String Name of the test for uniquely identifying / mapping test data with the test
     * @return String sitename
     */
    public String getSiteName(String testID)
    {
        String siteName = "";

        siteName = String.format("Site%s%s", UNIQUE_TESTRUN_NAME, testID);

        return siteName;
    }

    /**
     * Helper to consistently get the Site Short Name.
     * 
     * @param siteName String Name of the test for uniquely identifying / mapping test data with the test
     * @return String site short name
     */
    public String getSiteShortname(String siteName)
    {
        String siteShortname = "";
        String[] unallowedCharacters = { "_", "!" };

        for (String removeChar : unallowedCharacters)
        {
            siteShortname = siteName.replace(removeChar, "");
        }

        return siteShortname.toLowerCase();
    }

    /**
     * Helper to consistently get the filename.
     * 
     * @param partFileName String Part Name of the file for uniquely identifying / mapping test data with the test
     * @return String fileName
     */
    protected String getFileName(String partFileName)
    {
        String fileName = "";

        fileName = String.format("File%s-%s", UNIQUE_TESTRUN_NAME, partFileName);

        return fileName;
    }

    /**
     * Helper to consistently get the folderName.
     * 
     * @param partFolderName String Part Name of the folder for uniquely identifying / mapping test data with the test
     * @return String folderName
     */
    protected String getFolderName(String partFolderName)
    {
        String folderName = "";

        folderName = String.format("Folder%s-%s", UNIQUE_TESTRUN_NAME, partFolderName);

        return folderName;
    }

    /**
     * Common method to get the Authentication details based on the username specified.
     * 
     * @param authUsername String Username, User email
     * @return String array of auth details, consisting of username and password
     */
    public String[] getAuthDetails(String authUsername)
    {
        String[] authDetails = { username, password };

        if (authUsername == null)
        {
            authUsername = "";
        }

        if (!authUsername.isEmpty())
        {
            authDetails[0] = authUsername;
        }

        if (!authUsername.equals(username))
        {
            authDetails[1] = DEFAULT_PASSWORD;
        }

        return authDetails;
    }

    /**
     * This util method returns a random string of letters for the given length.
     * 
     * @param length int
     * @return String
     */
    public String getRandomString(int length)
    {
        StringBuilder rv = new StringBuilder();
        Random rnd = new Random();
        char[] from = "abcdefghijklmnopqrstuvwxyz".toCharArray();

        for (int i = 0; i < length; i++)
        {
            rv.append(from[rnd.nextInt(from.length - 1)]);
        }
        return rv.toString();
    }
    
    /*
     * Util returns the property name as it appears on the Document / Folder Details Page in Share
     * This util is necessary for now as getProperties() replaces ":" with "" in the Property lebel.
     * Need to investigate the impact of changing this
     */
    public String getDocDetailsPropName(String cmisPropertyName)
    {
        PageUtils.checkMandatoryParam("Specify appropriate Cmis Property Name", cmisPropertyName);
        return cmisPropertyName.replace(":", "");
    }
}

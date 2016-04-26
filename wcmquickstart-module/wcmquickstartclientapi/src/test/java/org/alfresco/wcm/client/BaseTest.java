package org.alfresco.wcm.client;

import org.alfresco.wcm.client.util.CmisSessionHelper;
import org.alfresco.wcm.client.util.CmisSessionPool;
import org.apache.chemistry.opencmis.client.api.Session;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import junit.framework.TestCase;

/**
 * @author Roy Wetherall
 */
public abstract class BaseTest extends TestCase
{
    protected static final String HOST = "localhost";
    protected static final int PORT = 8080;
    
    protected ApplicationContext appContext;    
    protected CmisSessionPool sessionPool;
    protected Session session = null;

    protected SectionFactory sectionFactory;
    protected AssetFactory assetFactory;
    protected CollectionFactory collectionFactory;
    protected WebSiteService webSiteService;
    protected DictionaryService dictionaryService;
    
    @Override
    protected void setUp() throws Exception
    {
        super.setUp();
             
        // Load the application context
        appContext = new ClassPathXmlApplicationContext("alfresco/wcmqs-api-context.xml");
        
        // Set the CMIS session up
        sessionPool = (CmisSessionPool)appContext.getBean("sessionPool");
        for (int i = 0; i < 5; ++i)
        {
            try
            {
                session = sessionPool.getGuestSession();
            }
            catch (Exception ex)
            {

            }
            if (session == null)
            {
                Thread.sleep(1000L);
            }
            else
            {
                break;
            }
        }
        CmisSessionHelper.setSession(session);
        
        // Get beans
        sectionFactory = (SectionFactory)appContext.getBean("sectionFactory");
        assetFactory = (AssetFactory)appContext.getBean("assetFactory");
        collectionFactory = (CollectionFactory)appContext.getBean("collectionFactory");
        webSiteService = (WebSiteService)appContext.getBean("webSiteService");    
        dictionaryService = (DictionaryService)appContext.getBean("dictionaryService");
    }
    
    @Override
    protected void tearDown() throws Exception
    {
        super.tearDown();
        sessionPool.closeSession(session);
    }
    
    protected WebSite getWebSite()
    {
        WebSite site = webSiteService.getWebSite(HOST, PORT, "wcmqs");
        assertNotNull("Unable to find site for host " + HOST + " and port " + PORT, site);
        return site;
    }
    
    protected Section getRootSection()
    {
        WebSite site = getWebSite();
        Section section = site.getRootSection();
        assertNotNull("Root section should not be null", section);
        return section;
    }
}

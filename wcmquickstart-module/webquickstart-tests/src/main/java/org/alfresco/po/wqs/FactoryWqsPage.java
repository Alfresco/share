package org.alfresco.po.wqs;

import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.SharePopup;
import org.alfresco.po.share.UnknownSharePage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.PageFactory;
import org.alfresco.webdrone.WebDrone;
import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Oana Caciuc on 22.02.2015.
 */
public class FactoryWqsPage implements PageFactory
{

    public static final String NODE_REFRESH_META_DATA_IDENTIFIER = "?refreshMetadata";
    protected static final String FAILURE_PROMPT = "div[id='prompt']";
    protected static final String NODE_REF_IDENTIFIER = "?nodeRef";
    protected static final By WQS_LOGIN_DIALOGUE = By.cssSelector("div[id='awe-login']");
    private static final String CREATE_PAGE_ERROR_MSG = "Unabel to instantiate the page";
    protected static ConcurrentHashMap<String, Class<? extends WcmqsAbstractPage>> pages;

    private static Log logger = LogFactory.getLog(FactoryWqsPage.class);

    static
    {
        pages = new ConcurrentHashMap<String, Class<? extends WcmqsAbstractPage>>();
        pages.put("wcmqs", WcmqsHomePage.class);
        pages.put("blog", WcmqsBlogPage.class);
        pages.put("blogpost", WcmqsBlogPostPage.class);
        pages.put("news", WcmqsNewsPage.class);
        pages.put("articles", WcmqsNewsArticleDetails.class);
        pages.put("contact", WcmqsContactPage.class);
        pages.put("publications", WcmqsAllPublicationsPage.class);
        pages.put("publicationpage", WcmqsPublicationPage.class);
        pages.put("search", WcmqsSearchPage.class);

    }

    @Override
    public HtmlPage getPage(WebDrone drone)
    {
        return resolveWqsPage(drone);
    }

    public static HtmlPage resolveWqsPage(final WebDrone drone) throws PageException
    {
        // Share Error PopUp
        try
        {
            WebElement errorPrompt = drone.find(By.cssSelector(FAILURE_PROMPT));
            if (errorPrompt.isDisplayed())
            {
                return new SharePopup(drone);
            }
        }
        catch (NoSuchElementException nse)
        {
        }

        // Determine what page we're on based on url
        return getPage(drone.getCurrentUrl(), drone);
    }

    /**
     * Resolves the required page based on the URL containing a keyword
     * that identify's the page the drone is currently on. Once a the name
     * is extracted it is used to get the class from the map which is
     * then instantiated.
     *
     * @param drone WebDriver browser client
     * @return SharePage page object
     */
    public static SharePage getPage(final String url, WebDrone drone)
    {
        String pageName = resolvePage(url);
        if (logger.isTraceEnabled())
        {
            logger.trace(url + " : page name: " + pageName);
        }
        if (pages.get(pageName) == null)
        {
            return instantiatePage(drone, UnknownSharePage.class);
        }
        return instantiatePage(drone, pages.get(pageName));
    }

    /**
     * Extracts the String value from the last occurrence of slash in the url.
     *
     * @param url String url.
     * @return String page title
     */
    protected static String resolvePage(String url)
    {
        if (url == null || url.isEmpty())
        {
            throw new UnsupportedOperationException("Empty url is not allowed");
        }
        if (url.endsWith("wcmqs/"))
        {
            return "wcmqs";
        }
        if (url.endsWith("news/"))
        {
            return "news";
        }
        if (url.endsWith("blog/"))
        {
            return "blog";
        }

        if (url.contains(NODE_REF_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REF_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }

        if (url.contains(NODE_REFRESH_META_DATA_IDENTIFIER))
        {
            int index = url.indexOf(NODE_REFRESH_META_DATA_IDENTIFIER);
            url = url.subSequence(0, index).toString();
        }

        // Get the last element of url
        StringTokenizer st = new StringTokenizer(url, "/");
        String val = "";
        while (st.hasMoreTokens())
        {
            if (st.hasMoreTokens())
            {
                val = st.nextToken();
            }
        }

        if (val.contains("contact"))
        {
            return "contact";
        }
        if (val.contains("publicationpage"))
        {
            return "publicationpage";
        }
        if (val.contains("publications"))
        {
            return "publications";
        }
        if (val.contains("research-reports"))
        {
            return "publications";
        }
        if (val.contains("white-papers"))
        {
            return "publications";
        }
        if (val.endsWith("global") || val.endsWith("companies") || val.endsWith("markets"))
        {
            return "news";
        }
        if (url.contains("news/") && val.endsWith(".html"))
        {
            return "articles";
        }
        if (url.contains("blog/") && val.endsWith(".html"))
        {
            return "blogpost";
        }
        if (val.contains("search"))
        {
            if (val.contains("phrase"))
            {
                return "search";
            }
        }

        /*
         * // Remove any clutter.
         * if (val.contains("?") || val.contains("#"))
         * {
         * val = extractName(val);
         * }
         */

        return val;
    }

    /**
     * Instantiates the page object matching the argument.
     *
     * @param drone {@link WebDrone}
     * @param pageClassToProxy expected Page object
     * @return {@link SharePage} page response
     */
    protected static <T extends HtmlPage> T instantiatePage(WebDrone drone, Class<T> pageClassToProxy)
    {
        if (drone == null)
        {
            throw new IllegalArgumentException("WebDrone is required");
        }
        if (pageClassToProxy == null)
        {
            throw new IllegalArgumentException("Page object is required for url: " + drone.getCurrentUrl());
        }
        try
        {
            try
            {
                Constructor<T> constructor = pageClassToProxy.getConstructor(WebDrone.class);
                return constructor.newInstance(drone);
            }
            catch (NoSuchMethodException e)
            {
                return pageClassToProxy.newInstance();
            }
        }
        catch (InstantiationException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (IllegalAccessException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
        catch (InvocationTargetException e)
        {
            throw new PageException(CREATE_PAGE_ERROR_MSG, e);
        }
    }

    /**
     * Helper method to return right Page for Login form displayed
     *
     * @return HtmlPage
     */
    private static HtmlPage resolveWqsDialoguePage(WebDrone drone)
    {
        SharePage sharePage = null;
        try
        {
            WebElement dialogue = drone.findFirstDisplayedElement(WQS_LOGIN_DIALOGUE);

            if (dialogue != null && dialogue.isDisplayed())
            {
                sharePage = new WcmqsLoginPage(drone);
            }
        }
        catch (NoSuchElementException e)
        {
        }
        return sharePage;
    }

    /**
     * Extracts the name from any url noise.
     *
     * @param pageName String page name
     * @return the page name
     */
    private static String extractName(String pageName)
    {
        String regex = "([?&#])";
        String vals[] = pageName.split(regex);
        return vals[0];
    }

}

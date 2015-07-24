package org.alfresco.po.share.util;

import org.alfresco.webdrone.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The Class PageUtils.
 */
public class PageUtils
{
    private static final String REGEXP_PROTOCOL = "\\w+\\:\\W+";
    private static final String REGEXP_SHARE_URL = "\\w+\\W?\\w+\\W?\\w+\\W?\\w+\\:?\\w+?\\/(share)";
    private static final String REGEXP_IP_WITH_PORT = "(\\d{1,3}\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})(:\\d{1,5})?";
    private static final String REGEXP_DOMAIN_WITH_PORT = "(\\w+(\\.\\w+)*(\\.\\w{2,}))(:\\d{1,5})?";
    private static final String REGEX_SITE_NAME = "\\/site\\/(.*)\\/(.*)";

    /**
     * Is a WebElement usable?
     *
     * @param element the WebElement
     * @return boolean
     */
    public static boolean usableElement(WebElement element)
    {
        if (element != null && element.isDisplayed() && element.isEnabled()
                && !StringUtils.contains(element.getAttribute("class"), "dijitDisabled")
                && !StringUtils.contains(element.getAttribute("aria-disabled"), "true"))
        {
            return true;
        }
        return false;
    }

    /**
     * Method to return current protocol from the url
     *
     * @param shareUrl String
     * @return String
     */
    public static String getProtocol(String shareUrl)
    {

        Pattern p1 = Pattern.compile(REGEXP_PROTOCOL);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group();
        else
            throw new PageException("Can't extract protocol");
    }

    /**
     * Method to return Share Url without the protocol string (i.e pbld01.alfresco.com/share)
     *
     * @param shareUrl String
     * @return String
     */
    public static String getShareUrl(String shareUrl)
    {

        Pattern p1 = Pattern.compile(REGEXP_SHARE_URL);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group();
        else
            throw new PageException("Can't extract Share URL");
    }

    /**
     * Method to return Share server address and port(i.e pbld01.alfresco.com, 127.0.0.1:8080)
     *
     * @param shareUrl String
     * @return String
     */
    public static String getAddress(String shareUrl)
    {
        Pattern p1 = Pattern.compile(REGEXP_IP_WITH_PORT);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
        {
            return m1.group();
        }
        else
        {
            p1 = Pattern.compile(REGEXP_DOMAIN_WITH_PORT);
            m1 = p1.matcher(shareUrl);
            if (m1.find())
            {
                return m1.group();
            }
        }
        throw new PageException("Can't extract address from URL");
    }

    /**
     * Method to retrieve site name from the url
     *
     * @param shareUrl String
     * @return String
     */
    public static String getSiteName(String shareUrl)
    {
        Pattern p1 = Pattern.compile(REGEX_SITE_NAME);
        Matcher m1 = p1.matcher(shareUrl);
        if (m1.find())
            return m1.group(1);
        else
            throw new PageException("Can't extract Share URL");
    }
}
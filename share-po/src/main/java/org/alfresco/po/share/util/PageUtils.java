/*
 * #%L
 * share-po
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
package org.alfresco.po.share.util;

import org.alfresco.po.exception.PageException;
import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebElement;

import java.util.Collection;
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
    /**
     * Helper method to check the parameters. This method should be used for all public methods.
     *
     * @author Tuna Aksoy
     * @param paramName {@link String} A name for the parameter to check
     * @param object {@link Object} The object to check
     * @param <E> type
     * @exception IllegalArgumentException will be thrown if the parameter value is null
     * (for {@link String} also if the value is empty or blank)
     * 
     */
    public static <E> void checkMandatoryParam(final String paramName, final Object object)
    {
        if(StringUtils.isBlank(paramName))
        {
            throw new IllegalArgumentException(String.format("The parameter paramName is required and can not be'%s'", paramName));
        }
        if (object == null)
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter and must have a value", paramName));
        }
        if(object instanceof String && StringUtils.isBlank((String) object))
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter", paramName));
        }
        if(object instanceof Collection<?> && ((Collection<?>)object).isEmpty())
        {
            throw new IllegalArgumentException(String.format("'%s' is a mandatory parameter and can not be empty", paramName));
        }
    }
}

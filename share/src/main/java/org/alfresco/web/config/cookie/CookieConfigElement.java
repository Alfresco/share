/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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

package org.alfresco.web.config.cookie;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

public class CookieConfigElement extends ConfigElementAdapter
{
    public static final String CONFIG_ELEMENT_ID = "cookie";

    // is remove cookie feature is enabled
    protected Boolean enableCookie;
    // cookies to remove from response
    protected Set<String> cookiesToRemove = new HashSet<>();

    /**
     * Default Constructor
     */
    public CookieConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    public CookieConfigElement(String name)
    {
        super(name);
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.extensions.surf.config.element.ConfigElementAdapter#combine(org.springframework.extensions
     * .surf.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        CookieConfigElement configElement = (CookieConfigElement) element;

        // new combined element
        CookieConfigElement combinedElement = new CookieConfigElement();
        combinedElement.enableCookie = this.enableCookie;
        if (configElement.enableCookie != null)
        {
            combinedElement.enableCookie = configElement.enableCookie;
        }
        return combinedElement;
    }

    /**
     * Constructs a new instance from an XML Element.
     * 
     * @param elem the XML element
     * @return the Cookie configuration element
     */
    @SuppressWarnings("unchecked")
    protected static ConfigElement newInstance(Element elem)
    {
        CookieConfigElement configElement = new CookieConfigElement();
        String enableCookie = elem.elementTextTrim("enableCookie");

        if (enableCookie != null && enableCookie.length() > 0)
        {
            configElement.enableCookie = Boolean.parseBoolean(enableCookie);
        }

        Element cookiesToRemoveElement = elem.element("cookies-to-remove");
        if (cookiesToRemoveElement != null)
        {
            List<Element> elements = cookiesToRemoveElement.elements("cookie-to-remove");
            for (Element element : elements)
            {
                configElement.cookiesToRemove.add(element.getText().trim());
            }
        }

        return configElement;
    }
    
    public boolean isCookieEnabled()
    {
        return (this.enableCookie != null) ? this.enableCookie.booleanValue() : Boolean.TRUE;
    }
    
    public Set<String> getCookiesToRemove() {
        return cookiesToRemove;
    }
}
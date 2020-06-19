/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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
package org.alfresco.web.site.servlet.config;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Describes the kerberos authentication properties stored within the <kerberos> block of the current configuration.
 * 
 * @author dward
 */
public class KerberosConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 4178518406841891833L;

    /** The password. */
    private String password;

    /** The realm. */
    private String realm;

    /** The Service Principal Name to use on the endpoint. This must be like: HTTP/host.name@REALM */
    private String endpointSPN;

    /** JAAS login configuration entry name. */
    private String loginEntryName;

    /** A Boolean which when true strips the @domain sufix from Kerberos authenticated usernames. Default is <tt>true</tt>. */
    private boolean stripUserNameSuffix = true;

    /**
     * Constructs a new Kerberos Config Element.
     */
    public KerberosConfigElement()
    {
        super("kerberos");
    }

    /*
     * (non-Javadoc)
     * @see
     * org.springframework.extensions.surf.config.element.ConfigElementAdapter#combine(org.springframework.extensions
     * .surf.config.ConfigElement)
     */
    public ConfigElement combine(ConfigElement element)
    {
        KerberosConfigElement configElement = (KerberosConfigElement) element;

        // new combined element
        KerberosConfigElement combinedElement = new KerberosConfigElement();

        combinedElement.password = configElement.password == null ? this.password : configElement.password;
        combinedElement.realm = configElement.realm == null ? this.realm : configElement.realm;
        combinedElement.endpointSPN = configElement.endpointSPN == null ? this.endpointSPN : configElement.endpointSPN;
        combinedElement.loginEntryName = configElement.loginEntryName == null ? this.loginEntryName
                : configElement.loginEntryName;
        combinedElement.stripUserNameSuffix = configElement.stripUserNameSuffix;

        // return the combined element
        return combinedElement;
    }

    /**
     * Gets the password.
     * 
     * @return the password
     */
    public String getPassword()
    {
        return password;
    }

    /**
     * Gets the realm.
     * 
     * @return the realm
     */
    public String getRealm()
    {
        return realm;
    }

    /**
     * Gets the Service Principal Name to use on the endpoint. This must be like: HTTP/host.name@REALM.
     * 
     * @return the endpoint SPN
     */
    public String getEndpointSPN()
    {
        return endpointSPN;
    }

    /**
     * Gets the JAAS login configuration entry name.
     * 
     * @return the login entry name
     */
    public String getLoginEntryName()
    {
        return loginEntryName == null ? "ShareHTTP" : loginEntryName;
    }

    /**
     * Gets the stripUserNameSuffix boolean property.
     * 
     * @return the stripUserNameSuffix boolean
     */
    public boolean getStripUserNameSuffix()
    {
        return stripUserNameSuffix;
    }

    /**
     * Constructs a new instance from an XML Element.
     * 
     * @param elem
     *            the XML element
     * @return the Kerberos configuration element
     */
    protected static KerberosConfigElement newInstance(Element elem)
    {
        KerberosConfigElement configElement = new KerberosConfigElement();

        String password = elem.elementTextTrim("password");
        if (password != null && password.length() > 0)
        {
            configElement.password = password;
        }

        String realm = elem.elementTextTrim("realm");
        if (realm != null && realm.length() > 0)
        {
            configElement.realm = realm;
        }

        String endpointSPN = elem.elementTextTrim("endpoint-spn");
        if (endpointSPN != null && endpointSPN.length() > 0)
        {
            configElement.endpointSPN = endpointSPN;
        }

        String loginEntryName = elem.elementTextTrim("config-entry");
        if (loginEntryName != null && loginEntryName.length() > 0)
        {
            configElement.loginEntryName = loginEntryName;
        }

        String stripUserNameSuffix = elem.elementTextTrim("stripUserNameSuffix");
        if (stripUserNameSuffix != null && stripUserNameSuffix.length() > 0)
        {
            configElement.stripUserNameSuffix = Boolean.parseBoolean(stripUserNameSuffix);
        }

        return configElement;
    }
}

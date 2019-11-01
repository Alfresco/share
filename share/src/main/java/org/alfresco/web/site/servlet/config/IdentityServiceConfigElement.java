/*
 * #%L
 * Alfresco Share WAR
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
package org.alfresco.web.site.servlet.config;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

public class IdentityServiceConfigElement extends ConfigElementAdapter {

    private static final long serialVersionUID = 4278518406841891833L;

    private boolean enabled = false;

    public IdentityServiceConfigElement() { super("identity-service"); }


    @Override
    public ConfigElement combine(ConfigElement element) {
        IdentityServiceConfigElement configElement = (IdentityServiceConfigElement) element;

        // new combined element
        IdentityServiceConfigElement combinedElement = new IdentityServiceConfigElement();

        combinedElement.enabled = configElement.enabled;

        // return the combined element
        return combinedElement;
    }


    public boolean getEnabled() {
        return enabled;
    }


    protected static IdentityServiceConfigElement newInstance(Element elem)
    {
        IdentityServiceConfigElement configElement = new IdentityServiceConfigElement();

        String enabled = elem.elementTextTrim("enabled");
        if (enabled != null && enabled.length() > 0)
        {
            configElement.enabled = Boolean.parseBoolean(enabled);
        }

        return configElement;
    }
}

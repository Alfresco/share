/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2019 Alfresco Software Limited
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

import org.keycloak.representations.adapters.config.AdapterConfig;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Mapped properties for AIMS config { "realm" : "alfresco", "resource" :
 * "alfresco", "auth-server-url" : "", "ssl-required" : "none", "public-client"
 * : true, "autodetect-bearer-only": true, "always-refresh-token": true,
 * "principal-attribute": "email", "enable-basic-auth": true }
 */
public class AIMSConfigElement extends ConfigElementAdapter {
	private static final long serialVersionUID = 4278518406841891833L;
	public static final String AIMS_CONFIG_CONDITION = "AIMS";
	public static final String AIMS_CONFIG_ELEMENT = "aims";
	public AdapterConfig keycloakConfigElem = null;

	/** AIMS enable **/
	private boolean enabled = false;

	public AIMSConfigElement() {
		super(AIMS_CONFIG_ELEMENT);
	}

	@Override
	public ConfigElement combine(ConfigElement element) {
		AIMSConfigElement configElement = (AIMSConfigElement) element;
		// New combined element
		AIMSConfigElement combinedElement = new AIMSConfigElement();
		combinedElement.enabled = configElement.enabled;
		combinedElement.keycloakConfigElem = configElement.keycloakConfigElem;

		// Return the combined element
		return combinedElement;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public AdapterConfig getKeycloakConfigElem() {
		return keycloakConfigElem;
	}

	protected static AIMSConfigElement newInstance(ConfigElement elem) {
		AIMSConfigElement configElement = new AIMSConfigElement();

		String enabled = elem.getChildValue("enabled");
		if (enabled != null && enabled.length() > 0) {
			configElement.enabled = Boolean.parseBoolean(enabled);
		}

		// build keycloakConfig object
		AdapterConfig keycloakConfigElem = new AdapterConfig();

		String realm = elem.getChildValue("realm");
		if (realm != null && realm.length() > 0) {
			keycloakConfigElem.setRealm(realm);
		}

		String resource = elem.getChildValue("resource");
		if (resource != null && resource.length() > 0) {
			keycloakConfigElem.setResource(resource);
		}

		String authServerUrl = elem.getChildValue("authServerUrl");
		if (authServerUrl != null && authServerUrl.length() > 0) {
			keycloakConfigElem.setAuthServerUrl(authServerUrl);
		}

		String sslRequired = elem.getChildValue("sslRequired");
		if (sslRequired != null && sslRequired.length() > 0) {
			keycloakConfigElem.setSslRequired(sslRequired);
		}

		String publicClient = elem.getChildValue("publicClient");
		if (publicClient != null && publicClient.length() > 0) {
			keycloakConfigElem.setPublicClient(Boolean.parseBoolean(publicClient));
		}

		String autodetectBearerOnly = elem.getChildValue("autodetectBearerOnly");
		if (autodetectBearerOnly != null && autodetectBearerOnly.length() > 0) {
			keycloakConfigElem.setAutodetectBearerOnly(Boolean.parseBoolean(autodetectBearerOnly));
		}

		String alwaysRefreshToken = elem.getChildValue("alwaysRefreshToken");
		if (alwaysRefreshToken != null && alwaysRefreshToken.length() > 0) {
			keycloakConfigElem.setAlwaysRefreshToken(Boolean.parseBoolean(alwaysRefreshToken));
		}

		String principalAttribute = elem.getChildValue("principalAttribute");
		if (principalAttribute != null && principalAttribute.length() > 0) {
			keycloakConfigElem.setPrincipalAttribute(principalAttribute);
		}

		String enableBasicAuth = elem.getChildValue("enableBasicAuth");
		if (enableBasicAuth != null && enableBasicAuth.length() > 0) {
			keycloakConfigElem.setEnableBasicAuth(Boolean.parseBoolean(enableBasicAuth));
		}
		configElement.keycloakConfigElem = keycloakConfigElem;

		return configElement;
	}
}

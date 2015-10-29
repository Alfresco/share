/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.web.site.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.extensions.config.Config;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigService;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A filter adding HTTP response headers to incoming requests to improve security for the webapp.
 *
 * The logic is configurable making it possible to configure which headers that shall be added.
 *
 * @author Erik Winlof
 */
public class SecurityHeadersFilter implements Filter
{
    private static Log logger = LogFactory.getLog(SecurityHeadersFilter.class);

    private ServletContext servletContext = null;

    private Boolean enabled = true;
    private List<Header> headers = new LinkedList<Header>();

    /**
     * Parses the headers config.
     *
     * @param config The filter config
     * @throws javax.servlet.ServletException if the headers filter config is invalid
     */
    @Override
    public void init(FilterConfig config) throws ServletException
    {
        servletContext = config.getServletContext();

        ApplicationContext context = getApplicationContext();

        ConfigService configService = (ConfigService)context.getBean("web.config");

        // Retrieve the remote configuration
        Config securityHeadersConfig = (Config) configService.getConfig("SecurityHeadersPolicy");
        if (securityHeadersConfig == null)
        {
            enabled = false;
            if (logger.isDebugEnabled())
                logger.debug("There is no 'SecurityHeadersPolicy' config, no headers will be added.");
        }
        else
        {
            ConfigElement headersConfig = securityHeadersConfig.getConfigElement("headers");
            if (headersConfig == null)
            {
                enabled = false;
                if (logger.isDebugEnabled())
                    logger.debug("The 'SecurityHeadersPolicy' config had no headers, no headers will be added.");
            }
            else
            {
                List<ConfigElement> headersConfigList = headersConfig.getChildren("header");
                if (headersConfigList == null || headersConfigList.size() == 0)
                {
                    enabled = false;
                    if (logger.isDebugEnabled())
                        logger.debug("The 'SecurityHeadersPolicy' headers config was empty, no headers will be added.");
                }
                else
                {
                    // Get and merge all configs
                    Map<String, Header> allHeaders = new HashMap<String, Header>();
                    for (ConfigElement headerConfig : headersConfigList)
                    {
                        // Name
                        String name = headerConfig.getChildValue("name");
                        Header header;
                        if (allHeaders.containsKey(name))
                        {
                            header = allHeaders.get(name);
                        }
                        else
                        {
                            header = new Header();
                            header.setName(name);
                            allHeaders.put(name, header);
                        }

                        // Vaule
                        ConfigElement valueConfig = headerConfig.getChild("value");
                        if (valueConfig != null)
                        {
                            header.setValue(valueConfig.getValue());
                        }

                        // Enabled
                        ConfigElement enabledConfig = headerConfig.getChild("enabled");
                        if (enabledConfig != null)
                        {
                            String enabled = enabledConfig.getValue();
                            header.setEnabled(enabled == null || enabled.equalsIgnoreCase("true"));
                        }
                    }

                    // Filter out all enabled configs
                    for (Header header: allHeaders.values())
                    {
                        if (header.getEnabled())
                        {
                            headers.add(header);
                        }
                    }
                }
            }
        }
    }


    /**
     * Will add the configured response headers to the response.
     *
     * @param servletRequest The servlet request
     * @param servletResponse The servlet response
     * @param filterChain The filter chain
     * @throws java.io.IOException
     * @throws ServletException
     */
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException
    {
        if (enabled && servletRequest instanceof HttpServletRequest && servletResponse instanceof HttpServletResponse)
        {
            HttpServletResponse response = (HttpServletResponse) servletResponse;
            for (Header header : headers)
            {
                response.setHeader(header.getName(), header.getValue());
            }
        }

        // Proceed as usual
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy()
    {
    }

    /**
     * Retrieves the root application context
     *
     * @return application context
     */
    private ApplicationContext getApplicationContext()
    {
        return WebApplicationContextUtils.getRequiredWebApplicationContext(servletContext);
    }

    /**
     * Internal representation of a header.
     */
    private class Header
    {
        private String name;
        private String value;
        private Boolean enabled = true;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }
    }
}

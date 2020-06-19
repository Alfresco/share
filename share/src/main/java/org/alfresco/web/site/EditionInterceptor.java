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
package org.alfresco.web.site;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import javax.servlet.http.HttpSession;

import org.alfresco.web.scripts.ShareManifest;
import org.alfresco.web.site.servlet.MTAuthenticationFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.springframework.extensions.config.ConfigBootstrap;
import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.exception.WebFrameworkServiceException;
import org.springframework.extensions.surf.mvc.AbstractWebFrameworkInterceptor;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorContext;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.ui.ModelMap;
import org.springframework.web.context.request.WebRequest;

/**
 * Framework interceptor responsible for checking repository license edition
 * and applying appropriate config overrides.
 * 
 * @author Kevin Roast
 */
public class EditionInterceptor extends AbstractWebFrameworkInterceptor
{
    /** public name of the value in the RequestContext */
    public static final String EDITION_INFO = "editionInfo";
    public static final String KEY_DOCS_EDITION = "docsEdition";
    public static final String URL_UTIL = "urlUtil";
    
    public static final String ENTERPRISE_EDITION = EditionInfo.ENTERPRISE_EDITION;
    public static final String TEAM_EDITION = EditionInfo.TEAM_EDITION;
    public static final String UNKNOWN_EDITION = EditionInfo.UNKNOWN_EDITION;
    public static final String UNKNOWN_HOLDER = EditionInfo.UNKNOWN_HOLDER;
    
    private static Log logger = LogFactory.getLog(EditionInterceptor.class);

    /*
     * These are maintained as static variables so that they don't have to be recreated 
     * upon each thread's call to the preHandle method.
     */
    private static EditionInfo EDITIONINFO = null;
    private static DocsEdition docsEdition = null;
    private static UrlUtil urlUtil = new UrlUtil();
    
    private static volatile boolean outputInfo = false;
    private static volatile boolean outputEditionInfo = false;
    private static final ReadWriteLock editionLock = new ReentrantReadWriteLock();

    private ShareManifest shareManifest;
    
    public void setShareManifest(ShareManifest shareManifest)
    {
        this.shareManifest = shareManifest;
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#preHandle(org.springframework.web.context.request.WebRequest)
     */
    @Override
    public void preHandle(WebRequest request) throws Exception
    {
        editionLock.readLock().lock();
        try
        {
            if (EDITIONINFO == null)
            {
                editionLock.readLock().unlock();
                editionLock.writeLock().lock();
                try
                {
                    // check again, as more than one thread could have been waiting on the Write lock 
                    if (EDITIONINFO == null)
                    {
                        // initiate a call to retrieve the edition and restrictions from the repository
                        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
                        Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco");
                        ConnectorContext ctx = new ConnectorContext();
                        ctx.setExceptionOnError(false);
                        Response response = conn.call("/api/admin/restrictions?guest=true");
                        if (response.getStatus().getCode() == Status.STATUS_UNAUTHORIZED)
                        {
                            // if this occurs we may be running a multi-tenant repository or guest auth is disabled
                            if (MTAuthenticationFilter.getCurrentServletRequest() != null)
                            {
                                HttpSession session = MTAuthenticationFilter.getCurrentServletRequest().getSession(false);
                                if (session != null)
                                {
                                    // we try now that a Session is acquired and we have an authenticated user
                                    // this is the only time that we can successfully retrieve the license information
                                    // when the repo is in multi-tenant mode - as guest auth is not supported otherwise
                                    conn = rc.getServiceRegistry().getConnectorService().getConnector(
                                            "alfresco", (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID), session);
                                    response = conn.call("/api/admin/restrictions");
                                }
                                else
                                {
                                    // as a last resort try retrieving the server version so that
                                    // we can at least determine what the edition is
                                    response = conn.call("/api/server");
                                }
                            }
                        }
                        if (response.getStatus().getCode() == Status.STATUS_OK)
                        {
                            EditionInfo editionInfo = new EditionInfo(response.getResponse());
                            docsEdition = new DocsEdition(editionInfo.getEdition(), shareManifest.getSpecificationVersion(), false);
                            if (editionInfo.getValidResponse())
                            {
                               logger.info("Successfully retrieved license information from Alfresco.");
                               EDITIONINFO = editionInfo;
                            }
                            else
                            {
                                if (!outputEditionInfo)
                                {
                                    logger.info("Successfully retrieved edition information from Alfresco.");
                                    outputEditionInfo = true;
                                }
                                
                                // set edition info for current thread
                                ThreadLocalRequestContext.getRequestContext().setValue(EDITION_INFO, editionInfo);
                                ThreadLocalRequestContext.getRequestContext().setValue(KEY_DOCS_EDITION, docsEdition);
                                ThreadLocalRequestContext.getRequestContext().setValue(URL_UTIL, urlUtil);
                                
                                // NOTE: We do NOT assign to the EDITIONINFO so that we re-evaluate next time.
                            }
                            
                            // apply runtime config overrides based on the repository edition
                            String runtimeConfig = null;
                            if (TEAM_EDITION.equals(editionInfo.getEdition()))
                            {
                                runtimeConfig = "classpath:alfresco/team-config.xml";
                            }
                            else if (ENTERPRISE_EDITION.equals(editionInfo.getEdition()))
                            {
                                runtimeConfig = "classpath:alfresco/enterprise-config.xml";
                            }
                            if (runtimeConfig != null)
                            {
                                // manually instantiate a ConfigBootstrap object that will
                                // register our override config with the main config source
                                List<String> configs = new ArrayList<String>(1);
                                configs.add(runtimeConfig);
                                
                                ConfigService configservice = rc.getServiceRegistry().getConfigService();
                                ConfigBootstrap cb = new ConfigBootstrap();
                                cb.setBeanName("share-edition-config");
                                cb.setConfigService(configservice);
                                cb.setConfigs(configs);
                                cb.register();
                                configservice.reset();
                            }
                            if (logger.isDebugEnabled())
                                logger.debug("Current EditionInfo: " + editionInfo);
                        }
                        else
                        {
                            // only output the warning once
                            if (!outputInfo)
                            {
                                logger.info("Unable to retrieve License information from Alfresco: " + response.getStatus().getCode());
                                outputInfo = true;
                            }
                            // set a value so scripts have something to work with - the interceptor will retry later
                            EditionInfo info = new EditionInfo();
                            ThreadLocalRequestContext.getRequestContext().setValue(EDITION_INFO, info);
                            DocsEdition tempDocsEdition = new DocsEdition();
                            ThreadLocalRequestContext.getRequestContext().setValue(KEY_DOCS_EDITION, tempDocsEdition);
                            if (logger.isDebugEnabled())
                                logger.debug("Current EditionInfo: " + info);
                        }
                    }
                }
                catch (JSONException err)
                {
                    throw new WebFrameworkServiceException("Unable to process response: " + err.getMessage(), err);
                }
                finally
                {
                    editionLock.readLock().lock();
                    editionLock.writeLock().unlock();
                }
            }
            if (EDITIONINFO != null)
            {
                ThreadLocalRequestContext.getRequestContext().setValue(EDITION_INFO, EDITIONINFO);
                ThreadLocalRequestContext.getRequestContext().setValue(KEY_DOCS_EDITION, docsEdition);
                ThreadLocalRequestContext.getRequestContext().setValue(URL_UTIL, urlUtil);
            }
        }
        finally
        {
            editionLock.readLock().unlock();
        }
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#postHandle(org.springframework.web.context.request.WebRequest, org.springframework.ui.ModelMap)
     */
    @Override
    public void postHandle(WebRequest request, ModelMap model) throws Exception
    {
    }
    
    /* (non-Javadoc)
     * @see org.springframework.web.context.request.WebRequestInterceptor#afterCompletion(org.springframework.web.context.request.WebRequest, java.lang.Exception)
     */
    @Override
    public void afterCompletion(WebRequest request, Exception ex) throws Exception
    {
    }

}
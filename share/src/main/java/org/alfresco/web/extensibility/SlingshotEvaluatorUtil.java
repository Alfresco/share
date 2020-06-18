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
package org.alfresco.web.extensibility;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.alfresco.web.site.SlingshotUserFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.config.RemoteConfigElement;
import org.springframework.extensions.config.RemoteConfigElement.EndpointDescriptor;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.WebFrameworkServiceRegistry;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.support.AlfrescoUserFactory;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.ScriptRemote;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Utility class for evaluators to pick values from the request and get site information etc.
 *
 * @author ewinlof
 */
public class SlingshotEvaluatorUtil {

    private static Log logger = LogFactory.getLog(SlingshotEvaluatorUtil.class);

    public static final String SITE_PRESET_CACHE = SlingshotEvaluatorUtil.class.getName() + ".sitePresets";

    /* Context attributes and url parameters/path tokens */
    protected static final String PORTLET_HOST = "portletHost"; // Set by the ProxyPortlet
    protected static final String PORTLET_URL = "portletUrl"; // Set by the ProxyPortlet
    protected static final String SITE_PRESET = "sitePreset";
    protected static final String SITE = "site";
    protected static final String PAGE_CONTEXT = "pagecontext";

    protected WebFrameworkServiceRegistry serviceRegistry = null;

    public void setServiceRegistry(WebFrameworkServiceRegistry serviceRegistry)
    {
        this.serviceRegistry = serviceRegistry;
    }

    /**
     * Helper for getting an evaluator parameter trimmed OR defaultValue if no value has been provided.
     *
     * @param params Map<String, String>
     * @param name String
     * @param defaultValue String
     * @return A trimmed evaluator parameter OR defaultValue if no value has been provided.
     */
    public String getEvaluatorParam(Map<String, String> params, String name, String defaultValue)
    {
        String value = params.get(name);
        if (value != null && !value.trim().isEmpty())
        {
            return value.trim();
        }
        return defaultValue;
    }

    /**
     * Returns true if we are viewed from inside a portal.
     *
     * @param context RequestContext
     * @return true if we are viewed from inside a portal
     */
    public Boolean getPortletHost(RequestContext context)
    {
        Boolean portletHost = (Boolean) context.getAttribute(PORTLET_HOST);
        if (portletHost == null)
        {
            String portletHostParam = context.getParameter(PORTLET_HOST);
            portletHost = portletHostParam != null && portletHostParam.equalsIgnoreCase("true");
        }
        return portletHost;
    }

    /**
     * Returns the portal url if we are inside a portal, otherwise null.
     * @param context RequestContext
     * @return the portal url if we are inside a portal, otherwise null.
     */
    public String getPortletUrl(RequestContext context)
    {
        String portletUrl = (String) context.getAttribute(PORTLET_URL);
        if (portletUrl == null)
        {
            portletUrl = context.getParameter(PORTLET_URL);
        }
        return portletUrl;
    }

    /**
     * Returns the current site id OR null if we aren't in a site
     *
     * @param context RequestContext
     * @return The current page id OR null if it doesn't exist
     */
    public String getPageId(RequestContext context)
    {
        // Look for pageId
        return context.getPageId();
    }

    /**
     * Returns the current site id OR null if we aren't in a site
     *
     * @param context RequestContext
     * @return The current site id OR null if we aren't in a site
     */
    public String getSite(RequestContext context)
    {
        // Look for siteId in url path & parameters
        String site = context.getUriTokens().get(SITE);
        if (site == null)
        {
            site = context.getParameter(SITE);
        }
        if (site == null)
        {
            String[] pathNames = context.getUri().substring(context.getContextPath().length()).split("/");
            for (int i = 0; i < pathNames.length; i++) {
                if (pathNames[i].equals(SITE) && (i + 1 < pathNames.length))
                {
                    site = pathNames[i + 1];
                    break;
                }
            }
        }
        return site;
    }
    
    /**
     * Returns the current page context id OR null if one isn't supplied
     *
     * @param context RequestContext
     * @return The current page context id OR null if there is no page context
     */
    public String getPageContext(RequestContext context)
    {
        // Look for siteId in url path & parameters
        String pageContext = context.getUriTokens().get(PAGE_CONTEXT);
        if (pageContext == null)
        {
            pageContext = context.getParameter(PAGE_CONTEXT);
        }
        if (pageContext == null)
        {
            String[] pathNames = context.getUri().substring(context.getContextPath().length()).split("/");
            for (int i = 0; i < pathNames.length; i++) {
                if (pathNames[i].equals(PAGE_CONTEXT) && (i + 1 < pathNames.length))
                {
                    pageContext = pathNames[i + 1];
                    break;
                }
            }
        }
        return pageContext;
    }

    /**
     * The site's sitePreset OR null if something goes wrong.
     *
     * @param context RequestContext
     * @param siteId The id of the site to retrieve the sitePreset for.
     * @return The site's sitePreset OR null if something goes wrong.
     */
    @SuppressWarnings({ "unchecked", "rawtypes" })
    public String getSitePreset(RequestContext context, String siteId)
    {
        // Get the preset request cache
        HashMap sitePresetCache = (HashMap) context.getAttributes().get(SITE_PRESET_CACHE);
        if (sitePresetCache == null)
        {
            sitePresetCache = new HashMap();
            context.getAttributes().put(SITE_PRESET_CACHE, sitePresetCache);
        }

        // Check if site's preset already has been asked for during this request
        String sitePresetId = (String) sitePresetCache.get(siteId);
        if (sitePresetId == null)
        {
            try
            {
                JSONObject site = jsonGet("/api/sites/" + URLEncoder.encode(siteId));
                if (site != null)
                {
                    sitePresetId = site.getString(SITE_PRESET);
                    sitePresetCache.put(siteId, sitePresetId);
                }
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("Could not get a sitePreset from site json.");
                }
            }
        }

        // Return sites preset
        return sitePresetId;
    }

    /**
     * Helper method for making a json get remote call to the default repository.
     *
     * @param uri The uri to get the content for (MUST contain a json response)
     * @return The content of the uri resource parsed into a json object.
     */
    public JSONObject jsonGet(String uri)
    {
        ScriptRemote scriptRemote = serviceRegistry.getScriptRemote();
        Response response = scriptRemote.connect().get(uri);
        if (response.getStatus().getCode() == 200)
        {
            try
            {
                return new JSONObject(response.getResponse());
            }
            catch (JSONException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("An error occurred when parsing response to json from the uri '" + uri + "': " + e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * Determines whether or not the current user is a member of the supplied group.
     *
     * @param context RequestContext
     * @param groups List<String>
     * @param memberOfAllGroups boolean
     * @return boolean
     */
    @SuppressWarnings({ "rawtypes" })
    public boolean isMemberOfGroups(RequestContext context, List<String> groups, boolean memberOfAllGroups)
    {
        // Initialise the default result to be null... we're intentionally using a Boolean object over boolean
        // primitive to give us access to the third value of null. This allows us to determine whether or not
        // any membership information has actually been processed (e.g. when NO groups have been specified).
        Boolean isMember = null;

        // We're going to store GROUP membership in the HttpSession as this changes infrequently but will be
        // accessing SITE membership for every request. Surf will ensure that requests are cached for each
        // page so we are not making the same request more than once per page. Site membership can change more
        // frequently so we need to be sure that the information we have is up-to-date.
        HttpSession session = ServletUtil.getSession();
        org.json.simple.JSONArray groupsList = null;
        String GROUP_MEMBERSHIPS = "AlfGroupMembershipsKey";

        // Get the current site
        String currentSite = getSite(context);

        boolean externalAuth = false;
        RemoteConfigElement config = (RemoteConfigElement) context.getServiceRegistry().getConfigService().getConfig("Remote").getConfigElement("remote");
        if (config != null)
        {
            EndpointDescriptor descriptor = config.getEndpointDescriptor(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
            if (descriptor != null)
            {
                externalAuth = descriptor.getExternalAuth();
            }
        }

        // Get all the group membership first so that we don't perform this operation multiple times... check
        // the HttpSession and if it's not already available then make a request for it and cache it for future
        // reference. Note that we're ONLY caching the current users membership information.
        Object _cachedGroupMemberships = session.getAttribute(GROUP_MEMBERSHIPS);
        if (_cachedGroupMemberships instanceof org.json.simple.JSONArray)
        {
            groupsList = (org.json.simple.JSONArray) _cachedGroupMemberships;
        }
        else
        {
            try
            {
                // Get the Site membership information...
                CredentialVault cv = context.getCredentialVault();
                if (cv != null)
                {
                    Credentials creds = cv.retrieve(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
                    // Check for external authentication
                    // MNT-11857
                    if (creds == null && !externalAuth)
                    {
                        // User is not logged in anymore
                        return false;
                    }
                    String userName = (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
                    Connector connector = context.getServiceRegistry().getConnectorService().getConnector(SlingshotUserFactory.ALFRESCO_ENDPOINT_ID, userName, ServletUtil.getSession());
                    Response res = connector.call("/api/people/" + URLEncoder.encode(context.getUserId()) + "?groups=true");
                    if (res.getStatus().getCode() == Status.STATUS_OK)
                    {
                        String response = res.getResponse();
                        org.json.simple.parser.JSONParser p = new org.json.simple.parser.JSONParser();
                        Object o2 = p.parse(response);
                        if (o2 instanceof org.json.simple.JSONObject)
                        {
                            org.json.simple.JSONObject jsonRes = (org.json.simple.JSONObject) o2;
                            groupsList = (org.json.simple.JSONArray) jsonRes.get("groups");
                            session.setAttribute(GROUP_MEMBERSHIPS, groupsList);
                        }
                    }
                }
            }
            catch (ConnectorServiceException e)
            {
                e.printStackTrace();
            }
            catch (ParseException e)
            {
                e.printStackTrace();
            }
        }

        // Work through the supplied list of groups to determine whether or not the current user is a member of them...
        for (String groupName: groups)
        {
            boolean isMemberOfCurrentGroup = false;
            if (groupName != null)
            {
                // If the requested groupName begins with "Site" then this indicates that we are looking
                // for a site specific group such as "SiteConsumer" and we therefore need to modify the
                // group name to reflect the current site. If we are not currently viewing a site then
                // we will automatically indicate that the user is not a member (how can they be a member
                // of the current site if they're not in a site?) and move onto the next group...
                if (groupName.startsWith("Site"))
                {
                    if (currentSite == null)
                    {
                        isMember = false;
                    }
                    else
                    {
                        // We're going to rely on URI tokens to determine if we're viewing a site - it's the
                        // best data available from the RequestContext.
                        try
                        {
                            CredentialVault cv = context.getCredentialVault();
                            if (cv != null)
                            {
                                Credentials creds = cv.retrieve(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID);
                                if (creds == null && !externalAuth)
                                {
                                    // User is not logged in anymore
                                    return false;
                                }
                                String userName = (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
                                Connector connector = context.getServiceRegistry().getConnectorService().getConnector(AlfrescoUserFactory.ALFRESCO_ENDPOINT_ID, userName, ServletUtil.getSession());
                                Response res = connector.call("/api/sites/" + currentSite + "/memberships/" + URLEncoder.encode(context.getUserId()));
                                if (res.getStatus().getCode() == Status.STATUS_OK)
                                {
                                    String response = res.getResponse();
                                    org.json.simple.parser.JSONParser p = new org.json.simple.parser.JSONParser();
                                    Object o2 = p.parse(response);
                                    if (o2 instanceof org.json.simple.JSONObject)
                                    {
                                        org.json.simple.JSONObject jsonRes = (org.json.simple.JSONObject) o2;
                                        String siteMembership = (String) jsonRes.get("role");
                                        isMemberOfCurrentGroup = siteMembership.equals(groupName);
                                    }
                                }
                                else
                                {
                                    // When the user is NOT a member of the site the request will actually return a 404 (rather than a 200)
                                    // so on any request that fails we will assume they are not a member of the site.
                                    isMemberOfCurrentGroup = false;
                                }
                            }
                        }
                        catch (ConnectorServiceException e)
                        {
                            e.printStackTrace();
                        }
                        catch (ParseException e)
                        {
                            e.printStackTrace();
                        }
                    }
                }
                else if (groupsList != null)
                {
                    // Check for regular GROUP membership... all non-site groups MUST begin "GROUP"...
                    Iterator i = groupsList.iterator();
                    while (i.hasNext())
                    {
                        org.json.simple.JSONObject group = (org.json.simple.JSONObject) i.next();
                        String currGroupName = group.get("itemName").toString();
                        if (currGroupName.equals(groupName))
                        {
                            isMemberOfCurrentGroup = true;
                            break;
                        }
                    }
                }
            }

            // Handle the requested membership logic and make a quick exit if possible...
            if (memberOfAllGroups)
            {
                isMember = (isMember == null) ? isMemberOfCurrentGroup : isMember && isMemberOfCurrentGroup;
                if (!isMember)
                {
                    // Break out of the main loop if the user must be a member of all groups and is not
                    // a member of at least one of them. There is no point in checking the remaining groups
                    break;
                }
            }
            else
            {
                isMember = (isMember == null) ? isMemberOfCurrentGroup :  isMember || isMemberOfCurrentGroup;
                if (isMember)
                {
                    // Break out of the main loop if the user is a member of at least one group as that
                    // is all that is required.
                    break;
                }
            }
        }
        return isMember;
    }

    /**
     * Gets the list of groups to check for membership of. This assumes that the groups have been
     * provided as a comma delimited string and will convert that string into a List removing trailing
     * whitespace along the way.
     *
     * @param groupsParm String
     * @return List<String>
     */
    public List<String> getGroups(String groupsParm)
    {
        List<String> groups = new ArrayList<String>();
        if (groupsParm != null)
        {
            String[] groupsArr = groupsParm.split(",");
            for (String group: groupsArr)
            {
                groups.add(group.trim());
            }
        }
        return groups;
    }

    /**
     * Helper method to get a request header value from the current request context
     *
     * @param name Header name to retrieve
     * @return string value or null
     */
    protected String getHeader(String name)
    {
        String header = null;
        if (name != null)
        {
            final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            header = rc.getHeader(name);
        }
        return header;
    }
}

/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.extensions.surf.support;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;
import org.springframework.extensions.surf.util.URLEncoder;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.AuthenticatingConnector;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.ConnectorService;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.Credentials;
import org.springframework.extensions.webscripts.connector.Response;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>
 * This factory loads users from Alfresco, fetching their properties
 * and so forth.  The data source is assumed to be a JSON provider.
 * </p><p>
 * By implementing this class, User derived objects are available to
 * all downstream components and templates.  These components and
 * templates can then consult the user profile as they execute.
 * </p><p>
 * The user is stored on the request context and can be fetched
 * using context.getUser(). The user is also available in the root
 * of the a script component context as 'user'. 
 * </p>
 * @author muzquiano
 * @author kevinr
 */
public class AlfrescoUserFactory extends AbstractUserFactory
{
    public static final String DEFAULT_USER_URL_PREFIX = "/webframework/content/metadata?user=";
    
    private static final String JSON_RESPONSE_CODE_VALUE_OK = "OK";
    private static final String JSON_RESPONSE_CODE = "code";

    private static Log logger = LogFactory.getLog(AlfrescoUserFactory.class);
    
    public static final String CM_AVATAR = "{http://www.alfresco.org/model/content/1.0}avatar";
    public static final String CM_COMPANYEMAIL = "{http://www.alfresco.org/model/content/1.0}companyemail";
    public static final String CM_COMPANYFAX = "{http://www.alfresco.org/model/content/1.0}companyfax";
    public static final String CM_COMPANYTELEPHONE = "{http://www.alfresco.org/model/content/1.0}companytelephone";
    public static final String CM_COMPANYPOSTCODE = "{http://www.alfresco.org/model/content/1.0}companypostcode";
    public static final String CM_COMPANYADDRESS3 = "{http://www.alfresco.org/model/content/1.0}companyaddress3";
    public static final String CM_COMPANYADDRESS2 = "{http://www.alfresco.org/model/content/1.0}companyaddress2";
    public static final String CM_COMPANYADDRESS1 = "{http://www.alfresco.org/model/content/1.0}companyaddress1";
    public static final String CM_INSTANTMSG = "{http://www.alfresco.org/model/content/1.0}instantmsg";
    public static final String CM_GOOGLEUSERNAME = "{http://www.alfresco.org/model/content/1.0}googleusername";
    public static final String CM_SKYPE = "{http://www.alfresco.org/model/content/1.0}skype";
    public static final String CM_MOBILE = "{http://www.alfresco.org/model/content/1.0}mobile";
    public static final String CM_TELEPHONE = "{http://www.alfresco.org/model/content/1.0}telephone";
    public static final String CM_PERSONDESCRIPTION = "{http://www.alfresco.org/model/content/1.0}persondescription";
    public static final String CM_EMAIL = "{http://www.alfresco.org/model/content/1.0}email";
    public static final String CM_LOCATION = "{http://www.alfresco.org/model/content/1.0}location";
    public static final String CM_ORGANIZATION = "{http://www.alfresco.org/model/content/1.0}organization";
    public static final String CM_JOBTITLE = "{http://www.alfresco.org/model/content/1.0}jobtitle";
    public static final String CM_LASTNAME = "{http://www.alfresco.org/model/content/1.0}lastName";
    public static final String CM_FIRSTNAME = "{http://www.alfresco.org/model/content/1.0}firstName";
    public static final String CM_USERNAME = "{http://www.alfresco.org/model/content/1.0}userName";
    
    public static final String ALFRESCO_ENDPOINT_ID = "alfresco";


    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#authenticate(org.alfresco.web.site.RequestContext, javax.servlet.http.HttpServletRequest, java.lang.String, java.lang.String)
     */
    public boolean authenticate(HttpServletRequest request, String username, String password)
    {
        boolean authenticated = false;
        try
        {
            // make sure our credentials are in the vault
            CredentialVault vault = frameworkUtils.getCredentialVault(request.getSession(), username);
            Credentials credentials = vault.newCredentials(ALFRESCO_ENDPOINT_ID);
            credentials.setProperty(Credentials.CREDENTIAL_USERNAME, username);
            credentials.setProperty(Credentials.CREDENTIAL_PASSWORD, password);
            
            // build a connector whose connector session is bound to the current session
            Connector connector = frameworkUtils.getConnector(request.getSession(), username, ALFRESCO_ENDPOINT_ID);
            AuthenticatingConnector authenticatingConnector;
            if (connector instanceof AuthenticatingConnector)
            {
                authenticatingConnector = (AuthenticatingConnector)connector;
            }
            else
            {
                // Manual connector retrieval and authenticator creation required.
                // This code path is followed if an SSO attempt has failed and the
                // login form is shown as a failover once all SSO attempts expire.
                ConnectorService cs = (ConnectorService)getApplicationContext().getBean("connector.service");
                authenticatingConnector = new AuthenticatingConnector(connector, cs.getAuthenticator("alfresco-ticket"));
            }
            authenticated = authenticatingConnector.handshake();
        }
        catch (Throwable ex)
        {
            // many things might have happened
            // an invalid ticket or perhaps a connectivity issue
            // at any rate, we cannot authenticate
            if (logger.isInfoEnabled())
                logger.info("Exception in AlfrescoUserFactory.authenticate()", ex);
        }
        
        return authenticated;
    }

    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String)
     */
    public User loadUser(RequestContext context, String userId)
        throws UserFactoryException
    {
        return loadUser(context, userId, null);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.web.site.UserFactory#loadUser(org.alfresco.web.site.RequestContext, java.lang.String, java.lang.String)
     */
    @SuppressWarnings("unchecked")
    public User loadUser(RequestContext context, String requestedUserId, String endpointId)
        throws UserFactoryException
    {
        if (endpointId == null)
        {
            endpointId = ALFRESCO_ENDPOINT_ID;
        }
        
        AlfrescoUser user = null;
        try
        {
            // ensure we bind the connector to the current user name - if this is the first load
            // of a user we will use the userId as passed into the method 
            String currentUserId = context.getUserId();
            if (currentUserId == null)
            {
                currentUserId = requestedUserId;
            }
            
            // get a connector whose connector session is bound to the current session
            HttpSession session = ServletUtil.getSession();
            Connector connector = frameworkUtils.getConnector(session, currentUserId, endpointId);
            
            // build the REST URL to retrieve requested user details
            String uri = buildUserMetadataRestUrl(context, requestedUserId, endpointId);
            
            // invoke and check for OK response
            Response response = connector.call(uri);
            if (Status.STATUS_OK != response.getStatus().getCode())
            {
                throw new UserFactoryException("Unable to create user - failed to retrieve user metadata: " + 
                        response.getStatus().getMessage(), (Exception)response.getStatus().getException());
            }
            
            // Load the user properties via the JSON parser
            JSONObject json = new JSONObject(response.getResponse());
            user = buildAlfrescoUser(json);
        }
        catch (Exception ex)
        {
            // unable to read back the user json object
            throw new UserFactoryException("Unable to retrieve user from repository", ex);
        }

        return user;
    }
    
    /**
     * <p>Build the REST URl to use to retrieve the metadata for the supplied user</p>
     * 
     * @param context
     * @param userId
     * @param endpointId
     * @return
     */
    protected String buildUserMetadataRestUrl(RequestContext context, String userId, String endpointId)
    {
        return DEFAULT_USER_URL_PREFIX + URLEncoder.encode(userId);
    }
    
    /**
     * Build the Alfresco User from the supplied JSON data
     *  
     * @param json  JSONObject
     * 
     * @return AlfrescoUser
     */
    protected AlfrescoUser buildAlfrescoUser(JSONObject json)
        throws JSONException, UserFactoryException
    {
        AlfrescoUser user = null;
        
        String code = json.getString(JSON_RESPONSE_CODE);
        
        if (JSON_RESPONSE_CODE_VALUE_OK.equals(code))
        {
            JSONObject jsonData = json.getJSONObject("data");
            JSONObject properties = jsonData.getJSONObject("properties");
            JSONObject capabilityJson = jsonData.getJSONObject("capabilities");
            
            Map<String, Boolean> capabilities = new HashMap<String, Boolean>(capabilityJson.length());
            Iterator<String> i = capabilityJson.keys();
            while (i.hasNext())
            {
                String capability = i.next();
                capabilities.put(capability, capabilityJson.getBoolean(capability));
            }
            
            // Alfresco 3.3.1 supports individual config for mutability of user properties
            Map<String, Boolean> immutability = null;
            if (jsonData.has("immutableProperties"))
            {
                JSONObject immutabilityJson = jsonData.getJSONObject("immutableProperties");
                immutability = new HashMap<String, Boolean>(immutabilityJson.length());
                i = immutabilityJson.keys();
                while (i.hasNext())
                {
                    String readonly = i.next();
                    immutability.put(readonly, immutabilityJson.getBoolean(readonly));
                }
            }
            
            user = constructAlfrescoUser(jsonData, properties, capabilities, immutability);
        }
        else
        {
            String message = "none";
            if (json.has("message"))
            {
                message = json.getString("message");                    
            }
            
            throw new UserFactoryException("Code '" + code + "' received while loading user object.  Message: " + message);
        }
        return user;
    }
    
    /**
     * Construct the Alfresco User from the supplied JSON data, properties and capabilities
     * 
     * @param jsonData      JSONObject
     * @param properties    Properties describing the user
     * @param capabilities  Map of user capability flags
     * @param immutability  Optional map of property qnames to immutability
     * 
     * @return AlfrescoUser
     */
    protected AlfrescoUser constructAlfrescoUser(
            JSONObject jsonData, JSONObject properties,
            Map<String, Boolean> capabilities, Map<String, Boolean> immutability)
        throws JSONException
    {
        // Construct the Alfresco User object based on the cm:person properties
        // ensure we have the correct username case
        AlfrescoUser user = constructUser(properties, capabilities, immutability);
        user.setFirstName(properties.has(CM_FIRSTNAME) ? properties.getString(CM_FIRSTNAME) : "");
        user.setLastName(properties.has(CM_LASTNAME) ? properties.getString(CM_LASTNAME) : "");
        if (properties.has(CM_JOBTITLE))
        {
            user.setJobTitle(properties.getString(CM_JOBTITLE));
        }
        if (properties.has(CM_ORGANIZATION))
        {
            user.setOrganization(properties.getString(CM_ORGANIZATION));
        }
        if (properties.has(CM_LOCATION))
        {
            user.setLocation(properties.getString(CM_LOCATION));
        }
        if (properties.has(CM_EMAIL))
        {
            user.setEmail(properties.getString(CM_EMAIL));
        }
        if (properties.has(CM_PERSONDESCRIPTION))
        {
            user.setBiography(properties.getString(CM_PERSONDESCRIPTION));
        }
        if (properties.has(CM_TELEPHONE))
        {
            user.setTelephone(properties.getString(CM_TELEPHONE));
        }
        if (properties.has(CM_MOBILE))
        {
            user.setMobilePhone(properties.getString(CM_MOBILE));
        }
        if (properties.has(CM_SKYPE))
        {
            user.setSkype(properties.getString(CM_SKYPE));
        }
        if (properties.has(CM_INSTANTMSG))
        {
            user.setInstantMsg(properties.getString(CM_INSTANTMSG));
        }
        if (properties.has(CM_GOOGLEUSERNAME))
        {
            user.setGoogleUsername(properties.getString(CM_GOOGLEUSERNAME));
        }
        if (properties.has(CM_COMPANYADDRESS1))
        {
            user.setCompanyAddress1(properties.getString(CM_COMPANYADDRESS1));
        }
        if (properties.has(CM_COMPANYADDRESS2))
        {
            user.setCompanyAddress2(properties.getString(CM_COMPANYADDRESS2));
        }
        if (properties.has(CM_COMPANYADDRESS3))
        {
            user.setCompanyAddress3(properties.getString(CM_COMPANYADDRESS3));
        }
        if (properties.has(CM_COMPANYPOSTCODE))
        {
            user.setCompanyPostcode(properties.getString(CM_COMPANYPOSTCODE));
        }
        if (properties.has(CM_COMPANYTELEPHONE))
        {
            user.setCompanyTelephone(properties.getString(CM_COMPANYTELEPHONE));
        }
        if (properties.has(CM_COMPANYFAX))
        {
            user.setCompanyFax(properties.getString(CM_COMPANYFAX));
        }
        if (properties.has(CM_COMPANYEMAIL))
        {
            user.setCompanyEmail(properties.getString(CM_COMPANYEMAIL));
        }
        
        if (jsonData.has("associations"))
        {
            JSONObject assocs = jsonData.getJSONObject("associations");
            JSONArray array = assocs.getJSONArray(CM_AVATAR);
            if (array.length() != 0)
            {
                user.setAvatarRef(array.getString(0));
            }
        }
        return user;
    }

    /**
     * Return the AlfrescoUser object
     * 
     * @return AlfrescoUser
     * 
     * @throws JSONException
     */
    protected AlfrescoUser constructUser(
            JSONObject properties, Map<String, Boolean> capabilities, Map<String, Boolean> immutability)
        throws JSONException
    {
        return new AlfrescoUser(properties.getString(CM_USERNAME), capabilities, immutability);
    }
}

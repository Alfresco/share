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

package org.springframework.extensions.surf.site;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.surf.support.AbstractUserFactory;
import org.springframework.extensions.surf.util.URLEncoder;

/**
 * @author muzquiano
 * @author kevinr
 */
public class AuthenticationUtil
{
    /** cookie names */
    private static final String COOKIE_ALFLOGIN = "alfLogin";
    private static final String COOKIE_ALFUSER = "alfUsername3";
    private static final int TIMEOUT = 60*60*24*7;
    
    private static final String MT_GUEST_PREFIX = AbstractUserFactory.USER_GUEST + "@"; // eg. for MT Share
    
    
    public static void logout(HttpServletRequest request, HttpServletResponse response)
    {
        // invalidate the web session - will remove all session bound objects
        // such as connector sessions, theme settings etc.
        request.getSession().invalidate();
        
        // remove cookie
        if (response != null)
        {
            Cookie userCookie = new Cookie(COOKIE_ALFUSER, "");
            userCookie.setPath(request.getContextPath());
            userCookie.setMaxAge(0);
            response.addCookie(userCookie);
        }
    }
    
    public static void login(HttpServletRequest request, String userId)
    {
        login(request, null, userId, true);
    }
    
    public static void login(HttpServletRequest request, HttpServletResponse response, String userId)
    {
        login(request, response, userId, true);
    }
    
    public static void login(HttpServletRequest request, HttpServletResponse response, String userId, boolean logout)
    {
        if (logout)
        {
            // check whether there is already a user logged in
            String currentUserId = (String) request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
            if (currentUserId != null)
            {
                // log out the current user
                logout(request, response);
            }
        }
        
        // place user id onto the session
        request.getSession().setAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID, userId);
        
        // set login and last username cookies
        if (response != null)
        {
            long timeInSeconds = System.currentTimeMillis() / 1000L;
            Cookie loginCookie = new Cookie(COOKIE_ALFLOGIN, Long.toString(timeInSeconds));
            loginCookie.setPath(request.getContextPath());
            loginCookie.setMaxAge(TIMEOUT);
            response.addCookie(loginCookie);
            
            if (isGuest(userId) == false)
            {
                Cookie userCookie;
                userCookie = new Cookie(COOKIE_ALFUSER, URLEncoder.encode(userId));
                userCookie.setPath(request.getContextPath());
                userCookie.setMaxAge(TIMEOUT);
                response.addCookie(userCookie);
            }
        }
    }
    
    public static void clearUserContext(HttpServletRequest request)
    {
        request.getSession().removeAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        request.getSession().removeAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
    }
    
    public static boolean isAuthenticated(HttpServletRequest request)
    {
        // get user id from the session
        String userId = (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
        
        // return whether is non-null and not 'guest'
        return (userId != null && !isGuest(userId));
    }
    
    public static boolean isGuest(String userId)
    {
        // return whether 'guest' (or 'guest@tenant')
        return (userId != null && (UserFactory.USER_GUEST.equals(userId) || userId.startsWith(MT_GUEST_PREFIX)));
    }
    
    public static boolean isExternalAuthentication(HttpServletRequest request)
    {
        return (request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_EXTERNAL_AUTH) != null);
    }
    
    public static String getUserId(HttpServletRequest request)
    {
        return (String)request.getSession().getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
    }
    
    /**
     * Helper to return cookie that saves the last login time for the current user.
     * 
     * @param httpRequest
     * 
     * @return Cookie if found or null if not present
     */
    public static Cookie getLastLoginCookie(HttpServletRequest request)
    {
        return getCookie(request, COOKIE_ALFLOGIN);
    }

    /**
     * Helper to return cookie that saves the last login time for the current user.
     * 
     * @param httpRequest
     * 
     * @return Cookie if found or null if not present
     */
    public static Cookie getUsernameCookie(HttpServletRequest request)
    {
        return getCookie(request, COOKIE_ALFUSER);
    }
    
    private static Cookie getCookie(HttpServletRequest request, String name)
    {
        Cookie cookie = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null)
        {
            for (int i=0; i<cookies.length; i++)
            {
                if (name.equals(cookies[i].getName()))
                {
                    // found cookie
                    cookie = cookies[i];
                    break;
                }
            }
        }
        return cookie;
    }
}
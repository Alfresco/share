package org.alfresco.web.scripts.bean;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.extensions.surf.UserFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.connector.User;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * Test if the current user Session contains an authenticated userid.
 * 
 * @author Kevin Roast
 */
public class Authenticated extends DeclarativeWebScript
{
   /* (non-Javadoc)
     * @see org.alfresco.web.scripts.DeclarativeWebScript#executeImpl(org.alfresco.web.scripts.WebScriptRequest, org.alfresco.web.scripts.WebScriptResponse)
     */
    @Override
    protected Map<String, Object> executeImpl(final WebScriptRequest req, final Status status)
    {
        if (req instanceof WebScriptServletRequest)
        {
            final WebScriptServletRequest webScriptServletRequest = (WebScriptServletRequest)req;
            final HttpSession session = webScriptServletRequest.getHttpServletRequest().getSession(false);
            
            boolean isAllowedToViewPage = false;
            if (session != null)
            {
                final String userID = (String)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_ID);
                if (userID != null && !UserFactory.USER_GUEST.equals(userID))
                {
                    final User user = (User)session.getAttribute(UserFactory.SESSION_ATTRIBUTE_KEY_USER_OBJECT);
                    final String auth = webScriptServletRequest.getHttpServletRequest().getParameter("a");
                    if (user != null)
                    {
                        isAllowedToViewPage = auth != null && auth.equals("admin") ? user.isAdmin() : true;
                    }
                }
            }
            if (!isAllowedToViewPage)
            {
                status.setCode(401);
                status.setMessage("There is no user ID in session or user is not permitted to view the page");
                status.setRedirect(true);
            }
        }
        return null;
    }
}

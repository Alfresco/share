package org.alfresco.web.site;

import java.util.Map;

import org.springframework.extensions.surf.FrameworkUtil;
import org.springframework.extensions.surf.exception.PlatformRuntimeException;
import org.springframework.extensions.surf.exception.UserFactoryException;
import org.springframework.extensions.surf.site.AlfrescoUser;

/**
 * User object extended to provide persistence back to an Alfresco repo.
 * 
 * @author Kevin Roast
 */
public class SlingshotUser extends AlfrescoUser
{
    /**
     * Instantiates a new user.
     * 
     * @param id            The user id
     * @param capabilities  Map of string keyed capabilities given to the user
     * @param immutability  Optional map of property qnames to immutability
     */
    public SlingshotUser(String id, Map<String, Boolean> capabilities, Map<String, Boolean> immutability)
    {
        super(id, capabilities, immutability);
    }
    
    /**
     * @see org.springframework.extensions.webscripts.connector.User#save()
     */
    @Override
    public void save()
    {
        try
        {
            ((SlingshotUserFactory)FrameworkUtil.getServiceRegistry().getUserFactory()).saveUser(this);
        }
        catch (UserFactoryException err)
        {
            throw new PlatformRuntimeException("Unable to save user details: " + err.getMessage(), err);
        }
    }
}

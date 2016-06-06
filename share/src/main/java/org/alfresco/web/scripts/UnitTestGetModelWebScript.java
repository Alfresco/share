
package org.alfresco.web.scripts;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpSession;

import org.springframework.extensions.directives.ProcessJsonModelDirective;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This WebScript controller attempts to retrieve a previously saved unit test page model
 * from the {@link HttpSession} which is then stored in the "jsonModel" attribute of the 
 * overall model. The associated FreeMarker template will call the {@link ProcessJsonModelDirective}
 * to convert the JSON string into a page model.
 * 
 * @author Dave Draper
 */
public class UnitTestGetModelWebScript extends DeclarativeWebScript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        HttpSession httpSession = ServletUtil.getSession(false);
        if (httpSession != null)
        {
            String jsonModel = (String) httpSession.getAttribute("unitTestModel");
            model.put("jsonModel", jsonModel);
        }
        return model;
    }
}
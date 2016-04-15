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
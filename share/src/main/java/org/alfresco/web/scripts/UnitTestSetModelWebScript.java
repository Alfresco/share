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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.WebScriptServletRequest;

/**
 * This WebScript controller accepts a posted JSON string that defines a page model to use
 * for a unit test and saves it to the {@link HttpSession}. The WebScript backed by the
 * {@link UnitTestGetModelWebScript} controller should then be immediately run (in the context
 * of a unit test page) to retrieve the JSON model and render it as a page. 
 * 
 * @author Dave Draper
 */
public class UnitTestSetModelWebScript extends DeclarativeWebScript
{
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>(7, 1.0f);
        
        try
        {
            String content = req.getContent().getContent();
            JSONParser jp = new JSONParser();
            Object o = jp.parse(content);
            if (o instanceof JSONObject)
            {
                JSONObject jsonData = (JSONObject) o;
                String jsonModel = (String)jsonData.get("unitTestModel");
                if (jsonModel != null)
                {
                    HttpServletRequest httpRequest = ((WebScriptServletRequest)req).getHttpServletRequest();
                    HttpSession httpSession = httpRequest.getSession();
                    httpSession.setAttribute("unitTestModel", jsonModel);
                    model.put("result", "SUCCESS");
                }
                else
                {
                    model.put("result", "MISSING MODEL");
                }
            }
        }
        catch (IOException e)
        {
            model.put("result", "IO Exception: " + e.getLocalizedMessage());
        }
        catch (ParseException e)
        {
            model.put("result", "ParseException: " + e.getLocalizedMessage());
        }
        finally
        {
            
        }
        return model;
    }
}
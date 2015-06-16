/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.json.simple.JSONObject;
import org.springframework.core.io.Resource;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * WebScript that provides a JSON view of a manifest file. The file is read once upon
 * initialisation and the resulting JSON is stored for the life of the web app
 * (the assumption is that this file will not change).
 * 
 * @author Matt Ward
 */
public class ShareManifest extends AbstractWebScript
{
    private final Resource resource; 
    private Manifest manifest;
    private String json;
    
    public ShareManifest(Resource resource)
    {
        if (resource == null)
        {
            throw new IllegalArgumentException("Manifest 'resource' parameter must not be null.");
        }
        this.resource = resource;
    }
    
    @Override
    public void init(Container container, Description description)
    {
        initWebScript(container, description);
        readManifest();
        manifestToJSON();
    }

    /**
     * This method exists simply to call the super {@link #init(Container, Description)} method
     * and serves to separate webscript-specific initialisation from the rest.
     *  
     * @param container
     * @param description
     */
    protected void initWebScript(Container container, Description description)
    {
        super.init(container, description);
    }

    /**
     * Read the manifest file that was specified in the constructor.
     */
    private void readManifest()
    {
        try (InputStream is = resource.getInputStream())
        {
            manifest = new Manifest(is);
        }
        catch (IOException e)
        {
            throw new RuntimeException("Error reading manifest.", e);
        }
    }

    /**
     * Convert the manifest to a JSON String.
     */
    private void manifestToJSON()
    {
        if (manifest == null)
        {
            throw new IllegalStateException("Manifest is null and must not be.");
        }
        Attributes attrs = manifest.getMainAttributes();
        JSONObject jsonObject = new JSONObject(attrs);
        json = jsonObject.toJSONString();
    }
    
    @Override
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        if (json == null)
        {
            throw new IllegalStateException("JSON respresentation of manifest is null.");
        }
        Writer w = null;
        PrintWriter pw = null;
        try
        {
            w = res.getWriter();
            pw = new PrintWriter(w);
            pw.print(json);
        }
        finally
        {
            if (pw != null)
            {
                pw.close();
            }
            if (w != null)
            {
                w.close();
            }
        }
    }
}

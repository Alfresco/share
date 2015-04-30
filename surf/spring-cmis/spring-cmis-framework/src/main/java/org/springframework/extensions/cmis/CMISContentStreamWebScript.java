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
package org.springframework.extensions.cmis;

import java.io.IOException;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Session;
import org.apache.chemistry.opencmis.commons.data.ContentStream;
import org.apache.chemistry.opencmis.commons.exceptions.CmisBaseException;
import org.apache.chemistry.opencmis.commons.exceptions.CmisObjectNotFoundException;
import org.springframework.extensions.webscripts.AbstractWebScript;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;
import org.springframework.util.FileCopyUtils;

public class CMISContentStreamWebScript extends AbstractWebScript
{
    public static final String DOC_ID = "id";
    public static final String STREAM_ID = "stream";
    public static final String CONNECTION = "conn";

    private CMISScriptParameterFactory scriptParameterFactory;

    public void setScriptParameterFactory(CMISScriptParameterFactory scriptParameterFactory)
    {
        this.scriptParameterFactory = scriptParameterFactory;
    }

    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        CMISConnection conn = null;

        String docId = req.getParameter(DOC_ID);
        String streamId = req.getParameter(STREAM_ID);

        String connectionId = req.getParameter(CONNECTION);

        if (connectionId != null)
        {
            conn = scriptParameterFactory.getConnection(
                    new CMISConnectionManagerImpl(scriptParameterFactory, req.getRuntime()), connectionId);
        } else
        {
            conn = scriptParameterFactory.getConnection(new CMISConnectionManagerImpl(scriptParameterFactory, req
                    .getRuntime()));
        }

        if (conn == null)
        {
            throw new WebScriptException(500, "Invalid connection!");
        }

        Session session = conn.getSession();

        // get object
        CmisObject object;
        try
        {
            object = session.getObject(docId);
        } catch (CmisBaseException e)
        {
            if (e instanceof CmisObjectNotFoundException)
            {
                throw new WebScriptException(404, "Object not found!", e);
            } else
            {
                throw new WebScriptException(500, e.getMessage(), e);
            }
        }

        if (!(object instanceof Document))
        {
            throw new WebScriptException(404, "Object is not a document!");
        }

        // get document content
        Document document = (Document) object;
        ContentStream stream = streamId == null ? document.getContentStream() : document.getContentStream(streamId);

        // stream content
        if (stream.getMimeType() != null)
        {
            res.setContentType(stream.getMimeType());
        }
        long length = stream.getLength();
        if (length != -1)
        {
            res.setHeader("Content-Length", Long.toString(length));
        }
        FileCopyUtils.copy(stream.getStream(), res.getOutputStream());
    }
}

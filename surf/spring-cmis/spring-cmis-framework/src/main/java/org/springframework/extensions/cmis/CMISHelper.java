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

import java.io.InputStream;
import java.math.BigInteger;
import java.util.HashMap;
import java.util.Map;

import org.apache.chemistry.opencmis.client.api.CmisObject;
import org.apache.chemistry.opencmis.client.api.Document;
import org.apache.chemistry.opencmis.client.api.Folder;
import org.apache.chemistry.opencmis.client.api.Policy;
import org.apache.chemistry.opencmis.client.api.Relationship;
import org.apache.chemistry.opencmis.commons.impl.dataobjects.ContentStreamImpl;
import org.springframework.extensions.surf.util.InputStreamContent;

public class CMISHelper
{
    /**
     * Creates a properties map.
     */
    public Map<String, Object> createMap()
    {
        return new HashMap<String, Object>();
    }

    /**
     * Creates a ContentStream object.
     */
    public ContentStreamImpl createContentStream(String filename, long length, String mimetype, InputStream stream)
    {
        return new ContentStreamImpl(filename, length < 0 ? null : BigInteger.valueOf(length), mimetype, stream);
    }

    /**
     * Creates a ContentStream object.
     */
    public ContentStreamImpl createContentStream(String filename, InputStreamContent content)
    {
        if (content == null)
        {
            throw new IllegalArgumentException("No content!");
        }

        return createContentStream(filename, content.getSize(), content.getMimetype(), content.getInputStream());
    }

    public boolean isDocument(CmisObject object)
    {
        return (object instanceof Document);
    }

    public boolean isFolder(CmisObject object)
    {
        return (object instanceof Folder);
    }

    public boolean isPolicy(CmisObject object)
    {
        return (object instanceof Policy);
    }

    public boolean isRelationship(CmisObject object)
    {
        return (object instanceof Relationship);
    }
}

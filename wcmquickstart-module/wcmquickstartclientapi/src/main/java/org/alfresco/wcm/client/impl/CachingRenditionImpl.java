/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.impl;

import java.io.IOException;

import org.alfresco.wcm.client.Rendition;

public class CachingRenditionImpl extends CachingContentStreamImpl implements Rendition
{
    private final long height;
    private final long width;
    
    public CachingRenditionImpl(Rendition rendition) throws IOException
    {
        super(rendition);
        this.height = rendition.getHeight();
        this.width = rendition.getWidth();
    }

    @Override
    public long getHeight()
    {
        return height;
    }

    @Override
    public long getWidth()
    {
        return width;
    }

}

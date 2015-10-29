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
package org.alfresco.wcm.client;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

public class ResourceNotFoundException extends RuntimeException
{
    private static final long serialVersionUID = -30710456153210458L;
    
    Set<String> ids = new TreeSet<String>();
    
    public ResourceNotFoundException(String id)
    {
        ids.add(id);
    }
    
    public ResourceNotFoundException(Collection<String> ids)
    {
        ids.addAll(ids);
    }

    public Set<String> getIds()
    {
        return ids;
    }
}

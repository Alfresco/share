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

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class CMISServerImpl implements CMISServer
{
    private String name;
    private String description;
    private Map<String, String> parameters;

    public CMISServerImpl(String name, String description, Map<String, String> parameters)
    {
        if (name == null)
        {
            throw new IllegalArgumentException("Name must be set!");
        }
        if (parameters == null)
        {
            throw new IllegalArgumentException("Parameters must be set!");
        }

        this.name = name;
        this.description = description;
        this.parameters = new HashMap<String, String>(parameters);
    }

    public String getName()
    {
        return name;
    }

    public String getDescription()
    {
        return description;
    }

    public Map<String, String> getParameters()
    {
        return Collections.unmodifiableMap(parameters);
    }
}

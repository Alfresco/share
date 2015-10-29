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

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * CMIS server configuration element.
 */
public class CMISServersConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = 1L;
    private static final String CONFIG_ELEMENT_ID = "cmis-servers";

    private final Map<String, CMISServer> serverDefintions = new HashMap<String, CMISServer>();

    private Element instantiatedElement = null;
    
    @SuppressWarnings("unchecked")
    public CMISServersConfigElement(Element element)
    {
        super(CONFIG_ELEMENT_ID);
        this.instantiatedElement = element;
        for (Element childElement : ((List<Element>) element.elements("server")))
        {
            Map<String, String> parameters = new LinkedHashMap<String, String>();
            String name = null;
            String description = null;

            for (Element parameterElement : ((List<Element>) childElement.elements("parameter")))
            {
                String key = parameterElement.attributeValue("key");
                String value = parameterElement.attributeValue("value");
                if (key != null && value != null)
                {
                    if (key.equals("name"))
                    {
                        name = value;
                    } else if (key.equals("description"))
                    {
                        description = value;
                    } else
                    {
                        parameters.put(key, value);
                    }
                }
            }

            if (name != null)
            {
                serverDefintions.put(name, new CMISServerImpl(name, description, parameters));
            }
        }
    }

    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        CMISServersConfigElement combinedElement = new CMISServersConfigElement(this.instantiatedElement);
        if (configElement instanceof CMISServersConfigElement)
        {
            combinedElement.serverDefintions.putAll(((CMISServersConfigElement) configElement).getServerDefinitions());
        }
        return combinedElement;
    }

    public Map<String, CMISServer> getServerDefinitions()
    {
        return serverDefintions;
    }
}

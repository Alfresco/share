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

package org.springframework.extensions.surf.uri;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.webscripts.UriTemplate;

/**
 * Index of application URI templates.
 *
 * Each template uses a simple form of the JAX-RS JSR-311 URI Template format - only basic variables
 * are specified in the URI template for matching.
 *
 * Example config:
 * <pre>
 *    <uri-templates>
 *       <uri-template>/page/site/{site}/{page}</uri-template>
 *       <uri-template>/page/site/{site}</uri-template>
 *       <uri-template>/page/user/{userid}/{page}</uri-template>
 *       <uri-template>/page/user/{userid}</uri-template>
 *    </uri-templates>
 * </pre>
 *
 * @author Kevin Roast
 */
public class UriTemplateListIndex
{
    private List<UriTemplate> uriTemplates;

    /**
     * Instantiates a new <code>UriTemplateListIndex</code> object using the list of <code>UriTemplates</code>
     * provided.
     */
    public UriTemplateListIndex(List<UriTemplate> uriTemplates)
    {
        this.uriTemplates = uriTemplates;
    }

    /**
     * Instantiates a new <code>UriTemplateList</code> object using the configuration found in the
     * <code>ConfigElement</code> provided. This means that in order to change the default templates
     * it is also necessary to change both the Spring configuration for the resolver and provide a new
     * resolver to cope with the template. The only place this is now used is in the <code>PageViewResolver</code>
     * and as soon as it is no longer required it will be deleted.
     *
     * @param config     ConfigElement pointing to the <uri-templates> sections (see above)
     */
    public UriTemplateListIndex(ConfigElement config)
    {
        List<ConfigElement> uriElements = config.getChildren("uri-template");
        if (uriElements != null)
        {
            this.uriTemplates = new ArrayList<UriTemplate>(uriElements.size());

            for (ConfigElement uriElement : uriElements)
            {
                String template = uriElement.getValue();
                if (template == null || template.trim().length() == 0)
                {
                    throw new IllegalArgumentException("<uri-template> config element must contain a value.");
                }

                // build the object to represent the Uri Template
                UriTemplate uriTemplate = new UriTemplate(template);

                // store the Uri Template
                this.uriTemplates.add(uriTemplate);
            }
        }
        else
        {
            this.uriTemplates = Collections.<UriTemplate>emptyList();
        }
    }

    /**
     * Search the URI index to locale a match for the specified URI.
     * If found, return the args that represent the matched URI pattern tokens
     * and the values as per the supplied URI value.
     *
     * @param uri  URI to match against the URI Templates in the index
     *
     * @return Map of token args to values or null if no match was found.
     */
    public Map<String, String> findMatch(String uri)
    {
        Map<String, String> match = null;
        for (UriTemplate template : this.uriTemplates)
        {
            match = template.match(uri);
            if (match != null)
            {
                break;
            }
        }

        return match;
    }
}

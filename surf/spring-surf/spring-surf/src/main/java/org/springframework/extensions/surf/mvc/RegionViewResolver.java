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

package org.springframework.extensions.surf.mvc;

import java.util.ArrayList;

import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.uri.UriTemplateListIndex;
import org.springframework.extensions.webscripts.UriTemplate;
import org.springframework.web.servlet.view.AbstractUrlBasedView;

/**
 * <p>Resolves a URI into a region view. Region views are recognized by a prefix of /r or /region. A region is identified by three keys:</p>
 * <ul>
 * <li>scope id: The scope (either "page", "template" or "global")</li>
 * <li>region id: The id of the region</li>
 * <li>source id: The id of the page, template or "global"</li>
 * </ul>
 * <p>URLs are expected to be invoked as shown:</p>
 * <ul>
 * <li>/prefix/regionId/{regionId} - renders the view/body for the region in the global scope</li>
 * <li>/prefix/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId} - renders the view/body for the region</li>
 * </ul>
 *
 * @author muzquiano
 * @author David Draper
 */
public class RegionViewResolver extends AbstractWebFrameworkViewResolver
{
    private static final String URI_PREFIX_REGION_SHORT = "r/";
    private static final String URI_PREFIX_REGION_FULL = "region/";

    public RegionViewResolver()
    {
        super();

        // Create templates for the REST URIs that are supported by this view resolver. Previously this was done using the
        // spring-surf-config.xml file but made little sense because to change the URI templates required changing the Spring Surf
        // configuration, the Spring Bean configuration and providing a new view resolver. It is simpler to define new templates
        // just through a new view resolver that is provided to support those templates.
        ArrayList<UriTemplate> uriTemplates = new ArrayList<UriTemplate>();
        uriTemplates.add(new UriTemplate("/regionId/{regionId}"));
        uriTemplates.add(new UriTemplate("/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId}"));
        setUriTemplateIndex(new UriTemplateListIndex(uriTemplates));

        // Populate the prefixes List with the 2 prefixes that we need to initially support. This code block
        // can be removed if we decide to allow prefixes to be specified via Spring property injection.
        addPrefix(URI_PREFIX_REGION_SHORT);
        addPrefix(URI_PREFIX_REGION_FULL);

        // NOTE: There are no request parameters to add as these are not currently used to identify region views.
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception
    {
    	RegionView view = null;

        String uri = processView(viewName);
        if (uri != null)
        {
            view = new RegionView(getWebframeworkConfigElement(), 
                                  getModelObjectService(), 
                                  getWebFrameworkResourceService(), 
                                  getWebFrameworkRenderService(),
                                  getTemplatesContainer());
            view.setUrl(uri);
            view.setUriTokens(ThreadLocalRequestContext.getRequestContext().getUriTokens());
            view.setUrlHelperFactory(getUrlHelperFactory()); // It doesn't matter if this is null, the result will be the DefaultURLHelper gets created
        }
        return view;
    }
}

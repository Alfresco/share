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
 * <p>Resolves a URI into a component view.</p>
 * <p>Component views are recognized by a prefix of /c or /component. A Surf component is identified by three keys:</p>
 * <ul>
 * <li>scope id: The scope (either "page", "template" or "global")</li>
 * <li>region id: The id of the region</li>
 * <li>source id: The id of the page, template or "global"</li>
 * </ul>
 * <p>Optional attributes include:</p>
 * <ul>
 * <li>mode: Either "view", "edit" or "help"</li>
 * <li>focus: Either "header", "body" or "all"</li>
 * </ul>
 * <p>URLs are expected to be invoked as shown:
 * <ul>
 * <li>/prefix/regionId/{regionId}: renders the view/body for the region in the global scope</li>
 * <li>/prefix/scope/{scope}/regionId/{regionId}/sourceId/{sourceId}: renders the view/body for the region</li>
 * <li>/prefix/focus/{focus}/scope/{scope}/regionId/{regionId}/sourceId/{sourceId}: renders the view/focus for the region</li>
 * <li>/prefix/mode/{mode}/scope/{scope}/regionId/{regionId}/sourceId/{sourceId}: renders the mode/focus for the region</li>
 * <li>/prefix/mode/{mode}/focus/{focus}/scope/{scope}/regionId/{regionId}/sourceId/{sourceId}: renders the mode/focus for the region</li>
 * </ul>
 *
 * @author muzquiano
 * @author David Draper
 */
public class ComponentViewResolver extends AbstractWebFrameworkViewResolver
{
    private static final String URI_PREFIX_COMPONENT_SHORT = "c/";
    private static final String URI_PREFIX_COMPONENT_FULL = "component/";

    public ComponentViewResolver()
    {
        super();

        // Create templates for the REST URIs that are supported by this view resolver. Previously this was done using the
        // spring-surf-config.xml file but made little sense because to change the URI templates required changing the Spring Surf
        // configuration, the Spring Bean configuration and providing a new view resolver. It is simpler to define new templates
        // just through a new view resolver that is provided to support those templates.
        ArrayList<UriTemplate> uriTemplates = new ArrayList<UriTemplate>();
        uriTemplates.add(new UriTemplate("/regionId/{regionId}"));
        uriTemplates.add(new UriTemplate("/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId}"));
        uriTemplates.add(new UriTemplate("/mode/{mode}/focus/{focus}/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId}"));
        uriTemplates.add(new UriTemplate("/focus/{focus}/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId}"));
        uriTemplates.add(new UriTemplate("/mode/{mode}/scope/{scopeId}/regionId/{regionId}/sourceId/{sourceId}"));
        setUriTemplateIndex(new UriTemplateListIndex(uriTemplates));

        // Populate the prefixes List with the 2 prefixes that we need to initially support. This code block
        // can be removed if we decide to allow prefixes to be specified via Spring property injection.
        addPrefix(URI_PREFIX_COMPONENT_SHORT);
        addPrefix(URI_PREFIX_COMPONENT_FULL);

        // NOTE: There are no request parameters to add as these are not currently used to identify component views.
    }

    /* (non-Javadoc)
     * @see org.springframework.web.servlet.view.UrlBasedViewResolver#buildView(java.lang.String)
     */
    protected AbstractUrlBasedView buildView(String viewName) throws Exception
    {
    	ComponentView view = null;

        String uri = processView(viewName);
        if (uri != null)
        {
        	view = new ComponentView(getWebframeworkConfigElement(), 
                                     getModelObjectService(), 
                                     getWebFrameworkResourceService(), 
                                     getWebFrameworkRenderService(),
                                     getTemplatesContainer());
        	view.setUrlHelperFactory(getUrlHelperFactory()); // It doesn't matter if this is null, the result will be the DefaultURLHelper gets created
        	view.setUrl(uri);
        	view.setUriTokens(ThreadLocalRequestContext.getRequestContext().getUriTokens());
        }

        return view;
    }
}

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
package org.alfresco.wcm.client.interceptor;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.wcm.client.WebSite;
import org.alfresco.wcm.client.directive.TemplateConstants;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import freemarker.template.TemplateDirectiveModel;

/**
 * Load data into the Spring Model
 * @author Chris Lack
 */
public class ModelDecorator
{
	private Map<String, TemplateDirectiveModel> freemarkerDirectives;

    public void populate(HttpServletRequest request, ModelAndView modelAndView)
    {		 
	    if (modelAndView != null && 
	    		( ! modelAndView.hasView() || ! (modelAndView.getView() instanceof RedirectView))) // Don't bother if redirect
	    {
		    Map<String,Object> model = modelAndView.getModel();
			
			// Store custom Freemarker directives in the spring model
		    model.putAll(freemarkerDirectives);
		    
		    // Store website, section and asset on spring model too for use in page meta data
			RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();	
			WebSite website = (WebSite)requestContext.getValue("webSite");
		    model.put("webSite", website);
		    model.put("section", requestContext.getValue("section"));
            model.put("asset", requestContext.getValue("asset"));
            model.put("rootSection", requestContext.getValue("rootSection"));
		    
		    // If spring freemarker macros are used then this line is needed as the Surf freemarker view
		    // resolver doesn't put the spring request context into the model.
		    model.put("springMacroRequestContext", 
		    			new org.springframework.web.servlet.support.RequestContext(request, model));
		    
		    // Put the pre-filtered/modified URI into the model
		    model.put("uri", requestContext.getAttribute("javax.servlet.forward.request_uri"));	
		    
		    // Flag that the site is the editorial version so that editorial-only features can
		    // be enabled
		    model.put("editorialSite", website.isEditorialSite());
		    
		    // Enable the web editor if this is the editorial site.
		    request.setAttribute(TemplateConstants.REQUEST_ATTR_KEY_WEF_ENABLED, website.isEditorialSite());
	    }
    }

    public void setFreemarkerDirectives(Map<String,TemplateDirectiveModel> directives) 
    {
		this.freemarkerDirectives = directives;
	}
}



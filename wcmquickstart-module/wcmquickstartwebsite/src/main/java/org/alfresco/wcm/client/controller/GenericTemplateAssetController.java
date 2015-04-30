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
package org.alfresco.wcm.client.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.alfresco.wcm.client.Asset;
import org.alfresco.wcm.client.exception.PageNotFoundException;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.mvc.UrlViewController;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * GenericTemplateAssetController is for assets which are not returned by a short url (see StreamedAssetController) and
 * do not have a specific controller (eg SearchFormController). The class extends Surf's UrlViewController.
 * @author Chris Lack
 */
public class GenericTemplateAssetController extends UrlViewController
{
	private List<Pattern> staticPages;
	
	@Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)	
	{
		String path = request.getPathInfo();
		String context = request.getContextPath();
		
		// Redirect /some-url/index.html to /some-url/ to avoid duplicate URLs being flagged by search engines.
		// Also Spring Surf resolves index.html requests so DynamicPageViewResolver doesn't get to process them.
		if (path.endsWith("/index.html"))			
		{
			int lastDelim = path.lastIndexOf('/');
			String uri = context + path.substring(0,lastDelim+1);
			RedirectView redirect = new RedirectView(uri,false,false);
			redirect.setStatusCode(HttpStatus.MOVED_PERMANENTLY);
			return new ModelAndView(redirect);			
		}
		
		// If no asset was found by ApplicationDataInterceptor then forward to 404 template page. 
		// Doing this within a controller prevents Surf's own PageViewResolver from processing a url as a 
		// template page name before the application's DynamicPageViewResolver does a lookup in the 
		// repository to get a template name from the url.
		RequestContext requestContext = ThreadLocalRequestContext.getRequestContext();
		Asset asset = (Asset) requestContext.getValue("asset");		
		if (asset == null && ! isStatic(path)) 
		{
			response.setStatus(HttpStatus.NOT_FOUND.value());
			throw new PageNotFoundException(path);
		}
		
		return super.handleRequestInternal(request, response);
	}
	
	private boolean isStatic(String path)
	{
        for (Pattern staticPage : staticPages)
        {
            Matcher matcher = staticPage.matcher(path);
            if (matcher.matches()) return true;
        }
        return false;
	}
	
	public void setStaticPages(Set<String> staticPages)
	{
		this.staticPages = new ArrayList<Pattern>();		
        for (String staticPage : staticPages)
        {
            this.staticPages.add(Pattern.compile(staticPage));
        }
	}
}

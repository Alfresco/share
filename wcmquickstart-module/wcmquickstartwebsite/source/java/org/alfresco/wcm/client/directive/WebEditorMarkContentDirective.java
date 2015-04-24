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
package org.alfresco.wcm.client.directive;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.awe.tag.AlfrescoTagUtil;
import org.alfresco.web.awe.tag.MarkedContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive which indicates an editable area of the page
 * Usage: <@markContent id=nodeRef title=mytitle formId=form nestedMarker='true'/>
 * id - The mandatory id attribute specifies the NodeRef of the Alfresco node to be edited. 
 * title - The mandatory title attribute defines a descriptive title for the editable area 
 * being marked. The title used will be used in the quick edit drop down menu of editable 
 * items, as the title of form edit popup/dialog and the 'alt' text and tooltip text of the 
 * edit icon. 
 * formId - The optional formId attribute specifies which form will be used when the marked 
 * area is edited. See the Form Configuration section below for more details.
 * nestedMarker - The optional nestedMarker attribute defines whether the editable area is 
 * nested within another HTML tag that represents the content being edited. If set to "true" 
 * the whole parent element is highlighted when the area is selected in the quick edit drop 
 * down menu. If set to "false" only the edit icon is highlighted.
 *  
 * @author Gavin Cornwell
 * @author Chris Lack
 */
public class WebEditorMarkContentDirective extends AbstractTemplateDirective
{
    private static final Log logger = LogFactory.getLog(WebEditorMarkContentDirective.class);

	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		SimpleScalar idParam = (SimpleScalar)params.get("id");
		SimpleScalar titleParam = (SimpleScalar)params.get("title");
		SimpleScalar formIdParam = (SimpleScalar)params.get("formId");
		SimpleScalar nestedMarkerParam = (SimpleScalar)params.get("nestedMarker");

		if (idParam == null) 
		{
			throw new TemplateModelException("id parameter is mandatory for markContent directive");
		}

		String contentId = idParam.getAsString();
		//String safeId = URLEncoder.encode(contentId, "UTF-8");
		String contentTitle = (titleParam == null) ? null : titleParam.getAsString();
		String formId = null;
		boolean nestedMarker = false;
		if (formIdParam != null) formId = formIdParam.getAsString();
		if (nestedMarkerParam != null) nestedMarker = "true".equals(nestedMarkerParam.getAsString()); 

        if (isEditingEnabled(env))
        {
            try
            {
                Writer out = env.getOut();
                HttpServletRequest request = getRequest(env);
                
                // get the prefix URL to the AWE assets
                String urlPrefix = getWebEditorUrlPrefix(env);

                // generate a unique id for this marked content
                List<MarkedContent> markedContent = AlfrescoTagUtil.getMarkedContent(request);
                String markerIdPrefix = (String) request.getAttribute(AlfrescoTagUtil.KEY_MARKER_ID_PREFIX);
                String markerId = markerIdPrefix + "-" + (markedContent.size() + 1);
                String redirectUrl = calculateRedirectUrl(request);

                // create marked content object and store
                MarkedContent content = new MarkedContent(markerId, contentId, contentTitle, formId, nestedMarker);
                markedContent.add(content);

                AlfrescoTagUtil.writeMarkContentHtml(out, urlPrefix, redirectUrl, content);


                if (logger.isDebugEnabled())
                    logger.debug("Completed markContent rendering for: " + content);
            }
            catch (IOException ioe)
            {
                throw new TemplateModelException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping markContent rendering as editing is disabled");
        }
		
    }
	
    /**
     * Calculates the redirect url for form submission, this will
     * be the current request URL.
     * 
     * @return The redirect URL
     */
    private String calculateRedirectUrl(HttpServletRequest request)
    {
        String redirectUrl = null;
        try
        {
        	// Build the redirect URL up bit by bit to avoid getting /service/ included.
        	String fullUrl = request.getRequestURL().toString();
        	if (logger.isDebugEnabled())
        	{
        	    logger.debug("Calculating redirect URL. Request URL is " + fullUrl);
        	}
        	int firstSep = fullUrl.indexOf("/", fullUrl.indexOf("://")+3);
            StringBuffer url = new StringBuffer();
            url.append(fullUrl.substring(0, firstSep));
            url.append(request.getContextPath());
            url.append(request.getPathInfo());            
            String queryString = request.getQueryString();
            if (queryString != null)
            {
                url.append("?").append(queryString);
            }

            redirectUrl = URLEncoder.encode(url.toString(), "UTF-8");
            if (logger.isDebugEnabled())
            {
                logger.debug("Calculated redirect URL: " + redirectUrl);
            }
        }
        catch (UnsupportedEncodingException uee)
        {
            // just return null
        }

        return redirectUrl;
    }	
}

	
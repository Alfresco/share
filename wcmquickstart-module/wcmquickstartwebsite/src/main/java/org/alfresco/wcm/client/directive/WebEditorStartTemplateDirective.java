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
import java.io.Writer;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.awe.tag.AlfrescoTagUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive which bootstraps the Web Editor
 * Usage: <@startTemplate toolbarLocation=xxx/>
 * The toolbarLocation attribute controls the initial location of the toolbar, 
 * valid values are "top", "left" and "right", the default is "top". (Optional) 
 * 
 * @author Gavin Cornwell
 * @author muzquiano
 * @author Chris Lack
 */
public class WebEditorStartTemplateDirective extends AbstractTemplateDirective
{
    protected static final Log logger = LogFactory.getLog(WebEditorStartTemplateDirective.class);
    private static final String ALF = "alf_";
		
    @SuppressWarnings("rawtypes")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		if (params.size() > 1) throw new TemplateModelException("truncate directive expects no more than one parameter");
					
		SimpleScalar locationParam = (SimpleScalar)params.get("toolbarLocation");
		String toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_TOP;
		if (locationParam != null)
		{
			String location = locationParam.getAsString();
			if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_TOP))
	        {
				toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_TOP;
	        }
	        else if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_LEFT))
	        {
	        	toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_LEFT;
	        }
	        else if (location.equalsIgnoreCase(TemplateConstants.TOOLBAR_LOCATION_RIGHT))
	        {
	        	toolbarLocation = TemplateConstants.TOOLBAR_LOCATION_RIGHT;
	        }
		}
		
        if (isEditingEnabled(env))
        {
        	HttpServletRequest request = getRequest(env);
        	
            // store the toolbar location into the request session
            request.setAttribute(TemplateConstants.REQUEST_ATTR_KEY_TOOLBAR_LOCATION, toolbarLocation);

            try
            {
                Writer out = env.getOut();

                // bootstrap WEF
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(getWebEditorUrlPrefix(env));
                out.write("/service/wef/bootstrap");
                if (isDebugEnabled(env))
                {
                    out.write("?debug=true");
                }
                
                // add in custom configuration
                request.setAttribute(AlfrescoTagUtil.KEY_MARKER_ID_PREFIX, ALF + System.currentTimeMillis());

				// end of bootstrap                
                out.write("\"></script>\n");
                
                if (logger.isDebugEnabled())
                    logger.debug("Completed startTemplate rendering");                
            }
            catch (IOException ioe)
            {
                throw new TemplateModelException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping startTemplate rendering as editing is disabled");
        }
    }
}

	
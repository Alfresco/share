/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
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

	
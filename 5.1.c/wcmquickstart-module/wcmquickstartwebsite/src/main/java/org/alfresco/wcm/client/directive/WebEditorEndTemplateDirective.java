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
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.alfresco.web.awe.tag.AlfrescoTagUtil;
import org.alfresco.web.awe.tag.MarkedContent;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;

/**
 * Freemarker directive which initialises the Web Editor with all marked areas of the page.
 * Usage: <@endTemplate/>
 * 
 * @author Gavin Cornwell 
 * @author Chris Lack
 */
public class WebEditorEndTemplateDirective extends AbstractTemplateDirective
{	
    protected static final Log logger = LogFactory.getLog(WebEditorEndTemplateDirective.class);	

	@SuppressWarnings("unchecked")
    @Override
    public void execute(Environment env, 
    		            Map params, 
    		            TemplateModel[] loopVars,
            			TemplateDirectiveBody body) throws TemplateException, IOException
    {
		if (params.size() > 0) throw new TemplateModelException("endTemplate directive expects no parameters");
		
        // get the toolbar location from the request
        String toolbarLocation = getToolbarLocation(env);
        
        if (isEditingEnabled(env) && toolbarLocation != null)
        {
            try
            {
                Writer out = env.getOut();
                HttpServletRequest request = getRequest(env);

                // render config required for ribbon and marked content
                out.write("<script type=\"text/javascript\">\n");
                out.write("WEF.ConfigRegistry.registerConfig('org.springframework.extensions.webeditor.ui.ribbon',\n");
                out.write("{ position: \"");
                out.write(toolbarLocation);
                out.write("\" });");
                
                // add in custom configuration
                // render JavaScript to configure toolbar and edit icons
                List<MarkedContent> markedContent = AlfrescoTagUtil.getMarkedContent(request);
                
                out.write("\nvar urlParts = window.location.href.split(\"/\");");
                out.write("\nvar categoryRootUrl =  urlParts[0] + \"//\";");
                out.write("\nfor (var i=2; i < urlParts.length - 1; i++)");
                out.write("\n{");
                out.write("\n   categoryRootUrl += urlParts[i] + \"/\";");
                out.write("\n}");
                out.write("\ncategoryRootUrl += \"index.html\";");
                out.write("\nWEF.ConfigRegistry.registerConfig('org.alfresco.awe',{id:'awe',name:'awe',editables:[\n");
                boolean first = true;
                for (MarkedContent content : markedContent)
                {
                    if (first == false)
                    {
                        out.write(",");
                    }
                    else
                    {
                        first = false;
                    }

                    out.write("\n{\n   id: \"");
                    out.write(encode(content.getMarkerId()));
                    out.write("\",\n   nodeRef: \"");
                    out.write(encode(content.getContentId()));
                    out.write("\",\n   title: \"");
                    out.write(encode(content.getContentTitle()));
                    out.write("\",\n   nested: ");
                    out.write(Boolean.toString(content.isNested()));
                    out.write(",\n   redirectUrl: categoryRootUrl");
                    if (content.getFormId() != null)
                    {
                        out.write(",\n   formId: \"");
                        out.write(encode(content.getFormId()));
                        out.write("\"");
                    }
                    out.write("\n}");
                }
                out.write("]});\n");

                if (logger.isDebugEnabled())
                {
                    logger.debug("Completed endTemplate rendering for " + markedContent.size() + 
                        " marked content items with toolbar location of: " + getToolbarLocation(env));
                }
                
                // close render config               
                out.write("\n</script>");

                // request all the resources
                out.write("<script type=\"text/javascript\" src=\"");
                out.write(getWebEditorUrlPrefix(env));
                out.write("/service/wef/resources\"></script>\n");
                
                if (logger.isDebugEnabled())
                    logger.debug("Completed endTemplate rendering");
            }
            catch (IOException ioe)
            {
                throw new TemplateModelException(ioe.toString());
            }
        }
        else if (logger.isDebugEnabled())
        {
            logger.debug("Skipping endTemplate rendering as editing is disabled");
        }
    }	
}

	
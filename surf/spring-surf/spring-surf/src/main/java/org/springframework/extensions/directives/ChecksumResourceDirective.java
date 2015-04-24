/*
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
package org.springframework.extensions.directives;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.types.TemplateInstance;

import freemarker.core.Environment;
import freemarker.template.TemplateDirectiveBody;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;

/**
 * <p>This directive is used to convert resource URLs into resource URLs containing a checksum that uniquely 
 * matches the resource contents.</p>
 * 
 * @author David Draper
 */
public class ChecksumResourceDirective extends AbstractDependencyExtensibilityDirective
{
    private static final Log logger = LogFactory.getLog(ChecksumResourceDirective.class);

    public ChecksumResourceDirective(String directiveName, ExtensibilityModel model)
    {
        super(directiveName, model);
    }
    
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void execute(Environment env,
                        Map params, 
                        TemplateModel[] loopVars, 
                        TemplateDirectiveBody body) throws TemplateException, IOException
    {
        if (getModelObject() instanceof TemplateInstance && getRequestContext().isPassiveMode())
        {
            // Don't process this when calculating WebScript dependencies. This checks needs to be done because it is perfectly valid
            // for TemplateInstance FreeMarker templates to use the dependency directives. Because of the double-pass processing to 
            // obtain WebScript dependencies. If we don't do this check then we're guaranteed to import the same dependency twice.
        }
        else
        {
            String src = getStringProperty(params, DirectiveConstants.SRC, true);
            if (getRequestContext().dependencyAlreadyRequested(src))
            {
                // This dependency has already been requested for the current request, no need to add it again.
                if (logger.isDebugEnabled())
                {
                    logger.debug("A duplicate request was made for \"" + src + "\". This duplicate request has been removed but may potentially cause problems resulting from unexpected ordering");
                }
                env.getOut().write("404_caused_by_duplicate_request: " + src);
            }
            else
            {
                String parm = getStringProperty(params, DirectiveConstants.CHECKSUM_PARM, false);
                ProcessedDependency pd = processDependency(src);
                if (this.dependencyHandler != null)
                {
                    // If no parameter argument has been provided just create a regular checksum path...
                    if (parm == null)
                    {
                        String checksumPath = this.dependencyHandler.getChecksumPath(getUpdatedSrc(pd));
                        if (checksumPath != null)
                        {
                            env.getOut().write(getToInsert(pd) + checksumPath);
                            getRequestContext().markDependencyAsRequested(src);
                        }
                        else
                        {
                            // Handle missing resource...
                            logger.error("It was not possible to generate the resource request for: \"" + src + "\" because the resource could not be found");
                        }
                    }
                    else
                    {
                        // If the checksum has been requested to be placed as a parameter then it is necessary
                        // to build the request slightly differently. This is a non-standard approach that has
                        // been created to solve specific use cases and currently is not as efficient.
                        String checksum = this.dependencyHandler.getChecksum(getUpdatedSrc(pd));
                        env.getOut().write(getToInsert(pd) + getUpdatedSrc(pd) + DirectiveConstants.QUESTION_MARK + parm + DirectiveConstants.EQUALS + checksum);
                        getRequestContext().markDependencyAsRequested(src);
                    }
                }
            }
        }
    }
}

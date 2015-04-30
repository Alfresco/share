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
package org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser;

import java.util.List;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.model.FileInfo;
import org.alfresco.service.cmr.repository.NodeRef;

/**
 * Section Path Context Parser
 * 
 * @author Roy Wetherall
 */
public class PdfRenditionPathContextParser extends ContextParser
{
    private FileFolderService fileFolderService;

    private Repository repository;

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }

    /**
     * @see org.alfresco.module.org_alfresco_module_wcmquickstart.util.contextparser.ContextParser#execute(org.alfresco.service.cmr.repository.NodeRef)
     */
    @Override
    public String execute(NodeRef context)
    {
        NodeRef section = siteHelper.getRelevantSection(context);
        if (section == null)
        {
            // For some reason we can't retrieve the section so return null
            // string
            return null;
        }

        List<FileInfo> path = null;
        try
        {
            path = fileFolderService.getNamePath(repository.getCompanyHome(), section);
        }
        catch (Exception e)
        {
            // Rethrow as a runtime exception
            throw new AlfrescoRuntimeException("Unable to retrieve section path information", e);
        }

        StringBuilder builder = new StringBuilder();
        boolean bFirst = true;
        for (FileInfo pathElement : path)
        {
            if (bFirst == true)
            {
                bFirst = false;
            }
            else
            {
                builder.append("/");
            }
            builder.append(pathElement.getName());
        }
        builder.append('/');

        //Figure out the name of the pdf rendition. It will be the same as the name of the source node but with the extension
        //changed to ".pdf"
        String currentName = (String) nodeService.getProperty(context, ContentModel.PROP_NAME);
        String[] splitName = currentName.split("\\.");
        if (splitName.length == 1)
        {
            //The source node had no extension on its file name. Simply append the appropriate one
            splitName[0] += ".pdf";
        }
        else
        {
            //Replace the part after the last '.' with "pdf"
            splitName[splitName.length - 1] = "pdf";
        }
        //Now reassemble the file name on the end of the path
        bFirst = true;
        for (int i = 0; i < splitName.length; ++i)
        {
            if (!bFirst)
            {
                builder.append('.');
            }
            builder.append(splitName[i]);
            bFirst = false;
        }

        return builder.toString();
    }

}

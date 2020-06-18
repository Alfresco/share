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

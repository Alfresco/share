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
package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.transfer.AbstractNodeFilter;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;

/**
 * This filter accepts only nodes that exist
 * @author Brian
 *
 */
public class CheckedOutNodeFilter extends AbstractNodeFilter
{
    private NodeService nodeService;

    @Override
    public void init()
    {
        super.init();
        nodeService = serviceRegistry.getNodeService(); 
    }

    @Override
    public boolean accept(NodeRef thisNode)
    {
        return (!nodeService.hasAspect(thisNode, ContentModel.ASPECT_WORKING_COPY));
    }

}

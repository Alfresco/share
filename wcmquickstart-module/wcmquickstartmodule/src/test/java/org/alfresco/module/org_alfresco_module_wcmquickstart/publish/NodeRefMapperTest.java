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

import junit.framework.TestCase;

import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.util.GUID;


public class NodeRefMapperTest extends TestCase
{
    public void test()
    {
        NodeRefMapper mapper = new NodeRefMapper();
        for (int i = 0; i < 3000; ++i)
        {
            NodeRef source = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, GUID.generate());

            NodeRef dest1 = mapper.mapSourceNodeRef(source);
            assertFalse(source.getId().equals(dest1.getId()));

            NodeRef dest2 = mapper.mapSourceNodeRef(dest1);
            assertFalse(dest1.getId().equals(dest2.getId()));
            assertFalse(source.getId().equals(dest2.getId()));
            
            NodeRef source2 = mapper.mapDestinationNodeRef(dest2);
            assertEquals(dest1, source2);

            NodeRef source3 = mapper.mapDestinationNodeRef(source2);
            assertEquals(source, source3);
        }
        NodeRef source = new NodeRef(StoreRef.STORE_REF_WORKSPACE_SPACESSTORE, "A short noderef");
        NodeRef dest = mapper.mapSourceNodeRef(source);
        assertEquals("A short nodereff", dest.getId());
        NodeRef newSource = mapper.mapDestinationNodeRef(dest);
        assertEquals(source, newSource);
    }
}

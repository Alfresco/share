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

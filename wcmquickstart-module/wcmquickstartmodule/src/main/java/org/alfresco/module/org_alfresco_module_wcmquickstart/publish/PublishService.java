package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import java.util.Collection;

import org.alfresco.service.cmr.repository.NodeRef;

public interface PublishService
{

    public void enqueuePublishedNodes(NodeRef... nodes);

    public void enqueueRemovedNodes(NodeRef... nodes);

    public void enqueuePublishedNodes(Collection<NodeRef> nodes);

    public void enqueueRemovedNodes(Collection<NodeRef> nodes);

    public void publishQueue(NodeRef websiteId);

    public String getTransferTargetName();

}

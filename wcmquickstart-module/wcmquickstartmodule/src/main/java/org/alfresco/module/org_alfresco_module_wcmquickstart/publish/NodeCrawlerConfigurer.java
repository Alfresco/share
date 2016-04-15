package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.service.cmr.transfer.NodeCrawler;

public interface NodeCrawlerConfigurer
{
    void configure(NodeCrawler crawler);
}

package org.alfresco.module.org_alfresco_module_wcmquickstart.publish;

import org.alfresco.service.cmr.transfer.NodeCrawler;

public class WqsNodeCrawlerConfigurerImpl implements NodeCrawlerConfigurer
{
    @Override
    public void configure(NodeCrawler crawler)
    {
        crawler.setNodeFilters(new ExistingNodeFilter(),
                new CheckedOutNodeFilter());
        crawler.setNodeFinders(
                new IndexPageSectionFinder(),
                new CriticalSectionInfoFinder(),
                new WebAssetCollectionPublishingFinder(),
                new ArticleImageFinder(),
                new RenditionsFinder());
    }
}

package org.alfresco.wcm.client;

import java.util.List;

public class CollectionFactoryTest extends BaseTest
{
    public void testGetCollection()
    {
        Section rootSection = getRootSection();

        AssetCollection topNews = collectionFactory.getCollection(rootSection.getId(), "news.top");
        assertNotNull(topNews);
        List<Asset> assets = topNews.getAssets();
        assertTrue(assets != null && assets.size() > 0);
    }

}

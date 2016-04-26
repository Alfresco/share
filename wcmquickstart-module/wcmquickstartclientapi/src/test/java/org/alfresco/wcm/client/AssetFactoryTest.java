package org.alfresco.wcm.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class AssetFactoryTest extends BaseTest 
{
	private final static Log log = LogFactory.getLog(AssetFactoryTest.class);

	public void testGetIndexAsset()
	{
		WebSite site = getWebSite();
        
		Section root = site.getRootSection();
        String rootId = root.getId();		
		
		Asset indexAsset = assetFactory.getSectionAsset(rootId, "index.html");
		assertEquals("index.html", indexAsset.getName());
		assertEquals(rootId, indexAsset.getContainingSection().getId());
		
		Asset indexAsset2 = assetFactory.getAssetById(indexAsset.getId());
        assertEquals(indexAsset.getId(), indexAsset2.getId());
        assertEquals(indexAsset.getName(), indexAsset2.getName());
        assertEquals(indexAsset.getProperties(), indexAsset2.getProperties());
        
        log.info(indexAsset.getProperties());
        
        List<Section> sections = site.getRootSection().getSections();
        List<String> indexPageIds = new ArrayList<String>();
        for (Section section : sections)
        {
            indexPageIds.add(assetFactory.getSectionAsset(section.getId(), "index.html").getId());
        }
        List<Asset> assets = assetFactory.getAssetsById(indexPageIds);
        for (Asset asset : assets)
        {
            assertTrue(indexPageIds.remove(asset.getId()));
        }
        assertTrue(indexPageIds.isEmpty());
	}
	
    public void testSearch()
    {
        WebSite site = getWebSite();
        
        Section rootSection = site.getRootSection();
        Query query = rootSection.createQuery();
        assertEquals(rootSection.getId(), query.getSectionId());
        
        //FIXME: bjr 20100720: Need reliable test data here
        query.setPhrase("test");
        SearchResults results = assetFactory.findByQuery(query);
        log.debug("Result count = " + results.getTotalSize());

        query.setPhrase(null);
        query.setTag("potato");
        results = assetFactory.findByQuery(query);
        log.debug("Result count = " + results.getTotalSize());
    }
    
    public void testRenditions()
    {
//        WebSite site = webSiteService.getWebSite("localhost", port);
//        assertNotNull(site);
//        
//        Section rootSection = site.getRootSection();
//        Asset pdf = rootSection.getAsset("test.pdf");
//        assertNotNull(pdf);
//        log.debug(pdf.getRenditions());
//        
    }
    
    public void testRelationships()
    {
//        WebSite site = getWebSite();
//        
        //Section rootSection = site.getRootSection();
        //FIXME: bjr 20100720: Need reliable test data here...
//        Asset testArticle = rootSection.getAsset("test-article2.html");
//        
//        log.debug(testArticle.getProperties());
//        Asset primaryImage = testArticle.getRelatedAsset("ws:primaryImage");
//        assertEquals("Chrysanthemum.jpg", primaryImage.getName());
    }
    
	
}

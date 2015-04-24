package org.alfresco.wcm.client;

import java.util.Collection;
import java.util.List;

import org.alfresco.wcm.client.impl.AssetImpl;
import org.junit.Test;

public class WebSiteServiceTest extends BaseTest
{
    public void testGetSections()
    {
        Collection<WebSite> webSites = webSiteService.getWebSites();
        for (WebSite webSite : webSites)
        {
            System.out.println(webSite.getHostName() + ":" + webSite.getHostPort());
        }

        final WebSite site = getWebSite();
        assertNotNull(site);
        assertEquals("localhost", site.getHostName());

        WebSite siteBad = webSiteService.getWebSite("localhost", 8085);
        assertNull(siteBad);

        WebSite siteBad2 = webSiteService.getWebSite("me.com", PORT);
        assertNull(siteBad2);

        Section rootSection = site.getRootSection();
        assertNotNull(rootSection);
        outputSection(0, rootSection);

        Asset pathTest = site.getAssetByPath("/blog/blog1.html");
        assertNotNull(pathTest);
        assertTrue(pathTest instanceof AssetImpl);
        assertEquals("blog1.html", pathTest.getName());

        Asset indexFromPath = site.getAssetByPath("/blog/");
        assertNotNull(indexFromPath);
        assertTrue(indexFromPath instanceof AssetImpl);
        assertEquals("index.html", indexFromPath.getName());

        Asset indexFromNull = site.getAssetByPath(null);
        assertNotNull(indexFromNull);
        assertTrue(indexFromNull instanceof AssetImpl);
        assertEquals("index.html", indexFromNull.getName());
    }

    // /blog should fail unless there is a file named "blog" ... /blog/ should
    // return the index.html
    @Test(expected = ResourceNotFoundException.class)
    public void testError()
    {
        final WebSite site = webSiteService.getWebSite("localhost", PORT);
        site.getAssetByPath("/blog");
    }

    public void testSectionConfig()
    {
        WebSite site = getWebSite();

        Asset blog1 = site.getAssetByPath("/blog/blog1.html");
        Section blog = blog1.getContainingSection();

        String template = blog.getTemplate("cmis:document");
        assertNotNull(template);
        assertEquals("baseTemplate", template);

        template = ((Asset) blog1).getTemplate();
        assertNotNull(template);
        assertEquals("articlepage2", template);

        Resource index = blog.getIndexPage();
        assertNotNull(index);
        template = ((Asset) index).getTemplate();
        assertNotNull(template);
        assertEquals("sectionpage2", template);
    }

    private void outputSection(int depth, Section section)
    {
        System.out.println(indentString(depth) + "/" + section.getName());
        List<Section> sections = section.getSections();
        for (Section child : sections)
        {
            assertEquals(section.getName(), child.getContainingSection().getName());
            outputSection(depth + 3, child);
        }
    }

    private String indentString(int size)
    {
        StringBuffer buffer = new StringBuffer(size);
        for (int i = 0; i < size; i++)
        {
            buffer.append(" ");
        }
        return buffer.toString();
    }
}

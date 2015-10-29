/**
 * 
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.test.FailedTestListener;
import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Ranjith Manyam
 *
 */
@Listeners(FailedTestListener.class)
public class ContentDetailsTest extends AbstractSiteDashletTest
{

    @Test
    public void testDefaultConstructor()
    {
        ContentDetails details = new ContentDetails();
        details.setName("Name");
        details.setTitle("Title");
        details.setDescription("Description");
        details.setContent("Content");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertEquals(details.getTitle(), "Title");
        Assert.assertEquals(details.getDescription(), "Description");
        Assert.assertEquals(details.getContent(), "Content");
    }

    @Test
    public void testNameFieldConstructor()
    {
        ContentDetails details = new ContentDetails("Name");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertNull(details.getTitle());
        Assert.assertNull(details.getDescription());
        Assert.assertNull(details.getContent());
    }

    @Test
    public void testConstructorWithAllFields()
    {
        ContentDetails details = new ContentDetails("Name", "Title", "Description", "Content");

        Assert.assertEquals(details.getName(), "Name");
        Assert.assertEquals(details.getTitle(), "Title");
        Assert.assertEquals(details.getDescription(), "Description");
        Assert.assertEquals(details.getContent(), "Content");
    }
    
}

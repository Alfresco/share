package org.alfresco.po.share.site.document;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

/**
 * @author Jamie Allison
 *
 */
@Test(groups="unit")
public class SortFieldTest
{

    @Test
    public void toStringTest()
    {
        assertEquals(SortField.NAME.toString(), "Name");
        assertEquals(SortField.POPULARITY.toString(), "Popularity");
        assertEquals(SortField.TITLE.toString(), "Title");
        assertEquals(SortField.DESCRIPTION.toString(), "Description");
        assertEquals(SortField.CREATED.toString(), "Created");
        assertEquals(SortField.CREATOR.toString(), "Creator");
        assertEquals(SortField.MODIFIED.toString(), "Modified");
        assertEquals(SortField.MODIFIER.toString(), "Modifier");
        assertEquals(SortField.SIZE.toString(), "Size");
        assertEquals(SortField.MIMETYPE.toString(), "Mimetype");
        assertEquals(SortField.TYPE.toString(), "Type");
    }
    
    @Test
    public void getSortLocatorTest()
    {
        assertEquals(SortField.NAME.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Name']");
        assertEquals(SortField.POPULARITY.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Popularity']");
        assertEquals(SortField.TITLE.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Title']");
        assertEquals(SortField.DESCRIPTION.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Description']");
        assertEquals(SortField.CREATED.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Created']");
        assertEquals(SortField.CREATOR.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Creator']");
        assertEquals(SortField.MODIFIED.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Modified']");
        assertEquals(SortField.MODIFIER.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Modifier']");
        assertEquals(SortField.SIZE.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Size']");
        assertEquals(SortField.MIMETYPE.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Mimetype']");
        assertEquals(SortField.TYPE.getSortLocator().toString(), "By.xpath: .//div[@class='sort-field']//a[text()='Type']");
    }
    
    @Test
    public void getEnumTest()
    {
        assertEquals(SortField.getEnum("Name"), SortField.NAME);
        assertEquals(SortField.getEnum("Popularity"), SortField.POPULARITY);
        assertEquals(SortField.getEnum("Title"), SortField.TITLE);
        assertEquals(SortField.getEnum("Description"), SortField.DESCRIPTION);
        assertEquals(SortField.getEnum("Created"), SortField.CREATED);
        assertEquals(SortField.getEnum("Creator"), SortField.CREATOR);
        assertEquals(SortField.getEnum("Modified"), SortField.MODIFIED);
        assertEquals(SortField.getEnum("Modifier"), SortField.MODIFIER);
        assertEquals(SortField.getEnum("Size"), SortField.SIZE);
        assertEquals(SortField.getEnum("Mimetype"), SortField.MIMETYPE);
        assertEquals(SortField.getEnum("Type"), SortField.TYPE);
    }
}

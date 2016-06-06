
package org.alfresco.po.share.enums;

import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.site.document.LibraryOption;
import org.testng.annotations.Test;

/**
 * @author Jamie Allison
 */
@Test(groups = "unit")
public class LibraryOptionTest
{
    @Test
    public void getOption()
    {
        assertEquals(LibraryOption.SHOW_FOLDERS.getOption(), ".showFolders");
        assertEquals(LibraryOption.HIDE_FOLDERS.getOption(), ".hideFolders");
        assertEquals(LibraryOption.SHOW_BREADCRUMB.getOption(), ".showPath");
        assertEquals(LibraryOption.HIDE_BREADCRUMB.getOption(), ".hidePath");
        assertEquals(LibraryOption.RSS_FEED.getOption(), ".rss");
        assertEquals(LibraryOption.FULL_WINDOW.getOption(), ".fullWindow");
        assertEquals(LibraryOption.FULL_SCREEN.getOption(), ".fullScreen");
        assertEquals(LibraryOption.SIMPLE_VIEW.getOption(), ".view.simple");
        assertEquals(LibraryOption.DETAILED_VIEW.getOption(), ".view.detailed");
        assertEquals(LibraryOption.GALLERY_VIEW.getOption(), ".view.gallery");
        assertEquals(LibraryOption.FILMSTRIP_VIEW.getOption(), ".view.filmstrip");
        assertEquals(LibraryOption.TABLE_VIEW.getOption(), ".view.table");
        assertEquals(LibraryOption.AUDIO_VIEW.getOption(), ".view.audio");
        assertEquals(LibraryOption.MEDIA_VIEW.getOption(), ".view.media_table");
    }
}
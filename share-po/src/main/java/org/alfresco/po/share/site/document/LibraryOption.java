
package org.alfresco.po.share.site.document;

/**
 * This class contains the CSS details of options in Document Library /
 * Repository Document.
 * 
 * @author Jamie Allison
 */
public enum LibraryOption
{
    SHOW_FOLDERS(".showFolders"),
    HIDE_FOLDERS(".hideFolders"),
    SHOW_BREADCRUMB(".showPath"),
    HIDE_BREADCRUMB(".hidePath"),
    RSS_FEED(".rss"),
    FULL_WINDOW(".fullWindow"),
    FULL_SCREEN(".fullScreen"),
    SIMPLE_VIEW(".view.simple"),
    DETAILED_VIEW(".view.detailed"),
    GALLERY_VIEW(".view.gallery"),
    FILMSTRIP_VIEW(".view.filmstrip"),
    TABLE_VIEW(".view.table"),
    AUDIO_VIEW(".view.audio"),
    MEDIA_VIEW(".view.media_table");

    private String linkValue;

    private LibraryOption(String link)
    {
        linkValue = link;
    }

    /**
     * Get value of CSS from the page type.
     * @return String
     */
    public String getOption()
    {
        return linkValue;
    }
}
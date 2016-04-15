package org.alfresco.po.share;

/**
 * The Browser Preference enum for FireFox and Chrome profile keys.
 * 
 * @author Ranjith Manyam
 * @since 1.9.0
 */
public enum BrowserPreference
{
    Language("intl.accept_languages", "--lang"),
    DownloadFolderList("browser.download.folderList", ""),
    DownloadDirectory("browser.download.dir", ""),
    SaveToDisk("browser.helperApps.neverAsk.saveToDisk", "");

    private String fireFoxKey;
    private String chromeKey;

    private BrowserPreference(String fireFoxKey, String chromeKey)
    {
        this.fireFoxKey = fireFoxKey;
        this.chromeKey = chromeKey;
    }

    public String getFireFoxKey()
    {
        return fireFoxKey;
    }

    public String getChromeKey()
    {
        return chromeKey;
    }
}

package org.alfresco.po.share.site.document;

public enum MimeType
{
        HTML("text/html"),
        TEXT("text/plain"),
        XHTML("application/xhtml+xml"),
        AlfrescContentPackage("application/acp"),
        XML("text/xml");
    private MimeType(String mimeCode)
    {
        this.mimeCode = mimeCode;
    }

    private String mimeCode;

    /**
     * Gets the mime code value as seen in
     * dropdown value attribute.
     * 
     * @return String value of mime type
     */
    public String getMimeCode()
    {
        return mimeCode;
    }

}

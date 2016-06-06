package org.alfresco.po.share.enums;



/**
 * This TinyMceColourCode enums are used to find the color code css in TinyMce Editor.
 * 
 * @author cbairaajoni
 *
 */
public enum TinyMceColourCode
{
    BLUE("div[title='Blue']", "div[title='Blue']"),
    BLACK("div[title='Black']", "div[title='Black']"),
    YELLOW("div[title='Yellow']", "div[title='Yellow']");


    private String foreColourLocator;
    private String bgColourLocator;

    private TinyMceColourCode(String foreColourLocator, String bgColourLocator)
    {
        this.foreColourLocator = foreColourLocator;
        this.bgColourLocator = bgColourLocator;
    }

    public String getForeColourLocator()
    {
        return foreColourLocator;
    }
    
    public String getBgColourLocator()
    {
        return bgColourLocator;
    }
}

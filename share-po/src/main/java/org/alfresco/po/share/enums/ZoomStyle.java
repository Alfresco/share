package org.alfresco.po.share.enums;

/**
 * This ZoomStyle enums is used to select zoom in or out using the 4 states in
 * Gallery View FileDirectoryInfo.
 * 
 * @author cbairaajoni
 */
public enum ZoomStyle
{
    SMALLEST(0), 
    SMALLER(20), 
    BIGGER(40), 
    BIGGEST(60);

    private int size;

    private ZoomStyle(int size)
    {
        this.size = size;
    }

    /**
     * @return the size zoom Style.
     */
    public int getSize()
    {
        return size;
    }

    /**
     * Find {@link ZoomStyle} based on it is size.
     * 
     * @param size int
     * @return {@link ZoomStyle}
     */
    public static ZoomStyle getZoomStyle(int size)
    {
        for (ZoomStyle style : ZoomStyle.values())
        {
            if (style.getSize() == size)
            {
                return style;
            }
        }
        throw new IllegalArgumentException("Invalid Size Value : " + size);
    }

}

package org.alfresco.po.share.enums;

/**
 * Enum to contain all Site Visibility options.
 */
public enum SiteVisibility
{

    PUBLIC("Public"),
    MODERATED("Moderated"),
    PRIVATE("Private");

    private String displayValue;

    /**
     * Instantiates a new site visibility.
     * 
     * @param displayValue the display value
     */
    private SiteVisibility(String displayValue)
    {
        this.displayValue = displayValue;
    }

    /**
     * Gets the display value.
     * 
     * @return the display value
     */
    public String getDisplayValue()
    {
        return displayValue;
    }

    /**
     * Gets the enum.
     * 
     * @param displayValue the display value
     * @return the enum
     */
    public static SiteVisibility getEnum(String displayValue)
    {
        if (displayValue == null)
        {
            throw new IllegalArgumentException();
        }
        for (SiteVisibility val : values())
        {
            if (displayValue.equalsIgnoreCase(val.getDisplayValue()))
            {
                return val;
            }
        }
        throw new IllegalArgumentException();
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString()
    {
        return this.getDisplayValue();
    }
}
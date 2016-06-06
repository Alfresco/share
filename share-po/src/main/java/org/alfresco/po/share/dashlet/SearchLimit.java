package org.alfresco.po.share.dashlet;

/**
 * Enum to handle Search Limit drop down.
 * 
 * @author Ranjith Manyam
 * @since 1.9
 */
public enum SearchLimit
{
    TEN(10),
    TWENTY_FIVE(25),
    FIFTY(50),
    HUNDRED(100);

    private int value;

    SearchLimit(int value)
    {
        this.value = value;
    }

    public int getValue()
    {
        return value;
    }

    /**
     * Returns {@link SearchLimit} based on given value.
     * 
     * @param intValue int
     * @return {@link SearchLimit}
     */
    public static SearchLimit getSearchLimit(int intValue)
    {
        for (SearchLimit limit : SearchLimit.values())
        {
            if (intValue == limit.value)
            {
                return limit;
            }
        }
        throw new IllegalArgumentException("Invalid SearchLimit Value : " + intValue);
    }
}

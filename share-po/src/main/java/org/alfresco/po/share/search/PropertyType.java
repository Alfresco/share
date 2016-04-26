package org.alfresco.po.share.search;

/**
 *
 * @author Charu
 * @since  4.3
 */
public enum PropertyType
{
    MIMETYPE("MIME Type"),
    DESCRIPTION("Description"),
    CREATOR("Creator"),
    MODIFIER("Modifier"),
    CREATED("Created"),
    MODIFIED("Modified"),
    SIZE("Size");  
    
    
    
    private PropertyType(String propertyCode)
    {
        this.propertyCode = propertyCode;
    }

    private String propertyCode;

    /**
     * Gets the property code value as seen in
     * dropdown value attribute.
     * 
     * @return String value of mime type
     */
    public String getPropertyCode()
    {
        return propertyCode;
    }

}


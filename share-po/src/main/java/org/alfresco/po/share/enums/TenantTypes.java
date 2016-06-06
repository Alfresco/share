package org.alfresco.po.share.enums;

/**
 * @author nshah
 */
public enum TenantTypes
{
	Free         ("0"), 
    Standard     ("100"), 
    Premium      ("1000");

    private String typeValue;

    private TenantTypes(String type)
    {
        typeValue = type;
    }

    public String getTenantType()
    {
        return typeValue;
    }
}

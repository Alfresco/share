package org.alfresco.po.share.site.document;

import org.apache.commons.lang3.StringUtils;

public enum Categories
{
    CATEGORY_ROOT("Category Root"), LANGUAGES("Languages"), REGIONS("Regions"), SOFTWARE_DOCUMENT_CLASSIFICATION("Software Document Classification"),
    TAGS("Tags"), CATEGORY_TEST_1("TestCategory1"), CATEGORY_TEST_2("TestCategory2"), SUB_CATEGORY_TEST("SubCategory");

    private String value;

    private Categories(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return this.value;
    }

    /**
     * Find the {@link Categories} based on name.
     *
     * @param name - category name
     * @return {@link Category}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static Categories getCategory(String name)
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't be null or empty, It is required.");
        }
        for (Categories aspect : Categories.values())
        {
            if (aspect.value != null && aspect.value.equalsIgnoreCase(name.trim()))
            {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Not able to find the Category for given name : " + name);
    }

}

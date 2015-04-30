package org.alfresco.module.org_alfresco_module_wcmquickstart.model;

import junit.framework.TestCase;


public class AssetCollectionDefinitionTest extends TestCase
{
    public void testQueryParsing()
    {
        AssetCollectionDefinition def = new AssetCollectionDefinition();
        def.setQuery("Hello %{me}");
        assertEquals("Hello ${me}", def.getQuery());
    }
}

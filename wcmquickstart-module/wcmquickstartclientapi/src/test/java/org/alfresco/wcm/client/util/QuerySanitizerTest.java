package org.alfresco.wcm.client.util;

import junit.framework.TestCase;


public class QuerySanitizerTest extends TestCase
{
    public void testSanitizer()
    {
        assertEquals("lhfusohfjkb7489374_-", QuerySanitizer.sanitize("lhfusohfjkb7489374_-"));
        assertEquals("    ", QuerySanitizer.sanitize("\\/!\""));
        assertEquals("                           ", QuerySanitizer.sanitize("\"'%?*()$^<>/{}[]#~@.,|\\+!:;"));
    }
}

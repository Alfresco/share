package org.alfresco.wcm.client;

/**
 * Dictionary service test
 * 
 * @author Roy Wetherall
 */
public class DictionaryServiceTest extends BaseTest
{
    public void testDictionaryService()
    {
        assertTrue(dictionaryService.isDocumentSubType(DictionaryService.TYPE_CMIS_DOCUMENT));
        assertTrue(dictionaryService.isDocumentSubType(DictionaryService.FULL_TYPE_CMIS_DOCUMENT));
        assertFalse(dictionaryService.isDocumentSubType(DictionaryService.TYPE_CMIS_FOLDER));
        assertFalse(dictionaryService.isDocumentSubType(DictionaryService.FULL_TYPE_CMIS_FOLDER));
        assertTrue(dictionaryService.isDocumentSubType("ws:indexPage"));
        assertTrue(dictionaryService.isDocumentSubType("D:ws:indexPage"));
        assertFalse(dictionaryService.isDocumentSubType("ws:section"));
        assertFalse(dictionaryService.isDocumentSubType("F:ws:section"));
        assertFalse(dictionaryService.isDocumentSubType("jk:junk"));

        assertFalse(dictionaryService.isFolderSubType(DictionaryService.TYPE_CMIS_DOCUMENT));
        assertFalse(dictionaryService.isFolderSubType(DictionaryService.FULL_TYPE_CMIS_DOCUMENT));
        assertTrue(dictionaryService.isFolderSubType(DictionaryService.TYPE_CMIS_FOLDER));
        assertTrue(dictionaryService.isFolderSubType(DictionaryService.FULL_TYPE_CMIS_FOLDER));
        assertFalse(dictionaryService.isFolderSubType("ws:indexPage"));
        assertFalse(dictionaryService.isFolderSubType("D:ws:indexPage"));
        assertTrue(dictionaryService.isFolderSubType("ws:section"));
        assertTrue(dictionaryService.isFolderSubType("F:ws:section"));
        assertFalse(dictionaryService.isFolderSubType("jk:junk"));

        assertEquals("D:cmis:document", dictionaryService.getParentType("ws:indexPage"));
        assertEquals("D:cmis:document", dictionaryService.getParentType("D:ws:indexPage"));
        assertEquals("cmis:document", dictionaryService.getParentType("ws:indexPage", true));
        assertEquals("cmis:document", dictionaryService.getParentType("D:ws:indexPage", true));
    }
}

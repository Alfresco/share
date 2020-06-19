/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
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

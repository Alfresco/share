/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

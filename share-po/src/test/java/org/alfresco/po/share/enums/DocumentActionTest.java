/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.enums;



import static org.testng.Assert.assertEquals;

import org.alfresco.po.share.site.document.DocumentAction;
import org.alfresco.po.share.site.document.DocumentAction.DetailsPageType;
import org.testng.annotations.Test;

/**
 * @author nshah
 *
 */
@Test(groups="unit")
public class DocumentActionTest
{
   
    
    @Test
    public void getCommonLinks()
    {
        
        assertEquals(DocumentAction.COPY_TO.getType(), DocumentAction.DetailsPageType.COMMON);
        assertEquals(DocumentAction.COPY_TO.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionCopyTo']");
        assertEquals(DocumentAction.MOVE_TO.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionMoveTo']");
        assertEquals(DocumentAction.DELETE_CONTENT.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionDelete']");
        assertEquals(DocumentAction.MANAGE_ASPECTS.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionManageAspects']");        
        assertEquals(DocumentAction.CHANGE_TYPE.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionChangeType']");
        assertEquals(DocumentAction.EDIT_PROPERTIES.getDocumentAction(DetailsPageType.COMMON), ".document-edit-metadata");
         
       
    }
    
    @Test
    public void getFolderLinks()
    {
        assertEquals(DocumentAction.MANAGE_RULES.getType(), DocumentAction.DetailsPageType.FOLDER);
        assertEquals(DocumentAction.MANAGE_RULES.getDocumentAction(DetailsPageType.FOLDER), ".folder-manage-rules");
        assertEquals(DocumentAction.DOWNLOAD_FOLDER.getDocumentAction(DetailsPageType.FOLDER), "div[id$='onActionFolderDownload']");
        assertEquals(DocumentAction.VIEW_IN_EXPLORER.getDocumentAction(DetailsPageType.FOLDER), ".view-in-explorer");
    }
    
    @Test
    public void getDocumentLinks()
    {
        assertEquals(DocumentAction.DOWNLOAD_DOCUMENT.getType(), DocumentAction.DetailsPageType.DOCUMENT);
        assertEquals(DocumentAction.DOWNLOAD_DOCUMENT.getDocumentAction(DetailsPageType.DOCUMENT), ".document-download");
        assertEquals(DocumentAction.VIEW_IN_EXLPORER.getDocumentAction(DetailsPageType.DOCUMENT), ".document-view-content");
        assertEquals(DocumentAction.UPLOAD_DOCUMENT.getDocumentAction(DetailsPageType.DOCUMENT), "div[id$='onActionUploadNewVersion']");
        assertEquals(DocumentAction.DOCUMENT_INLINE_EDIT.getDocumentAction(DetailsPageType.DOCUMENT), ".document-inline-edit");       
        assertEquals(DocumentAction.EDIT_OFFLINE.getDocumentAction(DetailsPageType.DOCUMENT), "div[id$='onActionEditOffline']");
        assertEquals(DocumentAction.GOOGLE_DOCS_EDIT.getDocumentAction(DetailsPageType.DOCUMENT), "div[id$='onGoogledocsActionEdit']");
        assertEquals(DocumentAction.START_WORKFLOW.getDocumentAction(DetailsPageType.DOCUMENT), "div[id$='onActionAssignWorkflow']");
        assertEquals(DocumentAction.PUBLISH_ACTION.getDocumentAction(DetailsPageType.DOCUMENT), "div[id$='onActionPublish']");
    }
}

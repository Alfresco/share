


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
        assertEquals(DocumentAction.CHNAGE_TYPE.getDocumentAction(DetailsPageType.COMMON), "div[id$='onActionChangeType']");
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

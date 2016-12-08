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
package org.alfresco.po.share.site.document;

/**
 * This class contains the CSS details of actions in details page of Folder,
 * Document.
 *
 * @author nshah
 */
public enum DocumentAction
{

    COPY_TO("div[id$='onActionCopyTo']", DetailsPageType.COMMON),
    MOVE_TO("div[id$='onActionMoveTo']", DetailsPageType.COMMON),
    DELETE_CONTENT("div[id$='onActionDelete']", DetailsPageType.COMMON),
    MANAGE_ASPECTS("div[id$='onActionManageAspects']", DetailsPageType.COMMON), //
    MANAGE_PERMISSION_DOC(".document-manage-granular-permissions", DetailsPageType.DOCUMENT),
    MANAGE_PERMISSION_FOL(".document-manage-granular-permissions", DetailsPageType.FOLDER),
    MANAGE_PERMISSION_REPO(".document-manage-repo-permissions", DetailsPageType.FOLDER),
    CHANGE_TYPE("div[id$='onActionChangeType']", DetailsPageType.COMMON),
    EDIT_PROPERTIES(".document-edit-metadata", DetailsPageType.COMMON),
    MANAGE_RULES(".folder-manage-rules", DetailsPageType.FOLDER),
    DOWNLOAD_FOLDER("div[id$='onActionFolderDownload']", DetailsPageType.FOLDER),
    VIEW_IN_EXPLORER(".view-in-explorer", DetailsPageType.FOLDER),
    DOWNLOAD_DOCUMENT(".document-download", DetailsPageType.DOCUMENT),
    VIEW_IN_EXLPORER(".document-view-content", DetailsPageType.DOCUMENT),
    UPLOAD_DOCUMENT("div[id$='onActionUploadNewVersion']", DetailsPageType.DOCUMENT),
    DOCUMENT_INLINE_EDIT(".document-inline-edit", DetailsPageType.DOCUMENT),
    EDIT_OFFLINE("div[id$='onActionEditOffline']", DetailsPageType.DOCUMENT),
    GOOGLE_DOCS_EDIT("div[id$='onGoogledocsActionEdit']", DetailsPageType.DOCUMENT),
    START_WORKFLOW("div[id$='onActionAssignWorkflow']", DetailsPageType.DOCUMENT),
    PUBLISH_ACTION("div[id$='onActionPublish']", DetailsPageType.DOCUMENT);

    public enum DetailsPageType
    {
        COMMON, DOCUMENT, FOLDER
    }

    ;

    private String linkValue;
    private DetailsPageType typeValue;

    private DocumentAction(String link, DetailsPageType type)
    {
        linkValue = link;
        typeValue = type;
    }

    /**
     * Get value of CSS from the page type.
     *
     * @param type DetailsPageType
     * @return String
     */
    public String getDocumentAction(DetailsPageType type)
    {
        if (this.typeValue.equals(DetailsPageType.COMMON) || typeValue.equals(type))
        {
            return linkValue;
        }
        else
        {
            // Checks for null as well.
            throw new UnsupportedOperationException("Wrong type of details page.");
        }
    }

    /**
     * Check the CSS present in page type.
     *
     * @return DetailsPageType
     */
    public DetailsPageType getType()
    {
        return typeValue;
    }

    public String getCssValue()
    {
        return linkValue;
    }

}

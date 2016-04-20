
package org.alfresco.po.share.site.document;


/**
 * This class contains the CSS details of options in Selected Items drop-down menu
 * 
 * @author Maryia Zaichanka
 */
public enum SelectedItemsOptions
{
    DELETE (".onActionDelete"),
    COPY_TO (".onActionCopyTo"),
    MOVE_TO (".onActionMoveTo"),
    DESELECT_ALL (".onActionDeselectAll"),
    START_WORKFLOW (".onActionAssignWorkflow"),
    DOWNLOAD_AS_ZIP (".onActionDownload");

    private String linkValue;

    private SelectedItemsOptions(String link)
    {
        linkValue = link;
    }

    /**
     * Get value of CSS from the page type.
     * 
     * @return String
     */
    public String getOption()
    {
        return linkValue;
    }
}

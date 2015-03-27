/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
     * @return
     */
    public String getOption()
    {
        return linkValue;
    }
}

/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site.document;

import org.alfresco.po.share.site.UpdateFilePage;
import org.alfresco.webdrone.WebDrone;

/**
 * When the user clicks on revert to version they will be provided with revert to version page.
 * This function extends UpdateFilePage and use the functionality of the page.
 * 
 * @author Ranjith Manyam
 * @since 1.7
 */

public class RevertToVersionPage extends UpdateFilePage
{
    /**
     * Constructor.
     */
    public RevertToVersionPage(WebDrone drone, String documentVersion, boolean editOffline)
    {
        super(drone, documentVersion, editOffline);
        setMinorVersionRadioButton("input[id$='minorVersion-radioButton']");
        setMajorVersionRadioButton("input[id$='majorVersion-radioButton']");
        setSubmitButton("button#alfresco-revertVersion-instance-ok-button-button");
        setCancelButton("button#alfresco-revertVersion-instance-cancel-button-button");
        setTextAreaCssLocation("textarea[id$='-description-textarea']");
    }
}

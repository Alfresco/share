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
package org.alfresco.web.resolver.doclib;

import org.json.simple.JSONObject;

/**
 * Resolves which action group if to use in the document library's document list.
 *
 * @author ewinlof
 */
public class DefaultDoclistActionGroupResolver implements DoclistActionGroupResolver
{
    /**
     * Will return the action group id matching action group configs in a, i.e. share-config-custom.xml file.
     *
     * @param jsonObject An item (i.e. document or folder) in the doclist.
     * @param view Name of the type of view in which the action will be displayed. I.e. "details"
     * @return The action group id to use for displaying actions
     */
    public String resolve(JSONObject jsonObject, String view)
    {
        String actionGroupId;
        JSONObject node = (JSONObject) jsonObject.get("node");
        boolean isContainer = (Boolean) node.get("isContainer");
        if (isContainer)
        {
            actionGroupId = "folder-";
        }
        else
        {
            actionGroupId = "document-";
        }
        boolean isLink = (Boolean) node.get("isLink");
        if (isLink)
        {
            actionGroupId += "link-" ;
        }
        if (view.equals("details"))
        {
            actionGroupId += "details";
        }
        else
        {
            actionGroupId += "browse";
        }
        return actionGroupId;
    }
}

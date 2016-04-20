package org.alfresco.web.resolver.doclib;

import org.json.simple.JSONObject;

/**
 * Resolves which action group if to use in the document library's document list.
 *
 * @author ewinlof
 */
public interface DoclistActionGroupResolver
{
    /**
     * Will return the action group id matching action group configs in a, i.e. share-config-custom.xml file.
     *
     * @param jsonObject An item (i.e. document or folder) in the doclist.
     * @param view Name of the type of view in which the action will be displayed. I.e. "details"
     * @return The action group id to use for displaying actions
     */
    public String resolve(JSONObject jsonObject, String view);
}

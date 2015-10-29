/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf.types;

import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;

/**
 * Interface for a ContentAssociation object type
 * 
 * @author muzquiano
 */
public interface ContentAssociation extends ModelObject
{
    // type
    public static String TYPE_ID = "content-association";
    
    // properties
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_SOURCE_TYPE = "source-type";
    public static String PROP_DEST_ID = "dest-id";
    public static String PROP_ASSOC_TYPE = "assoc-type";
    public static String PROP_FORMAT_ID = "format-id";    
    
    /**
     * Gets the source id.
     * 
     * @return the source id
     */
    public String getSourceId();

    /**
     * Sets the source id.
     * 
     * @param sourceId the new source id
     */
    public void setSourceId(String sourceId);

    /**
     * Gets the dest id.
     * 
     * @return the dest id
     */
    public String getDestId();

    /**
     * Sets the dest id.
     * 
     * @param destId the new dest id
     */
    public void setDestId(String destId);

    /**
     * Gets the association type.
     * 
     * @return the association type
     */
    public String getAssociationType();

    /**
     * Sets the source type.
     * 
     * @param sourceType the source type
     */
    public void setSourceType(String sourceType);

    /**
     * Gets the source type.
     * 
     * @return the source type
     */
    public String getSourceType();

    /**
     * Sets the association type.
     * 
     * @param associationType the new association type
     */
    public void setAssociationType(String associationType);

    /**
     * Gets the format id.
     * 
     * @return the format id
     */
    public String getFormatId();

    /**
     * Sets the format id.
     * 
     * @param formatId the new format id
     */
    public void setFormatId(String formatId);

    // Helpers

    /**
     * Gets the object that is being associated.
     * This is generally a template or a page.
     * 
     * @param context the context
     * 
     * @return the page
     */
    public ModelObject getObject(RequestContext context);
    
    /**
     * Checks if is template association.
     * 
     * @return true, if is template association
     */
    public boolean isTemplateAssociation();
    
    /**
     * Checks if is page association.
     * 
     * @return true, if is page association
     */
    public boolean isPageAssociation();
    
}

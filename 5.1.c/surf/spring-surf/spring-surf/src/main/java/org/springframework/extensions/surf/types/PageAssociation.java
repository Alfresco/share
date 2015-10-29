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
 * Interface for a PageAssociation object type
 * 
 * @author muzquiano
 */
public interface PageAssociation extends ModelObject
{
    // type
    public static String TYPE_ID = "page-association";
    
    // properties
    public static String PROP_SOURCE_ID = "source-id";
    public static String PROP_DEST_ID = "dest-id";
    public static String PROP_ASSOC_TYPE = "assoc-type";
    public static String PROP_ORDER_ID = "order-id";

    // values
    public static String CHILD_ASSOCIATION_TYPE_ID = "child";

    /* (non-Javadoc)
     * @see org.alfresco.web.framework.ModelObject#getTypeId()
     */
    public String getTypeId(); 

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
     * Sets the association type.
     * 
     * @param associationType the new association type
     */
    public void setAssociationType(String associationType);

    /**
     * Gets the order id.
     * 
     * @return the order id
     */
    public String getOrderId();

    /**
     * Sets the order id.
     * 
     * @param orderId the new order id
     */
    public void setOrderId(String orderId);

    // Helpers

    /**
     * Gets the source page.
     * 
     * @param context the context
     * 
     * @return the source page
     */
    public Page getSourcePage(RequestContext context);

    /**
     * Gets the dest object.
     * 
     * @param context the context
     * 
     * @return the dest object
     */
    public Page getDestPage(RequestContext context);
    
}

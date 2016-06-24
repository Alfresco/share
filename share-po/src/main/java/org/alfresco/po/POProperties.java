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
package org.alfresco.po;
/**
 * WebDriver properties interface, every page object library
 * that uses {@link WebDriver} get element by id will require to
 * load a properties file with all the ids from the page object.
 * 
 * 
 * @author Michael Suzuki
 * @since 1.9
 *
 */
public interface POProperties
{
    /**
     * Gets the HTML element id value for the given key
     * from the loaded properties file.
     * 
     * @param key String HTML element id
     * @return String value of key
     */
    String getElement(final String key);
    /**
     * Get the product version.
     * Alfresco Share 4.2 will return Enterprise4.2 enum
     * @param <T> version type
     * @return {@link Version} version 
     */
    <T extends Version> T getVersion();
}

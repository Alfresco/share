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
package org.alfresco.po.exception;
/**
 * Alfresco page operation exception thrown when an operation fail to execute
 * due page elements not being found.
 * 
 * @author Michael Suzuki
 * @version 1.7.0
 *
 */
public class PageOperationException extends RuntimeException 
{
    private static final long serialVersionUID = -3035419828674599203L;
    private static final String DEFAULT_MESSAGE = "The operation failed to complete";
    
    public PageOperationException(String reason)
    {
        super(reason);
    }

    public PageOperationException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public PageOperationException()
    {
        super(DEFAULT_MESSAGE);
    }
}

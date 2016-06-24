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
package org.alfresco.po.share.exception;

/**
 * Thrown when an Unexpected Share Page is presented and the Share Action can not be performed
 * @author mbhave
 */
public class UnexpectedSharePageException extends RuntimeException
{
    private static final long serialVersionUID = -5559559799354579197L;
    private static final String DEFAULT_MESSAGE = "User not on the appropriate Share page for this Action.";

    public UnexpectedSharePageException(String reason)
    {
        super(reason);
    }

    public UnexpectedSharePageException(String reason, Throwable cause)
    {
        super(reason, cause);
    }
    
    public UnexpectedSharePageException(Object expectedSharePageName, Throwable cause)
    {
        super(String.format("%s Expected Page: %s", DEFAULT_MESSAGE, expectedSharePageName.toString()), cause);
    }

    public UnexpectedSharePageException()
    {
        super(DEFAULT_MESSAGE);
    }

}

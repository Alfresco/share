/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.exception;

/**
 * Thrown when handling the Share Error Message (Popup) by {@link ShareErrorPopUp}.
 */
public class ShareException extends RuntimeException
{
    private static final long serialVersionUID = 850985590207217016L;
    private static final String DEFAULT_MESSAGE = "Share Error: Failed to create entity, Entity already exists";

    public ShareException(String reason)
    {
        super(reason);
    }

    public ShareException(String reason, Throwable cause)
    {
        super(reason, cause);
    }

    public ShareException()
    {
        super(DEFAULT_MESSAGE);
    }

}

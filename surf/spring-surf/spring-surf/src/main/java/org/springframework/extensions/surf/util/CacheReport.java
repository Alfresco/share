/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.springframework.extensions.surf.util;

/**
 * Structure of a single Cache Report item for @see CacheReporter implementations to return. 
 * 
 * @author Kevin Roast
 */
public class CacheReport
{
    private final String name;
    private final int count;
    private final long size;
    
    public CacheReport(final String cacheName, final int entryCount, final long valueSizeEstimate)
    {
        this.name = cacheName;
        this.count = entryCount;
        this.size = valueSizeEstimate;
    }
    
    public String getCacheName() {return this.name;}
    public int getEntryCount() {return this.count;}
    public long getValueSizeEstimate() {return this.size;}
}

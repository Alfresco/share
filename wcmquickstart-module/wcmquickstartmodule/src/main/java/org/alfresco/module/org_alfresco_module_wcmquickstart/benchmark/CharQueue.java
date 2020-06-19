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
package org.alfresco.module.org_alfresco_module_wcmquickstart.benchmark;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is based on the work done by Lawrence Kesteloot. 
 * 
 * Keeps a fixed-length queue of characters.  There are only three
 * operations on the queue: set the whole thing; append a character
 * (dropping the first); and retreive the whole thing.  This is useful
 * as a moving window on a text stream.
 */
public class CharQueue 
{
    private static Log logger = LogFactory.getLog(CharQueue.class);
    
    private int length;
    private char[] queue;
    private int count;

    /**
     * Create the queue with a fixed length.  The queue will be
     * filled with the value 0, so don't use the toString()
     * method until the queue has been filled with either
     * set() or put().
     */
    public CharQueue(int length) 
    {
        this.length = length;
        queue = new char[length];
        count = 0;
    }

    /**
     * Sets the contents of the queue.  The length of the string
     * must be the same as the length passed to the constructor.
     */
    public void set(String s) 
    {
        if (s.length() != length) 
        {
            logger.error("Lengths don't match");
            return;
        }

        queue = s.toCharArray();
        count = length;
    }

    /**
     * Appends the character to the queue.  If the resulting queue
     * would be longer than the length set in the constructor, then
     * the first character is dropped.
     * 
     * @param c
     */
    public void put(char c) 
    {
        if (count == length) 
        {
            System.arraycopy(queue, 1, queue, 0, length - 1);
            count--;
        }

        queue[count++] = c;
    }

    /**
     * Returns the contents of the queue as a string.  This does
     * not take into account the number of characters that have
     * been put into the queue.  The returned string's length
     * is always the length passed to the constructor.
     */
    public String toString() 
    {
        return new String(queue);
    }
}


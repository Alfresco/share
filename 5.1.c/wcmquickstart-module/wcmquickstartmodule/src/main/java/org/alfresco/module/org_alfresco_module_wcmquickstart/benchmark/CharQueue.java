/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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


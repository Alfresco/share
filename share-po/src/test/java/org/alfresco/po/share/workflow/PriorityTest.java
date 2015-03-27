/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.workflow;

import static org.alfresco.po.share.workflow.Priority.HIGH;
import static org.alfresco.po.share.workflow.Priority.LOW;
import static org.alfresco.po.share.workflow.Priority.MEDIUM;
import static org.alfresco.po.share.workflow.Priority.getPriority;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class PriorityTest
{

    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getPriorityWithNull()
    {
        getPriority(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getPriorityWithEmpty()
    {
        getPriority("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid Priority Value : Alfresco")
    public void getPriorityWithAlfresco()
    {
        getPriority("Alfresco");
    }
    
    @Test
    public void getKeepContentStrategyTest()
    {
        assertEquals(getPriority("High"), HIGH);
        assertEquals(getPriority("Low"), LOW);
        assertEquals(getPriority("Medium"), MEDIUM);
    }
    
    @Test
    public void getPriorityTest()
    {
        assertEquals(HIGH.getPriority(), "High");
        assertEquals(LOW.getPriority(), "Low");
        assertEquals(MEDIUM.getPriority(), "Medium");
    }
    
    @Test
    public void getValue()
    {
        assertEquals(HIGH.getValue(), "1");
        assertEquals(LOW.getValue(), "3");
        assertEquals(MEDIUM.getValue(), "2");
    }
}

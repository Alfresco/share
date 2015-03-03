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

import static org.alfresco.po.share.workflow.SendEMailNotifications.NO;
import static org.alfresco.po.share.workflow.SendEMailNotifications.YES;
import static org.alfresco.po.share.workflow.SendEMailNotifications.getValue;
import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

public class SendEMailNotificationsTest
{
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getValueWithNull()
    {
        getValue(null);
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Value can't be empty or null.")
    public void getValueWithEmpty()
    {
        getValue("");
    }
    
    @Test(expectedExceptions=IllegalArgumentException.class, expectedExceptionsMessageRegExp="Invalid SendEMailNotifications Value : Alfresco")
    public void getPriorityWithAlfresco()
    {
        getValue("Alfresco");
    }
    
    @Test
    public void getValuesTest()
    {
        assertEquals(getValue("Yes"), YES);
        assertEquals(getValue("No"), NO);
    }
    
    @Test
    public void getValueTest()
    {
        assertEquals(YES.getValue(), "Yes");
        assertEquals(NO.getValue(), "No");
    }

}

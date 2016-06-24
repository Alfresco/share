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
package org.alfresco.po.share.task;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Purpose of this test is to test the <code>TaskStatus</code> Enum.
 * 
 * @author Abhijeet Bharade
 * @since v1.6.2
 */
public class TaskStatusTest
{

    @Test(groups="unit")
    public void getTaskFromStringTest()
    {
        Assert.assertEquals(TaskStatus.getTaskFromString("Not Yet Started"), TaskStatus.NOTYETSTARTED, "Expected a NOTYETSTARTED status");
        Assert.assertEquals(TaskStatus.getTaskFromString("Not Yet Started123"), null, "Expected a null status");
    }

    @Test(groups="unit")
    public void getTaskNameTest()
    {
        Assert.assertEquals(TaskStatus.NOTYETSTARTED.getTaskName(), "Not Yet Started", "Expected a NOTYETSTARTED status");
    }
}

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
 * Purpose of this test is to test the <code>TaskDetails</code> Class.
 * 
 * @author Ranjith Manyam
 */
public class TaskDetailsTest
{

    @Test(groups="unit", enabled = true)
    public void testSetDueDateString()
    {
        TaskDetails td = new TaskDetails();
        td.setDue("(None)");
        Assert.assertEquals(td.getDue(), "(None)");
        td.setDue("06 February, 2014");
        Assert.assertEquals(td.getDue(), "06 February, 2014");
    }
}

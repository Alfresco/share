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

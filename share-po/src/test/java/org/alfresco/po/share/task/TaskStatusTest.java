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

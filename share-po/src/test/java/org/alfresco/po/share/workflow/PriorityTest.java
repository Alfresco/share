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

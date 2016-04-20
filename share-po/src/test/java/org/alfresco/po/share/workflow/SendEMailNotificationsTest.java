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

package org.alfresco.po.share.workflow;


import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;

/**
 * This enum holda the task type details needed for workflow form P
 * 
 * @author Siva Kaliyappan
 * @since 1.6.2
 */
public enum TaskType
{

    SIMPLE_CLOUD_TASK(By.cssSelector("option[value='task']"), "Simple Cloud Task"),
    CLOUD_REVIEW_TASK(By.cssSelector("option[value='review']"), "Cloud Review Task");

    public By getSelector()
    {
        return selector;
    }

    private By selector;
    private String type;

    TaskType(By selector, String type)
    {
        this.selector = selector;
        this.type = type;
    }

    public String getType()
    {
        return type;
    }

    /**
     * Returns {@link TaskType} based on given value.
     * 
     * @param value String
     * @return {@link TaskType}
     */
    public static TaskType getTaskType(String value)
    {
        if (StringUtils.isEmpty(value))
        {
            throw new IllegalArgumentException("Value can't be empty or null.");
        }
        for (TaskType type : TaskType.values())
        {
            if (value.equals(type.type))
            {
                return type;
            }
        }
        throw new IllegalArgumentException("Invalid Tasktype Value: " + value);
    }
}

package org.alfresco.po.share.adminconsole.replicationjobs;

/**
 * enum that holds jobs 'repeat every' values
 *
 * @author Marina.Nenadovets
 */
public enum RepeatEveryValue
{
    SECOND("Second(s)", "Second"),
    MINUTE("Minute(s)", "Minute"),
    HOUR("Hour(s)", "Hour"),
    DAY("Day(s)", "Day"),
    WEEK("Week(s)", "Week"),
    MONTH("Month(s)", "Month");

    public final String value;
    public final String name;

    RepeatEveryValue(String value, String name)
    {
        this.value = value;
        this.name = name;
    }
}

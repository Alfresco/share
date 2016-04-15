package org.alfresco.web.evaluator;

/**
 * @author mikeh
 */
public class NullValueComparator implements Comparator
{
    private String value = null;

    /**
     * Setter for static string value to compare to
     *
     * @param value
     */
    public void setValue(String value)
    {
        this.value = value;
    }

    @Override
    public boolean compare(Object nodeValue)
    {
        boolean match = this.value.equalsIgnoreCase("true");
        return match == (nodeValue == null);
    }
}

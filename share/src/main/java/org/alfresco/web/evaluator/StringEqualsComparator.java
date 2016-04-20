package org.alfresco.web.evaluator;

/**
 * Compares a node value against an optionally case-insensitive value
 *
 * @author mikeh
 */
public class StringEqualsComparator implements Comparator
{
    private Boolean caseInsensitive = true;
    private String value = null;

    /**
     * Setter for case insensitive comparison override
     *
     * @param caseInsensitive
     */
    public void setCaseInsensitive(Boolean caseInsensitive)
    {
        this.caseInsensitive = caseInsensitive;
    }

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
        if (nodeValue == null)
        {
            return false;
        }
        
        if (caseInsensitive)
        {
            return nodeValue.toString().equalsIgnoreCase(this.value);
        }
        return nodeValue.toString().equals(this.value);
    }
}

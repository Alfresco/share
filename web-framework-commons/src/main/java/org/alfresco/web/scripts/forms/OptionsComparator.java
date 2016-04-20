package org.alfresco.web.scripts.forms;

import java.util.Comparator;

public class OptionsComparator implements Comparator<String>
{
    /**
     * <p>The delimiter that marks the beginning of the label.</p>
     */
    public static final String delimiter = "|";
    
    /**
     * <p>Uses the default <code>compareTo</code> method defined by the {@link Comparable} interface
     * of {@link String} to compare labels returned from calls to <code>getLabel</code>.
     */
    @Override
    public int compare(String o1, String o2)
    {
        return getLabel(o1).compareTo(getLabel(o2));
    }

    /**
     * <p>Get the label from the supplied String. The label begins after the delimiter. The 
     * label is used as the basis for a natural sort. It is converted into lower case as a 
     * natural sort will place ALL upper-case characters before lower-case ones. If the 
     * delimiter is not found then the supplied string is returned.</p>
     * 
     * @param s The String to find the label within.
     * @return The label within the string converted to lower case or the supplied string if
     * the delimiter cannot be found.
     */
    protected String getLabel(String s)
    {
        String label = null;
        int delimiterIndex = s.indexOf(delimiter);
        if (delimiterIndex != -1)
        {
            label = s.substring(delimiterIndex);
        }
        else
        {
            label = s;
        }
        return label.toLowerCase();
    }
    
}

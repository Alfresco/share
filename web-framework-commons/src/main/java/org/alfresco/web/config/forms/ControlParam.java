
package org.alfresco.web.config.forms;
 
/**
 * This class represents a single control-param configuration item.
 * 
 * @author Neil McErlean.
 */
public class ControlParam
{
    private final String name;
    private String value;

    /**
     * Constructs a ControlParam object with the specified name and value.
     * 
     * @param name the name of the param.
     * @param value the value associated with that name.
     */
    public ControlParam(String name, String value)
    {
    	if (value == null)
    	{
    		value = "";
    	}
        this.name = name;
        this.value = value;
    }

    /**
     * Gets the name of this ControlParam.
     * @return the param name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the value of this ControlParam.
     * @return the value.
     */
    public String getValue()
    {
        return value;
    }
    
    /* default */ void setValue(String newValue)
    {
    	this.value = newValue;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString()
    {
        StringBuilder result = new StringBuilder();
        result.append(name).append(":").append(value);
        return result.toString();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode()
    {
        return name.hashCode() + 7 * value.hashCode();
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object otherObj)
    {
        if (otherObj == this)
        {
            return true;
        }
        else if (otherObj == null
                || !otherObj.getClass().equals(this.getClass()))
        {
            return false;
        }
        ControlParam otherCP = (ControlParam) otherObj;
        return otherCP.name.equals(this.name)
                && otherCP.value.equals(this.value);
    }
}
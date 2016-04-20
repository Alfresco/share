package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.List;

import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.element.ConfigElementAdapter;

/**
 * Custom config element that represents &lt;dependencies&gt; values for the
 * client.
 * 
 * @author Neil McErlean.
 */
public class DependenciesConfigElement extends ConfigElementAdapter
{
    private static final long serialVersionUID = -8573715101320883067L;

    public static final String CONFIG_ELEMENT_ID = "dependencies";
    private final List<String> cssDependencies = new ArrayList<String>();
    private final List<String> jsDependencies = new ArrayList<String>();

    /**
     * This constructor creates an instance with the default name.
     */
    public DependenciesConfigElement()
    {
        super(CONFIG_ELEMENT_ID);
    }

    /**
     * This constructor creates an instance with the specified name.
     * 
     * @param name the name for the ConfigElement.
     */
    public DependenciesConfigElement(String name)
    {
        super(name);
    }
    
    /**
     * This method returns the css dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return String[]
     */
    public String[] getCss()
    {
        if (this.cssDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.cssDependencies.toArray(new String[0]);
        }
    }
    
    /**
     * This method returns the JavaScript dependencies as an array of Strings containing
     * the values of the 'src' attribute. If there are no dependencies, <code>null</code>
     * is returned.
     * 
     * @return String[]
     */
    public String[] getJs()
    {
        if (this.jsDependencies.isEmpty())
        {
            return null;
        }
        else
        {
            return this.jsDependencies.toArray(new String[0]);
        }
    }

    /**
     * @see ConfigElement#getChildren()
     */
    @Override
    public List<ConfigElement> getChildren()
    {
        throw new ConfigException(
                "Reading the default-controls config via the generic interfaces is not supported");
    }

    /**
     * @see ConfigElement#combine(org.springframework.extensions.config.ConfigElement)
     */
    @Override
    public ConfigElement combine(ConfigElement configElement)
    {
        if (configElement == null)
        {
            return this;
        }

        DependenciesConfigElement otherDepsElement = (DependenciesConfigElement) configElement;
        DependenciesConfigElement result = new DependenciesConfigElement();

        // combine all the dependencies
        if (this.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(this.cssDependencies);
        }
        
        if (otherDepsElement.cssDependencies.isEmpty() == false)
        {
            result.addCssDependencies(otherDepsElement.cssDependencies);
        }

        if (this.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(this.jsDependencies);
        }
        
        if (otherDepsElement.jsDependencies.isEmpty() == false)
        {
            result.addJsDependencies(otherDepsElement.jsDependencies);
        }

        return result;
    }

    void addCssDependencies(List<String> cssDeps)
    {
        if (cssDeps == null)
        {
            return;
        }
        this.cssDependencies.addAll(cssDeps);
    }

    void addJsDependencies(List<String> jsDeps)
    {
        if (jsDeps == null)
        {
            return;
        }
        this.jsDependencies.addAll(jsDeps);
    }
}

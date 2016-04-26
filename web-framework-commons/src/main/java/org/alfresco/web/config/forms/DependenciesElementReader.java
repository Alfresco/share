package org.alfresco.web.config.forms;

import java.util.ArrayList;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.ConfigException;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * This class is a custom element reader to parse the config file for
 * &lt;dependencies&gt; elements.
 * 
 * @author Neil McErlean.
 */
public class DependenciesElementReader implements ConfigElementReader
{
    public static final String ELEMENT_DEPENDENCIES = "dependencies";

    /**
     * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
     */
    public ConfigElement parse(Element dependenciesElem)
    {
        DependenciesConfigElement result = null;
        if (dependenciesElem == null)
        {
            return null;
        }

        String name = dependenciesElem.getName();
        if (!name.equals(ELEMENT_DEPENDENCIES))
        {
            throw new ConfigException(this.getClass().getName()
                    + " can only parse " + ELEMENT_DEPENDENCIES
                    + " elements, the element passed was '" + name + "'");
        }

        result = new DependenciesConfigElement();

        List<String> cssDependencies = getSrcDependencies(dependenciesElem, "./css");
        List<String> jsDependencies = getSrcDependencies(dependenciesElem, "./js");

        result.addCssDependencies(cssDependencies);
        result.addJsDependencies(jsDependencies);

        return result;
    }

    /**
     * This method takes the specified xml node, finds children matching the specified
     * xpath expression and returns a List<String> containing the values of the "src"
     * attribute on each of those child nodes.
     * 
     * @param typeNode Element
     * @param xpathExpression String
     * @return List<String>
     */
    @SuppressWarnings("unchecked")
    private List<String> getSrcDependencies(Element typeNode, final String xpathExpression)
    {
        List<String> result = new ArrayList<String>();
        
        for (Object cssObj : typeNode.selectNodes(xpathExpression))
        {
            Element cssElem = (Element)cssObj;
            List<Attribute> cssAttributes = cssElem.selectNodes("./@*");
            for (Attribute nextAttr : cssAttributes)
            {
                String nextAttrName = nextAttr.getName();
                if (nextAttrName.equals("src"))
                {
                    String nextAttrValue = nextAttr.getValue();
                    result.add(nextAttrValue);
                }
                // Ignore attributes not called "src".
            }
        }
        
        return result;
    }
}

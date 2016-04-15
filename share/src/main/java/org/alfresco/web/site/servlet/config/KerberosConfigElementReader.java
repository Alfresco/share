package org.alfresco.web.site.servlet.config;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * Responsible for loading Kerberos configuration settings from the share-config*.xml files that are loaded via the
 * configuration service.
 * 
 * @author dward
 */
public class KerberosConfigElementReader implements ConfigElementReader
{
    public ConfigElement parse(Element elem)
    {
        ConfigElement configElement = null;
        if (elem != null)
        {
            configElement = KerberosConfigElement.newInstance(elem);
        }
        return configElement;
    }
}

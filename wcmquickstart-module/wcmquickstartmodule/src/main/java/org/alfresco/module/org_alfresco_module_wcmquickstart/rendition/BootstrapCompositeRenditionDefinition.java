package org.alfresco.module.org_alfresco_module_wcmquickstart.rendition;

import java.util.List;

/**
 * @author Roy Wetherall
 */
public class BootstrapCompositeRenditionDefinition extends BootstrapRenditionDefinition
{
	private List<BootstrapRenditionDefinition> definitions;
	
	public void setDefinitions(List<BootstrapRenditionDefinition> defintions)
    {
	    this.definitions = defintions;
    }
	
	public List<BootstrapRenditionDefinition> getDefinitions()
    {
	    return definitions;
    }
}

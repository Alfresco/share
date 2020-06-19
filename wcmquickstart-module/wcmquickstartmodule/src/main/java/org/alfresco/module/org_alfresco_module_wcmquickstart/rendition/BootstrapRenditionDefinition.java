/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.rendition;

import java.io.Serializable;
import java.util.Map;
import java.util.TreeMap;

import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.rendition.executer.ReformatRenderingEngine;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.GUID;

/**
 * Bootstrap rendition definition.
 * 
 * @author Roy Wetherall
 */
public class BootstrapRenditionDefinition implements WebSiteModel
{
	/** Rendition definition name */
	private String name;
	
	/** Rendering engine name */
	private String renderingEngineName = ReformatRenderingEngine.NAME;
	
	/** Execute rendering asynchronously */
	private Boolean executeAsynchronously = Boolean.FALSE;
	
	/** Rendering parameters */
	private Map<String, Serializable> parameters = new TreeMap<String, Serializable>();

	/**
	 * Get the QName rendition definition name.  Defaults the namespace to ws.
	 * @return	QName	rendition definition QName
	 */
	public QName getQName()
	{
		return QName.createQName(NAMESPACE, getName());
	}
	
	/**
	 * Get the name of the rendition definition.
	 * @return	String	rendition definition name
	 */
	public String getName()
    {
		// generate a name if none specified (usually used when creating
		// a composite rendition)
		if (name == null)
		{
			name = GUID.generate();
		}
	    return name;
    }

	/**
	 * Sets the name of the rendition definition.
	 * @param name	rendition definition name
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 * Gets the rendering engine name
	 * @return	String	rendering engine name
	 */
	public String getRenderingEngineName()
	{
		return renderingEngineName;
	}

	/**
	 * Sets the rendering engine name
	 * @param renderingEngineName	rendering engine name
	 */
	public void setRenderingEngineName(String renderingEngineName)
	{
		this.renderingEngineName = renderingEngineName;
	}

	/**
	 * Indicates whether the rendition should be created asynchronously.
	 * @return Boolean	true if asynchronous, false otherwise
	 */
	public Boolean isExecuteAsynchronously()
	{
		return executeAsynchronously;
	}

	/**
	 * Sets whether the rendition should be created asynchronously.
	 * @param executeAsynchronously	true is asynchronous, false otherwise
	 */
	public void setExecuteAsynchronously(Boolean executeAsynchronously)
	{
		this.executeAsynchronously = executeAsynchronously;
	}

	/**
	 * Gets the rendition definition parameter values.
	 * @return Map<String, Serializable>	map of parameter values
	 */
	public Map<String, Serializable> getParameters()
	{
		return parameters;
	}

	/**
	 * Set the rendition definition parameter values.
	 * @param parameters 	rendition definition parameter values
	 */
	public void setParameters(Map<String, Serializable> parameters)
	{
        this.parameters.putAll(parameters);
    }

    /**
     * Set the rendition definition default parameter values.
     * @param defaultParameters    rendition definition parameter values
     */
    public void setDefaultParameters(Map<String, Serializable> defaultParameters)
    {
        this.parameters.putAll(defaultParameters);
    }
}
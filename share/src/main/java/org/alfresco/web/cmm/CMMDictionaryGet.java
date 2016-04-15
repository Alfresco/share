/*
 * #%L
 * Alfresco Share WAR
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.web.cmm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.web.cmm.CMMService.TWrapper;
import org.alfresco.web.scripts.DictionaryQuery;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * Share REST API access to basic Dictionary services.
 * <p>
 * See cmm-dictionary.get.desc.xml for API descriptor.
 * 
 * @author Kevin Roast
 */
public class CMMDictionaryGet extends DeclarativeWebScript
{
    private static final Log logger = LogFactory.getLog(CMMDictionaryGet.class);
    
    protected DictionaryQuery dictionary;
    
    /**
     * Dictionary Query bean reference
     * 
     * @param dictionary                    DictionaryQuery
     */
    public void setDictionary(DictionaryQuery dictionary)
    {
        this.dictionary = dictionary;
    }
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> result = new HashMap<>();
        
        String[] classes = null;
        
        // look for an entity subclass on the request
        String entity = req.getServiceMatch().getTemplateVars().get("entity");
        if (entity != null && entity.length() != 0)
        {
            classes = this.dictionary.getSubTypes(entity);
        }
        else
        {
            // look for an aspects/types on the request
            String classtype = req.getServiceMatch().getTemplateVars().get("classtype");
            if (classtype != null)
            {
                switch (classtype)
                {
                    case "aspects":
                        classes = this.dictionary.getAllAspects();
                        break;
                    case "types":
                        classes = this.dictionary.getAllTypes();
                        break;
                }
            }
        }
        
        if (classes == null)
        {
            throw new IllegalArgumentException("No valid entity or types/aspects modifier specified.");
        }
        
        // build template wrappers for our entities
        List<TWrapper> entities = new ArrayList<>();
        for (String e: classes)
        {
            entities.add(new TWrapper(4)
                            .put("name", e)
                            .put("title", this.dictionary.getTitle(e))
                            .put("description", this.dictionary.getDescription(e)));
        }
        result.put("entities", entities);
        
        return result;
    }
}

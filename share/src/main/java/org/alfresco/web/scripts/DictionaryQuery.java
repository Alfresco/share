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
package org.alfresco.web.scripts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.util.ParameterCheck;
import org.alfresco.web.scripts.Dictionary.DictionaryAssoc;
import org.alfresco.web.scripts.Dictionary.DictionaryItem;
import org.alfresco.web.scripts.Dictionary.DictionaryProperty;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.extensions.surf.ClusterMessageAware;
import org.springframework.extensions.surf.ClusterService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.ServletUtil;
import org.springframework.extensions.surf.exception.ConnectorServiceException;
import org.springframework.extensions.surf.site.AuthenticationUtil;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.connector.Connector;
import org.springframework.extensions.webscripts.connector.Response;

/**
 * Slingleton scripting host object provided to allows scripts to execute basic
 * Alfresco Data Dictionary model queries.
 * <p>
 * Service object that maintains no state other than the dictionary itself.
 * <p>
 * Queries include: isSubType, isAspect, hasProperty, getParentType etc.
 * 
 * @author Kevin Roast
 */
@SuppressWarnings("serial")
public class DictionaryQuery extends SingletonValueProcessorExtension<Dictionary> implements Serializable, ClusterMessageAware
{
    private static Log logger = LogFactory.getLog(DictionaryQuery.class);
    
    
    /**
     * getAspects - return Array of all aspects in the dictionary.
     * 
     * @return Array of all aspects in the dictionary.
     */
    public String[] getAllAspects()
    {
        return getDictionary().getAllAspects();
    }
    
    /**
     * getTypes - return Array of all types in the dictionary.
     * 
     * @return Array of all types in the dictionary.
     */
    public String[] getAllTypes()
    {
        return getDictionary().getAllTypes();
    }
    
    /**
     * isSubType - return if the supplied type is a sub-type of a given type.
     * 
     * @param type      Type to test
     * @param isType    Is the type a subtype of this type?
     * 
     * @return true if the type is a subtype of isType or the same type
     */
    public boolean isSubType(final String type, final String isType)
    {
        ParameterCheck.mandatoryString("type", type);
        ParameterCheck.mandatoryString("isType", isType);
        
        return type.equals(isType) || getDictionary().isSubType(type, isType);
    }
    
    /**
     * getSubTypes - return Array of sub-types for the given class.
     * 
     * @param ddclass   DD class to get sub-types for
     * 
     * @return Array of  sub-types for the type or aspect, can be empty but never null.
     */
    public String[] getSubTypes(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getSubTypes(ddclass);
    }
    
    /**
     * hasDefaultAspect - return if the type definition has the default aspect applied..
     * 
     * @param type      Type to test
     * @param aspect    Aspect to look for in the default aspects
     * 
     * @return true if the aspect is one of the default aspects
     */
    public boolean hasDefaultAspect(final String type, final String aspect)
    {
        ParameterCheck.mandatoryString("type", type);
        ParameterCheck.mandatoryString("aspect", aspect);
        
        return getDictionary().hasDefaultAspect(type, aspect);
    }
    
    /**
     * getDefaultAspects - return Array of default aspects for the given type.
     * 
     * @param type      Type to inspect
     * 
     * @return Array of default aspects for the type, can be empty but never null.
     */
    public String[] getDefaultAspects(final String type)
    {
        ParameterCheck.mandatoryString("type", type);
        
        return getDictionary().getDefaultAspects(type);
    }
    
    /**
     * isAspect - return if the supplied dd class is an aspect.
     * 
     * @param ddclass   DD class to test
     * 
     * @return true if the supplied dd class is an aspect
     */
    public boolean isAspect(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return (getDictionary().getAspect(ddclass) != null);
    }
    
    /**
     * isType - return if the supplied dd class is a type.
     * 
     * @param ddclass   DD class to test
     * 
     * @return true if the supplied dd class is a type
     */
    public boolean isType(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return (getDictionary().getType(ddclass) != null);
    }
    
    /**
     * hasProperty - return if a type or aspect has the given property definition.
     * This method correctly reports properties inherited from base types.
     * 
     * @param ddclass   Type or aspect to test
     * @param property  Property to look for in the type or aspect definition
     * 
     * @return true if the property is defined on the type or aspect
     */
    public boolean hasProperty(final String ddclass, final String property)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        ParameterCheck.mandatoryString("property", property);
        
        return getDictionary().hasProperty(ddclass, property, false);
    }
    
    /**
     * hasProperty - return if a type or aspect has the given property definition.
     * This method correctly reports properties inherited from base types and also
     * optionally checks any default aspects applied to the type for the property.
     * 
     * @param ddclass   Type or aspect to test
     * @param property  Property to look for in the type or aspect definition
     * @param includeDefaultAspects If true, check default aspects for the given property.
     * 
     * @return true if the property is defined on the type or aspect or any of its default aspects.
     */
    public boolean hasProperty(final String ddclass, final String property, final boolean includeDefaultAspects)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        ParameterCheck.mandatoryString("property", property);
        
        return getDictionary().hasProperty(ddclass, property, true);
    }

    /**
     * getTitle - return the title string for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return title string
     */
    public String getTitle(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getTitle(ddclass);
    }
    
    /**
     * getDescription - return the description string for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return description string
     */
    public String getDescription(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getDescription(ddclass);
    }
    
    /**
     * getParent - return the parent for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return parent type or null for a root type with no parent.
     */
    public String getParent(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getParent(ddclass);
    }
    
    /**
     * isContainer - return if the specified type is a container
     * 
     * @param type      Type to inspect
     * 
     * @return true if the type is a container, false otherwise
     */
    public boolean isContainer(final String type)
    {
        ParameterCheck.mandatoryString("type", type);
        
        return getDictionary().isContainer(type);
    }
    
    /**
     * getProperty - return a single named property for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * @param property  Property to look for in the type or aspect definition
     * 
     * @return DictionaryProperty describing the property definition or null if not found
     */
    public DictionaryProperty getProperty(final String ddclass, final String property)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        ParameterCheck.mandatoryString("property", property);
        
        return getDictionary().getProperty(ddclass, property, false);
    }
    
    /**
     * getProperty - return a single named property for the given dd class, optionally
     * retrieve a property from the default aspects.
     * 
     * @param ddclass   DD class to inspect
     * @param property  Property to look for in the type or aspect definition
     * @param includeDefaultAspects If true, check default aspects for the given property.
     * 
     * @return DictionaryProperty describing the property definition or null if not found
     */
    public DictionaryProperty getProperty(final String ddclass, final String property, final boolean includeDefaultAspects)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        ParameterCheck.mandatoryString("property", property);
        
        return getDictionary().getProperty(ddclass, property, true);
    }
    
    /**
     * getProperties - return all properties for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return Array of DictionaryProperty objects describing the property definitions for the class.
     *         Can be empty but never null.
     */
    public DictionaryProperty[] getProperties(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getProperties(ddclass, false);
    }
    
    /**
     * getProperties - return all properties for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * @param includeDefaultAspects If true, also retrieve properties from the default aspects.
     * 
     * @return Array of DictionaryProperty objects describing the property definitions for the class
     *         and default aspects. Can be empty but never null.
     */
    public DictionaryProperty[] getProperties(final String ddclass, final boolean includeDefaultAspects)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getProperties(ddclass, true);
    }
    
    /**
     * getAssociations - return the target associations for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return Array of DictionaryAssoc objects describing the target associations for the class.
     *         Can be empty but never null.
     */
    public DictionaryAssoc[] getAssociations(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getAssociations(ddclass);
    }
    
    /**
     * getChildAssociations - return the child associations for the given dd class.
     * 
     * @param ddclass   DD class to inspect
     * 
     * @return Array of DictionaryAssoc objects describing the child associations for the class.
     *         Can be empty but never null.
     */
    public DictionaryAssoc[] getChildAssociations(final String ddclass)
    {
        ParameterCheck.mandatoryString("ddclass", ddclass);
        
        return getDictionary().getChildAssociations(ddclass);
    }
    
    /**
     * Add/update a JSON Array of classes to the current Dictionary instance
     * 
     * @param json  JSON array of DD classes to add/update
     */
    public void updateAddClasses(final String json)
    {
        ParameterCheck.mandatoryString("json", json);
        
        getDictionary().updateAddClasses(json);
        
        // inform cluster of update
        if (this.clusterService != null)
        {
            Map<String, Serializable> params = new HashMap<>(4);
            params.put(DictionaryUpdateMessage.PAYLOAD_ADD, json);
            params.put(DictionaryUpdateMessage.PAYLOAD_USERID, ThreadLocalRequestContext.getRequestContext().getUserId());
            this.clusterService.publishClusterMessage(DictionaryUpdateMessage.TYPE, params);
        }
    }
    
    /**
     * Remove a JSON Array of classes from the current Dictionary instance
     * 
     * @param json  JSON array of DD classes to remove
     */
    public void updateRemoveClasses(final String json)
    {
        ParameterCheck.mandatoryString("json", json);
        
        getDictionary().updateRemoveClasses(json);
        
        // inform cluster of update
        if (this.clusterService != null)
        {
            Map<String, Serializable> params = new HashMap<>(4);
            params.put(DictionaryUpdateMessage.PAYLOAD_REMOVE, json);
            params.put(DictionaryUpdateMessage.PAYLOAD_USERID, ThreadLocalRequestContext.getRequestContext().getUserId());
            this.clusterService.publishClusterMessage(DictionaryUpdateMessage.TYPE, params);
        }
    }
    
    @Override
    public String toString()
    {
        try
        {
            String out = "";
            final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
            final String userId = rc.getUserId();
            if (userId != null && !AuthenticationUtil.isGuest(userId))
            {
                int idx = userId.indexOf('@');
                if (idx != -1)
                {
                    out = "Dictionary for user domain: " + userId.substring(idx) + "\r\n";
                }
            }
            return out + getDictionary().toString();
        }
        catch (Throwable e)
        {
            return super.toString();
        }
    }

    /**
     * Return the dictionary for the current user context. Takes into account the current user
     * tenant domain and will retrieve the data dictionary from the remote Alfresco tier.
     * 
     * @return the dictionary for the current user context
     */
    private Dictionary getDictionary()
    {
        return getSingletonValue(isTenant());
    }
    
    /**
     * @return true for tenant specific Dictionary support, false to share a single Dictionary.
     */
    protected boolean isTenant()
    {
        return true;
    }
    
    @Override
    protected Dictionary retrieveValue(final String userId, final String storeId)
            throws ConnectorServiceException
    {
        Dictionary dictionary;
        
        // initiate a call to retrieve the dictionary from the repository
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        final Connector conn = rc.getServiceRegistry().getConnectorService().getConnector("alfresco", userId, ServletUtil.getSession());
        final Response response = conn.call("/api/dictionary");
        if (response.getStatus().getCode() == Status.STATUS_OK)
        {
            logger.info("Successfully retrieved Data Dictionary from Alfresco." +
                        (storeId.length() != 0 ? (" - for domain: " + storeId) : ""));
            
            final Map<String, DictionaryItem> types = new HashMap<String, DictionaryItem>(128);
            final Map<String, DictionaryItem> aspects = new HashMap<String, DictionaryItem>(128);
            
            // TODO: remove url field from response template? waste of space...
            
            // extract dictionary types and aspects
            try
            {
                JSONArray json = new JSONArray(response.getResponse());
                for (int i=0; i<json.length(); i++)
                {
                    // get the object representing the dd class
                    JSONObject ddclass = json.getJSONObject(i);
                    
                    // is this an aspect or a type definition?
                    String typeName = ddclass.getString(Dictionary.JSON_NAME);
                    if (ddclass.getBoolean(Dictionary.JSON_IS_ASPECT))
                    {
                        aspects.put(typeName, new DictionaryItem(typeName, ddclass));
                    }
                    else
                    {
                        types.put(typeName, new DictionaryItem(typeName, ddclass));
                    }
                }
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException(e.getMessage(), e);
            }
            
            dictionary = new Dictionary(types, aspects);
        }
        else
        {
           throw new AlfrescoRuntimeException("Unable to retrieve dictionary information from Alfresco: " + response.getStatus().getCode());
        }
        
        return dictionary;
    }

    @Override
    protected String getValueName()
    {
        return "dictionary information";
    }
    
    
    /**
     * Cluster message indicating that paths in persister cache should be invalidated.
     * The payload for this message is an array of path strings.
     */
    static interface DictionaryUpdateMessage
    {
        static final String TYPE = "dictionary-update";
        static final String PAYLOAD_ADD = "add";
        static final String PAYLOAD_REMOVE = "remove";
        static final String PAYLOAD_USERID = "user";
    }
    
    protected ClusterService clusterService;
    
    @Override
    public void setClusterService(ClusterService service)
    {
        this.clusterService = service;
    }
    
    @Override
    public String getClusterMessageType()
    {
        return DictionaryUpdateMessage.TYPE;
    }
    
    @Override
    public void onClusterMessage(Map<String, Serializable> payload)
    {
        final String userId = (String)payload.get(DictionaryUpdateMessage.PAYLOAD_USERID);
        final String jsonAdd = (String)payload.get(DictionaryUpdateMessage.PAYLOAD_ADD);
        final String jsonRemove = (String)payload.get(DictionaryUpdateMessage.PAYLOAD_ADD);
        
        if (jsonAdd != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Cluster message to update dictionary with ADD operation: " + jsonAdd);
            if (hasSingletonValue(isTenant(), userId))
            {
                getSingletonValue(isTenant(), userId).updateAddClasses(jsonAdd);
            }
        }
        if (jsonRemove != null)
        {
            if (logger.isDebugEnabled())
                logger.debug("Cluster message to update dictionary with REMOVE operation: " + jsonRemove);
            if (hasSingletonValue(isTenant(), userId))
            {
                getSingletonValue(isTenant(), userId).updateRemoveClasses(jsonRemove);
            }
        }
    }
}


/**
 * Class representing a Data Dictionary model instance. There will be a DD instance
 * for each tenant in Alfresco, for a single tenant instance there will only be one.
 * <p>
 * Handles the internals of various dictionary queries and manipulates the JSON data
 * that represents each type, aspect and its properties. The various public methods
 * on the DictionaryQuery should stay isolated from the JSON internals.
 * 
 * @author Kevin Roast
 */
class Dictionary
{
    static final String JSON_IS_ASPECT = "isAspect";
    static final String JSON_IS_CONTAINER = "isContainer";
    static final String JSON_DESCRIPTION = "description";
    static final String JSON_TITLE = "title";
    static final String JSON_PROPERTIES = "properties";
    static final String JSON_DEFAULT_ASPECTS = "defaultAspects";
    static final String JSON_NAME = "name";
    static final String JSON_PARENT = "parent";
    static final String JSON_DATATYPE = "dataType";
    static final String JSON_DEFAULTVALUE = "defaultValue";
    static final String JSON_MULTIVALUED = "multiValued";
    static final String JSON_MANDATORY = "mandatory";
    static final String JSON_ENFORCED = "enforced";
    static final String JSON_PROTECTED = "protected";
    static final String JSON_INDEXED = "indexed";
    static final String JSON_ASSOCIATIONS = "associations";
    static final String JSON_CHILDASSOCIATIONS = "childassociations";
    static final String JSON_SOURCE = "source";
    static final String JSON_TARGET = "target";
    static final String JSON_CLASS = "class";
    static final String JSON_ROLE = "role";
    static final String JSON_MANY = "many";
    
    private Map<String, DictionaryItem> types;
    private Map<String, DictionaryItem> aspects;
    
    /**
     * Constructor
     * 
     * @param types     Map of short type names to to DictionaryItem objects
     * @param aspects   Map of short aspect names to to DictionaryItem objects
     */
    Dictionary(Map<String, DictionaryItem> types, Map<String, DictionaryItem> aspects)
    {
        this.types = types;
        this.aspects = aspects;
    }
    
    public DictionaryItem getType(String type)
    {
        return this.types.get(type);
    }
    
    public DictionaryItem getAspect(String aspect)
    {
        return this.aspects.get(aspect);
    }
    
    public DictionaryItem getTypeOrAspect(String ddclass)
    {
        DictionaryItem item = this.types.get(ddclass);
        if (item == null)
        {
            item = this.aspects.get(ddclass);
        }
        return item;
    }
    
    public boolean isSubType(String type, String isType)
    {
        boolean isSubType = false;
        try
        {
            DictionaryItem ddtype = getType(type);
            while (!isSubType && ddtype != null)
            {
                // the parent JSON object will always exist, but may be empty
                JSONObject parent = ddtype.data.getJSONObject(JSON_PARENT);
                if (parent.has(JSON_NAME))
                {
                    // found a parent type specified for our type
                    String parentName = parent.getString(JSON_NAME);
                    ddtype = this.types.get(parentName);
                    if (ddtype != null)
                    {
                        isSubType = (isType.equals(ddtype.data.getString(JSON_NAME)));
                    }
                }
                else
                {
                    // no parent found - end the search
                    ddtype = null;
                }
            }
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'isSubType' information for: " + type, jsonErr);
        }
        return isSubType;
    }
    
    public String[] getSubTypes(String ddclass)
    {
        try
        {
            List<String> subTypes = new ArrayList<String>();
            DictionaryItem dditem = getType(ddclass);
            if (dditem != null)
            {
                // TODO: get parent of given ddclass - if that is non-null then can be used as a quick exit for the parent hiearchy search on types
                for (String typeName : this.types.keySet())
                {
                    DictionaryItem ddType = getType(typeName);
                    String parentType = null;
                    while (ddType != null)
                    {
                        // the parent JSON object will always exist, but name value may be empty
                        JSONObject parent = ddType.data.getJSONObject(JSON_PARENT);
                        parentType = parent.optString(JSON_NAME);
                        if (parentType != null)
                        {
                            if (parentType.equals(ddclass))
                            {
                                // found a sub-type - store and exit the loop
                                subTypes.add(parentType);
                                ddType = null;
                            }
                            else
                            {
                                // get the parent type for another loop round
                                ddType = getType(parentType);
                            }
                        }
                    }
                }
            }
            else
            {
                dditem = getAspect(ddclass);
                if (dditem != null)
                {
                    for (String aspectName : this.aspects.keySet())
                    {
                        DictionaryItem ddAspect = getAspect(aspectName);
                        String parentAspect = null;
                        while (ddAspect != null)
                        {
                            // the parent JSON object will always exist, but name value may be empty
                            JSONObject parent = ddAspect.data.getJSONObject(JSON_PARENT);
                            parentAspect = parent.optString(JSON_NAME);
                            if (parentAspect != null)
                            {
                                if (parentAspect.equals(ddclass))
                                {
                                    // found a sub-type - store and exit the loop
                                    subTypes.add(parentAspect);
                                    ddAspect = null;
                                }
                                else
                                {
                                    // get the parent aspect for another loop round
                                    ddAspect = getAspect(parentAspect);
                                }
                            }
                        }
                    }
                }
            }
            return subTypes.toArray(new String[subTypes.size()]);
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'subtype' information for: " + ddclass, jsonErr);
        }
    }
    
    public String[] getAllTypes()
    {
        return this.types.keySet().toArray(new String[this.types.keySet().size()]);
    }
    
    public String[] getAllAspects()
    {
        return this.aspects.keySet().toArray(new String[this.aspects.keySet().size()]);
    }
    
    public boolean hasDefaultAspect(String type, String aspect)
    {
        boolean hasAspect = false;
        try
        {
            DictionaryItem ddtype = getType(type);
            if (ddtype != null)
            {
                JSONObject aspects = ddtype.data.getJSONObject(JSON_DEFAULT_ASPECTS);
                Iterator<String> keys = aspects.keys();
                while (!hasAspect && keys.hasNext())
                {
                    hasAspect = (aspect.equals(keys.next()));
                }
            }
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'defaultAspects' information for: " + type, jsonErr);
        }
        return hasAspect;
    }
    
    public String[] getDefaultAspects(String type)
    {
        String[] defaultAspects = null;
        try
        {
            DictionaryItem ddtype = getType(type);
            if (ddtype != null)
            {
                JSONObject aspects = ddtype.data.getJSONObject(JSON_DEFAULT_ASPECTS);
                defaultAspects = new String[aspects.length()];
                int count = 0;
                Iterator<String> keys = aspects.keys();
                while (keys.hasNext())
                {
                    defaultAspects[count++] = keys.next();
                }
            }
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'defaultAspects' information for: " + type, jsonErr);
        }
        return defaultAspects != null ? defaultAspects : new String[0];
    }
    
    public boolean hasProperty(String ddclass, String property, boolean checkDefaultAspects)
    {
        boolean hasProperty = false;
        try
        {
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                JSONObject properties = dditem.data.getJSONObject(JSON_PROPERTIES);
                Iterator<String> props = properties.keys();
                while (!hasProperty && props.hasNext())
                {
                    // test properties and inherited properties
                    hasProperty = (property.equals(props.next()));
                }
                if (checkDefaultAspects && !hasProperty)
                {
                    // test each default aspect for the property
                    JSONObject aspects = dditem.data.getJSONObject(JSON_DEFAULT_ASPECTS);
                    Iterator<String> keys = aspects.keys();
                    while (!hasProperty && keys.hasNext())
                    {
                        // get each aspect defined on the type
                        DictionaryItem aspect = getAspect(keys.next());
                        if (aspect != null)
                        {
                            props = aspect.data.getJSONObject(JSON_PROPERTIES).keys();
                            while (!hasProperty && props.hasNext())
                            {
                                // test properties on each aspect
                                hasProperty = (property.equals(props.next()));
                            }
                        }
                    }
                }
            }
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'properties' information for: " + ddclass, jsonErr);
        }
        return hasProperty;
    }
   
    public String getTitle(String ddclass)
    {
        try
        {
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            return dditem != null ? dditem.data.getString(JSON_TITLE) : null;
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'title' information for: " + ddclass, jsonErr);
        }
    }
    
    public String getDescription(String ddclass)
    {
        try
        {
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            return dditem != null ? dditem.data.getString(JSON_DESCRIPTION) : null;
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'description' information for: " + ddclass, jsonErr);
        }
    }
    
    public String getParent(String ddclass)
    {
        try
        {
            String parentType = null;
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                // the parent JSON object will always exist, but may be empty
                JSONObject parent = dditem.data.getJSONObject(JSON_PARENT);
                parentType = parent.optString(JSON_NAME);
            }
            return parentType;
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'parent' information for: " + ddclass, jsonErr);
        }
    }
    
    public boolean isContainer(String type)
    {
        try
        {
            DictionaryItem ddtype = getType(type);
            return ddtype != null ? ddtype.data.getBoolean(JSON_IS_CONTAINER) : false;
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'isContainer' information for: " + type, jsonErr);
        }
    }
    
    public DictionaryProperty getProperty(String ddclass, String property, boolean checkDefaultAspects)
    {
        try
        {
            DictionaryProperty ddprop = null;
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                JSONObject properties = dditem.data.getJSONObject(JSON_PROPERTIES);
                if (properties.has(property))
                {
                    ddprop = new DictionaryProperty(property, properties.getJSONObject(property));
                }
                else if (checkDefaultAspects)
                {
                    // test each default aspect for the property
                    JSONObject aspects = dditem.data.getJSONObject(JSON_DEFAULT_ASPECTS);
                    Iterator<String> keys = aspects.keys();
                    while (ddprop == null && keys.hasNext())
                    {
                        // get each aspect defined on the type
                        DictionaryItem aspect = getAspect(keys.next());
                        if (aspect != null)
                        {
                            properties = aspect.data.getJSONObject(JSON_PROPERTIES);
                            if (properties.has(property))
                            {
                                ddprop = new DictionaryProperty(property, properties.getJSONObject(property));
                            }
                        }
                    }
                }
            }
            return ddprop;
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'properties' information for: " + ddclass, jsonErr);
        }
    }
    
    public DictionaryProperty[] getProperties(String ddclass, boolean checkDefaultAspects)
    {
        try
        {
            DictionaryProperty[] ddprops = null;
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                JSONObject properties = dditem.data.getJSONObject(JSON_PROPERTIES);
                List<DictionaryProperty> propList = new ArrayList<DictionaryProperty>(properties.length());
                Iterator<String> props = properties.keys();
                while (props.hasNext())
                {
                    String propName = props.next();
                    propList.add(new DictionaryProperty(propName, properties.getJSONObject(propName)));
                }
                if (checkDefaultAspects)
                {
                    JSONObject aspects = dditem.data.getJSONObject(JSON_DEFAULT_ASPECTS);
                    Iterator<String> keys = aspects.keys();
                    while (keys.hasNext())
                    {
                        // get each aspect defined on the type
                        DictionaryItem aspect = getAspect(keys.next());
                        if (aspect != null)
                        {
                            properties = aspect.data.getJSONObject(JSON_PROPERTIES);
                            props = properties.keys();
                            while (props.hasNext())
                            {
                                String propName = props.next();
                                propList.add(new DictionaryProperty(propName, properties.getJSONObject(propName)));
                            }
                        }
                    }
                }
                ddprops = new DictionaryProperty[propList.size()];
                propList.toArray(ddprops);
            }
            return ddprops != null ? ddprops : new DictionaryProperty[0];
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'properties' information for: " + ddclass, jsonErr);
        }
    }
    
    public DictionaryAssoc[] getAssociations(String ddclass)
    {
        try
        {
            DictionaryAssoc[] ddassocs = null;
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                JSONObject assocs = dditem.data.getJSONObject(JSON_ASSOCIATIONS);
                ddassocs = new DictionaryAssoc[assocs.length()];
                int count = 0;
                Iterator<String> assocNames = assocs.keys();
                while (assocNames.hasNext())
                {
                    String assocName = assocNames.next();
                    ddassocs[count++] = new DictionaryAssoc(assocName, assocs.getJSONObject(assocName));
                }
            }
            return ddassocs != null ? ddassocs : new DictionaryAssoc[0];
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'associations' information for: " + ddclass, jsonErr);
        }
    }
    
    public DictionaryAssoc[] getChildAssociations(String ddclass)
    {
        try
        {
            DictionaryAssoc[] ddassocs = null;
            DictionaryItem dditem = getTypeOrAspect(ddclass);
            if (dditem != null)
            {
                JSONObject assocs = dditem.data.getJSONObject(JSON_CHILDASSOCIATIONS);
                ddassocs = new DictionaryAssoc[assocs.length()];
                int count = 0;
                Iterator<String> assocNames = assocs.keys();
                while (assocNames.hasNext())
                {
                    String assocName = assocNames.next();
                    ddassocs[count++] = new DictionaryAssoc(assocName, assocs.getJSONObject(assocName));
                }
            }
            return ddassocs != null ? ddassocs : new DictionaryAssoc[0];
        }
        catch (JSONException jsonErr)
        {
            throw new AlfrescoRuntimeException("Error retrieving 'childassociations' information for: " + ddclass, jsonErr);
        }
    }
    
    public void updateAddClasses(String classes)
    {
        try
        {
            JSONArray json = new JSONArray(classes);
            
            // shallow copy types and aspects maps - do not modify the originals as iterators could be running in threads 
            final Map<String, DictionaryItem> types = (Map<String, DictionaryItem>)((HashMap)this.types).clone();
            final Map<String, DictionaryItem> aspects = (Map<String, DictionaryItem>)((HashMap)this.aspects).clone();
            for (int i=0; i<json.length(); i++)
            {
                // get the object representing the dd class
                JSONObject ddclass = json.getJSONObject(i);
                
                // is this an aspect or a type definition?
                String typeName = ddclass.getString(JSON_NAME);
                if (ddclass.getBoolean(JSON_IS_ASPECT))
                {
                    // add or update the aspect definition
                    aspects.put(typeName, new DictionaryItem(typeName, ddclass));
                }
                else
                {
                    // add or update the type definition
                    types.put(typeName, new DictionaryItem(typeName, ddclass));
                }
            }
            // Update the instance collection references - threads already iterating the original collections will not be
            // affected - when a new iterator is created it will have visibility of the new references and see the updates.
            // It is acceptable for this data to be "eventually consistent" and does not need to be a transactional update.
            this.types = types;
            this.aspects = aspects;
        }
        catch (JSONException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
    }
    
    public void updateRemoveClasses(String classes)
    {
        try
        {
            JSONArray json = new JSONArray(classes);
            
            // shallow copy types and aspects maps - do not modify the originals as iterators could be running in threads 
            final Map<String, DictionaryItem> types = (Map<String, DictionaryItem>)((HashMap)this.types).clone();
            final Map<String, DictionaryItem> aspects = (Map<String, DictionaryItem>)((HashMap)this.aspects).clone();
            for (int i=0; i<json.length(); i++)
            {
                // get the object representing the dd class
                JSONObject ddclass = json.getJSONObject(i);
                
                // is this an aspect or a type definition?
                String typeName = ddclass.getString(JSON_NAME);
                if (ddclass.getBoolean(JSON_IS_ASPECT))
                {
                    // remove the aspect definition
                    aspects.remove(typeName);
                }
                else
                {
                    // remove the type definition
                    types.remove(typeName);
                }
            }
            // Update the instance collection references - threads already iterating the original collections will not be
            // affected - when a new iterator is created it will have visibility of the new references and see the updates.
            // It is acceptable for this data to be "eventually consistent" and does not need to be a transactional update.
            this.types = types;
            this.aspects = aspects;
        }
        catch (JSONException e)
        {
            throw new AlfrescoRuntimeException(e.getMessage(), e);
        }
    }
    
    @Override
    public String toString()
    {
        return "Dictionary contains " + this.types.size() + " types and " + this.aspects.size() + " aspects.";
    }
    
    
    /**
     * Simple wrapper class representing a single Data Dictionary type or aspect instance.
     * <p>
     * The dd item is backed by the underlying JSON structure from the parent dictionary.
     */
    public static class DictionaryItem
    {
        final private String type;
        final private JSONObject data;
        
        DictionaryItem(String type, JSONObject data)
        {
            this.type = type;
            this.data = data;
        }
        
        @Override
        public int hashCode()
        {
            return this.type.hashCode();
        }
        
        @Override
        public boolean equals(Object obj)
        {
            return this.type.equals(obj);
        }
        
        @Override
        public String toString()
        {
            return this.type.toString() + "\r\n" + data.toString();
        }
    }
    
    
    /**
     * Base class representing a single Data Dictionary meta instance.
     * <p>
     * The meta is backed by the underlying JSON structure from the parent dictionary.
     */
    private static abstract class DictionaryMetaBase
    {
        final private JSONObject meta;
        final private String name;
        
        DictionaryMetaBase(String name, JSONObject meta)
        {
            this.name = name;
            this.meta = meta;
        }
        
        public String getName()
        {
            return this.name;
        }
        
        /**
         * Helper to get mandatory string value from the underlying JSON metadata.
         * 
         * @param value     name of the value to retrieve
         * 
         * @return the value
         */
        protected String getStringValue(String value)
        {
            try
            {
                return this.meta.getString(value);
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException("Error retrieving '" + value + "' information for item: " + name, e);
            }
        }
        
        /**
         * Helper to get optional string value from an object in the underlying JSON metadata.
         * 
         * @param object    name of the object containing the value
         * @param value     name of the value to retrieve
         * 
         * @return the value
         */
        protected String getStringValue(String object, String value)
        {
            try
            {
                String result = null;
                if (this.meta.has(object))
                {
                    result = this.meta.getJSONObject(object).optString(value);
                }
                return result;
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException("Error retrieving '" + value + "' information for item: " + name, e);
            }
        }
        
        /**
         * Helper to get mandatory boolean value from the underlying JSON metadata.
         * 
         * @param value     name of the value to retrieve
         * 
         * @return the value
         */
        protected boolean getBooleanValue(String value)
        {
            try
            {
                return this.meta.getBoolean(value);
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException("Error retrieving '" + value + "' information for item: " + name, e);
            }
        }
        
        /**
         * Helper to get mandatory boolean value from an object in the underlying JSON metadata.
         * 
         * @param object    name of the object containing the value
         * @param value     name of the value to retrieve
         * 
         * @return the value
         */
        protected boolean getBooleanValue(String object, String value)
        {
            try
            {
                boolean result = false;
                if (this.meta.has(object))
                {
                    result = this.meta.getJSONObject(object).getBoolean(value);
                }
                return result;
            }
            catch (JSONException e)
            {
                throw new AlfrescoRuntimeException("Error retrieving '" + value + "' information for item: " + name, e);
            }
        }
        
        @Override
        public String toString()
        {
             return this.name;
        }
    }
    
    
    /**
     * Simple structure class representing a single Data Dictionary property instance.
     * <p>
     * API is provided to retrieve the various meta-data of the property definition.
     */
    public static class DictionaryProperty extends DictionaryMetaBase
    {
        DictionaryProperty(String name, JSONObject property)
        {
            super(name, property);
        }
        
        public String getTitle()
        {
            return getStringValue(Dictionary.JSON_TITLE);
        }
        
        public String getDescription()
        {
            return getStringValue(Dictionary.JSON_DESCRIPTION);
        }
        
        public String getDataType()
        {
            return getStringValue(Dictionary.JSON_DATATYPE);
        }
        
        public String getDefaultValue()
        {
            return getStringValue(Dictionary.JSON_DEFAULTVALUE);
        }
        
        public boolean getIsMultiValued()
        {
            return getBooleanValue(Dictionary.JSON_MULTIVALUED);
        }
        
        public boolean getIsMandatory()
        {
            return getBooleanValue(Dictionary.JSON_MANDATORY);
        }
        
        public boolean getIsEnforced()
        {
            return getBooleanValue(Dictionary.JSON_ENFORCED);
        }
        
        public boolean getIsProtected()
        {
            return getBooleanValue(Dictionary.JSON_PROTECTED);
        }
        
        public boolean getIsIndexed()
        {
            return getBooleanValue(Dictionary.JSON_INDEXED);
        }
    }
    
    
    /**
     * Simple structure class representing a single Data Dictionary association instance.
     * <p>
     * API is provided to retrieve the various meta-data of the association definition.
     */
    public static class DictionaryAssoc extends DictionaryMetaBase
    {
        DictionaryAssoc(String name, JSONObject assoc)
        {
            super(name, assoc);
        }
        
        public String getTitle()
        {
            return getStringValue(Dictionary.JSON_TITLE);
        }
        
        public String getSourceClass()
        {
            return getStringValue(Dictionary.JSON_SOURCE, Dictionary.JSON_CLASS);
        }
        
        public String getSourceRole()
        {
            return getStringValue(Dictionary.JSON_SOURCE, Dictionary.JSON_ROLE);
        }
        
        public boolean getSourceIsMandatory()
        {
            return getBooleanValue(Dictionary.JSON_SOURCE, Dictionary.JSON_MANDATORY);
        }
        
        public boolean getSourceIsMany()
        {
            return getBooleanValue(Dictionary.JSON_SOURCE, Dictionary.JSON_MANY);
        }
        
        public String getTargetClass()
        {
            return getStringValue(Dictionary.JSON_TARGET, Dictionary.JSON_CLASS);
        }
        
        public String getTargetRole()
        {
            return getStringValue(Dictionary.JSON_TARGET, Dictionary.JSON_ROLE);
        }
        
        public boolean getTargetIsMandatory()
        {
            return getBooleanValue(Dictionary.JSON_TARGET, Dictionary.JSON_MANDATORY);
        }
        
        public boolean getTargetIsMany()
        {
            return getBooleanValue(Dictionary.JSON_TARGET, Dictionary.JSON_MANY);
        }
    }
}

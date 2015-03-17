/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.web.site;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelPersistenceContext;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.cache.ContentCache;
import org.springframework.extensions.surf.exception.ModelObjectPersisterException;
import org.springframework.extensions.surf.persister.PathStoreObjectPersister;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.json.JSONWriter;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Hazlecast cluster aware implementation of the PathStoreObjectPersister. Manages the
 * local ModelObject cache by broadcasting and receiving messages relating to pertinent
 * cache operations that affect other cluster nodes.
 * <p>
 * The current implementation uses a simple broadcast message that informs other cluster
 * nodes to invalidate specific path objects (remove them) from the persister cache. This
 * is enough to deal with addition (caching of nulls), removal and modification of model
 * objects relating to site and user dashboards and the site configuration object.
 * 
 * @see ClusterAwareRequestContext
 * 
 * @author Kevin Roast
 */
public class ClusterAwarePathStoreObjectPersister extends PathStoreObjectPersister implements MessageListener<String>
{
    private static Log logger = LogFactory.getLog(ClusterAwarePathStoreObjectPersister.class);
    
    /** The Hazelcast cluster bean instance */
    private HazelcastInstance hazelcastInstance;
    
    /** The Hazelcast topic name used for messaging between cluster nodes */
    private String hazelcastTopicName;
    
    /** The Hazelcast Topic resolved during Persister init */
    private ITopic<String> clusterTopic = null;
    
    /** Node identifier - to ensure multicast messages aren't processed by the sender */
    private static final String clusterNodeId = GUID.generate();
    
    
    /////////////////////////////////////////////////////////////////
    // Setters and Spring properties
    
    /**
     * @param hazelcastInstance     The HazelcastInstance cluster bean
     */
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance)
    {
        this.hazelcastInstance = hazelcastInstance;
    }
    
    /**
     * @param hazelcastTopicName    The topic name used for messaging between cluster nodes
     */
    public void setHazelcastTopicName(String hazelcastTopicName)
    {
        this.hazelcastTopicName = hazelcastTopicName;
    }
    
    
    /////////////////////////////////////////////////////////////////
    // PathStoreObjectPersister overrides
    
    /**
     * Override the persister init method to perform one time init of the Hazelcast cluster node.
     */
    @Override
    public void init(ModelPersistenceContext context)
    {
        super.init(context);
        
        // validate bean setup
        if (this.hazelcastInstance == null)
        {
            throw new IllegalArgumentException("The hazelcastInstance property (HazelcastInstance) is mandatory.");
        }
        if (this.hazelcastTopicName == null || this.hazelcastTopicName.length() == 0)
        {
            throw new IllegalArgumentException("The hazelcastTopicName property (String) is mandatory.");
        }
        
        // cluster initialisation
        ITopic<String> topic = this.hazelcastInstance.getTopic(this.hazelcastTopicName);
        if (topic == null)
        {
            throw new IllegalArgumentException(
                    "Did not find Hazelcast topic with name: '" + this.hazelcastTopicName + "' - cannot init.");
        }
        this.clusterTopic = topic;
        this.clusterTopic.addMessageListener(this);
    }
    
    @Override
    public boolean saveObject(ModelPersistenceContext context, ModelObject modelObject)
            throws ModelObjectPersisterException
    {
        final boolean saved = super.saveObject(context, modelObject);
        if (saved)
        {
            addInvalidCachePath(generatePath(modelObject.getTypeId(), modelObject.getId()));
        }
        return saved;
    }

    @Override
    public boolean removeObject(ModelPersistenceContext context, String objectTypeId, String objectId)
            throws ModelObjectPersisterException
    {
        final boolean removed = super.removeObject(context, objectTypeId, objectId);
        if (removed)
        {
            addInvalidCachePath(generatePath(objectTypeId, objectId));
        }
        return removed;
    }

    @Override
    protected ModelObject newObject(ModelPersistenceContext context, String objectTypeId, String objectId, boolean addToCache)
            throws ModelObjectPersisterException
    {
        final ModelObject modelObject = super.newObject(context, objectTypeId, objectId, addToCache);
        if (modelObject != null)
        {
             addInvalidCachePath(generatePath(objectTypeId, objectId));
        }
        return modelObject;
    }
    
    /**
     * Keep track of invalid cache paths to later inform cluster to invalidate the given paths from caches.
     * 
     * @param path      ModelObject storage path
     */
    private void addInvalidCachePath(final String path)
    {
        if (logger.isDebugEnabled())
            logger.debug("Adding invalid cache path: " + path);
        
        RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        if (!(rc instanceof ClusterAwareRequestContext))
        {
            throw new IllegalStateException("Incorrect Share cluster configuration detected - ClusterAwareRequestContextFactory is required.");
        }
        ((ClusterAwareRequestContext)rc).addInvalidCachePath(path);
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Cluster message send and receive
    
    /**
     * Push message out to the cluster - multicast or direct TCP depending on Hazelcast config.
     * 
     * @param msg   The message to be sent
     */
    public void pushMessage(ClusterMessage message)
    {
        String msg = message.toString();
        
        if (logger.isDebugEnabled())
            logger.debug("Pushing message:\r\n" + msg);
        
        this.clusterTopic.publish(msg);
    }
    
    /**
     * Hazelcast MessageListener implementation - called when a message is received from a cluster node
     * 
     * @param message   String message data
     */
    @Override
    public void onMessage(Message<String> message)
    {
        final boolean debug = logger.isDebugEnabled();
        
        // process message objects and extract the payload
        String messageObj = message.getMessageObject();
        MessageProcessor proc = new MessageProcessor(messageObj);
        if (!proc.isSender())
        {
            if (debug) logger.debug("Received message:\r\n" + messageObj);
            
            // process the mesage types we understand - only this one currently
            if (PathInvalidationMessage.TYPE.equals(proc.getMessageType()))
            {
                if (debug) logger.debug("Processing message of type: " + proc.getMessageType());
                final List<String> paths = (List<String>)proc.getMessagePayload().get(PathInvalidationMessage.PAYLOAD_PATHS);
                if (paths != null)
                {
                    this.cacheLock.writeLock().lock();
                    try
                    {
                        for (final String path: paths)
                        {
                            if (debug) logger.debug("...invalidating cache for path: " + path);
                            // default object cache (if no MT is used)
                            this.objectCache.remove(path);
                            // process each MT cache also
                            // TODO: this could be improved by passing the tenant with the path
                            if (this.caches.size() != 0)
                            {
                                for (Entry<String, ContentCache<ModelObject>> entry: this.caches.entrySet())
                                {
                                    entry.getValue().remove(path);
                                }
                            }
                        }
                    }
                    finally
                    {
                        this.cacheLock.writeLock().unlock();
                    }
                }
            }
            else
            {
                logger.warn("Received message of unknown type: " + proc.getMessageType());
            }
        }
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Inner classes and messaging contract interfaces
    
    /**
     * Contract for a cluster message.
     */
    interface ClusterMessage
    {
        static final String JSON_SENDER = "sender";
        static final String JSON_MESSAGE = "message";
        static final String JSON_TYPE = "type";
        static final String JSON_PAYLOAD = "payload";
        
        /**
         * @return the globally unique sender ID
         */
        String getSender();
        
        /**
         * @return the message type, will only be processed if understood by the receiver
         */
        String getType();
        
        /**
         * @return the arbitrary payload data bundle
         */
        Map<String, Object> getPayload();
    }
    
    /**
     * Abstract base class for cluster messages.
     * <p>
     * Wraps an object payload and the type of the message and handles the marshling
     * of the payload to JSON string message format.
     */
    static abstract class BaseMessage implements ClusterMessage
    {
        /** message type */
        final private String type;
        
        /** payload object */
        final Map<String, Object> payload;
        
        /**
         * Constructor
         * 
         * @param type      Type of this message
         * @param payload   Payload object for this message
         */
        BaseMessage(String type, Map<String, Object> payload)
        {
            this.type = type;
            this.payload = payload;
        }
        
        /**
         * @return the payload map for the message
         */
        public Map<String, Object> getPayload()
        {
            return this.payload;
        }

        /**
         * @return the unique ID for the cluster node in the message
         */
        public String getSender()
        {
            return clusterNodeId;
        }

        /**
         * @return the message type
         */
        public String getType()
        {
            return this.type;
        }
        
        @Override
        public String toString()
        {
            // serialise message to JSON and return final message string data
            try
            {
                final StringBuilderWriter buffer = new StringBuilderWriter(512);
                final JSONWriter writer = new JSONWriter(buffer);
                writer.startObject();
                writer.writeValue(ClusterMessage.JSON_SENDER, getSender());
                writer.startValue(ClusterMessage.JSON_MESSAGE);
                writer.startObject();
                writer.writeValue(ClusterMessage.JSON_TYPE, getType());
                writer.startValue(ClusterMessage.JSON_PAYLOAD);
                serialiseMessageObjects(writer, null, this.payload);
                writer.endValue();
                writer.endObject();
                writer.endValue();
                writer.endObject();
                return buffer.toString();
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Unable to output cluster message: " + e.getMessage(), e);
            }
        }
        
        /**
         * Recursively serialise objects to a JSONWriter.
         * <p>
         * Handles basic Java type suitable for the current messaging implementation.
         * 
         * @param writer    JSONWriter for output
         * @param name      Name of the current value, can be null for array item
         * @param obj       Object representing the value to serialise
         */
        static void serialiseMessageObjects(final JSONWriter writer, final String name, final Object obj)
            throws IOException
        {
            if (obj instanceof Map)
            {
                // recursively serialise the map entries
                if (name != null)
                {
                    writer.startValue(name);
                }
                writer.startObject();
                Map<String, Object> map = (Map<String, Object>)obj;
                for (final String key: map.keySet())
                {
                    serialiseMessageObjects(writer, key, map.get(key));
                }
                writer.endObject();
                if (name != null)
                {
                    writer.endValue();
                }
            }
            else if (obj instanceof List)
            {
                // recursively serialise the list items
                if (name != null)
                {
                    writer.startValue(name);
                }
                writer.startArray();
                for (final Object item: (List)obj)
                {
                    serialiseMessageObjects(writer, null, item);
                }
                writer.endArray();
                if (name != null)
                {
                    writer.endValue();
                }
            }
            else if (obj instanceof Integer)
            {
                if (name != null)
                {
                    writer.writeValue(name, (Integer)obj);
                }
                else
                {
                    writer.writeValue((Integer)obj);
                }
            }
            else if (obj instanceof Boolean)
            {
                if (name != null)
                {
                    writer.writeValue(name, (Boolean)obj);
                }
                else
                {
                    writer.writeValue((Boolean)obj);
                }
            }
            else if (obj instanceof Date)
            {
                if (name != null)
                {
                    writer.writeValue(name, ISO8601DateFormat.format((Date)obj));
                }
                else
                {
                    writer.writeValue(ISO8601DateFormat.format((Date)obj));
                }
            }
            else if (obj == null)
            {
                if (name != null)
                {
                    writer.writeNullValue(name);
                }
                else
                {
                    writer.writeNullValue();
                }
            }
            else
            {
                if (name != null)
                {
                    writer.writeValue(name, obj.toString());
                }
                else
                {
                    writer.writeValue(obj.toString());
                }
            }
        }
    }
    
    /**
     * Message indicating that paths in persister cache should be invalidated.
     * The payload for this message is an array of path strings.
     */
    static class PathInvalidationMessage extends BaseMessage
    {
        static final String TYPE = "cache-invalidation";
        static final String PAYLOAD_PATHS = "paths";
        
        /**
         * Constructor
         * 
         * @param paths The payload for this message is a List of path strings to be removed from caches
         */
        PathInvalidationMessage(List<String> paths)
        {
            super(TYPE, Collections.<String, Object>singletonMap(PathInvalidationMessage.PAYLOAD_PATHS, (Object)paths));
        }
    }
    
    /**
     * This class is responsible for deserialising a message string into objects.
     * <p>
     * The sender, message type and object payload can then be retrieved for further processing.
     */
    static class MessageProcessor
    {
        private final String sender;
        private final String type;
        private final Map<String, Object> payload;
        
        MessageProcessor(String msg)
        {
            // deserialise the message to retrieve the sender, type and payload objects
            try
            {
                Map<String, Object> json = (Map<String, Object>)new JSONParser().parse(msg, new ContainerFactory()
                    {
                        public Map createObjectContainer()
                        {
                            return new HashMap();
                        }
                        
                        public List creatArrayContainer()
                        {
                            return new ArrayList();
                        }
                    });
                this.sender = (String)json.get(ClusterMessage.JSON_SENDER);
                Map<String, Object> message = (Map<String, Object>)json.get(ClusterMessage.JSON_MESSAGE);
                this.type = (String)message.get(ClusterMessage.JSON_TYPE);
                this.payload = (Map<String, Object>)message.get(ClusterMessage.JSON_PAYLOAD);
            }
            catch (Throwable e)
            {
                throw new IllegalArgumentException("Unable to parse cluster JSON message: " + e.getMessage() + "\r\n" + msg);
            }
        }
        
        boolean isSender()
        {
            return clusterNodeId.equals(this.sender);
        }
        
        String getMessageType()
        {
            return this.type;
        }
        
        Map<String, Object> getMessagePayload()
        {
            return this.payload;
        }
    }
}
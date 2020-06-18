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
package org.alfresco.web.site;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.util.GUID;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.extensions.surf.ClusterMessageAware;
import org.springframework.extensions.surf.ClusterService;
import org.springframework.extensions.surf.util.ISO8601DateFormat;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.json.JSONWriter;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;

/**
 * Share cluster service - implemented on Hazelcast. Manages the web cluster by broadcasting
 * and receiving messages relating to Surf operations (such as cache changes) that affect
 * nodes in the cluster.
 * <p>
 * The current implementation uses a simple broadcast message pattern to other cluster nodes
 * that will then for instance; invalidate specific caches or remove/update resource paths.
 * <p>
 * In particular Share deals with addition (caching of nulls), removal and modification of
 * model objects relating to site and user dashboards and the site configuration objects.
 * These objects are the only dynamic entities in Share and therefore synching in the cluster.
 * <p>
 * Surf beans interested in listening for cluster messages or publishing cluster messages
 * should implement the ClusterMessageAware interface. This service will automatically find all
 * beans that implement those interfaces and provide them with cluster messages when appropriate.
 * Beans implementing that interface can also publish messages to the cluster.
 * 
 * @author Kevin Roast
 */
public class ClusterTopicService implements MessageListener<String>, ClusterService, ApplicationContextAware
{
    private static Log logger = LogFactory.getLog(ClusterTopicService.class);
    
    /** The Hazelcast cluster bean instance */
    private HazelcastInstance hazelcastInstance;
    
    /** The Hazelcast topic name used for messaging between cluster nodes */
    private String hazelcastTopicName;
    
    /** The Hazelcast Topic resolved during Persister init */
    private ITopic<String> clusterTopic = null;
    
    /** Registry of cluster message types to implementation beans */
    private Map<String, ClusterMessageAware> clusterBeans = null;
    
    /** Node identifier - to ensure multicast messages aren't processed by the sender */
    private static final String clusterNodeId = GUID.generate();
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    // Spring Init
    
    private ApplicationContext applicationContext = null;

    /**
     * Set ApplicationContext
     *
     * @param applicationContext    The Spring ApplicationContext
     */
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        this.applicationContext = applicationContext;
    }
    
    /**
     * Bean init method to perform one time setup of the Hazelcast cluster node.
     */
    public void init()
    {
        // validate bean setup
        if (this.hazelcastInstance == null)
        {
            throw new IllegalArgumentException("The 'hazelcastInstance' property (HazelcastInstance) is mandatory.");
        }
        if (this.hazelcastTopicName == null || this.hazelcastTopicName.length() == 0)
        {
            throw new IllegalArgumentException("The 'hazelcastTopicName' property (String) is mandatory.");
        }
        
        // cluster topic initialisation
        ITopic<String> topic = this.hazelcastInstance.getTopic(this.hazelcastTopicName);
        if (topic == null)
        {
            throw new IllegalArgumentException(
                    "Did not find Hazelcast topic with name: '" + this.hazelcastTopicName + "' - cannot init.");
        }
        
        // find the beans that are interested in cluster messages and register them with the service
        Map<String, ClusterMessageAware> beans = this.applicationContext.getBeansOfType(ClusterMessageAware.class);
        this.clusterBeans = new HashMap<>();
        for (final String id: beans.keySet())
        {
            final ClusterMessageAware bean = beans.get(id);
            final String messageType = bean.getClusterMessageType();
            // beans that do not specify a message type can still post messages via the service or just keep a reference
            // but they are not registered in the list of handler beans that can accept cluster messages
            if (messageType != null)
            {
                if (this.clusterBeans.containsKey(messageType))
                {
                    throw new IllegalStateException("ClusterMessageAware bean with id '" + id +
                            "' attempted to register with existing Message Type: " + messageType);
                }
                this.clusterBeans.put(messageType, bean);
            }
            bean.setClusterService(this);
        }
        if (logger.isDebugEnabled())
        {
            logger.debug("Registered beans for cluster messages:");
            for (final String id: beans.keySet())
            {
                logger.debug(id + " [" + beans.get(id).getClusterMessageType() + "]");
            }
        }
        
        // start listening for cluster messages
        this.clusterTopic = topic;
        this.clusterTopic.addMessageListener(this);
        
        logger.info("Init complete for Hazelcast cluster - listening on topic: " + hazelcastTopicName);
    }
    
    
    /////////////////////////////////////////////////////////////////
    // Hazelcast Cluster message send and receive
    
    /**
     * Push message out to the cluster - multicast or direct TCP depending on Hazelcast config.
     */
    @Override
    public void publishClusterMessage(String messageType, Map<String, Serializable> payload)
    {
        // construct the message object from the payload
        final ClusterMessage msg = new ClusterMessageImpl(messageType, payload);
        
        // serialise it to plain text for transmission
        final String serialised = msg.toString();
        if (logger.isDebugEnabled())
            logger.debug("Pushing message:\r\n" + serialised);
        
        // push the message out to the Hazelcast topic cluster
        this.clusterTopic.publish(serialised);
    }
    
    /**
     * Hazelcast MessageListener implementation - called when a message is received from a cluster node
     * 
     * @param message   Cluster message JSON string
     */
    @Override
    public void onMessage(final Message<String> message)
    {
        final boolean debug = logger.isDebugEnabled();
        
        // process message objects and extract the payload
        final String msg = message.getMessageObject();
        final MessageProcessor proc = new MessageProcessor(msg);
        if (!proc.isSender())
        {
            if (debug) logger.debug("Received message of type:" + proc.getMessageType() + "\r\n" + msg);
            
            // call an implementation of a message handler bean
            final ClusterMessageAware bean = this.clusterBeans.get(proc.getMessageType());
            if (bean != null)
            {
                bean.onClusterMessage(proc.getMessagePayload());
            }
            else
            {
                logger.warn("Received message of unknown type - no handler bean found: " + proc.getMessageType());
            }
        }
    }
    
    
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
        Map<String, Serializable> getPayload();
    }
    
    /**
     * Abstract base class for cluster messages.
     * <p>
     * Wraps an object payload and the type of the message and handles the marshling
     * of the payload to JSON string message format.
     */
    static class ClusterMessageImpl implements ClusterMessage
    {
        /** message type */
        final private String type;
        
        /** payload object */
        final Map<String, Serializable> payload;
        
        /**
         * Constructor
         * 
         * @param type      Type of this message
         * @param payload   Payload object for this message
         */
        ClusterMessageImpl(String type, Map<String, Serializable> payload)
        {
            this.type = type;
            this.payload = payload;
        }
        
        /**
         * @return the payload map for the message
         */
        public Map<String, Serializable> getPayload()
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
                serialiseMessageObjects(writer, null, (Serializable)this.payload);
                writer.endValue();
                writer.endObject();
                writer.endValue();
                writer.endObject();
                return buffer.toString();
            }
            catch (IOException e)
            {
                throw new IllegalStateException("Failed to serialise cluster message: " + e.getMessage(), e);
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
        static void serialiseMessageObjects(final JSONWriter writer, final String name, final Serializable obj)
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
                Map<String, Serializable> map = (Map<String, Serializable>)obj;
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
                    serialiseMessageObjects(writer, null, (Serializable)item);
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
     * This class is responsible for deserialising a message string into objects.
     * <p>
     * The sender, message type and object payload can then be retrieved for further processing.
     */
    static class MessageProcessor
    {
        private final String sender;
        private final String type;
        private final Map<String, Serializable> payload;
        
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
                this.payload = (Map<String, Serializable>)message.get(ClusterMessage.JSON_PAYLOAD);
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
        
        Map<String, Serializable> getMessagePayload()
        {
            return this.payload;
        }
    }
}
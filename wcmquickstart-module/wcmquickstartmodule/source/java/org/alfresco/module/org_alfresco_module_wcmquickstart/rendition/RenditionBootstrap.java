/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.alfresco.module.org_alfresco_module_wcmquickstart.rendition;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.error.AlfrescoRuntimeException;
import org.alfresco.module.org_alfresco_module_wcmquickstart.model.WebSiteModel;
import org.alfresco.repo.rendition.RenditionDefinitionImpl;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.transaction.RetryingTransactionHelper.RetryingTransactionCallback;
import org.alfresco.service.cmr.action.ParameterDefinition;
import org.alfresco.service.cmr.dictionary.DataTypeDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.rendition.CompositeRenditionDefinition;
import org.alfresco.service.cmr.rendition.RenderingEngineDefinition;
import org.alfresco.service.cmr.rendition.RenditionDefinition;
import org.alfresco.service.cmr.rendition.RenditionService;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.datatype.DefaultTypeConverter;
import org.alfresco.service.namespace.QName;
import org.alfresco.service.transaction.TransactionService;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationContextEvent;
import org.springframework.extensions.surf.util.AbstractLifecycleBean;

/**
 * Rendition bootstrap bean.
 * 
 * @author Roy Wetherall
 */
public class RenditionBootstrap implements ApplicationContextAware, ApplicationListener<ApplicationContextEvent>,
        WebSiteModel
{
    /** Bean lifecycle object */
    private Lifecycle lifecycle = new Lifecycle();

    /** Node service */
    private NodeService nodeService;

    /** Rendition service */
    private RenditionService renditionService;

    /** Dictionary service */
    private DictionaryService dictionaryService;

    /** Transaction service */
    private TransactionService transactionService;

    /** List of bootstrap rendition definitions */
    private List<BootstrapRenditionDefinition> definitions;

    /**
     * Set the node service
     * 
     * @param nodeService
     *            node service
     */
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    /**
     * Sets the rendition service
     * 
     * @param renditionService
     *            rendition service
     */
    public void setRenditionService(RenditionService renditionService)
    {
        this.renditionService = renditionService;
    }

    /**
     * Sets the dictionary service
     * 
     * @param dictionaryService
     *            dictionary service
     */
    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setTransactionService(TransactionService transactionService)
    {
        this.transactionService = transactionService;
    }

    /**
     * Set the list of bootstrap rendition definitions
     * 
     * @param definitions
     *            list of bootstrap rendition definitions
     */
    public void setDefinitions(List<BootstrapRenditionDefinition> definitions)
    {
        this.definitions = definitions;
    }

    /**
     * @see org.springframework.context.ApplicationContextAware#setApplicationContext(org.springframework.context.ApplicationContext)
     */
    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException
    {
        lifecycle.setApplicationContext(applicationContext);
    }

    /**
     * Initialises the rendition definitions into the repository.
     */
    private void initRenditions()
    {
        if (definitions != null)
        {
            for (final BootstrapRenditionDefinition definition : definitions)
            {
                
                
                
                // Rendition Definitions are persisted underneath the Data Dictionary for which Group ALL
                // has Consumer access by default. However, we cannot assume that that access level applies for all deployments. See ALF-7334.
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Void>()
                    {
                        @Override
                        public Void doWork() throws Exception
                        {
                            // Clear any existing defintions with the same name
                            RenditionDefinition existingRenDef = renditionService.loadRenditionDefinition(definition.getQName());
                            
                            if (existingRenDef != null)
                            {
                                nodeService.deleteNode(existingRenDef.getNodeRef());
                            }
                            
                            // Get the rendition definition
                            RenditionDefinition renDef = getRenditionDefinition(definition);
                            
                            // Save the rendition
                            renditionService.saveRenditionDefinition(renDef);
                            
                            return null;
                        }
                    }, AuthenticationUtil.getSystemUserName());
            }
        }
    }

    /**
     * 
     * @param definition
     * @return
     */
    private RenditionDefinition getRenditionDefinition(BootstrapRenditionDefinition definition)
    {
        RenditionDefinition renDef = null;

        // Get the rendering engine
        RenderingEngineDefinition engine = renditionService.getRenderingEngineDefinition(definition
                .getRenderingEngineName());
        if (engine == null)
        {
            throw new AlfrescoRuntimeException("Rendering engine " + definition.getRenderingEngineName()
                    + " does not exist.");
        }

        // Set the child rendition definitions
        if (definition instanceof BootstrapCompositeRenditionDefinition)
        {
            renDef = renditionService.createCompositeRenditionDefinition(definition.getQName());

            List<BootstrapRenditionDefinition> childDefs = ((BootstrapCompositeRenditionDefinition) definition)
                    .getDefinitions();
            for (BootstrapRenditionDefinition childDef : childDefs)
            {
                RenditionDefinition childRenDef = getRenditionDefinition(childDef);
                ((CompositeRenditionDefinition) renDef).addAction(childRenDef);
            }
        }
        else
        {
            renDef = renditionService.createRenditionDefinition(definition.getQName(), definition
                    .getRenderingEngineName());
        }

        // Set async
        renDef.setExecuteAsynchronously(definition.isExecuteAsynchronously());

        // Set parameters
        Map<String, Serializable> convertedValues = new HashMap<String, Serializable>(definition.getParameters().size());
        convertedValues.put(RenditionDefinitionImpl.RENDITION_DEFINITION_NAME, definition.getQName());
        for (Map.Entry<String, Serializable> entry : definition.getParameters().entrySet())
        {
            String key = entry.getKey();
            Serializable value = entry.getValue();

            // Get the parameter definition
            ParameterDefinition paramDef = engine.getParameterDefintion(key);
            if (paramDef == null)
            {
                throw new AlfrescoRuntimeException("No parameter definition for " + key
                        + " found for rendering engine " + definition.getRenderingEngineName());
            }

            // Get the parameter type definition
            QName paramType = paramDef.getType();
            DataTypeDefinition paramTypeDef = dictionaryService.getDataType(paramType);
            if (paramTypeDef == null)
            {
                throw new AlfrescoRuntimeException("No type definition for parameter " + key + " with type "
                        + paramType.toString() + " and rendering engine " + definition.getRenderingEngineName());
            }
            // Convert the parameter value
            Serializable convertedValue = (Serializable) DefaultTypeConverter.INSTANCE.convert(paramTypeDef, value);

            // Set the parameter value
            convertedValues.put(entry.getKey(), convertedValue);
        }
        renDef.setParameterValues(convertedValues);

        return renDef;
    }

    /**
     * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)
     */
    @Override
    public void onApplicationEvent(ApplicationContextEvent event)
    {
        lifecycle.onApplicationEvent(event);
    }

    /**
     * Lifecycle implementation
     */
    private class Lifecycle extends AbstractLifecycleBean
    {
        /**
         * @see org.alfresco.util.AbstractLifecycleBean#onBootstrap(org.springframework.context.ApplicationEvent)
         */
        @Override
        protected void onBootstrap(ApplicationEvent event)
        {
            if (!transactionService.isReadOnly())
            {
                AuthenticationUtil.runAs(new AuthenticationUtil.RunAsWork<Object>()
                {
                    @Override
                    public Object doWork() throws Exception
                    {
                        return transactionService.getRetryingTransactionHelper().doInTransaction(
                                new RetryingTransactionCallback<Object>()
                        {
                            @Override
                            public Object execute() throws Throwable
                            {
                                initRenditions();
                                return null;
                            }
                        });
                    }
                }, AuthenticationUtil.getSystemUserName());
            }
        }

        /**
         * @see org.alfresco.util.AbstractLifecycleBean#onShutdown(org.springframework.context.ApplicationEvent)
         */
        @Override
        protected void onShutdown(ApplicationEvent event)
        {
            // Intentionally empty
        }
    }
}

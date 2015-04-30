/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
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

package org.springframework.extensions.surf;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.persister.MultiObjectPersister;
import org.springframework.extensions.webscripts.SearchPath;
import org.springframework.extensions.webscripts.Store;
import org.springframework.extensions.webscripts.processor.FTLTemplateProcessor;
import org.springframework.extensions.webscripts.processor.JSScriptProcessor;

/**
 * <p>This class is configured as a Spring bean in the <code>spring-surf-services-context.xml</code> file to
 * provide a set of preset configurations that can be selected through the <{@code}autowire> element in
 * the Spring Surf configuration of the consuming application. The class sets up the WebScript and template
 * search path services and the default MultiObjectPersister service. Be aware that using this service will
 * have the effect of overriding some elements of the Spring application context defined in the XML configuration
 * files.</p>
 *
 * <p>The following modes are available:</p>
 * <ul>
 * <li>developer</li>
 * <li>production</li>
 * </ul>
 * <p>The following runtimes are available:</p>
 * <ul>
 * <li>classpath</li>
 * <li>webapp</li>
 * <li>local</li>
 * <li>alfresco</li>
 * </ul>
 *
 * @author David Draper
 */
public class AutowireService
{
    private static final Log logger = LogFactory.getLog(AutowireService.class);

    private SearchPath webScriptsSearchPath;
    private SearchPath templatesSearchPath;

    /**
     * <p>This is a list of the <code>org.springframework.extensions.webscripts.Store</code> instances that will
     * get added to the <code>org.springframework.extensions.webscripts.SearchPath</code> used to locate WebScripts
     * for all runtimes. This list will be added to with the contents of other lists depending upon the runtime
     * selected.</p>
     */
    private List<Store> commonWebScriptPaths;
    private List<Store> alfrescoWebScriptPaths;
    private List<Store> webInfWebScriptPaths;
    private List<Store> localWebScriptPaths;

    /**
     * <p>This is a list of the <code>org.springframework.extensions.webscripts.Store</code> instances that will
     * get added to the <code>org.springframework.extensions.webscripts.SearchPath</code> used to locate templates
     * for all runtimes. This list will be added to with the contents of other lists depending upon the runtime
     * selected.</p>
     */
    private List<Store> commonTemplatePaths;
    private List<Store> alfrescoTemplatePaths;
    private List<Store> webInfTemplatePaths;
    private List<Store> localTemplatePaths;
    private List<ModelObjectPersister> commonPersisterPaths;
    private List<ModelObjectPersister> alfrescoPersisterPaths;
    private List<ModelObjectPersister> webInfPersisterPaths;
    private List<ModelObjectPersister> localPersisterPaths;

    private FTLTemplateProcessor webscriptsTemplateProcessor;
    private JSScriptProcessor webscriptsScriptProcessor;
    private FTLTemplateProcessor templatesTemplateProcessor;
    private JSScriptProcessor templatesScriptProcessor;

    /**
     * <p>This method configures the autowire mode. In the default Spring Surf application context there are only
     * 2 available mode: "development" and "production". Each mode is optimised as its name implies.
     *
     * @param autowireModeId
     * @throws Exception
     */
    public void configureMode(WebFrameworkConfigElement webFrameworkConfig) throws Exception
    {
        String autowireModeId = (String) webFrameworkConfig.getAutowireModeId();
        if (autowireModeId != null)
        {
            // Autowire Mode: Development
            if ("developer".equalsIgnoreCase(autowireModeId) ||
                "development".equalsIgnoreCase(autowireModeId))
            {
                // For persister context, turn caching off
                webFrameworkConfig.getPersisterConfigDescriptor().setCacheEnabled(false);
                webFrameworkConfig.getPersisterConfigDescriptor().setCacheCheckDelay(0);

                // Disable caching on "webscripts" freemarker template processor
                webscriptsTemplateProcessor.setUpdateDelay(0);

                // Disable compilation for "webscripts" javascript script processor
                webscriptsScriptProcessor.setCompile(false);

                // Disable caching on "templates" template processor
                templatesTemplateProcessor.setUpdateDelay(0);

                // Disable compilation for "templates" script processor
                templatesScriptProcessor.setCompile(false);
            }

            // Autowire Mode: Production
            if ("production".equalsIgnoreCase(autowireModeId))
            {
                // For persister context, turn caching on
                webFrameworkConfig.getPersisterConfigDescriptor().setCacheEnabled(true);
                webFrameworkConfig.getPersisterConfigDescriptor().setCacheCheckDelay(-1);

                // Enable caching on "webscripts" template processor
                webscriptsTemplateProcessor.setUpdateDelay(60*60*24);

                // Enable compilation for "webscripts" script processor
                webscriptsScriptProcessor.setCompile(true);

                // Enable caching on "templates" template processor
                templatesTemplateProcessor.setUpdateDelay(60*60*24);

                // Enable compilation for "templates" script processor
                templatesScriptProcessor.setCompile(true);
            }
            
            if (logger.isInfoEnabled())
            {
                logger.info("Autowire Mode - " + autowireModeId);
            }
        }
        else
        {
            // If the autowireModeId is set to null then it has not been set, so we cannot configure a mode.
            // This is indicative of the <autowire> (or at the very least its <mode>) element not being found
            // in the Spring Surf configuration.
        }
    }

    /**
     * <p>This method configures the runtime based on the id provided. In the default Spring Surf autowire
     * application context configuration there are 4 different available runtimes:</p>
     * <ul>
     * <li>classpath</li>
     * <li>webapp</li>
     * <li>local</li>
     * <li>alfresco</li>
     * </ul>
     * <p>These runtime options are listed in from most basic to most comprehensive. The more comprehensive the runtime
     * the greater the number of <code>Stores</code> in the <code>SearchPaths</code> and the greater the number of
     * <code>ModelObjectPersisters</code> in the <code>MultiObjectPersister</code>. Each runtime contains the <code>Stores</code> 
     * and <code>ModelObjectPersisters</code> of those that precede it in the list. The exception to this rule is the 
     * "alfresco" runtime which currently means to use the application context configured <code>SearchPaths</code> and
     * <code>ModelObjectPersisters</code>.</p> 
     *
     * @param autowireRuntimeId The name of the selected runtime. In order for a runtime to be successfully be configured it should
     * exist in the above list. If it does not the runtime will only contain the common <code>Stores</code> and <code>ModelObjectPersisters</code>.
     * @throws Exception Thrown if the bean has not been configured with "webScriptsSearchPath", "templatesSearchPath" and
     * "multiObjectPersister" properties.
     */
    public void configureRuntime(WebFrameworkConfigElement webFrameworkConfig, MultiObjectPersister multiObjectPersister) throws Exception
    {
        String autowireRuntimeId = (String) webFrameworkConfig.getAutowireRuntimeId();
        if (autowireRuntimeId != null)
        {
            if (!autowireRuntimeId.equals("alfresco"))
            {
                // Autowire Runtime: Classpath
                if ("classpath".equalsIgnoreCase(autowireRuntimeId) ||
                    "webapp".equalsIgnoreCase(autowireRuntimeId) ||
                    "local".equalsIgnoreCase(autowireRuntimeId))
                {
                    this.commonWebScriptPaths.addAll(this.alfrescoWebScriptPaths);
                    this.commonTemplatePaths.addAll(this.alfrescoTemplatePaths);
                    this.commonPersisterPaths.addAll(this.alfrescoPersisterPaths);
                }

                // Autowire Runtime: Webapp
                if ("webapp".equalsIgnoreCase(autowireRuntimeId) ||
                    "local".equalsIgnoreCase(autowireRuntimeId))
                {
                    this.commonWebScriptPaths.addAll(this.webInfWebScriptPaths);
                    this.commonTemplatePaths.addAll(this.webInfTemplatePaths);
                    this.commonPersisterPaths.addAll(this.webInfPersisterPaths);
                }

                // Autowire Runtime: Local
                if ("local".equalsIgnoreCase(autowireRuntimeId))
                {
                    this.commonWebScriptPaths.addAll(this.localWebScriptPaths);
                    this.commonTemplatePaths.addAll(this.localTemplatePaths);
                    this.commonPersisterPaths.addAll(this.localPersisterPaths);
                }

                if (this.webScriptsSearchPath != null)
                {
                    this.webScriptsSearchPath.setSearchPath(this.commonWebScriptPaths);
                }
                else
                {
                    throw new Exception("Autowire service has not been configured with a \"webScriptsSearchPath\" property");
                }

                if (this.templatesSearchPath != null)
                {
                    this.templatesSearchPath.setSearchPath(this.commonTemplatePaths);
                }
                else
                {
                    throw new Exception("Autowire service has not been configured with a \"templatesSearchPath\" property");
                }


                if (multiObjectPersister != null)
                {
                    multiObjectPersister.setPersisters(this.commonPersisterPaths);
                }
                else
                {
                    throw new Exception("Autowire service has not been configured with a \"multiObjectPersister\" property");
                }
            }
            else
            {
                // If the runtime is set to "alfresco" then this essentially means: "do nothing". This is not exactly obvious
                // from the name but has been preserved to maintain the original behaviour of the autowire service and not 
                // break Alfresco Share.
            }
        }
        else
        {
            // If the autowireRuntimeId is set to null, then is has not been set so we cannot configure the runtime.
            // This is indicative of the <autowire> (or at the very least its <runtime>) element not being found
            // in the Spring Surf configuration.
        }

        if (logger.isInfoEnabled())
        {
            if (autowireRuntimeId != null)
            {
                logger.info("Autowire Runtime - " + autowireRuntimeId);
            }

            // info reporting for search paths and persister
            reportSearchPath(this.templatesSearchPath);
            reportSearchPath(this.webScriptsSearchPath);
            reportPersister(multiObjectPersister);
        }
        
        
    }

    /**
     * Logs info-level searchpath configuration info during autowire startup
     *
     * @param searchPath
     */
    protected void reportSearchPath(SearchPath searchPath)
    {
        if (logger.isInfoEnabled())
        {
            StringBuilder s = new StringBuilder();
            s.append("[");

            if (searchPath != null)
            {
                Iterator<Store> it = searchPath.getStores().iterator();
                while (it.hasNext())
                {
                    Store store = (Store) it.next();

                    s.append("'");
                    s.append(store.toString());
                    s.append("'");

                    if (it.hasNext())
                    {
                        s.append(",");
                    }
                }
            }

            s.append("]");

            logger.info("Search Path: " + searchPath + " = " + s.toString());
        }
    }

    /**
     * Logs info-level persister configuration info during autowire startup
     *
     * @param searchPathId
     */
    protected void reportPersister(ModelObjectPersister persister)
    {
        if (logger.isInfoEnabled())
        {
            StringBuilder s = new StringBuilder();
            s.append("[");

            if (persister != null)
            {
                if (persister instanceof MultiObjectPersister)
                {
                    MultiObjectPersister mop = (MultiObjectPersister) persister;

                    List<ModelObjectPersister> persisters = mop.getPersisters();
                    for (int i = 0; i < persisters.size(); i++)
                    {
                        ModelObjectPersister p = persisters.get(i);

                        s.append("'");
                        s.append(p.getId());
                        s.append("'");

                        if (i + 1 < persisters.size());
                        {
                            s.append(",");
                        }
                    }
                }
            }

            s.append("]");

            logger.info("Persister: " + persister + " = " + s.toString());
        }
    }

    /* **************************************************
     *                                                  *
     * SETTER AND GETTER METHODS FOR BEAN PROPERTIES... *
     *                                                  *
     ************************************************** */

    public void setCommonWebScriptPaths(List<Store> commonWebScriptPaths)
    {
        this.commonWebScriptPaths = commonWebScriptPaths;
    }

    public void setAlfrescoWebScriptPaths(List<Store> alfrescoWebScriptPaths)
    {
        this.alfrescoWebScriptPaths = alfrescoWebScriptPaths;
    }

    public void setWebInfWebScriptPaths(List<Store> webInfWebScriptPaths)
    {
        this.webInfWebScriptPaths = webInfWebScriptPaths;
    }

    public void setLocalWebScriptPaths(List<Store> localWebScriptPaths)
    {
        this.localWebScriptPaths = localWebScriptPaths;
    }

    public void setCommonTemplatePaths(List<Store> commonTemplatePaths)
    {
        this.commonTemplatePaths = commonTemplatePaths;
    }

    public void setAlfrescoTemplatePaths(List<Store> alfrescoTemplatePaths)
    {
        this.alfrescoTemplatePaths = alfrescoTemplatePaths;
    }

    public void setWebInfTemplatePaths(List<Store> webInfTemplatePaths)
    {
        this.webInfTemplatePaths = webInfTemplatePaths;
    }

    public void setLocalTemplatePaths(List<Store> localTemplatePaths)
    {
        this.localTemplatePaths = localTemplatePaths;
    }

    public void setCommonPersisterPaths(List<ModelObjectPersister> commonPersisterPaths)
    {
        this.commonPersisterPaths = commonPersisterPaths;
    }

    public void setAlfrescoPersisterPaths(List<ModelObjectPersister> alfrescoPersisterPaths)
    {
        this.alfrescoPersisterPaths = alfrescoPersisterPaths;
    }

    public void setWebInfPersisterPaths(List<ModelObjectPersister> webInfPersisterPaths)
    {
        this.webInfPersisterPaths = webInfPersisterPaths;
    }

    public void setLocalPersisterPaths(List<ModelObjectPersister> localPersisterPaths)
    {
        this.localPersisterPaths = localPersisterPaths;
    }

    public void setWebscriptsTemplateProcessor(FTLTemplateProcessor webscriptsTemplateProcessor)
    {
        this.webscriptsTemplateProcessor = webscriptsTemplateProcessor;
    }

    public void setWebscriptsScriptProcessor(JSScriptProcessor webscriptsScriptProcessor)
    {
        this.webscriptsScriptProcessor = webscriptsScriptProcessor;
    }

    public void setTemplatesTemplateProcessor(FTLTemplateProcessor templatesTemplateProcessor)
    {
        this.templatesTemplateProcessor = templatesTemplateProcessor;
    }

    public void setTemplatesScriptProcessor(JSScriptProcessor templatesScriptProcessor)
    {
        this.templatesScriptProcessor = templatesScriptProcessor;
    }

    public void setWebScriptsSearchPath(SearchPath webScriptsSearchPath)
    {
        this.webScriptsSearchPath = webScriptsSearchPath;
    }

    public void setTemplatesSearchPath(SearchPath templatesSearchPath)
    {
        this.templatesSearchPath = templatesSearchPath;
    }

    public SearchPath getWebScriptsSearchPath()
    {
        return webScriptsSearchPath;
    }

    public SearchPath getTemplatesSearchPath()
    {
        return templatesSearchPath;
    }

    public List<Store> getCommonWebScriptPaths()
    {
        return commonWebScriptPaths;
    }

    public List<Store> getAlfrescoWebScriptPaths()
    {
        return alfrescoWebScriptPaths;
    }

    public List<Store> getWebInfWebScriptPaths()
    {
        return webInfWebScriptPaths;
    }

    public List<Store> getLocalWebScriptPaths()
    {
        return localWebScriptPaths;
    }

    public List<Store> getCommonTemplatePaths()
    {
        return commonTemplatePaths;
    }

    public List<Store> getAlfrescoTemplatePaths()
    {
        return alfrescoTemplatePaths;
    }

    public List<Store> getWebInfTemplatePaths()
    {
        return webInfTemplatePaths;
    }

    public List<Store> getLocalTemplatePaths()
    {
        return localTemplatePaths;
    }

    public List<ModelObjectPersister> getCommonPersisterPaths()
    {
        return commonPersisterPaths;
    }

    public List<ModelObjectPersister> getAlfrescoPersisterPaths()
    {
        return alfrescoPersisterPaths;
    }

    public List<ModelObjectPersister> getWebInfPersisterPaths()
    {
        return webInfPersisterPaths;
    }

    public List<ModelObjectPersister> getLocalPersisterPaths()
    {
        return localPersisterPaths;
    }

    public FTLTemplateProcessor getWebscriptsTemplateProcessor()
    {
        return webscriptsTemplateProcessor;
    }

    public JSScriptProcessor getWebscriptsScriptProcessor()
    {
        return webscriptsScriptProcessor;
    }

    public FTLTemplateProcessor getTemplatesTemplateProcessor()
    {
        return templatesTemplateProcessor;
    }

    public JSScriptProcessor getTemplatesScriptProcessor()
    {
        return templatesScriptProcessor;
    }
}

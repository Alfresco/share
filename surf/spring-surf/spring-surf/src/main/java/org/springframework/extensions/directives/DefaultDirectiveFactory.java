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
package org.springframework.extensions.directives;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyAggregator;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.DojoDependencyHandler;
import org.springframework.extensions.surf.I18nDependencyHandler;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.ModelObjectService;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.webscripts.LocalWebScriptRuntimeContainer;
import org.springframework.extensions.webscripts.MessagesWebScript;

/**
 * <p>The default Surf bean for instantiating FreeMarker directives.</p>
 * 
 * @author David Draper
 */
public class DefaultDirectiveFactory implements DirectiveFactory
{
    private LocalWebScriptRuntimeContainer webScriptsContainer;
    public void setWebScriptsContainer(LocalWebScriptRuntimeContainer webScriptsContainer)
    {
        this.webScriptsContainer = webScriptsContainer;
    }
    private ConfigService configService;
    public void setConfigService(ConfigService configService)
    {
        this.configService = configService;
    }
    private DojoDependencyHandler dojoDependencyHandler;
    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }
    private WebFrameworkConfigElement webFrameworkConfig;
    public void setWebFrameworkConfig(WebFrameworkConfigElement webFrameworkConfig)
    {
        this.webFrameworkConfig = webFrameworkConfig;
    }
    private DependencyHandler dependencyHandler;
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }
    private DependencyAggregator dependencyAggregator;
    public void setDependencyAggregator(DependencyAggregator dependencyAggregator)
    {
        this.dependencyAggregator = dependencyAggregator;
    }
    private ModelObjectService modelObjectService;
    public void setModelObjectService(ModelObjectService modelObjectService)
    {
        this.modelObjectService = modelObjectService;
    }
    private I18nDependencyHandler i18nDependencyHandler;
    public void setI18nDependencyHandler(I18nDependencyHandler i18nDependencyHandler)
    {
        this.i18nDependencyHandler = i18nDependencyHandler;
    }
    private MessagesWebScript messagesWebScript;
    
    /**
     * <p>Returns the message WebScript.
     * @return
     */
    public MessagesWebScript getMessagesWebScript()
    {
        return messagesWebScript;
    }
    
    /**
     * <p>Sets the {@link MessagesWebScript}.</p>
     * @param messagesWebScript
     */
    public void setMessagesWebScript(MessagesWebScript messagesWebScript)
    {
        this.messagesWebScript = messagesWebScript;
    }

    /**
     * <p>Creates a new {@link MessagesDependencyDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public MessagesDependencyDirective createMessagesDependencyDirective(String directiveName,
                                                                         ModelObject object,
                                                                         ExtensibilityModel extensibilityModel,
                                                                         RequestContext context)
    {
        MessagesDependencyDirective d = new MessagesDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link JavaScriptDependencyDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public JavaScriptDependencyDirective createJavaScriptDependencyDirective(String directiveName,
                                                                             ModelObject object,
                                                                             ExtensibilityModel extensibilityModel, 
                                                                             RequestContext context)
    {
        JavaScriptDependencyDirective d = new JavaScriptDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link CssDependencyDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public CssDependencyDirective createCssDependencyDirective(String directiveName,
                                                               ModelObject object,
                                                               ExtensibilityModel extensibilityModel, 
                                                               RequestContext context)
    {
        CssDependencyDirective d = new CssDependencyDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link ChecksumResourceDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public ChecksumResourceDirective createChecksumResourceDirective(String directiveName,
                                                                     ModelObject object,
                                                                     ExtensibilityModel extensibilityModel, 
                                                                     RequestContext context)
    {
        ChecksumResourceDirective d = new ChecksumResourceDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link AddInlineJavaScriptDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public AddInlineJavaScriptDirective createAddInlineJavaScriptDirective(String directiveName,
                                                                           ModelObject object,
                                                                           ExtensibilityModel extensibilityModel, 
                                                                           RequestContext context)
    {
        AddInlineJavaScriptDirective d = new AddInlineJavaScriptDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link CreateWebScriptWidgetsDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public CreateWebScriptWidgetsDirective createCreateWebScriptsDirective(String directiveName,
                                                                           ModelObject object,
                                                                           ExtensibilityModel extensibilityModel, 
                                                                           RequestContext context)
    {
        CreateWebScriptWidgetsDirective d = new CreateWebScriptWidgetsDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link OutputCSSDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public OutputCSSDirective createOutputCssDirective(String directiveName,
                                                       ModelObject object,
                                                       ExtensibilityModel extensibilityModel, 
                                                       RequestContext context)
    {
        OutputCSSDirective d = new OutputCSSDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link OutputJavaScriptDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public OutputJavaScriptDirective createOutputJavaScriptDirective(String directiveName,
                                                                     ModelObject object,
                                                                     ExtensibilityModel extensibilityModel, 
                                                                     RequestContext context)
    {
        OutputJavaScriptDirective d = new OutputJavaScriptDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link RelocateJavaScriptOutputDirective} directive. </p>
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public RelocateJavaScriptOutputDirective createRelocateJavaScriptDirective(String directiveName,
                                                                               ModelObject object,
                                                                               ExtensibilityModel extensibilityModel, 
                                                                               RequestContext context)
    {
        RelocateJavaScriptOutputDirective d = new RelocateJavaScriptOutputDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link ChromeDetectionDirective}.</p>
     * @param directiveName The name of the directive
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param webFrameworkConfigElement The {@link WebFrameworkConfigElement} for the application.
     * @param context The current {@link RequestContext}
     * @return
     */
    public ChromeDetectionDirective createChromeDetectionDirective(String directiveName,
                                                                   ExtensibilityModel extensibilityModel, 
                                                                   WebFrameworkConfigElement webFrameworkConfigElement,
                                                                   RequestContext context)
    {
        ChromeDetectionDirective d = new ChromeDetectionDirective(directiveName, extensibilityModel, webFrameworkConfigElement, context);
        return d;
    }
    
    /**
     * <p>Creates a new {@link ChromeDetectionDirective}.</p>
     * @param directiveName The name of the directive
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param webFrameworkConfigElement The {@link WebFrameworkConfigElement} for the application.
     * @param context The current {@link RequestContext}
     * @return
     */
    public StandaloneWebScriptWrapper createStandaloneWebScriptWrapperDirective(String directiveName,
            ModelObject object,
            ExtensibilityModel extensibilityModel, 
            RequestContext context)
    {
        StandaloneWebScriptWrapper d = new StandaloneWebScriptWrapper(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        return d;
    }
    
    /**
     * <p>Sets up a directive with the core objects needed.</p>
     * 
     * @param directive
     * @param object
     * @param context
     */
    protected void setupDirective(AbstractDependencyExtensibilityDirective directive,
                                ModelObject object,
                                RequestContext context)
    {
        directive.setModelObject(object);
        directive.setRequestContext(context);
        directive.setDependencyHandler(this.dependencyHandler);
        directive.setDependencyAggregator(this.dependencyAggregator);
        directive.setWebFrameworkConfig(this.webFrameworkConfig);
    }

    public CreateComponentDirective createCreateComponentDirective(String directiveName)
    {
        CreateComponentDirective d = new CreateComponentDirective(directiveName);
        d.setModelObjectService(this.modelObjectService);
        return d;
    }

    public ProcessJsonModelDirective createProcessJsonModelDirective(String directiveName, 
                                                           ModelObject object,
                                                           ExtensibilityModel extensibilityModel, 
                                                           RequestContext context, 
                                                           WebFrameworkConfigElement webFrameworkConfig)
    {
        ProcessJsonModelDirective d = new ProcessJsonModelDirective(directiveName, extensibilityModel);
        setupDirective(d, object, context);
        d.setDojoDependencyHandler(this.dojoDependencyHandler);
        d.setI18nDependencyHandler(this.i18nDependencyHandler);
        d.setConfigService(this.configService);
        d.setWebScriptsContainer(this.webScriptsContainer);
        d.setWebFrameworkConfigElement(this.webFrameworkConfig);
        return d;
    }
    
    public AutoComponentRegionDirective createAutoComponentRegionDirective(String directiveName,
                                                                           RequestContext context, 
                                                                           RenderService renderService)
    {
        AutoComponentRegionDirective d = new AutoComponentRegionDirective(directiveName);
        d.setRenderService(renderService);
        d.setRequestContext(context);
        return d;
    }
}

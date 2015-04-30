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

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.ModelObject;
import org.springframework.extensions.surf.RequestContext;
import org.springframework.extensions.surf.extensibility.ExtensibilityModel;
import org.springframework.extensions.surf.render.RenderService;
import org.springframework.extensions.webscripts.MessagesWebScript;

public interface DirectiveFactory
{
    /**
     * <p>The {@link MessagesWebScript} is required for setting up i18n messages so will be needed by the <code>createMessagesDependencyDirective</code>
     * method. It should be set through Spring bean configuration so that it can easily be reconfigured without changing the Surf source code.</p>
     * @return
     */
    public MessagesWebScript getMessagesWebScript();
    
    public MessagesDependencyDirective createMessagesDependencyDirective(String directiveName,
                                                                         ModelObject object,
                                                                         ExtensibilityModel extensibilityModel,
                                                                         RequestContext context);
    
    /**
     * <p>Creates a new {@link JavaScriptDependencyDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public JavaScriptDependencyDirective createJavaScriptDependencyDirective(String directiveName,
                                                                             ModelObject object,
                                                                             ExtensibilityModel extensibilityModel, RequestContext context);
    
    /**
     * <p>Creates a new {@link CssDependencyDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public CssDependencyDirective createCssDependencyDirective(String directiveName,
                                                               ModelObject object,
                                                               ExtensibilityModel extensibilityModel, RequestContext context);

    /**
     * <p>Creates a new {@link ChecksumResourceDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public ChecksumResourceDirective createChecksumResourceDirective(String directiveName,
                                                                     ModelObject object,
                                                                     ExtensibilityModel extensibilityModel, RequestContext context);
    
    /**
     * <p>Creates a new {@link AddInlineJavaScriptDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public AddInlineJavaScriptDirective createAddInlineJavaScriptDirective(String directiveName,
                                                                           ModelObject object,
                                                                           ExtensibilityModel extensibilityModel, RequestContext context);
    
    /**
     * <p>Creates a new {@link CreateWebScriptWidgetsDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public CreateWebScriptWidgetsDirective createCreateWebScriptsDirective(String directiveName,
                                                                           ModelObject object,
                                                                           ExtensibilityModel extensibilityModel, RequestContext context);
    
    /**
     * <p>Creates a new {@link OutputCSSDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public OutputCSSDirective createOutputCssDirective(String directiveName,
                                                       ModelObject object,
                                                       ExtensibilityModel extensibilityModel, RequestContext context);
    
    /**
     * <p>Creates a new {@link OutputJavaScriptDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public OutputJavaScriptDirective createOutputJavaScriptDirective(String directiveName,
                                                                     ModelObject object,
                                                                     ExtensibilityModel extensibilityModel, 
                                                                     RequestContext context);
    
    /**
     * <p>Creates a new {@link RelocateJavaScriptOutputDirective} directive. </p>
     * @param directiveName The name of the directive
     * @param object The current {@link ModelObject} being processed.
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @return
     */
    public RelocateJavaScriptOutputDirective createRelocateJavaScriptDirective(String directiveName,
                                                                               ModelObject object,
                                                                               ExtensibilityModel extensibilityModel, RequestContext context);

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
                                                                   RequestContext context);
    
    public StandaloneWebScriptWrapper createStandaloneWebScriptWrapperDirective(String directiveName,
            ModelObject object,
            ExtensibilityModel extensibilityModel, 
            RequestContext context);
    
    /**
     * <p>Creates a new {@link CreateComponentDirective}.</p>
     * @param directiveName The name of the directive
     * @return
     */
    public CreateComponentDirective createCreateComponentDirective(String directiveName);
    
    /**
     * <p>Creates a new {@link ProcessJsonModelDirective}.</p>
     * @param directiveName The name of the directive
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @param webFrameworkConfig TODO
     * @param webFrameworkConfigElement The {@link WebFrameworkConfigElement} for the application.
     * @return
     */
    public ProcessJsonModelDirective createProcessJsonModelDirective(String directiveName,
                                                                     ModelObject object,
                                                                     ExtensibilityModel extensibilityModel, 
                                                                     RequestContext context, 
                                                                     WebFrameworkConfigElement webFrameworkConfig);
    
    /**
     * <p>Creates a new {@link AutoComponentRegionDirective}.</p>
     * @param directiveName The name of the directive
     * @param extensibilityModel The current {@link ExtensibilityModel} being worked on.
     * @param context The current {@link RequestContext}
     * @param webFrameworkConfig TODO
     * @param webFrameworkConfigElement The {@link WebFrameworkConfigElement} for the application.
     * @return
     */
    public AutoComponentRegionDirective createAutoComponentRegionDirective(String directiveName,
                                                                           RequestContext context, 
                                                                           RenderService renderService);
}

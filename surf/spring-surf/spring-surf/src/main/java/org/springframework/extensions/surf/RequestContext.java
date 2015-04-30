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

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.config.ConfigService;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.directives.CssDependencyDirective;
import org.springframework.extensions.directives.JavaScriptDependencyDirective;
import org.springframework.extensions.surf.extensibility.ExtensibilityModuleHandler;
import org.springframework.extensions.surf.extensibility.HandlesExtensibility;
import org.springframework.extensions.surf.extensibility.SubComponentEvaluation;
import org.springframework.extensions.surf.render.RenderMode;
import org.springframework.extensions.surf.resource.Resource;
import org.springframework.extensions.surf.types.Component;
import org.springframework.extensions.surf.types.Configuration;
import org.springframework.extensions.surf.types.ExtensionModule;
import org.springframework.extensions.surf.types.Page;
import org.springframework.extensions.surf.types.SubComponent;
import org.springframework.extensions.surf.types.SubComponent.RenderData;
import org.springframework.extensions.surf.types.TemplateInstance;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.surf.util.Content;
import org.springframework.extensions.webscripts.connector.CredentialVault;
import org.springframework.extensions.webscripts.connector.User;

/**
 * <p>Represents the context of the original request to the web page.
 * </p><p>
 * This context object is manufactured at the top of the request chain
 * and is then made available to all templates, regions, components,
 * chrome and anything else downstream.
 * </p><p>
 * This object provides a single point of reference for information
 * about the user, the current rendering page and other context.  It
 * provides this information so that individual rendering pieces do
 * not need to load it themselves.
 * </p>
 * @author David Draper
 * @author muzquiano
 */
public interface RequestContext extends Serializable, HandlesExtensibility
{
    public static final String VALUE_HEAD_TAGS = "headTags";
    public static final String VALUE_CREDENTIAL_VAULT = "credential_vault";
    public static final String VALUE_IDENTITY_VAULT = "identity_vault";

    public static final String DEBUG_MODE_VALUE_COMPONENTS = "components";

    // attribute key for storing the request context into the request
    public static final String ATTR_REQUEST_CONTEXT = "requestContext";

    /**
     * Request level marker to the factory impl to override the endpoint used to load user
     * meta-data during the instantiation of the request context.
     */
    public static final String USER_ENDPOINT = "alfUserEndpoint";


    /**
     * Each request context instance is stamped with a unique id
     * @return The id of the request context
     */
    public String getId();

    /**
     * If the site has a configuration XML, then this will return it
     * @return Configuration instance for the site
     */
    public Configuration getSiteConfiguration();

    /**
     * Returns the title of the web site.  This is drawn from the
     * site configuration XML if available.
     *
     * @return
     */
    public String getWebsiteTitle();

    /**
     * Returns the title of the current page.  This is drawn from
     * the current page instance, if set.
     *
     * @return The title of the current page.
     */
    public String getPageTitle();

    /**
     * Sets a custom attribute onto the request context
     *
     * @param key
     * @param value
     */
    public void setValue(String key, Serializable value);

    /**
     * Retrieves a custom value from the request context
     *
     * @param key
     * @return
     */
    public Serializable getValue(String key);

    /**
     * Removes a custom value from the request context
     *
     * @param key
     */
    public void removeValue(String key);

    /**
     * Returns true if a custom value exists in the request context
     *
     * @param key
     * @return true if a custom value exists in the request context
     */
    public boolean hasValue(String key);

    /**
     * Returns the underlying map of the custom key/values pairs
     * stored on this RequestContext instance. Use with caution!
     *
     * @return the underlying map of custom key/value pairs.
     */
    public Map<String, Serializable> getValuesMap();

    /**
     * Retrieves a parameter from the request context
     *
     * @param key
     * @return
     */
    public String getParameter(String key);

    /**
     * Returns true if a parameter exists in the request context
     *
     * @param key
     * @return true if a custom value exists in the request context
     */
    public boolean hasParameter(String key);

    /**
     * Returns a map of parameters
     *
     * @return the underlying map of parameters
     */
    public Map<String, String> getParameters();

    /**
     * Sets the currently executing uri.
     */
    public void setUri(String uri);

    /**
     * @return the currently executing uri.
     */
    public String getUri();

    /**
     * Spring MVC view name - generally the same as the page ID but may
     * be different due to URI template 'pageid' token matching.
     *
     * @param viewName  Spring MVC view name
     */
    public void setViewName(String viewName);

    /**
     * @return Spring MVC view name
     */
    public String getViewName();

    /**
     * If a page instance is currently executing, it can be retrieved
     * from the request context.
     *
     * @return The current page
     */
    public Page getPage();

    /**
     * Sets the currently executing page.
     *
     * @param page
     */
    public void setPage(Page page);

    /**
     * Returns the id of the currently executing page.  If a currently
     * executing page is not set, this will return null.
     *
     * @return The current page id (or null)
     */
    public String getPageId();

    /**
     * Returns the LinkBuilder to be used for the currently executing
     * page.  In general, you will have one link builder per site but
     * this hook allows for the possibility of multiple.
     *
     * @return
     */
    public LinkBuilder getLinkBuilder();

    /**
     * Returns the root page for a site.  A root page is designated
     * if it either has a root-page property in its XML or the site
     * configuration has specifically designated a root page.
     *
     * @return The root page of the application
     */
    public Page getRootPage();

    /**
     * Returns the current executing template.
     *
     * @return
     */
    public TemplateInstance getTemplate();

    /**
     * Sets the current executing template.
     *
     * @return
     */
    public void setTemplate(TemplateInstance currentTemplate);

    /**
     * Returns the id of the currently executing template.
     * If no template is set, this will return null.
     *
     * @return The current template id or null
     */
    public String getTemplateId();

    /**
     * Returns the id of the current object
     * If no object has been set, then the id will be null.
     *
     * @return The id of the current object
     */
    public String getCurrentObjectId();

    /**
     * Returns the current object
     * If no object has been set, then null is returned
     *
     * @return The current object
     */
    public Resource getCurrentObject();

    /**
     * Sets the current object
     *
     * @param object
     */
    public void setCurrentObject(Resource object);

    /**
     * Returns the current format id
     *
     * @return
     */
    public String getFormatId();

    /**
     * Sets the current format id
     *
     * @param formatId
     */
    public void setFormatId(String formatId);

    /**
     * Sets the current user for this request
     * @param user
     */
    public void setUser(User user);

    /**
     * Returns the current user
     *
     * @return
     */
    public User getUser();

    /**
     * Returns the current user id
     *
     * @return
     */
    public String getUserId();

    /**
     * Returns the credential vault
     *
     * @return
     */
    public CredentialVault getCredentialVault();

    /**
     * @return the current Theme object or null if not set
     */
    public Theme getTheme();

    /**
     * Sets the theme
     *
     * @param theme
     */
    public void setTheme(Theme theme);

    /**
     * Returns the current theme id
     */
    public String getThemeId();

    /**
     * Returns the components that were bound to this and any of its parent context
     * during the rendering.  This is useful to determine what other components
     * are configured on the current page.
     *
     * If no rendering components are set, null will be returned
     *
     * @return  An array of Component objects
     */
    public Component[] getRenderingComponents();

    /**
     * Indicates that the given component is being rendered as part of
     * the rendering execution for this and any parent rendering context.
     *
     * @param component The component that is being rendered
     */
    public void setRenderingComponent(Component component);

    /**
     * Returns the method of the incoming request
     *
     * @return request method
     */
    public String getRequestMethod();

    /**
     * Returns the body of the incoming POST content
     * This is applicable for multipart form requests
     *
     * @return content
     */
    public Content getRequestContent();

    /**
     * <p>Returns the protocol (e.g. HTTP/HTTPS) of the request.</p>
     * @return
     */
    public String getRequestScheme();
    
    /**
     * Release any resources held by the request context
     *
     * As part of the contract for a RequestContext object, this will only ever be called once
     * and no further method calls will be made to the RequestContext object.
     */
    public void release();

    /**
     * Returns the spring mvc model
     *
     * @return model
     */
    public Map<String, Object> getModel();

    /**
     * Sets the spring mvc model
     *
     * @param model
     */
    public void setModel(Map<String, Object> model);

    /**
     * Returns the service registry
     *
     * @return services registry
     */
    public WebFrameworkServiceRegistry getServiceRegistry();

    /**
     * Returns the model object service
     *
     * @return model object service
     */
    public ModelObjectService getObjectService();

    /**
     * Returns the request context path
     *
     * @return context path
     */
    public String getContextPath();

    public String getServletContextPath();
    
    public void setServletContextPath(String path);
    
    
    /**
     * Retrieves an attribute from the request context
     *
     * @param key
     * @return
     */
    public Serializable getAttribute(String key);

    /**
     * Returns true if an attribute exists in the request context
     *
     * @param key
     * @return whether the attribute exists
     */
    public boolean hasAttribute(String key);

    /**
     * Returns a map of attributes
     *
     * @return the underlying map of attributes
     */
    public Map<String, Serializable> getAttributes();

    /**
     * Retrieves an header from the request context
     *
     * @param key
     * @return
     */
    public String getHeader(String key);

    /**
     * Returns true if an header exists in the request context
     *
     * @param key
     * @return whether the header exists
     */
    public boolean hasHeader(String key);

    /**
     * Returns a map of headers
     *
     * @return the underlying map of headers
     */
    public Map<String, String> getHeaders();

    /**
     * Sets the parsed URI tokens
     *
     * @param uriTokens
     */
    public void setUriTokens(Map<String,String> uriTokens);

    /**
     * Retrieves any URI tokens parsed in the view resolver.
     *
     * @return uri tokens
     */
    public Map<String, String> getUriTokens();

    /**
     * <p>A <code>RenderMode</code> can be set by a ComponentTypes to allow different outputs
     * to be rendered when requesting a Component directly by specifying a "mode" request
     * parameter. The RenderMode defaults to <code>RenderMode.VIEW</code> but will be set
     * in the context if the "mode" request parameter is detected when a component is
     * directly requested to be rendered.</p>
     *
     * @return The <code>RenderMode</code> for the current context.
     */
    public RenderMode getRenderMode();

    /**
     * <p>This method can be called to update the <code>RenderMode</code> used for rendering.
     * The <code>RenderMode</code> only affects the rendering of Components. Different
     * <code>RenderModes</code> can be configured in ComponentType configuration files.</p>
     *
     * @param renderMode
     */
    public void setRenderMode(RenderMode renderMode);

    /**
     * <p>This method must be implemented to return the <code>HttpServletResponse</code> associated
     * with the <code>RequestContext</code>. However, if the <code>RequestContext</code> has been
     * set to "passive" (via the <code>setPassiveMode</code> method then this should return a
     * <code>FakeHttpServletResponse</code> to ensure that rendering isn't output twice as passive
     * mode is used to calculate dependencies (see
     * {@link org.springframework.extensions.surf.render.bean.TemplateInstanceRenderer}).</p>
     *
     * @return An <code>HttpServletResponse</code> when <code>isPassiveMode()</code> returns false
     * and a <code>FakeHttpServletResponse</code> when <code>isPassiveMode()</code> returns true.
     */
    public HttpServletResponse getResponse();

    /**
     * <p>This method must be implemented to set the supplied <code>HttpServletResponse</code>
     * as the response for the <code>RequestContext</code>. More importantly it should also use
     * the supplied <code>HttpServletResponse</code> to instantiate and set the <code>FakeHttpServletResponse</code>
     * to be returned by the <code>.getResponse()</code> method when the <code>RequestContext</code>
     * has been set to run in passive mode.</p>
     *
     * @param response The <code>HttpServletResponse</code> to associate with the <code>RequestContext</code>.
     */
    public void setResponse(HttpServletResponse response);

    /**
     * <p>This method must be implemented to store whether or not the <code>RequestContext</code> is being
     * used in passive mode or not. Passive mode is used to calculate component dependencies for bound
     * regions before the templates are executed properly</p>
     *
     * @param passiveMode
     */
    public void setPassiveMode(boolean passiveMode);

    /**
     * <p>This method can be used to determine whether or not the <code>RequestContext</code> is set to
     * currently run in passive mode. Passive mode is used to calculate component dependencies for bound
     * regions before the templates are executed properly</p>
     *
     * @return <code>true</code> if the <code>RequestContext</code> has been set to run in passive mode
     * and <code>false</code> otherwise.
     */
    public boolean isPassiveMode();

    /**
     * <p>This method must be implemented to return the result of calling <code>getContentAsString()</code>
     * method of the <code>FakeHttpServletResponse</code> that is returned from the <code>getResponse()</code>
     * method when the <code>RequestContext</code> is in passive mode.</p>
     *
     * @return A String representing the output that would be generated by a real response.
     * @throws UnsupportedEncodingException
     */
    public String getContentAsString() throws UnsupportedEncodingException;
    
    public void setExtensibilityModuleHandler(ExtensibilityModuleHandler extensibilityModuleHandler);
    public void setDependencyHandler(DependencyHandler dependencyHandler);
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement);
    public void setConfigService(ConfigService configService);
    
    /**
     * <p>This method allows JavaScript resources to be added into the same list of dependencies added to by extension 
     * modules. This has only been added as a workaround for the migration of applications from double-pass to single-pass
     * WebScript processing. This allows both WebScripts with and without .head.ftl files to add dependencies.</p> 
     * @param dependency The JavaScript resource to add as a dependency.
     */
    public void addJSDependency(String dependency);
    
    /**
     * <p>This method allows CSS resources to be added into the same list of dependencies added to by extension 
     * modules. This has only been added as a workaround for the migration of applications from double-pass to single-pass
     * WebScript processing. This allows both WebScripts with and without .head.ftl files to add dependencies.</p> 
     * @param dependency The CSS resource to add as a dependency.
     */
    public void addCssDependency(String dependency, String media);
    
    /**
     * <p>Returns the dependencies requested by extension modules as a formatted String. The dependency requests are processed
     * by the associated {@link DependencyHandler} to take advantage of caching and optional checksum identification and
     * CSS data image generation.</p>
     * @return A String of dependency requests.
     */
    public String getExtensionDependencies();
    
    public List<ExtensionModule> getEvaluatedModules();
    public void setEvaluatedProperties(Map<String, Serializable> properties);
    public Map<String, Serializable> getEvaluatedProperties();
    
    /**
     * <p>Retrieves the {@link RenderData} for the specified {@link SubComponent}. This will contain all the information
     * about the {@link SubComponentEvaluation} that ultimately determined the content of the {@link SubComponent} and 
     * the URI, properties, etc. that were used.</p>
     * 
     * @param id The identifier of the {@link SubComponent} to retrieve the {@link RenderData} for.
     * @return The {@link RenderData} for the requested {@link SubComponent} or <code>null</code> if it could not be found.
     */
    public RenderData getSubComponentDebugData(String id);
    
    /**
     * <p>Adds the {@link RenderData} for the specified {@link SubComponent}. This will contain all the information
     * about the {@link SubComponentEvaluation} that ultimately determined the content of the {@link SubComponent} and 
     * the URI, properties, etc. that were used.</p>
     * 
     * @param id The identifier of the {@link SubComponent} to retrieve the {@link RenderData} for.
     * @param The {@link RenderData} to add.
     */
    public void addSubComponentDebugData(String id, RenderData data);
    
    /**
     * <p>Checks whether or not the supplied dependency has already been requested.</p>
     * 
     * @param dep The path to the dependency to check.
     * @return <code>true</code> if the dependency has already been requested and <code>false</code> otherwise.
     */
    public boolean dependencyAlreadyRequested(String dep);
    
    /**
     * <p>This method can be used to indicate that the supplied dependency has been requested by other means.
     * This is provided to ensure that dependencies requested directly on the output stream (e.g. via the 
     * {@link JavaScriptDependencyDirective}, {@link CssDependencyDirective}, etc.)</p>
     * 
     * @param dep The path to the dependency to mark as requested.
     */
    public void markDependencyAsRequested(String dep);
}

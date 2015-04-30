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

package org.springframework.extensions.webscripts;

import java.io.IOException;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.DependencyHandler;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.extensions.surf.util.StringBuilderWriter;
import org.springframework.extensions.webscripts.json.JSONWriter;

/**
 * WebScript responsible for returning a JavaScript response containing a JavaScript
 * associative array of all I18N messages name/key pairs installed on the web-tier.
 * <p>
 * The JavaScript object is created as 'SpringSurf.messages' - example usage:
 * <code>
 * var msg = SpringSurf.messages["messageid"];
 * </code>
 * 
 * @author Kevin Roast
 */
public class MessagesWebScript extends AbstractWebScript
{
    /**
     * As WebScript beans as singletons, we can create a new Date() once when runtime
     * instantiates bean and use this as the "last modified" for the global messages
     * WebScript response Cache value.
     */
    final protected Cache cache;
    
    /**
     * The response is built once per locale and cached - they do not change for the life
     * of the server instance.
     */
    final protected Map<String, String> messages = new HashMap<String, String>(8);
    
    /**
     * When configured to use checksum dependencies this will contain a mapping of the 
     * locale to the last checksum generated for the cached message contents. 
     */
    final protected Map<String, String> localeToChecksum = new HashMap<String, String>(8);
    
    
    
    /** Lock object for cache construction */
    final private ReadWriteLock cacheLock = new ReentrantReadWriteLock();
    
    /**
     * <p>Lock object for message content checksums mapped against locales<p>
     */
    final private ReadWriteLock checksumLock = new ReentrantReadWriteLock();
    
    /**
     * <p>Required to determine whether or not Surf is running in "useChecksumDependencies" mode. This variable
     * should be set through the Spring application context configuration.</p>
     */
    private WebFrameworkConfigElement webFrameworkConfigElement;
    
    /**
     * <p>Setter provided to allow the Spring framework to set the {@link WebFrameworkConfigElement}.</p>
     * @param webFrameworkConfigElement
     */
    public void setWebFrameworkConfigElement(WebFrameworkConfigElement webFrameworkConfigElement)
    {
        this.webFrameworkConfigElement = webFrameworkConfigElement;
    }

    /**
     * <p>Used to generate checksums from the i18n messages contents. This is only required when Surf is
     * run in "useChecksumDependencies" mode. It should be set through the Spring application context
     * configuration.</p> 
     */
    private DependencyHandler dependencyHandler;
    
    /**
     * <p>Setter provided to allow the Spring framework to set the link {@link DependencyHandler}.</p>
     * @param dependencyHandler
     */
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }

    /**
     * Construction
     */
    public MessagesWebScript()
    {
        cache = new Cache();
        cache.setNeverCache(false);
        cache.setMustRevalidate(true);
        cache.setLastModified(new Date());
        cache.setMaxAge(6000L);
    }
    
    /**
     * Execute the webscript and return the cached JavaScript response
     */
    public void execute(WebScriptRequest req, WebScriptResponse res) throws IOException
    {
        res.setContentType(Format.JAVASCRIPT.mimetype() + ";charset=UTF-8");
        res.setCache(cache);
        
        String locale = req.getParameter("locale");
        if (locale == null || locale.length() == 0)
        {
            throw new WebScriptException("Locale parameter is mandatory.");
        }
        
        // Create a key to use on the cache...
        String cacheKey = this.generateCacheKey(locale);
        
        // TODO: Currently the i18n messages are fixed for the up-time of the server - but if and when extensibility support is made available for dynamically altering the result it will be necessary to check the checksum 
        
        String result = "";
        this.cacheLock.readLock().lock();
        try
        {
            // test the cache for this locale
            result = messages.get(cacheKey);
            if (result == null)
            {
                this.cacheLock.readLock().unlock();
                this.cacheLock.writeLock().lock();
                try
                {
                    // Check to see whether or not checksum dependencies are being used. When they are we want
                    // to be able to provide a checksum for the last generated messages to ensure that all 
                    // requests are for up-to-date content and that the browser cannot use stale cached data.
                    if (this.webFrameworkConfigElement != null && this.webFrameworkConfigElement.useChecksumDependencies())
                    {
                        // Construct the result from a prefix, the messages and a suffix. We need to build
                        // the result this way when using checksum dependencies so that the MessagesDependencyDirective
                        // can obtain the messages without needing to invoke a WebScript or without requiring the
                        // WebScriptRequest and WebScriptResponse...
                        StringBuilder sb = new StringBuilder();
                        sb.append(getMessagesPrefix(req, res, locale));
                        String messages = generateMessages(locale); // By default this will generate the checksum and map it to the locale
                        sb.append(messages);
                        sb.append(getMessagesSuffix(req, res, locale));
                        result = sb.toString();
                    }
                    else
                    {
                        result = generateMessages(req, res, locale);
                    }
                    
                    // cache result for this locale
                    messages.put(cacheKey, result);
                }
                finally
                {
                    this.cacheLock.readLock().lock();
                    this.cacheLock.writeLock().unlock();
                }
            }
        }
        finally
        {
            this.cacheLock.readLock().unlock();
        }
        res.getWriter().write(result);
        res.getWriter().flush();
        res.getWriter().close();
    }

    /**
     * It is necessary to include the protocol as well as the locale as the cache key.
     * This is because the response could be different for HTTP versus HTTPS (in particular when
     * Surf is run with Alfresco it will return an image with the appropriate protocol) - see ALF-16900
     * @param protocol
     * @param locale
     * @return
     */
    public String generateCacheKey(String locale)
    {
        String protocol = ThreadLocalRequestContext.getRequestContext().getRequestScheme();
        String cacheKey = protocol + "_" + locale;
        return cacheKey;
    }
    
    /**
     * <p>Attempts to retrieve the checksum for messages generated for the supplied locale. This checksum
     * will only exist if the messages have already been generated (i.e. the <code>generateMessages()</code>
     * method has been called.</p>
     * 
     * @param cacheKey
     * @return
     */
    public String generateCachedLocaleChecksum(String locale)
    {
        String cacheKey = this.generateCacheKey(locale);
        String checksum = getCachedLocaleChecksum(cacheKey);
        if (checksum == null)
        {
            generateMessages(locale);
            checksum = getCachedLocaleChecksum(cacheKey);
        }
        return checksum;
    }
    
    private String getCachedLocaleChecksum(String locale)
    {
        String checksum = null;
        this.checksumLock.readLock().lock();
        try
        {
            String cacheKey = this.generateCacheKey(locale);
            checksum = this.localeToChecksum.get(cacheKey);
        }
        finally
        {
            this.checksumLock.readLock().unlock();
        }
        return checksum;
    }
    
    /**
     * <p>Maps a locale to a checksum in the cache</p>
     * @param cacheKey The locale to map against
     * @param checksum The checksum to map
     */
    protected void setCachedLocaleChecksum(String locale, String checksum)
    {
        try
        {
            this.checksumLock.writeLock().lock();
            String cacheKey = this.generateCacheKey(locale);
            this.localeToChecksum.put(cacheKey, checksum);
        }
        finally
        {
            this.checksumLock.writeLock().unlock();
        }
    }
    
    /**
     * <p>Generates a String prefix of JavaScript that by default sets up a namespace for the messages object
     * to which the messages JSON output should be assigned.</p>
     * 
     * @param req The current {@link WebScriptRequest}
     * @param res The current {@link WebScriptResponse}
     * @param locale The locale for the messages being generated.
     * @return A String containing a JavaScript prefix that defines a namespace and variale to assign the messages
     * object to.
     * @throws IOException
     */
    protected String getMessagesPrefix(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException
    {
        return "if (typeof SpringSurf == \"undefined\" || !SpringSurf) {var SpringSurf = {};}\r\nSpringSurf.messages = SpringSurf.messages || {global: null, scope: {}}\r\nSpringSurf.messages.global = ";
    }
    
    /**
     * <p>Generates a String suffix of JavaScript that defaults to a closing semi-colon.</p>
     * @param req The current {@link WebScriptRequest}
     * @param res The current {@link WebScriptResponse}
     * @param locale The locale for the messages being generated.
     * @return A closing semi-colon
     * @throws IOException
     */
    protected String getMessagesSuffix(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException
    {
        return ";\r\n";
    }
    
    /**
     * <p>Generates a String containing a JSON representation of all the i18n messages. This method 
     * also uses the associated {@link DependencyHandler} to generate a checksum from that String and
     * maps against the supplied locale. This method has been declared as final to ensure that it
     * the checksum is always generated and set.</p> 
     * 
     * @param locale The locale to generate messages for
     * @return A String containing the messages rendered as JSON
     */
    protected final String generateMessages(String locale)
    {
        Writer writer = new StringBuilderWriter(8192);
        JSONWriter out = new JSONWriter(writer);
        try
        {
            out.startObject();
            Map<String, String> messages = I18NUtil.getAllMessages(I18NUtil.parseLocale(locale));
            for (Map.Entry<String, String> entry : messages.entrySet())
            {
                out.writeValue(entry.getKey(), entry.getValue());
            }
            out.endObject();
        }
        catch (IOException jsonErr)
        {
            throw new WebScriptException("Error building messages response.", jsonErr);
        }
        
        String messages = writer.toString();
        if (this.webFrameworkConfigElement.useChecksumDependencies())
        {
            // Generate a checksum from the messages and set it in the cache... 
            String checksum = this.dependencyHandler.generateCheckSum(messages);
            String cacheKey = this.generateCacheKey(locale);
            this.setCachedLocaleChecksum(cacheKey, checksum);
        }
        return messages;
    }
    
    /**
     * Generate the message for a given locale.
     * 
     * @param locale    Java locale format
     * 
     * @return messages as JSON string
     * 
     * @throws IOException
     */
    protected String generateMessages(WebScriptRequest req, WebScriptResponse res, String locale) throws IOException
    {
        Writer writer = new StringBuilderWriter(8192);
        writer.write("if (typeof SpringSurf == \"undefined\" || !SpringSurf) {var SpringSurf = {};}\r\n");
        writer.write("SpringSurf.messages = SpringSurf.messages || {global: null, scope: {}}\r\n");
        writer.write("SpringSurf.messages.global = ");
        JSONWriter out = new JSONWriter(writer);
        try
        {
            out.startObject();
            Map<String, String> messages = I18NUtil.getAllMessages(I18NUtil.parseLocale(locale));
            for (Map.Entry<String, String> entry : messages.entrySet())
            {
                out.writeValue(entry.getKey(), entry.getValue());
            }
            out.endObject();
        }
        catch (IOException jsonErr)
        {
            throw new WebScriptException("Error building messages response.", jsonErr);
        }
        writer.write(";\r\n");
        
        return writer.toString();
    }
}

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

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.WebFrameworkConfigElement;
import org.springframework.extensions.surf.support.ThreadLocalRequestContext;
import org.springframework.extensions.surf.types.Theme;
import org.springframework.extensions.webscripts.ScriptConfigModel;

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;

/**
 * This is a pure LESS driven CSS Theme Handler. 
 * 
 * @author Dave Draper
 */
public class LessForJavaCssThemeHandler extends CssThemeHandler
{
    private static final Log logger = LogFactory.getLog(LessForJavaCssThemeHandler.class);
    
    /**
     * The engine to use for LESS processing.
     */
    protected LessEngine engine;
    
    /**
     * Sets up a new {@link LessEngine} instance.
     */
    public LessForJavaCssThemeHandler()
    {
        this.engine = new LessEngine();
    }

    public static final String LESS_TOKEN = "less-variables";
    
        /**
     * The default LESS configuration. This will be populated with the contents of a file referenced by the 
     * web-framework > defaults > dojo-pages > default-less-configuration.
     */
    private String defaultLessConfig = null;
    
    /**
     * Returns the current default LESS configuration. If it has not previously been retrieved then it will
     * attempt to load it.
     * 
     * @return A String containing the default LESS configuration variables.
     */
    @SuppressWarnings("unchecked")
    protected String getDefaultLessConfig()
    {
        final RequestContext rc = ThreadLocalRequestContext.getRequestContext();
        if (this.defaultLessConfig == null)
        {
            String defaultLessConfigPath = null;
            ScriptConfigModel config = rc.getExtendedScriptConfigModel(null);
            Map<String, ConfigElement> configs = (Map<String, ConfigElement>)config.getScoped().get("WebFramework");
            if (configs != null)
            {
                WebFrameworkConfigElement wfce = (WebFrameworkConfigElement) configs.get("web-framework");
                defaultLessConfigPath = wfce.getDojoDefaultLessConfig();
            }
            else
            {
                defaultLessConfigPath = this.getWebFrameworkConfigElement().getDojoDefaultLessConfig();
            }
            try
            {
                InputStream in = this.getDependencyHandler().getResourceInputStream(defaultLessConfigPath);
                if (in != null)
                {
                    this.defaultLessConfig = this.getDependencyHandler().convertResourceToString(in);
                }
                else
                {
                    if (logger.isErrorEnabled())
                    {
                        logger.error("Could not find the default LESS configuration at: " + defaultLessConfigPath);
                    }
                    // Set the configuration as the empty string as it's not in the configured location
                    this.defaultLessConfig = "";
                }
            }
            catch (IOException e)
            {
                if (logger.isErrorEnabled())
                {
                    logger.error("An exception occurred retrieving the default LESS configuration from: " + defaultLessConfigPath, e);
                }
            }
        }
        return defaultLessConfig;
    }
    
    /**
     * Looks for the LESS CSS token which should contain the LESS style variables that 
     * can be applied to each CSS file. This will be prepended to each CSS file processed.
     * 
     * @return The String of LESS variables.
     */
    public String getLessVariables()
    {
        String variables = this.getDefaultLessConfig();
        Theme currentTheme = ThreadLocalRequestContext.getRequestContext().getTheme();
        if (currentTheme == null)
        {
            currentTheme = ThreadLocalRequestContext.getRequestContext().getObjectService().getTheme("default");
        }
        String themeVariables = currentTheme.getCssTokens().get(LessForJavaCssThemeHandler.LESS_TOKEN);
        if (themeVariables != null)
        {
            // Add a new line just to make sure the first theme specific variable isn't appended to 
            // the end of the last default variable!
            variables += "\n" + themeVariables;
        }
        return variables;
    }
    
    /**
     * Overrides the default implementation to add LESS processing capabilities.
     * 
     * @param path The path of the file being processed (used only for error output)
     * @param cssContents The CSS to process
     * @throws IOException when accessing file contents.
     */
    @Override
    public String processCssThemes(String path, StringBuilder cssContents) throws IOException
    {
        String compiledCss = null;
        String fullCSS = this.getLessVariables() + cssContents;
        try
        {
            compiledCss = this.engine.compile(fullCSS);
        }
        catch (LessException e)
        {
            compiledCss = "/*" + LessForJavaCssThemeHandler.logLessException(e, path) + "*/\n\n " + cssContents;
            
        }
        catch (ClassCastException e)
        {
            compiledCss = "/*" + LessForJavaCssThemeHandler.logLessException(e, path) + "*/\n\n " + cssContents;
        }
        return compiledCss;
    }

    /**
     * This function is used to log exceptions that occur during LESS compilation. Unfortunately the
     * {@link LessException} that is thrown from the {@link LessEngine} does not capture all exception
     * eventualities. When a JavaScript error occurs in Rhino this can result in a {@link ClassCastException}
     * which needs to be caught separately. Currently Surf still supports Java 6 so cannot process
     * multiple exceptions so the error handling has been abstracted to a helper method. The method is static
     * because it is also used by the {@link HybridCssThemeHandler}. Inheritance is not possible because
     * the {@link HybridCssThemeHandler} needs to extend the {@link CssThemeHandler}.
     * 
     * @param e The exception that has been thrown.
     * @param path The path being processed that caused the exception
     * @return The error message generated
     */
    public static String logLessException(Exception e, String path)
    {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String errorMsg = "LESS for Java Engine error compiling: '" + path + "': " + sw.toString();
        if (logger.isErrorEnabled())
        {
            logger.error(errorMsg);
        }
        return errorMsg;
    }
}

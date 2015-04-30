/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * This is the rule that is used to detect Dojo dependencies defined by the standard "define" function call in Dojo source
 * files. It is also the class that should be inherited from when defining other rule classes.
 *  
 * @author David Draper
 */
public class DojoDependencyRule
{
    /**
     * The regular expression {@link Pattern} that defines how to detect a group of dependencies. 
     */
    private String declarationRegex;
    
    /**
     * The regular expression {@link Pattern} that defines how to detect a group of dependencies. 
     */
    private Pattern declarationRegexPattern;
    
    /**
     * The regular expression {@link Pattern} that defines how to detect a single dependency within a group.
     */
    private String dependencyRegex;
    
    /**
     * The regular expression {@link Pattern} that defines how to detect a single dependency within a group.
     */
    private Pattern dependencyRegexPattern;
    
    /**
     * The target {@link Matcher} group that identifies the group of dependencies detected by the <code>declarationRegex</code>
     */
    private int targetGroup;
    
    /**
     * A {@link DojoDependencyHandler} is required for accessing Dojo configuration and processing dependencies.
     */
    private DojoDependencyHandler dojoDependencyHandler;
    
    /**
     * A {@link DependencyHandler} is required for general dependency resource handling actions.
     */
    private DependencyHandler dependencyHandler;

    
    /**
     * This is a map of Regular Expression {@link Pattern} instances that identify tokens to be replaced
     * mapped to the replacement value. This is populated by the default constructor to just contain the
     * resource controller token.
     */
    private Map<Pattern, String> tokenMap = new HashMap<Pattern, String>(1);
    
    /**
     * Gets the token map for replacing tokens in dependencies.
     * 
     * @return
     */
    public Map<Pattern, String> getTokens()
    {
        return this.tokenMap;
    }
    
    /**
     * Replaces any tokens in the supplied input String. This currently isn't used by any of the default
     * Surf dependency rules but has been left for extensions to make use of.
     * 
     * @param input
     * @return The input String with tokens replaced.
     */
    protected String replaceTokens(String input)
    {
       String output = input;
       StringBuffer sb = new StringBuffer();
       Map<Pattern, String> tokens = getTokens();
       if (tokens != null)
       {
           for (Entry<Pattern, String> tokenEntry: tokens.entrySet())
           {
              Matcher matcher = tokenEntry.getKey().matcher(input);
              while (matcher.find())
              {
                 matcher.appendReplacement(sb, tokenEntry.getValue());
              }
              matcher.appendTail(sb);
              output = sb.toString();
           }
       }
       return output;
    }
    
    /**
     * <p>This method processes the regular expression that has been defined to detect a group of dependencies.</p>
     * 
     * @param filePath The path of the dependency that has been requested.
     * @param currentFileContents The current modified file contents
     * @param declarationRegexPattern The regular expression that identifies the overall dependency statement
     * @param targetGroup The group within the declarationRegex that identifies the list of dependencies
     * @param dependencyRegexPattern The regular expression that identifies each dependency within the list of dependencies
     */
    protected void processRegexRules(String filePath,
                                     String fileContents,
                                     DojoDependencies dependencies)
    {
        Matcher m1 = this.declarationRegexPattern.matcher(fileContents);
        while (m1.find())
        {
            if (m1.groupCount() >= targetGroup)
            {
                // The second group in a regex match will contain array of dependencies...
                String deps = m1.group(targetGroup);
                if (deps != null)
                {
                    // This second regex will break the dependencies into each single entry...
                    Matcher m2 = this.dependencyRegexPattern.matcher(deps);
                    while (m2.find())
                    {
                        String dep = m2.group(1);
                        if (dep != null)
                        {
                            processDependency(dep, filePath, fileContents, m2, dependencies);
                        }
                    }
                }
            }
        }
    }
    
    /**
     * 
     * @param dependency The dependency as specified in the file being processed
     * @param sourcePath The source where the file was being processed
     * @param sourceContents TODO
     * @param dependencies The current {@link DojoDependencies} object for source file being processed 
     */
    protected void processDependency(String dependency, 
                                     String sourcePath, 
                                     String sourceContents,
                                     Matcher matcher, 
                                     DojoDependencies dependencies)
    {
        // Get rid of spaces and quotes...
        dependency = dependency.trim();
        if (dependency.startsWith(CssImageDataHandler.DOUBLE_QUOTES) || dependency.startsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
           dependency = dependency.substring(1);
        }
        if (dependency.endsWith(CssImageDataHandler.DOUBLE_QUOTES) || dependency.startsWith(CssImageDataHandler.SINGLE_QUOTE))
        {
            dependency = dependency.substring(0, dependency.length()-1);
        }
        if (dependency.contains("!"))
        {
            // Process all "dojo/text" plugin requests...
            if (dependency.contains("dojo/text!"))
            {
                String textDependency = dependency.substring(dependency.indexOf("!") + 1);
                dependencies.addTextDep(textDependency);
            }
            else if (dependency.contains("/i18n!"))
            {
                String i18nDependency = dependency.substring(dependency.indexOf("!") + 1);
                Locale locale = I18NUtil.getLocale();
                String depPath = this.dojoDependencyHandler.getPath(sourcePath, i18nDependency) + ".js";
                addJavaScriptDependency(dependencies, depPath);
                
                String languageDep = null;
                String languageCountryDep = null;
                int lastSlash = i18nDependency.lastIndexOf("/");
                if (lastSlash == -1)
                {
                    languageDep = locale.getLanguage() + "/" + i18nDependency;
                    languageCountryDep = locale.getLanguage() + "-" + locale.getCountry().toLowerCase() + "/" + i18nDependency;
                }
                else
                {
                    String prefix = i18nDependency.substring(0, lastSlash);
                    String suffix = i18nDependency.substring(lastSlash);
                    languageDep = prefix + "/" + locale.getLanguage() + suffix;
                    languageCountryDep = prefix + "/" + locale.getLanguage() + "-" + locale.getCountry().toLowerCase() + suffix;
                }
                if (languageDep != null)
                {
                    depPath = this.dojoDependencyHandler.getPath(sourcePath, languageDep) + ".js";
                    addJavaScriptDependency(dependencies, depPath);
                }
                if (languageCountryDep != null)
                {
                    depPath = this.dojoDependencyHandler.getPath(sourcePath, languageCountryDep) + ".js";
                    addJavaScriptDependency(dependencies, depPath);
                }
            }
            
            // Add the dependency prior to the "!" as this should still map to a JavaScript file...
            String pluginDep = dependency.substring(0, dependency.indexOf("!"));
            String pluginPath = this.dojoDependencyHandler.getPath(sourcePath, pluginDep) + ".js";
            addJavaScriptDependency(dependencies, pluginPath);
            
            // The dojo/has! dependency supports ternary operators to make decisions based on what should be imported. We 
            // should include all the available options as dependencies regardless of whether or not they might be selected
            // or not (the rationale for this is that the processing on the server will be faster than requiring additional
            // HTTP requests - plus the data will be cached for future requests so will only need to happen once!)
            if (pluginPath.endsWith("dojo/has.js"))
            {
                int eIdx = dependency.indexOf("!");
                if (eIdx != -1)
                {
                    String ternary = dependency.substring(eIdx+1);
                    Pattern p1 = Pattern.compile("([^:|\\?])*");
                    Matcher m1 = p1.matcher(ternary);
                    while (m1.find())
                    {
                        String ternaryDep = m1.group(0);
                        if (ternaryDep != null && !ternaryDep.equals(""))
                        {
                            String ternaryDepPath = this.dojoDependencyHandler.getPath(sourcePath, ternaryDep) + ".js";
                            addJavaScriptDependency(dependencies, ternaryDepPath);
                        }
                    }
                }
            }
        }
        else
        {
            // The dependency referenced in the JavaScript file will not contain the .js extension so we need to append it now
            // to locate the file...
            String depPath = this.dojoDependencyHandler.getPath(sourcePath, dependency) + ".js";
            addJavaScriptDependency(dependencies, depPath);
        }
    }
    
    /**
     * Adds the supplied path to the supplied {@link DojoDependencies} object if the path exists. This uses the {@link DependencyHandler}
     * to check the paths existence and that a {@link InputStream} can be returned from it.
     * @param dependencies The {@link DojoDependencies} to update
     * @param path The path to check and add
     */
    protected void addJavaScriptDependency(DojoDependencies dependencies, String path)
    {
        try
        {
            if (this.dependencyHandler.resourceInCache(path))
            {
                dependencies.addJavaScriptDep(path);
            }
            else
            {
                InputStream in = this.dependencyHandler.getResourceInputStream(path);
                if (in != null)
                {
                    try
                    {
                        dependencies.addJavaScriptDep(path);
                    }
                    finally
                    {
                        in.close();
                    }
                }
            }
        }
        catch (IOException e)
        {
            // It doesn't matter if an exception is thrown. We just won't add the dependency.
        }
    }
    
    /**
     * Adds the supplied path to the supplied {@link DojoDependencies} object if the path exists. This uses the {@link DependencyHandler}
     * to check the paths existence and that a {@link InputStream} can be returned from it.
     * 
     * @param dependencies The {@link DojoDependencies} to update
     * @param path The path to check and add
     */
    protected void addNonAmdJavaScriptDependency(DojoDependencies dependencies, String path)
    {
        try
        {
            if (path.startsWith(CssImageDataHandler.FORWARD_SLASH))
            {
                path = path.substring(1);
            }
            InputStream in = this.dependencyHandler.getResourceInputStream(path);
            if (in != null)
            {
                try
                {
                    dependencies.addNonAmdDep(path);
                }
                finally
                {
                    in.close();
                }
            }
        }
        catch (IOException e)
        {
            // It doesn't matter if an exception is thrown. We just won't add the dependency.
        }
    }
    
    /* **********************************************************************
     *                                                                      *
     * GETTERS AND SETTERS                                                  *
     *                                                                      *
     * **********************************************************************/
     
    public String getDeclarationRegex()
    {
        return this.declarationRegex;
    }

    public Pattern getDeclarationRegexPattern()
    {
        return this.declarationRegexPattern;
    }
    
    public void setDeclarationRegex(String declarationRegex)
    {
        this.declarationRegex = declarationRegex;
        this.declarationRegexPattern = Pattern.compile(declarationRegex);
    }

    public String getDependencyRegex()
    {
        return this.dependencyRegex;
    }
    
    public Pattern getDependencyRegexPattern()
    {
        return this.dependencyRegexPattern;
    }

    public void setDependencyRegex(String dependencyRegex)
    {
        this.dependencyRegex = dependencyRegex;
        this.dependencyRegexPattern = Pattern.compile(dependencyRegex);
    }

    public int getTargetGroup()
    {
        return this.targetGroup;
    }

    public void setTargetGroup(int targetGroup)
    {
        this.targetGroup = targetGroup;
    }

    public void setDojoDependencyHandler(DojoDependencyHandler dojoDependencyHandler)
    {
        this.dojoDependencyHandler = dojoDependencyHandler;
    }

    public DojoDependencyHandler getDojoDependencyHandler()
    {
        return this.dojoDependencyHandler;
    }
    
    public void setDependencyHandler(DependencyHandler dependencyHandler)
    {
        this.dependencyHandler = dependencyHandler;
    }

    public DependencyHandler getDependencyHandler()
    {
        return this.dependencyHandler;
    }
}

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

import java.util.LinkedHashSet;
import java.util.Set;

/**
 * <p>Instances of this class are used to record all of the different Dojo dependencies that are required for any given Dojo JavaScript file.
 * The dependencies are broken down into the following types:
 * <ul><li>JavaScript dependency - typically another Dojo file</li>
 * <li>Text dependency - typically an HTML template for a Dojo widget</li>
 * <li>CSS dependency - typically a CSS file required by a Dojo widget</li>
 * </ul>
 * </p>
 * 
 * @author David Draper
 */
public class DojoDependencies
{
    /**
     * The list of JavaScript dependencies.
     */
    private Set<String> javaScriptDeps = new LinkedHashSet<String>();
    
    /**
     * The list of text dependencies.
     */
    private Set<String> textDeps = new LinkedHashSet<String>();
    
    /**
     * The list of CSS dependencies.
     */
    private Set<CssDependency> cssDeps = new LinkedHashSet<CssDependency>();
    
    /**
     * The list of basic i18n dependencies - these are not the Dojo specific i18n dependencies.
     */
    private Set<I18nDependency> i18nDeps = new LinkedHashSet<I18nDependency>();
    
    /**
     * The list of dependencies that fall outside the Dojo AMD handling remit.
     */
    private Set<String> nonAmdDependencies = new LinkedHashSet<String>();
    
    /**
     * Adds a new JavaScript dependency.
     * @param javaScriptDep
     */
    public void addJavaScriptDep(String javaScriptDep)
    {
        this.javaScriptDeps.add(javaScriptDep);
    }
    
    /**
     * Adds a new text dependency
     * @param textDep
     */
    public void addTextDep(String textDep)
    {
        this.textDeps.add(textDep);
    }
    
    /**
     * Adds a new CSS dependency
     * @param path The dependency path
     * @param mediaType The CSS media type
     */
    public void addCssDep(String path, String mediaType)
    {
        this.cssDeps.add(new CssDependency(path, mediaType));
    }
    
    /**
     * Adds a new i18n dependency
     * @param dep
     */
    public void addI18nDep(String path, String scope)
    {
        this.i18nDeps.add(new I18nDependency(path, scope));
    }
    
    /**
     * Adds a new non-AMD dependency
     * @param dep
     */
    public void addNonAmdDep(String path)
    {
        this.nonAmdDependencies.add(path);
    }
    
    /**
     * @return The set of JavaScript dependencies.
     */
    public Set<String> getJavaScriptDeps()
    {
        return this.javaScriptDeps;
    }
    
    /**
     * @return The set of text dependencies.
     */
    public Set<String> getTextDeps()
    {
        return this.textDeps;
    }
    
    /**
     * @return The set of CSS dependencies.
     */
    public Set<CssDependency> getCssDeps()
    {
        return this.cssDeps;
    }
    
    /**
     * @return The set of i18n dependencies
     */
    public Set<I18nDependency> getI18nDeps()
    {
        return i18nDeps;
    }

    /**
     * @return The set of non-AMD dependencies
     */
    public Set<String> getNonAmdDependencies()
    {
        return nonAmdDependencies;
    }

    @Override
    public String toString()
    {
        return "JavaScript Deps=" + this.javaScriptDeps.toString() + ", CSS Deps=" + this.cssDeps.toString() + ", Text Deps=" + this.textDeps.toString();
    }

    /**
     * An inner class for defining CSS depdendencies.
     * @author David Draper
     *
     */
    public class CssDependency
    {
        private String path;
        private String mediaType;
        public CssDependency(String path, String mediaType)
        {
            this.path = path;
            this.mediaType = mediaType;
        }
        public String getPath()
        {
            return this.path;
        }
        public String getMediaType()
        {
            return this.mediaType;
        }
        @Override
        public String toString()
        {
            return "Path=" + this.path + ", media type=" + this.mediaType;
        }
    }
    
    /**
     * An inner class for defining CSS depdendencies.
     * @author David Draper
     *
     */
    public class I18nDependency
    {
        private String path;
        private String scope;
        public I18nDependency(String path, String scope)
        {
            this.path = path;
            this.scope = scope;
        }
        public String getPath()
        {
            return this.path;
        }
        public String getScope()
        {
            return this.scope;
        }
        @Override
        public String toString()
        {
            return "Path=" + this.path + ", scope=" + this.scope;
        }
    }
}
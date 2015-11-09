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

import com.asual.lesscss.LessEngine;
import com.asual.lesscss.LessException;

/**
 * This is a pure LESS driven CSS Theme Handler. Uses the Java+Rhino implementation of LESS.
 * 
 * @author Dave Draper
 */
public class LessForJavaCssThemeHandler extends LessCssThemeHandler
{
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
            compiledCss = "/*" + logLessException(e, path) + "*/\n\n " + cssContents;
            
        }
        catch (ClassCastException e)
        {
            compiledCss = "/*" + logLessException(e, path) + "*/\n\n " + cssContents;
        }
        return compiledCss;
    }
}

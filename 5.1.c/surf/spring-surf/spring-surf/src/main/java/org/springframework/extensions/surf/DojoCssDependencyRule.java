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

import java.util.regex.Matcher;

/**
 * <p>In order to prevent CSS resources being unnecessarily imported we can specify CSS files relevant to a widget in its JavaScript file.
 * This {@link DojoDependencyRule} allows us to check for requested CSS dependencies so that we can dynamically include them when we build
 * the page. The definition should be written as follows:
 * <pre>cssRequirements: [{cssFile:"./css/LeftAndRight.css",mediaType:"screen"}],</pre>
 * 
 * TODO: We could aim to support different aggregation groups as well.  
 * </p>
 * @author David Draper
 */
public class DojoCssDependencyRule extends DojoDependencyRule
{
    /**
     * <p>Overrides the default implementation to retrieve both the CSS dependency path and the
     * media type that the CSS path should be used against.</p>
     */
    @Override
    protected void processDependency(String dependency, 
                                     String sourcePath, 
                                     String sourceContents,
                                     Matcher matcher, DojoDependencies dependencies)
    {
        String cssDep = matcher.group(1);
        String mediaType = matcher.group(3);
        if (mediaType == null || mediaType.equals(""))
        {
            mediaType = "screen";
        }
        String cssPath = getDojoDependencyHandler().getPath(sourcePath, cssDep);
        dependencies.addCssDep(cssPath, mediaType);
    }
}

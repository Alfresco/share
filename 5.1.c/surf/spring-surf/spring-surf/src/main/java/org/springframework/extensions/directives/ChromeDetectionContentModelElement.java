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

import org.springframework.extensions.surf.extensibility.ContentModelElement;
import org.springframework.extensions.surf.extensibility.impl.DefaultContentModelElement;

/**
 * <p>A {@link ContentModelElement} that will wrap its contents in a <{@code}div> element with a unique
 * id when Surf Region Chrome is disabled.</p>
 * 
 * @author David Draper
 */
public class ChromeDetectionContentModelElement extends DefaultContentModelElement 
{
    // Constants used for outputting the the <div> containing the unique id...
    public static final String OPEN_ELEMENT_1 = "<div id=\"";
    public static final String OPEN_ELEMENT_2 = "\">\n";
    public static final String CLOSE_ELEMENT = "</div>\n";
    
    /**
     * <p>Instantiates a new {@link ChromeDetectionContentModelElement}</p>
     * 
     * @param id The id of the directive.
     * @param directiveName The name of the directive.
     * @param htmlId The unique HTML ID that should be used as the "id" attribute of a wrapping <{@code}div> element
     * when Surf Region Chrome is disabled. If this is <code>null</code> then the <{@code}div> will not be output.
     */
    public ChromeDetectionContentModelElement(String id,
                                              String directiveName,
                                              String htmlId)
    {
        super(id, directiveName);
        this.htmlId = htmlId;
    }

    /**
     * <p>The unique HTML ID to use as the "id" attribute of a wrapping <{@code}div> element when Surf Region Chrome is disabled.</p>
     */
    private String htmlId = null;
    
    /**
     * <p>Overrides the default implementation to wrap the content in a <{@code}div> element with a unique "id" attribute when 
     * Surf Region Chrome is disabled.</p>
     */
    @Override
    public String flushContent()
    {
        StringBuilder content = new StringBuilder();
        if (htmlId != null)
        {
            content.append(OPEN_ELEMENT_1);
            content.append(htmlId);
            content.append(OPEN_ELEMENT_2);
        }
        content.append(super.flushContent());
        if (htmlId != null)
        {
            content.append(CLOSE_ELEMENT);
        }
        
        return content.toString();
    }
}

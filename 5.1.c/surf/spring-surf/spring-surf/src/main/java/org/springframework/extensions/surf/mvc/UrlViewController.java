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

package org.springframework.extensions.surf.mvc;

import org.springframework.web.servlet.mvc.UrlFilenameViewController;

/**
 * Default URL View Controller - extends the Spring UrlFilenameViewController to
 * fix the issue where the view name from a URL is determined to be located up to
 * the last index of the "." character - but does not check for further URL elements.
 * 
 * For example:
 *  /products/something = products/something
 *  /page/mrsmith/dashboard = page/mrsmith/dashboard
 *  /page/mr.smith/dashboard = page/mr <-- incorrect! should resolve to: page/mr.smith/dashboard
 * 
 * @author Kevin Roast
 */
public class UrlViewController extends UrlFilenameViewController
{
    /**
     * Extract the URL filename from the given request URI.
     * @param uri the request URI; for example <code>"/index.html"</code>
     * @return the extracted URI filename; for example <code>"index"</code>
     */
    @Override
    protected String extractViewNameFromUrlPath(final String uri)
    {
        return uri.substring((uri.charAt(0) == '/' ? 1 : 0));
    }
}
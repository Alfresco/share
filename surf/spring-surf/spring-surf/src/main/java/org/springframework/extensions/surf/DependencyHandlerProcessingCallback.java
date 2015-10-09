/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.springframework.extensions.surf;

import java.io.IOException;

/**
 * @author Kevin Roast
 */
public interface DependencyHandlerProcessingCallback
{
    /**
     * Process the given dependency file content.
     * <p>
     * Typically used to provide additional processing steps for a file e.g. compress JavaScript
     * or run a LESS CSS process.
     * 
     * @param handler   Reference to calling DependencyHandler, for bean access
     * @param path      Path to the dependency e.g. to retrieve file extension or parent folder
     * @param contents  Content of the dependency
     * 
     * @return resource content after processing
     */
    public String process(DependencyHandler handler, String path, String contents)
            throws IOException;
}

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

package org.springframework.extensions.surf.exception;

/**
 * Class that describes an exception which has occurred while processing
 * tags in a tag executor.
 * 
 * @author muzquiano
 */
public class TagExecutionException extends Exception
{
    
    /**
     * Instantiates a new tag execution exception.
     * 
     * @param message the message
     */
    public TagExecutionException(String message)
    {
        super(message);
    }

    /**
     * Instantiates a new tag execution exception.
     * 
     * @param message the message
     * @param ex the ex
     */
    public TagExecutionException(String message, Exception ex)
    {
        super(message, ex);
    }
}

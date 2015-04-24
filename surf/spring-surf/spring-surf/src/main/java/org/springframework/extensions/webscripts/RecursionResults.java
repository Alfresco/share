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

/**
 * This class was created for use in the {@link ScriptWidgetUtils} methods for storing
 * data that would otherwise be lost through recursion. Primarily it keeps track of 
 * whether or not the operation is a success or not but new attributes can be added as 
 * necessary over time to capture more data.  
 * 
 * @author Dave Draper
 */
public class RecursionResults
{
    /**
     * Used to keep track whether or not the operation was a success.
     */
    private boolean success = false;
    
    /**
     * Setter to indicate whether or not the result was a success or not.
     * @param success
     */
    public void setSuccess(boolean success)
    {
        this.success = success;
    }

    /**
     * Returns a boolean value indicating whether or not the operation was successful.
     * @return
     */
    public boolean isSuccess()
    {
        return success;
    }
}

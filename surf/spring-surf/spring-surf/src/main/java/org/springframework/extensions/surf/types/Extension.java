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
package org.springframework.extensions.surf.types;

import java.util.List;

import org.dom4j.DocumentException;
import org.springframework.extensions.surf.ModelObject;

public interface Extension extends ModelObject
{
    public static String TYPE_ID = "extension";

    // properties
    public static String PROP_EXTENSION_TYPE = "extension-type";

    public String getExtensionType();
    
    public void setExtensionType(String extensionType);
    
    public static String PROP_MODULES = "modules";
    
    public List<ExtensionModule> getExtensionModules(); 
    
    public ExtensionModule addExtensionModule(String xmlFragment) throws DocumentException; 
    
    public ExtensionModule deleteExtensionModule(String moduleId) throws DocumentException;
}

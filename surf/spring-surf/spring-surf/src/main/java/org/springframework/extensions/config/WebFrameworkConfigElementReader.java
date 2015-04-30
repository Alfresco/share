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

package org.springframework.extensions.config;

import org.dom4j.Element;
import org.springframework.extensions.config.ConfigElement;
import org.springframework.extensions.config.xml.elementreader.ConfigElementReader;

/**
 * Responsible for loading Web Framework configuration settings from
 * the web-framework-config*.xml files that are loaded via the configuration
 * service.
 * 
 * @author muzquiano
 */
public class WebFrameworkConfigElementReader implements ConfigElementReader
{   
   /**
    * Called from the configuration service to handle the loading of the
    * Web Framework configuration XML.
    * 
    * @param element the element
    * 
    * @return the config element
    * 
    * @see org.springframework.extensions.config.xml.elementreader.ConfigElementReader#parse(org.dom4j.Element)
    */
   public ConfigElement parse(Element elem)
   {
       ConfigElement configElement = null;
       if (elem != null)
       {
           configElement = WebFrameworkConfigElement.newInstance(elem);
       }
       return configElement;
   }
}

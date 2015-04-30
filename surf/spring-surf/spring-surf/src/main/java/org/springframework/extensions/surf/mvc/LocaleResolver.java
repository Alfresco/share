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

import java.util.Locale;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.springframework.extensions.surf.util.I18NUtil;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;

/**
 * Alfresco specific extension of Spring AcceptHeaderLocaleResolver.
 * 
 * @author kevinr
 */
public class LocaleResolver extends AcceptHeaderLocaleResolver 
{
    @Override
    public Locale resolveLocale(HttpServletRequest request) 
    {
        Locale locale = Locale.getDefault();
        
        // set language locale from browser header if available
        final String acceptLang = request.getHeader("Accept-Language");
        if (acceptLang != null && acceptLang.length() != 0)
        {
           StringTokenizer t = new StringTokenizer(acceptLang, ",; ");
           
           // get language and convert to java locale format
           String language = t.nextToken().replace('-', '_');
           locale = I18NUtil.parseLocale(language);
        }
        
        // set locale onto Alfresco thread local
        I18NUtil.setLocale(locale);           
        
        return locale;
    }
}

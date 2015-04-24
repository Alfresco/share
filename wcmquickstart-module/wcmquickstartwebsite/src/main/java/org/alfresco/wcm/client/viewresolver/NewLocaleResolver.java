/*
 * Copyright (C) 2005-2010 Alfresco Software Limited.
 *
 * This file is part of the Alfresco Web Quick Start module.
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
package org.alfresco.wcm.client.viewresolver;

import org.springframework.extensions.surf.mvc.LocaleResolver;
import org.springframework.extensions.surf.util.I18NUtil;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;

/**
 * An Alfresco specific extension of Spring AcceptHeaderLocaleResolver,
 *  which prefers the current I18NUtil locale, and falls back on the
 *  browser if unavailable.
 *
 * This uses Spring Surf, and avoids repeated parsing calls.
 * 
 * @author Ian Norton
 */
public class NewLocaleResolver extends LocaleResolver
{
   /**
    * TODO Is this needed still?
    */
   public Locale MlLocaleResolver(HttpServletRequest request)
   {
      return resolveLocale(request);
   }

   public Locale resolveLocale(HttpServletRequest request)
   {
      Locale locale = I18NUtil.getLocale();
      if (locale == null)
      {
         // Ask the normal locale resolver to check the accept language
         //  and other things
         locale = super.resolveLocale(request);
      }
      return locale;
   }
}

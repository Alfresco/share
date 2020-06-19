/*
 * Copyright 2005 - 2020 Alfresco Software Limited.
 *
 * This file is part of the Alfresco software.
 * If the software was purchased under a paid Alfresco license, the terms of the paid license agreement will prevail.
 * Otherwise, the software is provided under the following open source license terms:
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

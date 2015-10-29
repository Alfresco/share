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

package org.springframework.extensions.surf.site.servlet;

import java.util.StringTokenizer;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.extensions.surf.util.I18NUtil;

/**
 * @author kevinr
 * @author muzquiano
 */
public abstract class BaseServlet extends HttpServlet
{
    /**
     * Apply the headers required to disallow caching of the response in the browser
     */
    public static void setNoCacheHeaders(HttpServletResponse response)
    {
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }
    
   /**
    * Apply Client and Repository language locale based on the 'Accept-Language' request header
    */
   public static void setLanguageFromRequestHeader(HttpServletRequest req)
   {
      // set language locale from browser header
      String acceptLang = req.getHeader("Accept-Language");
      if (acceptLang != null && acceptLang.length() != 0)
      {
         StringTokenizer t = new StringTokenizer(acceptLang, ",; ");
         // get language and convert to java locale format
         String language = t.nextToken().replace('-', '_');
         I18NUtil.setLocale(I18NUtil.parseLocale(language));
      }
   }
}
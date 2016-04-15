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

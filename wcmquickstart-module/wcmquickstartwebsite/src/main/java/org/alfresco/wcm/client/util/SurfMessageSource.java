package org.alfresco.wcm.client.util;

import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.extensions.surf.util.I18NUtil;

/**
 * This class adapts the Surf I18NUtil bean as a Spring MessageSource so
 * that it is available for use by the Spring freemarker macros.
 * @author Chris Lack
 */
public class SurfMessageSource implements MessageSource
{
	@Override
    public String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException
    {		
		String lastCode = null;
		for (String code : resolvable.getCodes())
		{
			String message = I18NUtil.getMessage(code, locale);
			if (message != null) return message;
			lastCode = code;
		}
		throw new NoSuchMessageException(lastCode, locale);
    }

	@Override
    public String getMessage(String code, Object[] args, Locale locale) throws NoSuchMessageException
    {
	    String message = I18NUtil.getMessage(code, locale, args);
	    if (message == null) throw new NoSuchMessageException(code, locale);
	    return message;
    }

	@Override
    public String getMessage(String code, Object[] args, String defaultMessage, Locale locale)
    {
		String message = I18NUtil.getMessage(code, args, locale);
		if (message == null) message = defaultMessage;
		return message;
    }
}

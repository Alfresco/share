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

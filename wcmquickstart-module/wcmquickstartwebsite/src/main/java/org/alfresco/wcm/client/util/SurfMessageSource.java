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

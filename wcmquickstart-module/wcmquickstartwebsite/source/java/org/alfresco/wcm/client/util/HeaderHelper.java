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

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.wcm.client.Asset;

public abstract class HeaderHelper 
{
    private ThreadLocal<SimpleDateFormat> httpDateFormat = new ThreadLocal<SimpleDateFormat>() 
    {
        @Override
        protected SimpleDateFormat initialValue()
        {
            return new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        }
    };  	     

    /**
     * This base implementation simply returns true to indicate that the request should be re-rendered.
     * Override in a subclass as necessary
     * @param asset
     * @param request
     * @param response
     * @return boolean true if browser has old copy and so content should be rendered
     * @throws IOException 
     * @throws ParseException 
     */
    public boolean setHeaders(Asset asset, HttpServletRequest request, HttpServletResponse response) 
    {
        return setHeaders(asset, false, request, response);
    }
    
    /**
     * This base implementation simply returns true to indicate that the request should be re-rendered.
     * Override in a subclass as necessary
     * @param asset
     * @param request
     * @param response
     * @return boolean true if browser has old copy and so content should be rendered
     */
    public boolean setHeaders(Asset asset, boolean attach, HttpServletRequest request, HttpServletResponse response) 
    {
        return true;
    }
    
    public final String getHttpDate(Date date)
    {
        return dateFormatter().format(date);
    }

    public final Date getDateFromHttpDate(String date) throws ParseException
    {
        return dateFormatter().parse(date);
    }
    
    /**
     * Get a date formatter for the thread as SimpleDateFormat is not thread-safe
     * @return
     */
    public final SimpleDateFormat dateFormatter() 
    {
    	return httpDateFormat.get();
    }	
}

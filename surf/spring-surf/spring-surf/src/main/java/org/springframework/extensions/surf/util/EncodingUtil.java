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

package org.springframework.extensions.surf.util;

import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Provides helper methods for encoding and decoding strings into a given
 * encoding scheme.
 * 
 * @author muzquiano
 */
public class EncodingUtil
{
    
    /** The DEFAULT encoding. */
    public static final String DEFAULT_ENCODING = "utf-8";

    /**
     * Encodes the given String into the default encoding. The default encoding
     * is specified by DEFAULT_ENCODING.
     * 
     * If the encoding is unable to be performed, null is returned.
     * 
     * @param input
     *            The String to be encoded
     * 
     * @return The encoded String
     */
    public static String encode(String input)
    {
        return encode(input, DEFAULT_ENCODING);
    }

    /**
     * Encodes the given String into the given encoding.
     * 
     * If the encoding is unable to be performed, null is returned.
     * 
     * @param input
     *            The String to be encoded
     * @param encoding
     *            The encoding to be used
     * 
     * @return The encoded String
     */
    public static String encode(String input, String encoding)
    {
        String output = null;
        try
        {
            output = URLEncoder.encode(input, encoding);
        }
        catch (Exception ex)
        {
        }
        return output;
    }

    /**
     * Decode.
     * 
     * @param input
     *            the input
     * 
     * @return the string
     */
    public static String decode(String input)
    {
        return decode(input, DEFAULT_ENCODING);
    }

    /**
     * Decode.
     * 
     * @param input
     *            the input
     * @param encoding
     *            the encoding
     * 
     * @return the string
     */
    public static String decode(String input, String encoding)
    {
        String output = null;
        try
        {
            output = URLDecoder.decode(input, encoding);
        }
        catch (Exception ex)
        {
        }
        return output;
    }

}

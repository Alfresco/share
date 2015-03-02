/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.user;

/**
 * All Languages displayed in Language Settings drop down
 * 
 * @author Ranjith Manyam
 * @since v1.9.0
 */
public enum Language
{
    ENGLISH_US("en_US", "english.properties"),
    FRENCH("fr_FR", "french.properties"),
    DEUTSCHE("de_DE", "deutsche.properties"),
    SPANISH("es_ES", "spanish.properties"),
    ITALIAN("it_IT", "italian.properties"),
    JAPANESE("ja_JA", "japanese.properties");

    private String value;
    private String propertyFileName;

    private Language(String value, String propertyFileName)
    {
        this.value = value;
        this.propertyFileName = propertyFileName;
    }

    public String getLanguageValue()
    {
        return value;
    }

    public String getLanguagePropertyFileName()
    {
        return propertyFileName;
    }

    /**
     * Returns the Language from string value.
     * 
     * @param value - string value of enum eg - "en_US"
     * @return
     */
    public static Language getLanguageFromValue(String value)
    {
        for (Language status : Language.values())
        {
            if (value.equalsIgnoreCase(status.value))
            {
                return status;
            }
        }
        throw new IllegalArgumentException("Invalid Language : " + value);
    }
}

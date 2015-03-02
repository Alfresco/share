/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
package org.alfresco.po.share.site.document;

import org.apache.commons.lang3.StringUtils;

/**
 * Different Aspects of Documents.
 * 
 * @author Shan Nagarajan
 * @since 1.6.1
 */
public enum DocumentAspect
{
    CLASSIFIABLE("Classifiable", "P:cm:generalclassifiable"),
    VERSIONABLE("Versionable", "P:cm:versionable"),
    AUDIO("Audio", "P:audio:audio"),
    INDEX_CONTROL("Index Control", "P:cm:indexControl"),
    COMPLIANCEABLE("Complianceable", "P:cm:complianceable"),
    DUBLIN_CORE("Dublin Core", "P:cm:dublincore"),
    EFFECTIVITY("Effectivity", "P:cm:effectivity"),
    SUMMARIZABLE("Summarizable", "P:cm:summarizable"),
    TEMPLATABLE("Templatable", "P:cm:templatable"),
    EMAILED("Emailed", "P:cm:emailed"),
    ALIASABLE_EMAIL("Aliasable (Email)", "P:emailserver:aliasable"),
    TAGGABLE("Taggable", "P:cm:taggable"),
    INLINE_EDITABLE("Inline Editable", "P:app:inlineeditable"),
    GOOGLE_DOCS_EDITABLE("Google Docs Editable", "P:gd:googleEditable"),
    GEOGRAPHIC("Geographic", "P:cm:geographic"),
    EXIF("EXIF", "P:exif:exif"),
    RESTRICTABLE("Restrictable", "P:dp:restrictable");

    private String value;
    private String property;

    private DocumentAspect(String value, String property)
    {
        this.value = value;
        this.property = property;
    }

    public String getValue()
    {
        return this.value;
    }

    public String getProperty()
    {
        return this.property;
    }

    /**
     * Find the {@link DocumentAspect} based it is name.
     * 
     * @param name - Aspect's Name
     * @return {@link DocumentAspect}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static DocumentAspect getAspect(String name) throws Exception
    {
        if (StringUtils.isEmpty(name))
        {
            throw new UnsupportedOperationException("Name can't null or empty, It is required.");
        }
        for (DocumentAspect aspect : DocumentAspect.values())
        {
            if (aspect.value != null && aspect.value.equalsIgnoreCase(name.trim()))
            {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Not able to find the Document Aspect for given name : " + name);
    }

    /**
     * Find the {@link DocumentAspect} based it is property.
     *
     * @param property - Aspect's property
     * @return {@link DocumentAspect}
     * @throws Exception - Throws {@link Exception} if not able to find
     */
    public static DocumentAspect getAspectByProperty(String property) throws Exception
    {
        if (StringUtils.isEmpty(property))
        {
            throw new UnsupportedOperationException("Name can't null or empty, It is required.");
        }
        for (DocumentAspect aspect : DocumentAspect.values())
        {
            if (aspect.property != null && aspect.property.equalsIgnoreCase(property.trim()))
            {
                return aspect;
            }
        }
        throw new IllegalArgumentException("Not able to find the Document Aspect for given name : " + property);
    }
}

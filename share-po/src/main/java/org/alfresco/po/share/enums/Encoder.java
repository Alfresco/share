/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.enums;

import org.alfresco.po.share.util.PageUtils;

/**
 * Enum to contain all the possible Encoders
 * 
 * @author Meenal Bhave
 */
public enum Encoder
{
    ENCODER_HTML("Html"),
    ENCODER_JAVASCRIPT("JavaScript"),
    ENCODER_NOENCODER("No");

    private String name;

    private Encoder(String name)
    {
        this.name = name;
    }

    public String getName()
    {
        return name;
    }

    /**
     * Gets appropriate Encoder type.
     * 
     * @param name String
     * @return {@link Encoder}
     */
    public static Encoder getEncoder(String name)
    {
        PageUtils.checkMandatoryParam("name", name);

        if (name.equalsIgnoreCase((ENCODER_HTML.getName())))
        {
            return ENCODER_HTML;
        }
        else if (name.equalsIgnoreCase((ENCODER_JAVASCRIPT.getName())))
        {
            return ENCODER_JAVASCRIPT;
        }
        else if (name.equalsIgnoreCase((ENCODER_NOENCODER.getName())))
        {
            return ENCODER_NOENCODER;
        }

        throw new IllegalArgumentException("Not able to find the Encoder for given type: " + name);
    }

}

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



/**
 * This TinyMceColourCode enums are used to find the color code css in TinyMce Editor.
 * 
 * @author cbairaajoni
 *
 */
public enum TinyMceColourCode
{
    BLUE("div[title='Blue']", "div[title='Blue']"),
    BLACK("div[title='Black']", "div[title='Black']"),
    YELLOW("div[title='Yellow']", "div[title='Yellow']");


    private String foreColourLocator;
    private String bgColourLocator;

    private TinyMceColourCode(String foreColourLocator, String bgColourLocator)
    {
        this.foreColourLocator = foreColourLocator;
        this.bgColourLocator = bgColourLocator;
    }

    public String getForeColourLocator()
    {
        return foreColourLocator;
    }
    
    public String getBgColourLocator()
    {
        return bgColourLocator;
    }
}

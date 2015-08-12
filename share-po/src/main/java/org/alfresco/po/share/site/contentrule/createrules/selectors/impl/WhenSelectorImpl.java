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
package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractWhenSelector;
import org.openqa.selenium.support.FindBy;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:08
 */
@FindBy(tagName="form")
public class WhenSelectorImpl extends AbstractWhenSelector
{
    private enum WhenOptions
    {
        INBOUND(0), UPDATE(1), OUTBOUND(2);

        private final int numberPosition;

        WhenOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public void selectInbound()
    {
        selectWhenOption(WhenOptions.INBOUND.numberPosition);
    }

    public void selectOutbound()
    {
        selectWhenOption(WhenOptions.OUTBOUND.numberPosition);
    }

    public void selectUpdate()
    {
        selectWhenOption(WhenOptions.UPDATE.numberPosition);
    }
}

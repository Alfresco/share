/*
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of Alfresco
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
package org.alfresco.po.share.enums;

import static org.testng.Assert.assertEquals;
import static org.alfresco.po.share.enums.ZoomStyle.BIGGER;
import static org.alfresco.po.share.enums.ZoomStyle.BIGGEST;
import static org.alfresco.po.share.enums.ZoomStyle.SMALLER;
import static org.alfresco.po.share.enums.ZoomStyle.SMALLEST;
import org.testng.annotations.Test;

/**
 * Unit Test for {@link ZoomStyle}
 * 
 * @author Shan Nagarajan
 *
 */
public class ZoomStyleTest
{

    @Test(groups="unit")
    public void getSize()
    {
        assertEquals(BIGGER.getSize(), 40);
        assertEquals(BIGGEST.getSize(), 60);
        assertEquals(SMALLER.getSize(), 20);
        assertEquals(SMALLEST.getSize(), 0);
    }
    
    @Test(groups="unit", expectedExceptions=IllegalArgumentException.class)
    public void getZoomStyle()
    {
        assertEquals(ZoomStyle.getZoomStyle(BIGGER.getSize()), BIGGER);
        assertEquals(ZoomStyle.getZoomStyle(BIGGEST.getSize()), BIGGEST);
        assertEquals(ZoomStyle.getZoomStyle(SMALLER.getSize()), SMALLER);
        assertEquals(ZoomStyle.getZoomStyle(SMALLEST.getSize()), SMALLEST);
        ZoomStyle.getZoomStyle(-5);
    }
    
}

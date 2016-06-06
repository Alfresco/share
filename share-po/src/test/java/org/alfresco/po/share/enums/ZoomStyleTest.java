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

package org.alfresco.po.share;

import static org.alfresco.webdrone.RenderElement.getVisibleRenderElement;

import org.alfresco.webdrone.RenderElement;
import org.alfresco.webdrone.RenderTime;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

/**
 * Simple unit test against SharePage method 
 * @author Michael Suzuki
 */
@Test(groups={"alfresco-one"})
public class SharePageTest extends AbstractTest
{
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void elementRenderWithNull()
    {
        SharePage page = new DashBoardPage(drone);
        RenderElement elements[] = null;
        page.elementRender(null, elements);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void elementRenderWithNullRenderTimeObject()
    {
        SharePage page = new DashBoardPage(drone);
        page.elementRender(null, getVisibleRenderElement(By.cssSelector("bla")) );
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void elementRenderWithNullSelector()
    {
        SharePage page = new DashBoardPage(drone);
        RenderElement elements[] = null;
        page.elementRender(new RenderTime(1), elements);
    }
    
    @Test(expectedExceptions=UnsupportedOperationException.class)
    public void elementRenderWithEmptySelector()
    {
        SharePage page = new DashBoardPage(drone);
        RenderElement empty[] = {};
        page.elementRender(new RenderTime(1), empty);
    }
}

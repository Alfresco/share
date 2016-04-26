package org.alfresco.po.share.dashlet;

import org.alfresco.po.RenderTime;


/**
 * A dashlet interface.
 * 
 * @author Michael Suzuki
 * @since 1.0
 */
public interface Dashlet
{
    /**
     * Dashlet renderer verifies the page has rendered
     * by checking java script page loaded status is complete.
     * 
     * @param timer {@link RenderTime}
     * @return Dashlet object response
     */
    <T extends Dashlet> T render(final RenderTime timer);
    <T extends Dashlet> T render();
}

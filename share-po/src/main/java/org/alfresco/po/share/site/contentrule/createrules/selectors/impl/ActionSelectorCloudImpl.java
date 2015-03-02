package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractActionSelector;
import org.alfresco.webdrone.WebDrone;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:07
 */
public class ActionSelectorCloudImpl extends AbstractActionSelector
{

    private enum PerformActions
    {
        COPY(0), MOVE(1), TRANSFORM_AND_COPY_CONTENT(2), TRANSFORM_AND_COPY_IMAGE(3);

        private final int numberPosition;

        PerformActions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public ActionSelectorCloudImpl(WebDrone drone)
    {
        super(drone);
    }

    public void selectCopy(String siteName, String... folders)
    {
        super.selectAction(PerformActions.COPY.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectMove(String siteName, String... folders)
    {
        super.selectAction(PerformActions.MOVE.numberPosition);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectTransformAndCopy(String visibleText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_CONTENT.numberPosition);
        super.selectTransformContent(visibleText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

    public void selectTransformAndCopyImg(String visibleText, String siteName, String... folders)
    {
        super.selectAction(PerformActions.TRANSFORM_AND_COPY_IMAGE.numberPosition);
        super.selectTransformContent(visibleText);
        super.selectDestination(siteName, folders).selectOkButton();
    }

}

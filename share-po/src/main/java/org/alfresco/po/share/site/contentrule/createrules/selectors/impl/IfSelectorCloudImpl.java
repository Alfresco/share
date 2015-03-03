package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.webdrone.WebDrone;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:08
 */
public class IfSelectorCloudImpl extends AbstractIfSelector
{

    private enum IfOptions
    {
        ALL_ITEMS(0),
        SIZE(1),
        CREATED_DATE(2),
        MODIFIED_DATE(3),
        CREATOR(4),
        MODIFIER(5),
        AUTHOR(6),
        MIME_TYPE(7),
        DESCRIPTION(8),
        NAME(9),
        TITLE(10),
        HAS_TAG(11);

        private final int numberPosition;

        IfOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public IfSelectorCloudImpl(WebDrone drone)
    {
        super(drone);
    }

    public void selectAllItems()
    {
        super.selectAllItems(IfOptions.ALL_ITEMS.numberPosition);
    }

    public void selectSize(SizeCompareOption sizeCompareOption, String compareSize)
    {
        super.selectSize(IfOptions.SIZE.numberPosition, sizeCompareOption, compareSize);
    }

    public void selectCreatedDate(SizeCompareOption sizeCompareOption, String date, String time)
    {
        super.selectCreatedDate(IfOptions.CREATED_DATE.numberPosition, sizeCompareOption, date, time);
    }

    public void selectModifiedDate(SizeCompareOption sizeCompareOption, String date, String time)
    {
        super.selectCreatedDate(IfOptions.MODIFIED_DATE.numberPosition, sizeCompareOption, date, time);
    }

    public void selectCreator(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.CREATOR.numberPosition, stringCompareOption, compareString);
    }

    public void selectModifier(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.MODIFIER.numberPosition, stringCompareOption, compareString);
    }

    public void selectAuthor(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.AUTHOR.numberPosition, stringCompareOption, compareString);
    }

    public void selectMimeType(String mimeTypeVisibleText)
    {
        super.selectIs(IfOptions.MIME_TYPE.numberPosition, mimeTypeVisibleText);
    }

    public void selectDescription(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.DESCRIPTION.numberPosition, stringCompareOption, compareString);
    }

    public void selectName(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.NAME.numberPosition, stringCompareOption, compareString);
    }

    public void selectTitle(StringCompareOption stringCompareOption, String compareString)
    {
        super.selectStringCompare(IfOptions.TITLE.numberPosition, stringCompareOption, compareString);
    }

    @Deprecated
    public void selectHasTag()
    {
        // todo: necessary to complete
        super.selectWithButton(IfOptions.HAS_TAG.numberPosition);
    }
}

package org.alfresco.po.share.site.contentrule.createrules.selectors.impl;

import org.alfresco.po.share.site.contentrule.createrules.selectors.AbstractIfSelector;
import org.alfresco.webdrone.WebDrone;

/**
 * User: aliaksei.bul
 * Date: 08.07.13
 * Time: 12:08
 */
public class IfSelectorEnterpImpl extends AbstractIfSelector
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
        ENCODING(8),
        DESCRIPTION(9),
        NAME(10),
        TITLE(11),
        HAS_TAG(12),
        HAS_CATEGORY(13),
        CONTENT_OF_TYPE_OR_SUBTYPE(14),
        HAS_ASPECT(15),
        SHOW_MORE(16);

        private final int numberPosition;

        IfOptions(int numberPosition)
        {
            this.numberPosition = numberPosition;
        }
    }

    public IfSelectorEnterpImpl(WebDrone drone)
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

    public void selectEncoding(String compareValue)
    {
        super.selectIFOption(IfOptions.ENCODING.numberPosition);
        super.fillField(COMPARE_FIELD, compareValue);
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
        // todo: necessary to complete( see method super.selectWithButton(..) )
        super.selectWithButton(IfOptions.HAS_TAG.numberPosition);
    }

    @Deprecated
    public void selectHasCategory()
    {
        // todo: necessary to complete( see method super.selectWithButton(..) )
        super.selectWithButton(IfOptions.HAS_CATEGORY.numberPosition);
    }

    public void selectContentOfTypeOrSubType(String typeVisibleText)
    {
        super.selectIs(IfOptions.CONTENT_OF_TYPE_OR_SUBTYPE.numberPosition, typeVisibleText);
    }

    @Deprecated
    public void selectHasAspect()
    {
        // todo: necessary to complete( see method super.selectWithButton(..) )
        super.selectWithButton(IfOptions.HAS_ASPECT.numberPosition);
    }

    @Deprecated
    public void selectShowMove()
    {
        super.selectIFOption(IfOptions.SHOW_MORE.numberPosition);
        // todo if need to testCases.
    }

}

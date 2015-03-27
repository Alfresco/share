package org.alfresco.po.share.site.datalist;

import static org.alfresco.po.share.enums.DataLists.CONTACT_LIST;
import static org.alfresco.po.share.site.datalist.DataListPage.selectOptions.INVERT_SELECT;
import static org.alfresco.po.share.site.datalist.DataListPage.selectOptions.SELECT_ALL;
import static org.alfresco.po.share.site.datalist.DataListPage.selectOptions.SELECT_NONE;
import static org.alfresco.po.share.site.datalist.DataListPage.selectedItemsOptions.DELETE;
import static org.alfresco.po.share.site.datalist.DataListPage.selectedItemsOptions.DESELECT_ALL;
import static org.alfresco.po.share.site.datalist.DataListPage.selectedItemsOptions.DUPLICATE;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.dashlet.AbstractSiteDashletTest;
import org.alfresco.po.share.site.CustomizeSitePage;
import org.alfresco.po.share.site.SitePageType;
import org.alfresco.po.share.site.datalist.lists.ContactList;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * Holds tests for Data Lists web elements
 * 
 * @author Marina.Nenadovets
 */

@Listeners(FailedTestListener.class)
@Test(groups = { "Enterprise-only" })
public class DataListPageTest extends AbstractSiteDashletTest
{
    DashBoardPage dashBoard;
    CustomizeSitePage customizeSitePage;
    DataListPage dataListPage = null;
    NewListForm newListForm;
    ContactList contactList;
    String text = getClass().getSimpleName();
    String editedText = text + "edited";

    @BeforeClass
    public void createSite() throws Exception
    {
        dashBoard = loginAs(username, password);
        siteName = "datalist" + System.currentTimeMillis();
        SiteUtil.createSite(drone, siteName, "description", "Public");
        navigateToSiteDashboard();
    }

    @AfterClass
    public void tearDown()
    {
        SiteUtil.deleteSite(drone, siteName);
    }

    @Test
    public void addDataListPage()
    {
        customizeSitePage = siteDashBoard.getSiteNav().selectCustomizeSite();
        List<SitePageType> addPageTypes = new ArrayList<SitePageType>();
        addPageTypes.add(SitePageType.DATA_LISTS);
        customizeSitePage.addPages(addPageTypes);
        newListForm = (NewListForm) siteDashBoard.getSiteNav().selectDataListPage();
        dataListPage = newListForm.clickCancel();
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "addDataListPage")
    public void isNoListFoundDisplayed()
    {
        assertTrue(dataListPage.isNoListFoundDisplayed());
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "isNoListFoundDisplayed")
    public void createContactDataList()
    {
        assertTrue(dataListPage.isNewListEnabled());
        dataListPage = dataListPage.createDataList(CONTACT_LIST, text, text);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "createContactDataList")
    public void getDataListDescription()
    {
        assertTrue(dataListPage.getDataListDescription(text).contains(text), "Data list '" + text + "' description isn't displayed");
    }

    @Test(dependsOnMethods = "getDataListDescription")
    public void checkCreateItemForm()
    {

        dataListPage.selectDataList(text);
        contactList = new ContactList(drone).checkCreateItemForm().render();
        assertNotNull(contactList);
    }

    @Test(dependsOnMethods = "checkCreateItemForm")
    public void createItem()
    {
        dataListPage.selectDataList(text);
        contactList = new ContactList(drone).createItem(text).render();
        assertNotNull(contactList);
    }

    @Test(dependsOnMethods = "createItem")
    public void editDataList()
    {
        dataListPage.editDataList(text, editedText, editedText);
        dataListPage.selectDataList(editedText);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "editDataList")
    public void duplicateItems()
    {
        assertTrue(contactList.isDuplicateDisplayed(text));
        dataListPage.duplicateAnItem(text);
        assertEquals(contactList.getItemsCount(), 2);
    }

    @Test(dependsOnMethods = "duplicateItems")
    public void editAnItem()
    {
        assertTrue(contactList.isEditDisplayed(text));
        contactList.editItem(text, editedText);
        assertNotNull(dataListPage);
    }

    @Test(dependsOnMethods = "editAnItem")
    public void testSelectItems()
    {
        dataListPage.select(SELECT_ALL);
        assertTrue(contactList.isCheckBoxSelected(text) && contactList.isCheckBoxSelected(editedText), "Items were not selected");
        dataListPage.select(SELECT_NONE);
        assertFalse(contactList.isCheckBoxSelected(text) && contactList.isCheckBoxSelected(editedText), "Items are still selected");
        contactList.selectAnItem(text);
        assertTrue(contactList.isCheckBoxSelected(text), "Item isn't selected");
        dataListPage.select(INVERT_SELECT);
        assertTrue(contactList.isCheckBoxSelected(editedText), "The selection wasn't inverted");
        assertFalse(contactList.isCheckBoxSelected(text), "The selection wasn't inverted");
    }

    @Test(dependsOnMethods = "testSelectItems")
    public void testSelectedItemsActions()
    {
        int expCount = contactList.getItemsCount();
        dataListPage.chooseSelectedItemOpt(DUPLICATE);
        assertEquals(contactList.getItemsCount(), expCount+1, "The item wasn't duplicated");

        dataListPage.chooseSelectedItemOpt(DELETE);
        contactList.confirmDelete();
        assertEquals(contactList.getItemsCount(), expCount, "The item wasn't deleted");

        dataListPage.selectAnItem(editedText);
        dataListPage.chooseSelectedItemOpt(DESELECT_ALL);
        assertFalse(contactList.isCheckBoxSelected(editedText) && contactList.isCheckBoxSelected(text), "The items were not deselected");
    }

    @Test(dependsOnMethods = "testSelectedItemsActions")
    public void deleteItem()
    {
        int expNum = contactList.getItemsCount();
        contactList.deleteAnItemWithConfirm(editedText);
        assertEquals(contactList.getItemsCount(), expNum - 1);
    }

    @Test(dependsOnMethods = "deleteItem")
    public void deleteList()
    {
        int expNum = dataListPage.getListsCount();
        dataListPage.deleteDataListWithConfirm(editedText);
        assertEquals(dataListPage.getListsCount(), expNum - 1);
    }
}

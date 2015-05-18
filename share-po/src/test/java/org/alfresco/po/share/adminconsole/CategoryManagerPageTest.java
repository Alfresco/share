/*
 * Copyright (C) 2005-2012 Alfresco Software Limited.
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
package org.alfresco.po.share.adminconsole;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

/**
 * @author Aliaksei Boole
 */
@Listeners(FailedTestListener.class)
public class CategoryManagerPageTest extends AbstractTest
{
    private String categoryName = "TestCategory" + System.currentTimeMillis();
    private String subCategoryName = "SubCategory" + System.currentTimeMillis();
    private String renameCategoryName = "RenameCategory" + System.currentTimeMillis();
    private String rootCategoryName = "Category Root";
    private long solrWaitTime = 20000;

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkThatFactoryReturnCategoryManagerPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getCategoryManagerPage().render();
        assertTrue(drone.getCurrentPage().render() instanceof CategoryManagerPage);
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnCategoryManagerPage", groups = "Enterprise-only", timeOut = 400000)
    public void checkCategoryCountAndAddNew() throws Exception
    {
        CategoryManagerPage categoryManagerPage = drone.getCurrentPage().render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        assertTrue(categoryCount > 0);
        categoryManagerPage.addNewCategory(rootCategoryName, categoryName);
        Thread.sleep(solrWaitTime); //wait solr  
        categoryManagerPage.getNav().getCategoryManagerPage().render();
        //drone.refresh();
        assertTrue(categoryCount < categoryManagerPage.getCategoriesCount(), "Before " + categoryCount + ", after " + categoryManagerPage.getCategoriesCount() +" ACE-3037");
        assertTrue(categoryManagerPage.isCategoryPresent(categoryName));
    }

    @Test(dependsOnMethods = "checkCategoryCountAndAddNew", groups = "Enterprise-only", timeOut = 400000)
    public void checkOpenSubCategory() throws Exception
    {
        CategoryManagerPage categoryManagerPage = drone.getCurrentPage().render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        categoryManagerPage.addNewCategory(categoryName, subCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        //drone.refresh();
        categoryManagerPage.getNav().getCategoryManagerPage().render();
        assertTrue(categoryCount == categoryManagerPage.getCategoriesCount());
        categoryManagerPage.openSubCategoryList(categoryName);
        Thread.sleep(solrWaitTime);
        assertTrue(categoryCount + 1 == categoryManagerPage.getCategoriesCount());
        assertTrue(categoryManagerPage.isCategoryPresent(subCategoryName));
    }

    @Test(dependsOnMethods = "checkOpenSubCategory", groups = "Enterprise-only", timeOut = 400000)
    public void checkChangeCategoryName() throws Exception
    {
        CategoryManagerPage categoryManagerPage = drone.getCurrentPage().render();
        categoryManagerPage.editCategory(categoryName, renameCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        //drone.refresh();
        categoryManagerPage.getNav().getCategoryManagerPage().render();
        assertTrue(categoryManagerPage.isCategoryPresent(renameCategoryName));
        categoryManagerPage.openSubCategoryList(renameCategoryName);
        assertTrue(categoryManagerPage.isCategoryPresent(subCategoryName));
    }

    @Test(dependsOnMethods = "checkChangeCategoryName", groups = "Enterprise-only", timeOut = 400000)
    public void checkDeleteCategory() throws Exception
    {
        CategoryManagerPage categoryManagerPage = drone.getCurrentPage().render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        categoryManagerPage.deleteCategory(renameCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        //drone.refresh();
        categoryManagerPage.getNav().getCategoryManagerPage().render();
        assertFalse(categoryManagerPage.isCategoryPresent(renameCategoryName));
        assertTrue(categoryCount - 2 == categoryManagerPage.getCategoriesCount());
    }
}

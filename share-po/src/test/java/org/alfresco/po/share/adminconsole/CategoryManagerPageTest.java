/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
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
 * #L%
 */
package org.alfresco.po.share.adminconsole;

import org.alfresco.po.AbstractTest;
import org.alfresco.po.share.SharePage;
import org.alfresco.test.FailedTestListener;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

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

    @Test(groups = "Enterprise-only", timeOut = 400000)
    public void checkThatFactoryReturnCategoryManagerPage() throws Exception
    {
        SharePage page = loginAs("admin", "admin");
        page.getNav().getCategoryManagerPage().render();
        assertTrue(resolvePage(driver).render() instanceof CategoryManagerPage);
    }

    @Test(dependsOnMethods = "checkThatFactoryReturnCategoryManagerPage", groups = "Enterprise-only", timeOut = 400000)
    public void checkCategoryCountAndAddNew() throws Exception
    {
        CategoryManagerPage categoryManagerPage = resolvePage(driver).render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        assertTrue(categoryCount > 0);
        categoryManagerPage.addNewCategory(rootCategoryName, categoryName);
        Thread.sleep(solrWaitTime); //wait solr
        driver.navigate().refresh();
        assertTrue(categoryCount < categoryManagerPage.getCategoriesCount(), "Before " + categoryCount + ", after " + categoryManagerPage.getCategoriesCount() +" ACE-3037");
        assertTrue(categoryManagerPage.isCategoryPresent(categoryName));
    }

    @Test(dependsOnMethods = "checkCategoryCountAndAddNew", groups = "Enterprise-only", timeOut = 400000)
    public void checkOpenSubCategory() throws Exception
    {
        CategoryManagerPage categoryManagerPage = resolvePage(driver).render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        categoryManagerPage.addNewCategory(categoryName, subCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        driver.navigate().refresh();
        assertTrue(categoryCount == categoryManagerPage.getCategoriesCount());
        categoryManagerPage.openSubCategoryList(categoryName);
        Thread.sleep(solrWaitTime);
        assertTrue(categoryCount + 1 == categoryManagerPage.getCategoriesCount());
        assertTrue(categoryManagerPage.isCategoryPresent(subCategoryName));
    }

    @Test(dependsOnMethods = "checkOpenSubCategory", groups = "Enterprise-only", timeOut = 400000)
    public void checkChangeCategoryName() throws Exception
    {
        CategoryManagerPage categoryManagerPage = resolvePage(driver).render();
        categoryManagerPage.editCategory(categoryName, renameCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        driver.navigate().refresh();
        categoryManagerPage.render();
        assertTrue(categoryManagerPage.isCategoryPresent(renameCategoryName));
        categoryManagerPage.openSubCategoryList(renameCategoryName);
        assertTrue(categoryManagerPage.isCategoryPresent(subCategoryName));
    }

    @Test(dependsOnMethods = "checkChangeCategoryName", groups = "Enterprise-only", timeOut = 400000)
    public void checkDeleteCategory() throws Exception
    {
        CategoryManagerPage categoryManagerPage = resolvePage(driver).render();
        int categoryCount = categoryManagerPage.getCategoriesCount();
        categoryManagerPage.deleteCategory(renameCategoryName);
        Thread.sleep(solrWaitTime); //wait solr
        driver.navigate().refresh();
        assertFalse(categoryManagerPage.isCategoryPresent(renameCategoryName));
        assertTrue(categoryCount - 2 == categoryManagerPage.getCategoriesCount());
    }
}

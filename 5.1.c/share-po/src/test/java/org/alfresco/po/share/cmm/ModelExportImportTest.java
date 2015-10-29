/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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

package org.alfresco.po.share.cmm;

/**
 * Test class to test Export and Import options work on ModelManager Page
 * 
 * @author mbhave
 */

import org.alfresco.po.share.admin.ActionsSet;
import org.alfresco.po.share.cmm.admin.ConstraintDetails;
import org.alfresco.po.share.cmm.admin.ImportModelPopUp;
import org.alfresco.po.share.cmm.admin.ManagePropertiesPage;
import org.alfresco.po.share.cmm.admin.ManageTypesAndAspectsPage;
import org.alfresco.po.share.cmm.admin.ModelManagerPage;
import org.alfresco.po.share.cmm.admin.ModelRow;
import org.alfresco.po.share.cmm.enums.ConstraintTypes;
import org.alfresco.po.share.cmm.enums.DataType;
import org.alfresco.po.share.cmm.enums.MandatoryClassifier;
import org.alfresco.test.FailedTestListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class ModelExportImportTest extends AbstractTestCMM
{

    /** The logger */
    private Log logger = LogFactory.getLog(this.getClass());

    private String name = "model1" + System.currentTimeMillis();

    private String exportAction = "Export";

    @BeforeClass(groups = { "alfresco-one" }, alwaysRun = true)
    public void setup() throws Exception
    {
        exportAction = factoryPage.getValue("cmm.model.action.export");

        loginAs(driver, username, password);
    }

    @AfterClass
    public void cleanupSession()
    {
        cleanSession(driver);
    }

    // TODO: Enable the tests in this class after a workable solution for download issue on bamboo is found.
    // Test works locally with the settings: -Dwebdriver.browser=FireFoxDownloadToDir -Dwebdriver.download.directory=C:\\DownloadAlfresco\\
    @Test(groups = { "Enterprise-only" }, priority = 1, enabled = false)
    public void clickExportModelTest() throws Exception
    {
        String modelIE = name + "import-export";
        String compositeTypeName = modelIE + ":type";
        String compositeAspectName = modelIE + ":aspect";
        String compositePropNameT = modelIE + ":propT";
        String compositePropNameA = modelIE + ":propA";

        Assert.assertNotNull(downloadDirectory, "Download Directory is:" + downloadDirectory);

        logger.info("Download Directory is:" + downloadDirectory);

        // Create Model
        ModelManagerPage cmmPage = cmmActions.navigateToModelManagerPage(driver).render();
        cmmActions.createNewModel(driver, modelIE);

        // Create Types And Aspects
        cmmActions.viewTypesAspectsForModel(driver, modelIE);
        cmmActions.createType(driver, "type");
        cmmActions.createAspect(driver, "aspect");

        // Create Properties
        cmmActions.viewProperties(driver, compositeTypeName);
        cmmActions.createProperty(driver, "propT", "propT", "propT", DataType.Text, MandatoryClassifier.Mandatory, false, "propT");

        ConstraintDetails constraintDetails = new ConstraintDetails();
        constraintDetails.setType(ConstraintTypes.REGEX);
        constraintDetails.setValue("\\.*@alfresco.com");
        constraintDetails.setMatchRequired(true);

        cmmActions.viewProperties(driver, compositeAspectName);
        cmmActions.createPropertyWithConstraint(
                driver,
                "propA",
                "propA",
                "propA",
                DataType.Text,
                MandatoryClassifier.Mandatory,
                false,
                "propA@alfresco.com",
                constraintDetails);

        cmmPage = cmmActions.navigateToModelManagerPage(driver);

        // Export Model
        ModelRow row = cmmPage.getCustomModelRowByName(modelIE);
        ActionsSet actions = row.getCmActions();

        Assert.assertTrue(actions.hasActionByName(exportAction));

        cmmPage = actions.clickActionByName(exportAction).render();
        cmmPage.waitForFile(downloadDirectory + modelIE + ".zip");

        // Delete Model
        cmmActions.deleteModel(driver, modelIE);

        // Import Model
        ImportModelPopUp importModelPage = cmmPage.clickImportModelButton().render();
        cmmPage = importModelPage.importModel(downloadDirectory + modelIE + ".zip").render();

        // Model Row is displayed
        Assert.assertTrue(cmmPage.isCustomModelRowDisplayed(modelIE));

        // Type and Aspects are displayed
        ManageTypesAndAspectsPage typesAspectsList = cmmActions.viewTypesAspectsForModel(driver, modelIE).render();
        Assert.assertTrue(typesAspectsList.isCustomTypeRowDisplayed(compositeTypeName), "Model > Type not imported correctly");
        Assert.assertTrue(typesAspectsList.isPropertyGroupRowDisplayed(compositeAspectName), "Model > Aspect not imported correctly");

        // Type and Aspects' Properties are displayed
        ManagePropertiesPage PropList = cmmActions.viewProperties(driver, compositeTypeName).render();
        Assert.assertTrue(PropList.isPropertyRowDisplayed(compositePropNameT), "Model > Type > PropertyT not imported correctly");

        PropList = cmmActions.viewProperties(driver, compositeAspectName).render();
        Assert.assertTrue(PropList.isPropertyRowDisplayed(compositePropNameA), "Model > Aspect > PropertyA not imported correctly");
    }
}
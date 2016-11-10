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

package org.alfresco.po.share.steps;

/**
 * Class contains UserProfile related steps / actions / utils for regression tests
 * 
 *  @author mbhave
 */

import java.util.List;

import org.alfresco.po.exception.PageOperationException;
import org.alfresco.po.share.DashBoardPage;
import org.alfresco.po.share.SharePage;
import org.alfresco.po.share.exception.UnexpectedSharePageException;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.user.TrashCanDeleteConfirmDialog;
import org.alfresco.po.share.user.TrashCanDeleteConfirmationPage;
import org.alfresco.po.share.user.TrashCanItem;
import org.alfresco.po.share.user.TrashCanPage;
import org.alfresco.po.share.user.TrashCanRecoverConfirmDialog;
import org.alfresco.po.share.user.TrashCanValues;
import org.alfresco.po.share.util.PageUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.WebDriver;
import org.springframework.stereotype.Component;
@Component
public class UserProfileActions extends CommonActions
{
    private static Log logger = LogFactory.getLog(UserProfileActions.class);
    
    /**
     * Navigate to User Profile page 
     * @param driver WebDriver Instance
     * @return {@link MyProfilePage}
     */
    public MyProfilePage navigateToMyProfile(WebDriver driver)
    {
        try
        {
            SharePage dashBoard = getSharePage(driver);

            MyProfilePage myprofile = dashBoard.getNav().selectMyProfile().render();
            return myprofile;
        }
        catch (ClassCastException c)
        {
            throw new UnexpectedSharePageException(DashBoardPage.class, c);
        }
    }
    
    /**
     * Navigate to User's TrashCan page 
     * @param driver WebDriver Instance
     * @return {@link TrashCanPage}
     */
    public TrashCanPage navigateToTrashCan(WebDriver driver)
    {
        MyProfilePage myprofile = navigateToMyProfile(driver);
        TrashCanPage trashCan = myprofile.getProfileNav().selectTrashCan().render();
        return trashCan;
    }   
    
    /**
     * Delete specified content from User's TrashCan page 
     * @param driver WebDriver Instance
     * @param contentType TrashCanValues
     * @param contentName String
     * @param path String
     * @return {@link TrashCanPage}
     */
    public TrashCanPage deleteFromTrashCan(WebDriver driver, TrashCanValues contentType, String contentName, String path)
    {
        PageUtils.checkMandatoryParam("Content Type must be specified", contentType);
        PageUtils.checkMandatoryParam("Content Name must be specified", contentName);
        PageUtils.checkMandatoryParam("Appropriate path for the content must be specified", path);
        
        TrashCanPage trashCan = getSharePage(driver).render();

        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(contentType, contentName, path);
        if (item1.isEmpty())
        {
            throw new PageOperationException("Content not found in TrashCan: " + contentName);
        }
        
        if (item1.size() > 1)
        {
            logger.info("More than 1 matching items found in the TrashCan. Deleting 1st found item!");
        }
        
        trashCan = item1.get(0).selectTrashCanItemCheckBox();
        TrashCanDeleteConfirmationPage trashCanDeleteConfirmation = trashCan.selectedDelete().render();
        
        TrashCanDeleteConfirmDialog trashCanConfrimDialog = (TrashCanDeleteConfirmDialog) trashCanDeleteConfirmation.clickOkButton().render();
        trashCan = trashCanConfrimDialog.clickDeleteOK().render();
        return trashCan;
    } 
    
    
    /**
     * Delete specified content from User's TrashCan page 
     * @param driver WebDriver Instance
     * @param contentType TrashCanValues
     * @param contentName String
     * @param path String
     * @return {@link TrashCanPage}
     */
    public TrashCanPage recoverFromTrashCan(WebDriver driver, TrashCanValues contentType, String contentName, String path)
    {
        PageUtils.checkMandatoryParam("Content Type must be specified", contentType);
        PageUtils.checkMandatoryParam("Content Name must be specified", contentName);
        PageUtils.checkMandatoryParam("Appropriate path for the content must be specified", path);
        
        TrashCanPage trashCan = getSharePage(driver).render();

        List<TrashCanItem> item1 = trashCan.getTrashCanItemForContent(contentType, contentName, path);
        if (item1.isEmpty())
        {
            throw new PageOperationException("Content not found in TrashCan: " + contentName);
        }
        
        if (item1.size() > 1)
        {
            logger.info("More than 1 matching items found in the TrashCan. Recovering 1st found item!");
        }
        
        trashCan = item1.get(0).selectTrashCanItemCheckBox();
        TrashCanRecoverConfirmDialog trashCanRecoverConfirmation = trashCan.selectedRecover().render();
        
        trashCan= trashCanRecoverConfirmation.clickRecoverOK().render();
        return trashCan;
    } 
}

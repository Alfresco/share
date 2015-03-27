/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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


package org.alfresco.po.share.dashlet;

import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.FOURTEEN_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.LAST_DAY_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.SEVEN_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsHistoryFilter.TWENTY_EIGHT_DAYS_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.ALL_TOPICS;
import static org.alfresco.po.share.dashlet.MyDiscussionsTopicsFilter.MY_TOPICS;

import java.util.List;

import org.alfresco.po.share.ShareLink;
import org.alfresco.po.share.dashlet.MyDiscussionsDashlet.LinkType;
import org.alfresco.po.share.dashlet.mydiscussions.CreateNewTopicPage;
import org.alfresco.po.share.dashlet.mydiscussions.TopicDetailsPage;
import org.alfresco.po.share.enums.Dashlets;
import org.alfresco.po.share.site.CustomiseSiteDashboardPage;
import org.alfresco.po.share.user.MyProfilePage;
import org.alfresco.po.share.util.SiteUtil;
import org.alfresco.test.FailedTestListener;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

@Listeners(FailedTestListener.class)
public class MyDiscussionsDashletTest extends AbstractSiteDashletTest
{
    private static final String MY_DISCUSSIONS = "my-discussions";
    private MyDiscussionsDashlet myDiscussionsDashlet = null;
    private CustomiseSiteDashboardPage customiseSiteDashBoard = null;
    private CreateNewTopicPage createNewTopicPage = null;
    private TopicDetailsPage topicDetailsPage = null;
    private MyProfilePage myProfilePage = null;
    private static final String EMPTY_DASHLET_MESSAGE = "There are no topics matching your filters.";
    private static final String EXPECTED_HELP_BALOON_MESSAGES = "Discussion Forum dashlet.View your latest posts on the Discussion Forum.";
    private static final String CREATE_NEW_PAGE_TITLE = "Create New Topic";
    private static final String TOPIC_DETAILS_TITLE_ONE = "topic title 1";
    private static final String TOPIC_DETAILS_TITLE_TWO = "topic title 2";
    private static final String TOPIC_DETAILS_TITLE_THREE = "topic title 3";
    private static final String TOPIC_DETAILS_AUTHOR = "Administrator";
    private static final String TOPIC_DETAILS_CREATED_ON = "Created on:";
    private static final String EDIT_TOPIC_TITLE = "Edit Topic";
    private static final String CREATED_BY_JUST_NOW = "Created by Administrator just now.";
    private static final String UPDATED_JUST_NOW = "(Updated just now)";
    private static final String NUMBER_OF_REPLIES = "There is 1 reply.";
    private static final String REPLY_DETAILS = "The last reply was posted by Administrator just now.";
    
    @BeforeTest
    public void prepare() 
    {
        siteName = "mydiscussionsdashlettest" + System.currentTimeMillis();
     }
    
    
    @BeforeClass
    public void loadFile() throws Exception
    {
        uploadDocument();
        navigateToSiteDashboard();
  
    }
    
    
    @AfterClass
    public void deleteSite()
    {
        SiteUtil.deleteSite(drone, siteName);
    }
    
    
    @Test
    public void instantiateDashlet()
    {
        customiseSiteDashBoard = siteDashBoard.getSiteNav().selectCustomizeDashboard();
        customiseSiteDashBoard.render();
        siteDashBoard = customiseSiteDashBoard.addDashlet(Dashlets.MY_DISCUSSIONS, 1).render();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        Assert.assertNotNull(myDiscussionsDashlet);
    }
    
    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyEmptyDashletMessage()
    {   
        String emptyDashletMessage = myDiscussionsDashlet.getEmptyDashletMessage(); 
        Assert.assertEquals(emptyDashletMessage, EMPTY_DASHLET_MESSAGE);
    }
 
    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyHelpIcon()
    {
        Assert.assertTrue(myDiscussionsDashlet.isHelpButtonDisplayed());
    }

    @Test(dependsOnMethods="instantiateDashlet")
    public void verifyNewTopicIcon()
    {
        Assert.assertTrue(myDiscussionsDashlet.isNewTopicLinkDisplayed());
    }
     
    @Test(dependsOnMethods="verifyHelpIcon")
    public void selectHelpIcon() 
    {
        myDiscussionsDashlet.clickHelpButton();
        Assert.assertTrue(myDiscussionsDashlet.isBalloonDisplayed());
        String actualHelpBallonMsg = myDiscussionsDashlet.getHelpBalloonMessage();
        Assert.assertEquals(actualHelpBallonMsg, EXPECTED_HELP_BALOON_MESSAGES);
    }
    
    @Test(dependsOnMethods="selectHelpIcon")
    public void closeHelpIcon() 
    {
        myDiscussionsDashlet.closeHelpBallon();
        Assert.assertFalse(myDiscussionsDashlet.isBalloonDisplayed());
    }
    
    
    @Test(dependsOnMethods="verifyNewTopicIcon")
    public void selectNewTopicIcon() 
    {   
        createNewTopicPage = myDiscussionsDashlet.clickNewTopicButton().render();
        Assert.assertNotNull(createNewTopicPage);
        Assert.assertEquals(createNewTopicPage.getPageTitle(), CREATE_NEW_PAGE_TITLE);
    }
    
    
    @Test(dependsOnMethods = "instantiateDashlet")
    public void getCurrentTopicFilter() throws Exception
    {
        MyDiscussionsTopicsFilter currentTopicFilter = myDiscussionsDashlet.getCurrentTopicFilter();
        Assert.assertEquals(currentTopicFilter, MY_TOPICS);
    }
    
    @Test(dependsOnMethods = "instantiateDashlet")
    public void getCurrentHistoryFilter() throws Exception
    {
        MyDiscussionsHistoryFilter currentHistoryFilter = myDiscussionsDashlet.getCurrentHistoryFilter();
        Assert.assertEquals(currentHistoryFilter, LAST_DAY_TOPICS);
    }
    
    @Test(dependsOnMethods = "instantiateDashlet")
    public void getAllTopicsFilters() throws Exception
    {
        myDiscussionsDashlet.clickTopicsButtton();
        List<MyDiscussionsTopicsFilter> allTopicFilters = myDiscussionsDashlet.getTopicFilters();
        Assert.assertTrue(allTopicFilters.contains(ALL_TOPICS));
        Assert.assertTrue(allTopicFilters.contains(MY_TOPICS));
    }
    
    @Test(dependsOnMethods = "instantiateDashlet")
    public void getAllHistoryFilters() throws Exception
    {
        myDiscussionsDashlet.clickHistoryButtton();
        List<MyDiscussionsHistoryFilter> allHistoryFilters = myDiscussionsDashlet.getHistoryFilters();
        Assert.assertTrue(allHistoryFilters.contains(LAST_DAY_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(SEVEN_DAYS_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(FOURTEEN_DAYS_TOPICS));
        Assert.assertTrue(allHistoryFilters.contains(TWENTY_EIGHT_DAYS_TOPICS));
    }
        
 
    @Test(dependsOnMethods = "selectNewTopicIcon")
    public void createNewTopics() throws Exception
    {
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE);
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicAuthor(), TOPIC_DETAILS_AUTHOR);
        Assert.assertFalse("".equalsIgnoreCase(topicDetailsPage.getTopicCreationDate()));
        
        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO);
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicAuthor(), TOPIC_DETAILS_AUTHOR);
        Assert.assertFalse("".equalsIgnoreCase(topicDetailsPage.getTopicCreationDate()));

        createNewTopicPage = topicDetailsPage.clickOnNewTopicLink().render();
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_THREE);
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(topicDetailsPage.getTopicAuthor(), TOPIC_DETAILS_AUTHOR);
        Assert.assertFalse("".equalsIgnoreCase(topicDetailsPage.getTopicCreationDate()));
        
    }
    
    @Test(dependsOnMethods="createNewTopics")
    public void getTopics() throws Exception
    {
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
        List<ShareLink> topicsUsers = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(topicsUsers);
        Assert.assertEquals(topicsUsers.size(), 3);
        for (ShareLink topicUser : topicsUsers)
        {
            Assert.assertEquals(topicUser.getDescription(), TOPIC_DETAILS_AUTHOR);
        }
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 3);

        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(topicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicsTitles.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);
    }
 
    @Test(dependsOnMethods="getTopics")
    public void selectTopicTitle() throws Exception
    {
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_THREE).click().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        
    }
 
    @Test(dependsOnMethods = "selectTopicTitle")
    public void selectTopicUser() throws Exception
    {
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
        myProfilePage = myDiscussionsDashlet.selectTopicUser(TOPIC_DETAILS_AUTHOR).click().render();
        Assert.assertNotNull(myProfilePage);
        Assert.assertTrue(myProfilePage.titlePresent());
        
    }
    
    @Test(dependsOnMethods = "selectTopicUser")
    public void selectTopicsFilter() throws Exception
    {
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
        myDiscussionsDashlet.clickTopicsButtton();
        
        siteDashBoard = myDiscussionsDashlet.selectTopicsFilter(ALL_TOPICS).render();
        List<ShareLink> allTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(allTopics);
        Assert.assertEquals(allTopics.size(), 3);
        Assert.assertEquals(allTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(allTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(allTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);
             
        myDiscussionsDashlet.clickTopicsButtton();
        siteDashBoard = myDiscussionsDashlet.selectTopicsFilter(MY_TOPICS).render();
        List<ShareLink> myTopics = myDiscussionsDashlet.getTopics(LinkType.User);
        Assert.assertNotNull(myTopics);
        Assert.assertEquals(myTopics.size(), 3);
        for (ShareLink myTopic : myTopics)
        {
            Assert.assertEquals(myTopic.getDescription(), TOPIC_DETAILS_AUTHOR);
        }
        
    }
    
    
    @Test(dependsOnMethods = "selectTopicsFilter")
    public void selectHistoryFilter() throws Exception
    {
        myDiscussionsDashlet.clickHistoryButtton();
        siteDashBoard = myDiscussionsDashlet.selectTopicsHistoryFilter(LAST_DAY_TOPICS).render();
        List<ShareLink> lastDayTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(lastDayTopics);
        Assert.assertEquals(lastDayTopics.size(), 3);
        Assert.assertEquals(lastDayTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(lastDayTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(lastDayTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);
        
        myDiscussionsDashlet.clickHistoryButtton();
        siteDashBoard = myDiscussionsDashlet.selectTopicsHistoryFilter(SEVEN_DAYS_TOPICS).render();
        List<ShareLink> sevenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(sevenDaysTopics);
        Assert.assertEquals(sevenDaysTopics.size(), 3);
        Assert.assertEquals(sevenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(sevenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(sevenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);     
                
        myDiscussionsDashlet.clickHistoryButtton();
        siteDashBoard = myDiscussionsDashlet.selectTopicsHistoryFilter(FOURTEEN_DAYS_TOPICS).render();
        List<ShareLink> fourteenDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(fourteenDaysTopics);
        Assert.assertEquals(fourteenDaysTopics.size(), 3);
        Assert.assertEquals(fourteenDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(fourteenDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(fourteenDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);
        
        myDiscussionsDashlet.clickHistoryButtton();
        siteDashBoard = myDiscussionsDashlet.selectTopicsHistoryFilter(TWENTY_EIGHT_DAYS_TOPICS).render();
        List<ShareLink> twentyEightDaysTopics = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(twentyEightDaysTopics);
        Assert.assertEquals(twentyEightDaysTopics.size(), 3);
        Assert.assertEquals(twentyEightDaysTopics.get(0).getDescription(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(twentyEightDaysTopics.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(twentyEightDaysTopics.get(2).getDescription(), TOPIC_DETAILS_TITLE_ONE);        
        
    }
    
    
    @Test(dependsOnMethods="selectHistoryFilter")
    public void getUpdatedTopics() throws Exception
    {
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE)));
        //myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_THREE).click().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_THREE);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        
        //edit topic 
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_THREE + " Updated");        
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();
                
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_THREE + " Updated")));
        
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_TWO).click().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_TWO);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        
        //edit topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_TWO + " Updated");        
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();
        
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_TWO + " Updated")));
        
        topicDetailsPage = myDiscussionsDashlet.selectTopicTitle(TOPIC_DETAILS_TITLE_ONE).click().render();
        Assert.assertNotNull(topicDetailsPage);
        Assert.assertEquals(topicDetailsPage.getTopicTitle(), TOPIC_DETAILS_TITLE_ONE);
        Assert.assertEquals(topicDetailsPage.getTopicCreatedBy(), TOPIC_DETAILS_CREATED_ON);
        
        //edit topic
        createNewTopicPage = topicDetailsPage.clickOnEditLink().render();
        Assert.assertEquals(createNewTopicPage.getPageTitle(), EDIT_TOPIC_TITLE);
        createNewTopicPage.enterTopicTitle(TOPIC_DETAILS_TITLE_ONE + " Updated");        
        topicDetailsPage =  createNewTopicPage.saveTopic().render();
        topicDetailsPage.clickOnReplyLink().render();
        topicDetailsPage.clickOnCreateReply().render();
        
        navigateToSiteDashboard();
        myDiscussionsDashlet = siteDashBoard.getDashlet(MY_DISCUSSIONS).render();
        drone.findAndWaitWithRefresh(By.xpath(String.format("//a[text()='%s']", TOPIC_DETAILS_TITLE_ONE + " Updated")));
        
        List<ShareLink> topicsTitles = myDiscussionsDashlet.getTopics(LinkType.Topic);
        Assert.assertNotNull(topicsTitles);
        Assert.assertEquals(topicsTitles.size(), 3);

        Assert.assertEquals(topicsTitles.get(0).getDescription(), TOPIC_DETAILS_TITLE_ONE + " Updated");
        Assert.assertEquals(topicsTitles.get(1).getDescription(), TOPIC_DETAILS_TITLE_TWO + " Updated");
        Assert.assertEquals(topicsTitles.get(2).getDescription(), TOPIC_DETAILS_TITLE_THREE + " Updated");
        
        
        List<TopicStatusDetails> topicStatusDetails = myDiscussionsDashlet.getUpdatedTopics();
        
        for (TopicStatusDetails topicStatusDetail : topicStatusDetails)
        {
            Assert.assertEquals(topicStatusDetail.getCreationTime(), CREATED_BY_JUST_NOW);
            Assert.assertEquals(topicStatusDetail.getUpdateTime(), UPDATED_JUST_NOW);
            Assert.assertEquals(topicStatusDetail.getNumberOfReplies(), NUMBER_OF_REPLIES);
            Assert.assertEquals(topicStatusDetail.getReplyDetails(), REPLY_DETAILS);
        }
        
    }
      
}

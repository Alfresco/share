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
package org.alfresco.po.share.systemsummary;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.exception.PageOperationException;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebElement;

/**
 * @author sergey.kardash on 4/14/14.
 */
@SuppressWarnings("unchecked")
public class SystemSummaryPage extends AdvancedAdminConsolePage
{

    public final By AUT_DIRECTORIES_HEAD = By.cssSelector("tbody>tr>th");
    public final By AUT_DIRECTORIES = By.cssSelector("tbody>tr>td");

    public static enum systemInformation {
        AlfreacoHome("//span[contains(text(), 'Alfresco Home:')]/.."),
        AlfreacoEdition("//span[contains(text(), 'Alfresco Edition:')]/.."),
        AlfreacoVersion("//span[contains(text(), 'Alfresco Version:')]/.."),
        JavaHome("//span[contains(text(), 'Java Home:')]/.."),
        JavaVersion("//span[contains(text(), 'Java Version:')]/.."),
        JavaVmVendor("//span[contains(text(), 'Java VM Vendor:')]/.."),
        OperatingSystem("//span[contains(text(), 'Operating System:')]/.."),
        Version("//span[text()='Version:']/.."),
        Architecture("//span[contains(text(), 'Architecture:')]/.."),
        FreeMemory("//span[contains(text(), 'Architecture:')]/.."),
        MaximumMemory("//span[contains(text(), 'Architecture:')]/.."),
        TotalMemory("//span[contains(text(), 'Architecture:')]/..");


        String get;
        systemInformation(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum fileSystems {
        CIFS("//span[contains(text(), 'CIFS:')]/.."),
        FTP("//span[contains(text(), 'FTP:')]/.."),
        NFS("//span[contains(text(), 'NFS:')]/.."),
        WebDAV("//span[contains(text(), 'WebDAV:')]/.."),
        SPP("//span[contains(text(), 'SPP:')]/..");

        String get;
        fileSystems(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum email {
        Inbound("//span[contains(text(), 'Inbound:')]/.."),
        IMAP("//span[contains(text(), 'IMAP:')]/..");

        String get;
        email(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum transformationServices {
        OpenOfficeDirect("//span[contains(text(), 'Office Suite:')]/.."),
        JODConverter("//span[contains(text(), 'JOD Converter:')]/.."),
        SWFTools("//span[contains(text(), 'SWF Tools:')]/.."),
        FFMpeg("//span[contains(text(), 'FFMpeg:')]/.."),
        ImageMagic("//span[contains(text(), 'ImageMagic:')]/..");

        String get;
        transformationServices(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum auditingServices {
        Audit("//span[contains(text(), 'Audit:')]/.."),
        CMISChangeLog("//span[contains(text(), 'CMIS Change Log:')]/.."),
        AlfrescoAccess("//span[contains(text(), 'Alfresco Access:')]/.."),
        Tagging("//span[contains(text(), 'Tagging:')]/.."),
        Sync("//span[contains(text(), 'Sync:')]/..");

        String get;
        auditingServices(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum indexingSubsystem {
        Solr("//span[contains(text(), 'Solr:')]/.."),
        Solr4("//span[contains(text(), 'Solr 4:')]/.."),
        Lucene("//span[contains(text(), 'Lucene:')]/.."),
        NoIndex("//span[contains(text(), 'No Index:')]/..");

        String get;
        indexingSubsystem(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }

        public String getStatus(){
            return get+"/span/img/following-sibling::span";
        }
    }

    public static enum contentStores {
        StorePath("//span[contains(text(),'Store Path:')]/.."),
        SpaceUsed("//span[contains(text(), 'Space Used (MB):')]/.."),
        SpaceAvailable("//span[contains(text(), 'Space Available (MB):')]/..");

        String get;
        contentStores(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum repositoryClustering {
        Clustering("//span[contains(text(), 'Clustering:')]/.."),
        ClusterName("//span[contains(text(), 'Cluster Name:')]/.."),
        ClusterMembers("//span[contains(text(), 'Cluster Members:')]/..");

        String get;
        repositoryClustering(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum activitiesFeed {
        Feed("//span[contains(text(), 'Feed:')]/..");

        String get;
        activitiesFeed(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum modulePackages {
        CurrentlyInstalled("//span[contains(text(), 'Currently Installed:')]/.."),
        PreviouslyInstalled("//span[contains(text(), 'Previously Installed:')]/..");

        String get;
        modulePackages(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    public static enum usersAndGroups {
        Users("//span[contains(text(), 'Users:')]/.."),
        Groups("//span[contains(text(), 'Groups:')]/..");

        String get;
        usersAndGroups(String get){
            this.get = get;
        }
        public String get(){
            return get;
        }
    }

    /**
     * Gets header names of the Authentication Directories table
     *
     * @return names of Authentication Directories table header
     */
    public List<String> getAutHeadDirectories()
    {
        List<String> columnNames = new ArrayList<String>();
        try
        {
            List<WebElement> elements = driver.findElements(AUT_DIRECTORIES_HEAD);
            for (WebElement webElement : elements)
            {
                columnNames.add(webElement.getText());
            }
            return columnNames;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find elements", nse);
        }

    }

    /**
     * Gets list of the Authentication Directories
     *
     * @return names of the Authentication Directories
     */
    public List<String> getAutDirectoriesNames()
    {
        List<String> columnNames = new ArrayList<String>();
        try
        {
            List<WebElement> elements = driver.findElements(AUT_DIRECTORIES);
            for (WebElement webElement : elements)
            {
                columnNames.add(webElement.getText());
            }
            return columnNames;
        }
        catch (NoSuchElementException nse)
        {
            throw new PageOperationException("Unable to find elements", nse);
        }

    }

}

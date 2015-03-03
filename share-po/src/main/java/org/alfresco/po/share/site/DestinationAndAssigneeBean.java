/*
 * Copyright (C) 2005-2013 Alfresco Software Limited.
 * This file is part of Alfresco
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */
package org.alfresco.po.share.site;

import java.util.Arrays;

/**
 * @author nshah
 *         version 1.7.0
 */
public class DestinationAndAssigneeBean
{
    private String network;
    private String siteName;
    private String[] syncToPath;
    private boolean isLockOnPrem = false;
    private boolean isExcludeSubFolder = false;
    private static final String DEFAULT_FOLDER_NAME = "Documents";

    public String getNetwork()
    {
        return network;
    }

    public void setNetwork(String network)
    {
        this.network = network;
    }

    public String getSiteName()
    {
        return siteName;
    }

    public void setSiteName(String siteName)
    {
        this.siteName = siteName;
    }

    /**
     * If user does not set path for the sync file location on cloud
     * default return will be "Documents".
     * 
     * @return
     */
    public String[] getSyncToPath()
    {
        if (syncToPath == null || syncToPath.length < 1)
        {
            return new String[] { DEFAULT_FOLDER_NAME };
        }
        return Arrays.copyOf(syncToPath, syncToPath.length);
    }

    public void setSyncToPath(String... syncToPath)
    {
        this.syncToPath = syncToPath;
    }

    public boolean isLockOnPrem()
    {
        return isLockOnPrem;
    }

    public void setLockOnPrem(boolean isLockOnPrem)
    {
        this.isLockOnPrem = isLockOnPrem;
    }

    public boolean isExcludeSubFolder()
    {
        return isExcludeSubFolder;
    }

    public void setExcludeSubFolder(boolean isIncludeSubFolder)
    {
        this.isExcludeSubFolder = isIncludeSubFolder;
    }

}

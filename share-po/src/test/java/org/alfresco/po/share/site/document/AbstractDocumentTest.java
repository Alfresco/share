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
package org.alfresco.po.share.site.document;

import java.io.File;

import org.alfresco.po.share.AbstractTest;
import org.alfresco.po.share.site.SiteDashboardPage;
import org.alfresco.webdrone.HtmlPage;
import org.alfresco.webdrone.exception.PageException;
/**
 * Abstract test holds all common methods and functionality to test against
 * Document based tests.
 * 
 * @author Michael Suzuki
 */
public abstract class AbstractDocumentTest extends AbstractTest
{
    /**
     * Helper method to navigate to document library page of a site that we
     * create for the test.
     * @param siteName String site identifier
     * @return HtmlPage document library page.
     * @throws PageException if error
     */
    protected DocumentLibraryPage getDocumentLibraryPage(final String siteName) throws PageException
    {
        SiteDashboardPage site = getSiteDashboard(siteName);
        return site.getSiteNav().selectSiteDocumentLibrary().render();
    }
    
    /**
     * Prepare test by getting the drone to the correct page.
     * 
     * @param fileName String file name
     * @return {@link DocumentDetailsPage} page object
     * @throws PageException if error
     */
    protected HtmlPage selectDocument(File file) throws PageException
    {
        DocumentLibraryPage docsPage = drone.getCurrentPage().render();
        return docsPage.selectFile(file.getName()).render();
    }
}

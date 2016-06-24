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
package org.alfresco.po.share;

import org.alfresco.po.RenderTime;
import org.alfresco.po.share.site.document.DocumentLibraryPage;

/**
 * Repository page object, holds all element of the HTML page relating to
 * share's repository page.
 * 
 * @author Michael Suzuki
 * @author Shan Nagarajan
 * @since 1.0
 */
@SuppressWarnings("unchecked")
public class RepositoryPage extends DocumentLibraryPage
{

    @Override
    public RepositoryPage render(RenderTime timer)
    {
        super.render(timer);
        return this;
    }

    /**
     * Verify if people finder title is present on the page
     * 
     * @return true if exists
     */
    public boolean titlePresent()
    {
        return isBrowserTitle("Repository Browser");
    }
}

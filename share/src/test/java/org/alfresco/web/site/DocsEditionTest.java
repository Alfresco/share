/*
 * #%L
 * Alfresco Share WAR
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
package org.alfresco.web.site;


import static org.junit.Assert.*;

import org.json.JSONException;
import org.junit.Test;

public class DocsEditionTest
{
    private DocsEdition edition;
    
    @Test
    public void getValueReturnsCommunityWhenEditionUNKNOWN() throws JSONException
    {
        edition = new DocsEdition(EditionInfo.UNKNOWN_EDITION, null, false);
        assertEquals(DocsEdition.COMMUNITY, edition.getValue());
        
        edition = new DocsEdition();
        assertEquals(DocsEdition.COMMUNITY, edition.getValue());
    }
    
    @Test
    public void getValueReturnsCommunityWhenEditionEnterpriseButSpecificationVersionUnknown() throws JSONException
    {
        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, null, false);
        assertEquals(DocsEdition.COMMUNITY, edition.getValue());
        
        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "", false);
        assertEquals(DocsEdition.COMMUNITY, edition.getValue());
        
        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "abc", false);
        assertEquals(DocsEdition.COMMUNITY, edition.getValue());
    }
    
    @Test
    public void getValueReturnsVersionWhenEditionIsInCloud() throws JSONException
    {
        edition = new DocsEdition(true);
        assertEquals(DocsEdition.CLOUD, edition.getValue());
        
        edition = new DocsEdition(EditionInfo.UNKNOWN_EDITION, null, true);
        assertEquals(DocsEdition.CLOUD, edition.getValue());

        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "5.1.0", true);
        assertEquals(DocsEdition.CLOUD, edition.getValue());
    }
    
    @Test
    public void getValueReturnsVersionWhenEditionENTERPRISE() throws JSONException
    {
        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "5.1.0", false);
        assertEquals("5.1", edition.getValue());

        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "5.1-SNAPSHOT", false);
        assertEquals("5.1", edition.getValue());

        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "5.1", false);
        assertEquals("5.1", edition.getValue());

        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "10.10.10", false);
        assertEquals("10.10", edition.getValue());

        edition = new DocsEdition(EditionInfo.ENTERPRISE_EDITION, "10.10", false);
        assertEquals("10.10", edition.getValue());
    }
}

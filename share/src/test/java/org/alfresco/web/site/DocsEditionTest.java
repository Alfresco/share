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

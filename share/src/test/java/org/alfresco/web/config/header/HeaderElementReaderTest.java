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
package org.alfresco.web.config.header;

import static org.mockito.Mockito.*;

import java.util.ArrayList;

import org.dom4j.Element;
import org.junit.Test;

public class HeaderElementReaderTest
{

    @Test
    public void testALF18394()
    {
        Element configElement = mock(Element.class);
        when(configElement.getName()).thenReturn(HeaderElementReader.ELEMENT_HEADER);
        when(configElement.elements(HeaderItemsElementReader.ELEMENT_LEGACY)).thenReturn(new ArrayList<Element>());
        when(configElement.element(HeaderItemsElementReader.ELEMENT_MAX_RECENT_SITES)).thenReturn(null);
        when(configElement.element(HeaderItemsElementReader.ELEMENT_MAX_DISPLAYED_SITE_PAGES)).thenReturn(null);
        
        HeaderElementReader reader = new HeaderElementReader();
        reader.parse(configElement);
    }

}

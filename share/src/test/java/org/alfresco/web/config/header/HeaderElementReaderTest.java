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

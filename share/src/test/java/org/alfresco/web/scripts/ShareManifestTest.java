/*
 * Copyright (C) 2005-2015 Alfresco Software Limited.
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
package org.alfresco.web.scripts;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.io.FileSystemResource;
import org.springframework.extensions.webscripts.Container;
import org.springframework.extensions.webscripts.Description;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.WebScriptResponse;

/**
 * TODO: comment me!
 * @author Matt Ward
 */
@RunWith(MockitoJUnitRunner.class)
public class ShareManifestTest
{
    private ShareManifest shareManifest;
    private File manifestFile;
    private @Mock WebScriptRequest req;
    private @Mock WebScriptResponse res;
    private @Mock Container container;
    private @Mock Description description;
    private StringWriter stringWriter;
    private boolean webScriptInitialised = false;
    
    @Before
    public void setUp() throws Exception
    {
        // Write a sample manifest file that we can read with the class under test.
        manifestFile = File.createTempFile("Manifest-Test", "MF");
        manifestFile.deleteOnExit();
        try (PrintWriter pw = new PrintWriter(manifestFile))
        {
            pw.println("First-Attr: Red");
            pw.println("Second-Attr: Green");
        }
        
        // Collect output from the webscript response in a StringBuffer so that
        // we can later verify what was sent to the client by the webscript.
        stringWriter = new StringWriter();
        when(res.getWriter()).thenReturn(stringWriter);
        
        // Create an instance of the class under test.
        shareManifest = new ShareManifest(new FileSystemResource(manifestFile))
        {
            // Override so that we simply flag whether the method was called. We really don't
            // want to have to mock out all sorts of webscript-specific init implementation details
            // as this stuff should be tested elsewhere (and if we did, this test would become very brittle).
            @Override
            protected void initWebScript(Container container, Description description)
            {
                webScriptInitialised = true;
            }
        };
        
        shareManifest.init(container, description);
        assertTrue("initWebScript should have been called during initialisation.", webScriptInitialised);
    }

    @Test
    public void manifestFileIsConvertedToJSON() throws IOException
    {
        shareManifest.execute(req, res);
        
        String expectedJSON = "{\"First-Attr\":\"Red\",\"Second-Attr\":\"Green\"}";
        assertEquals(expectedJSON, stringWriter.getBuffer().toString());
    }
}

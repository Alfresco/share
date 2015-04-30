/**
 * Copyright (C) 2005-2014 Alfresco Software Limited.
 *
 * This file is part of the Spring Surf Extension project.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.extensions.surf.mvc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * This controller has been added specifically to address the issue of WebScripts running in the Surf container not
 * being able to generate a binary response. Currently this controller only addresses a single use case which is to
 * generate a sample Extension Module JAR but in the future could be extended to address other use cases.
 * 
 * @author dave
 */
public class GeneratorController extends AbstractController
{
    private static Log logger = LogFactory.getLog(GeneratorController.class);
    
    /**
     * This is a basic template for an extension module. It contains tokens that can be replaced with specific
     * data for targeting a WebScripts.
     */
    public static final String TEMPLATE = 
       "<extension>\n" + 
       "  <modules>\n" +
       "    <module>\n" +
       "      <id>${id}</id>\n" +
       "      <auto-deploy>true</auto-deploy>\n" + 
       "      <evaluator type=\"default.extensibility.evaluator\"/>\n" +
       "      <customizations>\n" +
       "        <customization>\n" + 
       "           <targetPackageRoot>${target}</targetPackageRoot>\n" + 
       "           <sourcePackageRoot>${source}</sourcePackageRoot>\n" + 
       "        </customization>\n" +
       "      </customizations>\n" + 
       "    </module>\n" +
       "  </modules>\n" +
       "</extension>";
    
    /**
     * This is a template for the JavaScript controller. It is just a comment.
     */
    public static final String CONTROLLER_TEMPLATE = 
        "// Add JavaScript to modify the JSON model for the page";
    
    /**
     * 
     */
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = null;
        try
        {
            // Grab the WebScript ID provided as this will be used for constructing the extension 
            // and customization controller details...
            String path = "";
            String fileName = "";
            String webscriptId = request.getParameter("webscriptId");
            if (webscriptId != null)
            {
                int index = webscriptId.lastIndexOf("/");
                if (index != -1)
                {
                    path = webscriptId.substring(0, index);
                    fileName = webscriptId.substring(index);
                }
            }
            
            // Replace the tokens in the standard extension module configuration template...
            String s = TEMPLATE;
            s = s.replace("${id}", "Extension Module");
            s = s.replace("${target}", path.replace("/", "."));
            s = s.replace("${source}", path.replace("/", ".") + ".customization");
            
            // Create the extension module configuration file...
            zos = new ZipOutputStream(baos);
            
            InputStream stream = new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8));
            InputStreamReader isr = new InputStreamReader(stream);
            ZipEntry ze = new ZipEntry("alfresco/site-data/extensions/extension.xml");
            zos.putNextEntry(ze);
            int c;
            while ((c = isr.read()) != -1)
            {
                zos.write(c);
            }
            zos.closeEntry();
            
            // Create the JavaScript controller customization file...
            ze = new ZipEntry("alfresco/site-webscripts/" + path +  "/customization" + fileName + ".js");
            zos.putNextEntry(ze);
            stream = new ByteArrayInputStream(CONTROLLER_TEMPLATE.getBytes(StandardCharsets.UTF_8));
            isr = new InputStreamReader(stream);
            while ((c = isr.read()) != -1)
            {
                zos.write(c);
            }
            zos.closeEntry();
            
            // Add a META-INF folder, this is where JavaScript/CSS resources can be added...
            zos.putNextEntry(new ZipEntry("META-INF/."));
        }
        catch (IOException e)
        {
            logger.error("The following error occurred attempting to generate an extension module JAR", e);
        }
        finally
        {
            try
            {
                zos.close();
            }
            catch (IOException e)
            {
                logger.error("The following error occurred attempting to close a ZipOutputStream for a generated extension module JAR file", e);
            }
        }
       
        // Extension JAR headers...
        String filename = "Extension.jar";
        response.setHeader("Content-Disposition","attachment; filename=\"" + filename + "\"");
        response.setContentType("application/zip");
        response.setHeader("Content-Transfer-Encoding", "binary");

        try
        {
            response.getOutputStream().write(baos.toByteArray());
        }
        catch (IOException e)
        {
            logger.error("The following error occurred attempting write the contents of a generated extension module JAR file", e);
        }
        finally
        {
            baos.close();
        }
        return null;
    }
}

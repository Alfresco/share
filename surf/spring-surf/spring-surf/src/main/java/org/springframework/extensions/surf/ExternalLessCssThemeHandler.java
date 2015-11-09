/**
 * Copyright (C) 2005-2009 Alfresco Software Limited.
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
package org.springframework.extensions.surf;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * This is a LESS CSS handler driven by a pre-configured external LESS process.
 * <p>
 * Typically uses a Node "lessc" module preinstalled via NPM or similar. This is not
 * expected to be used in production environments where adding additional 3rd party
 * modules to the web-tier is not desired or permitted. 
 * @see <a href="http://lesscss.org/#using-less-installation">http://lesscss.org/</a>
 * 
 * @author Kevin Roast
 * @since 5.2
 */
public class ExternalLessCssThemeHandler extends LessCssThemeHandler
{
    private static final Log logger = LogFactory.getLog(ExternalLessCssThemeHandler.class);
    
    private String cmd;
    
    /**
     * @param cmd   The external cmd to execute. For example Node lessc this would be "lessc -".
     *              The command must be able to accept LESS CSS as stdin and return output from stdout.
     */
    public void setCmd(String cmd)
    {
        this.cmd = cmd;
    }

    /**
     * Sets up a new instance.
     */
    public ExternalLessCssThemeHandler()
    {
    }

    /**
     * Overrides the default implementation to add LESS processing capabilities.
     * 
     * @param path The path of the file being processed (used only for error output)
     * @param cssContents The CSS to process
     * @throws IOException when accessing file contents.
     */
    @Override
    public String processCssThemes(String path, StringBuilder cssContents) throws IOException
    {
        if (this.cmd == null || cmd.length() == 0)
        {
            throw new IllegalArgumentException("External LESS 'cmd' not set correctly in bean config.");
        }
        
        // setup our external process and retrieve streams
        Process proc = Runtime.getRuntime().exec(this.cmd);
        BufferedWriter stdIn = new BufferedWriter(new OutputStreamWriter(proc.getOutputStream()));
        BufferedReader stdOut = new BufferedReader(new InputStreamReader(proc.getInputStream()));
        BufferedReader stdError = new BufferedReader(new InputStreamReader(proc.getErrorStream()));
        
        // push our CSS to the standard Input of the external process
        stdIn.append(this.getLessVariables());
        stdIn.append(cssContents.toString());
        stdIn.close();
        
        // read the output from the command
        StringBuilder buf = new StringBuilder(1024);
        String s;
        while ((s = stdOut.readLine()) != null) {
            buf.append(s);
        }
        
        // read any errors from the attempted command
        if ((s = stdError.readLine()) != null)
        {
            // error occured, collect information and throw exception with the message
            buf = new StringBuilder("Error during external LESS compilation for path: ").append(path).append("\r\n");
            do {
                buf.append(s);
            } while ((s = stdOut.readLine()) != null);
            throw new IOException(buf.toString());
        }
        
        return buf.toString();
    }
}

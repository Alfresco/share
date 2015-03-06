***********************
*                     *
*  ABOUT THIS FOLDER  *
*                     *
***********************


 1. FILES
==========

src/                     - Alfresco sources for using the org.alfresco.previewer.Previewer as a Flex component
src/assets               - Skinning graphics: icons & cursors
src/assets/test          - Pre generated .swf files from pdf2swf to test against
src/com/
src/com/wayne_dash_marsh - Support for simulating threads in Flash Player
src/madebypi             - Adds support for non english keyboard input on windows
src/org
src/org/alfresco         - The Alfresco AS3 source files
src/org/hasseg           - Adds support for scrolling mouse wheel on mac
copy-to-html-template    - Files to copy to Flash Builder project's html-template folder


 2. SETUP DEV ENV EXAMPLE
==========================

1. Install Flash Builder 4.7 (Use trial if you don't have a license)

2. File > Switch Workspace > Other:
   /Users/erikwinlof/Documents/Adobe Flash Builder 4/Alfresco_HEAD

3. File > New > Flex Project

   Project Location
   ----------------
   Project Name: WebPreviewer
   Project location: /Users/erikwinlof/Development/projects/head/code/root/projects/slingshot/source/as/webpreviewer
   Application type: Web
   Use a specific JDK: 3.6A (This will make it possible to use a Flash Player of version 9.0.124)
   [Next]

   Server Settings
   ---------------
   Application server type: None/Other
   Output folder: /Users/erikwinlof/Development/projects/head/software/tomcat-app/webapps/share/WebPreviewer_Test
   [Next]

   Build Paths
   ---------------------
   Main source folder: src
   Main application file: WebPreviewer.mxml
   Output folder: http://localhost:8081/share/WebPreviewer_Test
   [Finish]

   Project build....

4. Once build is finished copy the files in "copy-to-html-template" into "html-template" folder (overwrite duplicates).
   This will make sure extra javascript resources is imported to the test page and that we apply all WebPreviewer
   specific input parameters when the previewer is debugged and displayed.

5. Project > Clean
   Run > Run > WebPreviewer (will open browser w previewer)

   Note!
   Ignore the errors like "ReferenceError: Error #1065: Variabeln stop_fla:MainTimeline hasn't been defined."
   Which may be thrown when you load external .swf-files (the document to view) into the WebPreviewer.swf using the Flash Debug Player

6. Develop... (use Run > Debug > WebPreviewer to debug)

7. When satisfied, make sure to include it in Share by replacing ...
   /Users/ewinlof/Development/projects/head/code/root/projects/slingshot/source/web/components/preview/WebPreviewer.swf
   ... with ...
   /Users/erikwinlof/Development/projects/head/software/tomcat-app/webapps/share/WebPreviewer_Test/WebPreviewer.swf
   ... then test in Share!

8. Commit and make sure you DO NOT include any Flash Builder / Eclipse project files   

Note!
If you hit a security violation its probably because you view the files on your local filesystem instead of on a web browser.
This shall NOT happen if you follow the instructions below.

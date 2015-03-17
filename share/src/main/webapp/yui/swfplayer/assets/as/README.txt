DEBUGGING AND DEVELOPOING ACTIONSCRIPT FILES WITH FLEX BUILDER


SETTING UP THE PROJECT

1. Open Flex builder 
2. Choose File > New > ActionScript project
3. Name it "AlfrescoShareComponent"
4. Deactivate "Use dafult folder" and click browse and select:    
   <branch>/code/root/projects/slingshot/source/web/yui/swfplayer/assets
5. Click browse for "Main application file" and select one of the actionscript files, ie SWFPlayer.as.
6. Click browse for "Output folder" and select the following folder or equivavlent:
   <branch>/software/apache-tomcat-5.5.23-app/webapps/share/yui/swfplayer/assets
   This will make your changes easy to run inside share during development.
   Note! Don't forget to move them to the following dir after you are satisfied with your changes:
   <branch>/code/root/projects/slingshot/source/web/yui/swfplayer/assets
7. Click browse for "Output folder url" and write the path to the folder thought the webserver:
   http://localhost:8080/share/yui/swfplayer/assets
8. Click Finish


RUNNING AND DEBUGGING SWFPLAYER

1. Since it in reality is the Share UI that embeds the SWFPlayer.swf file and communicates 
   with it throught javascript you will have to change a line to debug the file.
2. Look in the SWFPlayer constructor for a commented line that says:
   //test();
3. Uncomment this line and click "Run" (the play button) or "Debug" (the bug icon).
   This will create a html wrapper file that will invoke the SWFPlayer.swf.
   Instead of receiving events from javascript the SWFPlayer will run its test method and try 
   to display an external file.
4. To specify what extenral .swf go in to the test() method and change the url.
5. After debugging DON NOT forget to comment out test().
6. If you have made changes move the SWFPlayer.swf file to its correct location mentioned in the first section.

   

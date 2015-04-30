<!-- This file was referenced from "WEB-INF\surf-config\templates\template-with-regions.xml" -->
<html>
    <head>
        <title>Example Surf Page</title>
        
        <#-- This is where the JavaScript and CSS dependencies will initially be added through the use of the 
             <@script> and <@link> directives. The JavaScript can be moved through the use 
             of the <@relocateJavaScript> directive (i.e. to move it to the end of the page). These directives 
             must be placed before directives that add dependencies to them otherwise those resources will
             be placed in the output of the ${head} variable (i.e. this applied to all usage of those directives
             in *.head.ftl files) -->
        <@outputJavaScript/>
        <@outputCSS/>
        
        <#-- This variable will be populated with the contents of any WebScript *.head.ftl files that are processed
             for a page. This was the orginal way that JavaScript and CSS dependency files were included in the 
             HTML <head> element. This approach is still used in versions of Alfresco 4.x but has been superceded 
             by the <@outputJavaScript> and <@ouputCSS> directives in the latest code.

             Any depdendencies added through the use of the <dependencies> element in Extension modules will also
             be output in this variable. -->
        ${head}
    </head>
    <body>
        <div id="main">
            <@region id="header" scope="page"/>
            <@region id="body" scope="page"/>
        </div>
        <@region id="footer" scope="page"/>
    </body>
</html>
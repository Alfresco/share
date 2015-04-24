<!-- This file was referenced from "WEB-INF\surf-config\templates\template-with-regions.xml" -->
<html>
    <head>
        <title>Page With Regions</title>
    </head>
    <body>
        <@region id="region1" scope="global"/>
		<@region id="region2" scope="page"/>
		<@region id="region3" scope="template"/>
    </body>
</html>
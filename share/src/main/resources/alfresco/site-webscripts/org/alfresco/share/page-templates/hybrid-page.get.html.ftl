<#include "./hybrid-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   <div id="content">
      <#assign regionId = page.url.templateArgs.webscript?replace("/", "-")/>
      <@autoComponentRegion uri="/${page.url.templateArgs.webscript}"/>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
</@>
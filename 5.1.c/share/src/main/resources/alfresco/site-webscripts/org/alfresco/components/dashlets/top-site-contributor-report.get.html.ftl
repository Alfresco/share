<@markup id="widgets">
   <@processJsonModel group="share-dashlets" rootModule="alfresco/core/Page"/>
</@>

<@markup id="html">
<!-- todo use component's id here so we get a unique id -->
<div id="${args.htmlid?html}"></div>
</@>

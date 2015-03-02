<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/discussions/replies.css" group="discussions"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/discussions/replies.js" group="discussions"/>
</@>

<@markup id="widgets">
   <@createWidgets group="discussions"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
     <div id="${args.htmlid}-replies-root" class="indented hidden"></div>
   </@>
</@>



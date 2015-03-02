<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/modules/taglibrary/taglibrary.css" group="discussions" />
   <@link href="${url.context}/res/components/discussions/topic.css" group="discussions" />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/modules/taglibrary/taglibrary.js" group="discussions"/>
   <@script src="${url.context}/res/components/discussions/topic.js" group="discussions"/>
</@>

<@markup id="widgets">
   <@createWidgets group="discussions"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-topic">
         <div id="${args.htmlid}-topic-view-div">
         </div>
         <div id="${args.htmlid}-topic-edit-div" class="hidden">
         </div>
      </div>
   </@>
</@>


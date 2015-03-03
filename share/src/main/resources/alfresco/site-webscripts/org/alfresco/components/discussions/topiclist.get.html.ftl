<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/discussions/topiclist.css" group="discussions"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/discussions/topiclist.js" group="discussions"/>
</@>

<@markup id="widgets">
   <@createWidgets group="discussions"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="topiclist-infobar yui-gd theme-bg-color-4">
         <div class="yui-u first">
            <div id="${args.htmlid}-listtitle" class="listTitle">
               ${msg("title.generic")}
            </div>
         </div>
         <div class="yui-u flat-button">
            <div id="${args.htmlid}-paginator" class="paginator">&nbsp;</div>
            <div class="simple-view">
               <button id="${args.htmlid}-simpleView-button" name="topiclist-simpleView-button">${msg("header.simpleList")}</button>
            </div>
         </div>
      </div>
      <div id="${args.htmlid}-topiclist" class="topiclist"></div>
   </@>
</@>
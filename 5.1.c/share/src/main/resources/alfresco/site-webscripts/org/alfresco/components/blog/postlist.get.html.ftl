<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/postlist.css" group="blog"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/postlist.js" group="blog"/>
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="postlist-infobar yui-gd theme-bg-color-4">
         <div class="yui-u first">
            <div id="${args.htmlid}-listtitle" class="listTitle">
               ${msg("title.postlist")}
            </div>
         </div>
         <div class="yui-u flat-button">
            <div id="${args.htmlid}-paginator" class="paginator">&nbsp;</div>
            <div class="simple-view">
               <button id="${args.htmlid}-simpleView-button" name="postlist-simpleView-button">${msg("header.simpleList")}</button>
            </div>
         </div>
      </div>
      <div id="${args.htmlid}-postlist" class="blog-postlist"></div>
   </@>
</@>
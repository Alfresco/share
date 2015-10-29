<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/blog/commentlist.css" group="blog"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/blog/commentlist.js" group="blog"/>
</@>

<@markup id="widgets">
   <@createWidgets group="blog"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div id="${args.htmlid}-body" class="comment-list" style="display:none;">
         <div class="postlist-infobar">
            <div id="${args.htmlid}-title" class="commentsListTitle"></div>
            <div id="${args.htmlid}-paginator" class="paginator"></div>
         </div>
         <div class="clear"></div>
         <div id="${args.htmlid}-comments"></div>
      </div>
   </@>
</@>
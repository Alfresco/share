<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/comments/comments-list.css" group="comments"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/comments/comments-list.js" group="comments"/>
</@>

<@markup id="widgets">
   <#if nodeRef??>
      <@createWidgets group="comments"/>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if nodeRef??>
         <#assign el=args.htmlid?html>
         <div id="${el}-body" class="comments-list">
            <h2 class="thin dark">${msg("header.comments")}</h2>
            <div id="${el}-add-comment">
               <div id="${el}-add-form-container" class="theme-bg-color-4 hidden"></div>
            </div>
            <div class="comments-list-actions">
               <div class="left">
                  <div id="${el}-actions" class="hidden">
                     <button class="alfresco-button" name=".onAddCommentClick">${msg("button.addComment")}</button>
                  </div>
               </div>
               <div class="right">
                  <div id="${el}-paginator-top"></div>
               </div>
               <div class="clear"></div>
            </div>
            <hr class="hidden"/>
            <div id="${el}-comments-list"></div>
            <hr class="hidden"/>
            <div class="comments-list-actions">
               <div class="left">
               </div>
               <div class="right">
                  <div id="${el}-paginator-bottom"></div>
               </div>
               <div class="clear"></div>
            </div>
         </div>
      </#if>
   </@>
</@>
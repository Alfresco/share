<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/console/tag-management.css" group="admin"/>
   </@>
   
   <@markup id="js">
      <#-- JavaScript Dependencies -->
      <@script type="text/javascript" src="${url.context}/res/modules/form/control-wrapper.js" group="admin"/>
      <@script type="text/javascript" src="${url.context}/res/components/console/tag-management.js" group="admin"/>
   </@>
   
   <@markup id="widgets">
      <@createWidgets group="admin"/>
   </@>
   
   <@markup id="html">
      <@uniqueIdDiv>
         <#assign el=args.htmlid?html>
         <div id="${el}-dialog" class="manage-tags">
            <div id="${el}-dialogTitle" class="hd">${msg("label.title")}</div>
            <div class="bd">
               <form id="${el}-form" action="" method="post">
                  <div class="yui-gd">
                     <div class="yui-u first"><label for="${el}-name">${msg("label.newName")}</label>:</div>
                     <div class="yui-u"><input id="${el}-name" type="text" name="name" tabindex="0" value="${tagName?html}"/>&nbsp;*</div>
                  </div>
                  <div class="bdft">
                     <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
                     <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
                  </div>
               </form>
            </div>
         </div>
      </@>
   </@>
</@>

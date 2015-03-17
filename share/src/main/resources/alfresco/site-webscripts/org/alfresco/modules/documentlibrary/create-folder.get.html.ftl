<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="create-folder">
   <div id="${el}-dialogTitle" class="hd">${msg("title")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="post">
         <div class="yui-g">
            <h2 id="${el}-dialogHeader">${msg("header")}</h2>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-name">${msg("label.name")}:</label></div>
            <div class="yui-u"><input id="${el}-name" type="text" name="name" tabindex="0" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${el}-title" type="text" name="title" tabindex="0" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-description">${msg("label.description")}:</label></div>
            <div class="yui-u"><textarea id="${el}-description" name="description" rows="3" cols="20" tabindex="0" ></textarea></div>
         </div>
         <div class="bdft">
            <input type="button" id="${el}-ok" value="${msg("button.ok")}" tabindex="0" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" tabindex="0" />
         </div>
      </form>
   </div>
</div>
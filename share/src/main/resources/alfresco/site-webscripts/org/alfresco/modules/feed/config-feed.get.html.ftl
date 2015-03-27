<#assign el=args.htmlid?html>
<div id="${el}-configDialog" class="config-feed">
   <div class="hd">${msg("label.enterUrl")}:</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-url">${msg("label.url")}:</label></div>
            <div class="yui-u"><input id="${el}-url" type="text" name="url" value="" maxlength="2048" />&nbsp;*</div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label>${msg("label.displayItems")}:</label></div>
            <div class="yui-u">
               <select id="${el}-limit" name="limit">
                <option value="all">${msg("label.all")}</option>
                <#assign limits = [5, 10, 15, 20, 25]>
                <#list limits as limit><option value="${limit}">${msg("label.limit."+limit)}</option></#list>
               </select>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-new_window">${msg("label.newWindow")}:</label></div>
            <div class="yui-u"><input type="checkbox" id="${el}-new_window" name="new_window" /></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
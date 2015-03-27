<#assign el=args.htmlid?html>
<div id="${el}-configDialog">
   <div class="hd">${msg("label.enterSearchTerm")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-searchTerm">${msg("label.searchTerm")}:</label></div>
            <div class="yui-u"><input id="${el}-searchTerm" type="text" name="searchTerm" value="" maxlength="2048" />&nbsp;*</div>
            <div class="yui-u first"><label for="${el}-title">${msg("label.title")}:</label></div>
            <div class="yui-u"><input id="${el}-title" type="text" name="title" value="" maxlength="2048" /></div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label>${msg("label.displayResults")}:</label></div>
            <div class="yui-u">
               <select id="${el}-limit" name="limit" style="width: 60px">
                <#assign limits = [10, 25, 50, 100]>
                <#list limits as limit><option value="${limit}">${msg("label.limit." + limit)}</option></#list>
               </select>
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
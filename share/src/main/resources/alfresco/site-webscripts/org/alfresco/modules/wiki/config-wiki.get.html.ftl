<#assign el=args.htmlid?html>
<div id="${el}-configDialog" class="config-feed">
   <div class="hd">${msg("label.header")}</div>
   <div class="bd">
      <form id="${el}-form" action="" method="POST">
         <input type="hidden" name="siteId" value="${url.templateArgs.siteId}"/>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-url">${msg("label.url")}:</label></div>
            <div class="yui-u">
            <#if pageList.pages?size &gt; 0>
               <select name="wikipage">
               <#list pageList.pages as p>
                  <option value="${p.title?html}">${p.title?html}</option>
               </#list>
               </select>
            <#else>
                ${msg("label.no-pages")}
            </#if>
            </div>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok" value="${msg("button.ok")}" />
            <input type="button" id="${el}-cancel" value="${msg("button.cancel")}" />
         </div>
      </form>
   </div>
</div>
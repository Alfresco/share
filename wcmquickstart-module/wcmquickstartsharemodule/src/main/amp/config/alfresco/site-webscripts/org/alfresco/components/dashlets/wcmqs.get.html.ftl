<script type="text/javascript">//<![CDATA[
   new Alfresco.dashlet.WCMQS("${args.htmlid}").setOptions(
   {
      siteId: "${page.url.templateArgs.site!""}"
   }).setMessages(${messages});
//]]></script>

<div class="dashlet">
   <div class="title">${msg("label.title")}</div>
   <div class="body theme-color-1">      
   
   <#if !dataloaded>             
      <div id="${args.htmlid}-load-data" class="detail-list-item last-item" >
         <div id="${args.htmlid}-load-data-label">${msg("label.importdata")}</div>
         <select id="${args.htmlid}-load-data-options">         		 
		 <#list importids as importid>
         	<option value="${importid}">${importidlabels[importid_index]?html}</option>
         </#list>   
         <br>      
         </select>   
         <button type="button" id="${args.htmlid}-load-data-link">&nbsp;&nbsp;&nbsp;${msg("label.button.import")}&nbsp;&nbsp;&nbsp;</button>      
      </div>      
   <#else>
      <div class="detail-list-item last-item" >
         <a href="${msg("url.help")}" target="_new">${msg("label.help_link")}</a>
      </div>
   </#if>
   
   </div>
</div>
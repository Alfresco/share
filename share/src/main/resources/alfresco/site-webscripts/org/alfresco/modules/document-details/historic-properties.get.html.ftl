<#assign el=args.htmlid?html>
<#assign fileExtIndex = document.fileName?last_index_of(".")>
<#assign fileExt = (fileExtIndex > -1)?string(document.fileName?substring(fileExtIndex + 1), "generic")>
<div id="${el}-dialog" class="historic-properties-viewer">
   <div class="hd">
      <span id="${el}-header-span" class="historic-properties-header" style="background-image:url(${url.context}/res/components/images/filetypes/${fileExt}-file-32.png);"></span>
   </div>
   <div class="bd">
      <div class="bdhd">
         <div class="nav flat-button">
				<a href="#" rel="previous" class="historic-properties-nav prev">previous</a>
				<a href="#" rel="next" class="historic-properties-nav next">next</a>
				<span id="${el}-versionNav-button" class="yui-button yui-push-button">
	            <span class="first-child">
	               <button name="historic-properties-versionNav-menu"></button>
	            </span>
	         </span>
				<select id="${el}-versionNav-menu"></select>
			</div>
      </div>
      
      <div id="${el}-properties-form">
         <p>"${msg("message.loading")}"</p>
      </div>
      
      <div class="bdft">
         <input id="${el}-cancel-button" type="button" value="${msg("historicProperties.dialogue.exit")}" />
      </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.HistoricPropertiesViewer");
//]]></script>

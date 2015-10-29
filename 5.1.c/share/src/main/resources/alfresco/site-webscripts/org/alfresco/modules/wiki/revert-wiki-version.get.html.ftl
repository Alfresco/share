<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="revert-wiki-version">
   <div class="hd">${msg("header.revert")}</div>
   <div class="bd">
         <div class="bdbd">
            <p id="${el}-prompt-span"></p>
         </div>
         <div class="bdft">
            <input id="${el}-ok-button" type="button" value="${msg("button.ok")}" />
            <input id="${el}-cancel-button" type="button" value="${msg("button.cancel")}" />
         </div>
   </div>
</div>

<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RevertWikiVersion");
//]]></script>
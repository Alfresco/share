<#assign el=args.htmlid?html>
<div id="${el}-dialog" class="checkin">
   <div class="hd">${msg("header")}</div>
   <div class="bd">
      <form id="${el}-form" method="POST" action="" enctype="application/json">
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-version-minor">${msg("label.version")}:</label></div>
            <div class="yui-u">
               <input id="${el}-version-minor" type="radio" name="version" tabindex="0" value="minor"/><label for="${el}-version-minor">${msg("label.minor")}</label>
               <input id="${el}-version-major" type="radio" name="version" tabindex="0" value="major"/><label for="${el}-version-major">${msg("label.major")}</label>
            </div>
         </div>
         <div class="yui-gd">
            <div class="yui-u first"><label for="${el}-comments">${msg("label.comments")}:</label></div>
            <div class="yui-u"><textarea id="${el}-comments" name="comments" rows="3" tabindex="0"></textarea></div>
         </div>
         <div class="bdft">
            <input type="submit" id="${el}-ok-button" value="${msg("button.ok")}" tabindex="0"/>
            <input type="button" id="${el}-cancel-button" value="${msg("button.cancel")}" tabindex="0"/>
         </div>
      </form>
   </div>
</div>
<script type="text/javascript">//<![CDATA[
Alfresco.util.addMessages(${messages}, "Alfresco.module.RulesVersioningAction");
//]]></script>
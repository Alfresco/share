<#assign el=args.htmlid?html>
   <div class="hd">${msg("label.cloud-auth")}</div>
   <div class="bd">
      <form id="${el}-form" action="${url.context}/proxy/alfresco/cloud/person/credentials" method="post" class="cloud-auth-form">
         <div class="header-bar"><span class="error">${msg("label.cloud-auth.error")}</span></div>
         <div class="row">
            <span class="label"><label for="username">${msg("label.cloud-email")}</label></span>
            <span><input type="text" id="username" value="${email}" /></span>
         </div>
         <div class="row">
            <span class="label"><label for="password">${msg("label.cloud-password")}</label></span>
            <span><input type="password" id="password" /></span>
         </div>

         <div class="row saved">
            <span>${msg("label.cloud-auth.save")}</span>
         </div>

         <div class="buttons">
            <button id="${el}-button-ok" name="save">${msg("button.ok")}</button>
            <button id="${el}-button-cancel" name="cancel">${msg("button.cancel")}</button>
         </div>
         <div class="extra-links">
            <a target="_blank" href="http://www.alfresco.com/cloud?utm_source=AlfEnt4&utm_medium=anchor&utm_campaign=claimnetwork" class="theme-color-1">No Account? Sign up for free</a>
            <span class="cloud-core-login-separator">|</span>
            <a target="_blank" href="https://my.alfresco.com/share/page/forgot-password" class="theme-color-1">Forgot password?</a>
         </div>
      </form>
   </div>
<script type="text/javascript">//<![CDATA[
   var componentId = "${el}";
   componentId = componentId.replace("-authForm", "");
   Alfresco.util.ComponentManager.find({id: componentId})[0];
//]]></script>
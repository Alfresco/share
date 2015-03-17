<#assign el=args.htmlid?js_string/>
<div id="${el}-body" class="theme-overlay hidden">
   <!-- Logo -->
   <div class="theme-company-logo"></div>

   <#if notification??>

      <!-- Notification -->
      <div class="message theme-border-3 theme-bg-color-8">
         <h3 class="thin">${paramMsg("notification.header." + notification)}</h3>
         <hr/>
         <#if msg("notification.text." + notification) != ("notification.text." + notification)>
         <p>${msg("notification.text." + notification)?html}</p>
         </#if>
         <#if msg("notification.link." + notification) != "notification.link." + notification>
         <p>${msg("notification.link." + notification)}</p>
         </#if>
      </div>

   <#elseif error??>

      <!-- Error -->
      <div class="message theme-border-3 theme-bg-color-8">
         <h3 class="thin error">${msg("error.header." + error)}</h3>
         <hr/>
         <#if msg("error.text." + error) != ("error.text." + error)>
         <p>${msg("error.text." + error)?html}</p>
         </#if>
         <#if msg("error.link." + error) != "error.link." + error>
         <p>${msg("error.link." + error)}</p>
         </#if>
      </div>

   </#if>

</div>
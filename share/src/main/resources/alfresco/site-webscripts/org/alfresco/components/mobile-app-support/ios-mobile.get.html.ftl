<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/mobile-app-support/ios-mobile.css" group="header"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/mobile-app-support/ios-mobile.js" group="header"/>
</@>

<@markup id="resources">
   <#if nodeRef?? && appURL??>
   <meta name="apple-itunes-app" content="app-id=459242610, app-argument=${appURL + "&browserUrl="}"></meta>
   </#if>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#if nodeRef?? && appURL??>
         <#assign id = args.htmlid?html>
         <div id="${id}-mobile" class="ios-mobile">
            <a id="${id}-mobile-link" href="${appURL + "&browserUrl="}" ontouchstart="" ontouchend="">${msg("ios.tap-to-open")}<br />
               <span class="small">${msg("ios.information")}</span>
            </a>
         </div>
<script type="text/javascript">//<![CDATA[
   Alfresco.util.iOSMobileAppLink("${id}");
//]]></script>
      </#if>
   </@>
</@>


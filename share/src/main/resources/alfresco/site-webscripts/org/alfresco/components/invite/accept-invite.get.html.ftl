<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/accept-invite.css" group="invite"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="accept-invite-body">
      <#if (!doRedirect)>
         <h1>${msg("error.acceptfailed.title")}</h1>
         <p>${msg("error.acceptfailed.text")}</p>
      <#else>
         <script type="text/javascript">//<![CDATA[
            window.location = "${url.context}/page/site-index?site=${siteShortName}";
         //]]></script>
         <h1>${msg("acceptregistered.title")}</h1>
         <p>${msg("acceptregistered.text")}</p>
         <br />
         <a href="${url.context}/page/site-index?site=${siteShortName}">${url.context}/page/site-index?site=${siteShortName}</a>
      </#if>
      </div>
   </@>
</@>


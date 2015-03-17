<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="dashlet site-profile">
         <div class="title">${msg("header.siteProfile")}</div>
         <div class="body">
            <div class="msg dashlet-padding">
               <h2><#if (profile.title != "")>${msg("text.welcome", profile.title)?html}<#else>${msg("text.welcome", profile.shortName)?html}</#if></h2>
               <br />
      <#if (profile.description != "")>
               <p>${profile.description?html}</p>
      </#if>
      <#if profile.customProperties??>
         <#list profile.customProperties?keys as prop>
            <#assign customValue=profile.customProperties[prop].value>
            <#if customValue?starts_with('alfresco-php://') == true>
               <p><a href="${url.context}/proxy/alfresco-php/${customValue?substring(15)?html}" target="_blank" class="theme-color-1">${profile.customProperties[prop].title?html}</a></p>
            <#else>
               <p>
                  <span class="label">${profile.customProperties[prop].title?html}</span>
                  <span>${customValue?html}</span>
               </p>
             </#if>
         </#list>
      </#if>
      <#if (sitemanagers?exists && sitemanagers?size &gt; 0)>
               <p>
                  <span class="label">${msg("label.siteAdmin")}</span>
         <#list sitemanagers as sitemanager>
            <#if sitemanager.authority.authorityType = 'USER'>
                  <a href="${url.context}/page/user/${sitemanager.authority.userName?url}/profile" class="theme-color-1">${sitemanager.authority.firstName?html}<#if sitemanager.authority.lastName != ""> ${sitemanager.authority.lastName?html}</#if></a><#if sitemanager_has_next>,</#if>
            <#else>
                  <span>${sitemanager.authority.displayName?html}</span><#if sitemanager_has_next>,</#if>
            </#if>
         </#list>
               </p>
      </#if>
               <p><span class="label">${msg("label.visibility")}</span>&nbsp;${msg("text." + (profile.visibility!"unknown")?lower_case)}</p>
            </div>
            <div class="clear"></div>
         </div>
      </div>
   </@>
</@>
<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/profile.css" group="profile"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/profile/usersites.js" group="profile"/>
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html>
      <div id="${el}-body" class="profile">
         <div class="viewcolumn">
            <div class="header-bar">${msg("label.sites")}</div>
            <#if (numSites >0)>
            <ul id="${el}-sites" class="sites">
            <#list sites as site>
               <li <#if (site_index == 0)>class="first"</#if>>
                  <a href="${url.context}/page/site/${site.shortName}" class="thmb"><img src="${url.context}/res/components/site-finder/images/site-64.png"/></a>
                  <p><a href="${url.context}/page/site/${site.shortName}" class="theme-color-1">${site.title?html!""}</a>
                  <span>${site.description?html!""}</span></p>
                  <div style="float: right;margin-right:10px;">
                  <#if user.name == userid>
                  <#if feedControls?seq_contains(site.shortName)><#-- Emails disabled -->
                  <span class="yui-button yui-push-button" id="${el}-button-${site.shortName}">
                     <span class="first-child"><button id="${el}_notification_${site.shortName}" name="enable" title="${msg("button.enable.tooltip", site.title!"")?html}">${msg("button.enable")?html}</button></span>
                  </span>
                  <#else><#-- Emails enabled -->
                  <span class="yui-button yui-push-button yui-button-checked" id="${el}-button-${site.shortName}">
                     <span class="first-child"><button id="${el}_notification_${site.shortName}" name="disable" title="${msg("button.disable.tooltip", site.title!"")?html}">${msg("button.disable")?html}</button></span>
                  </span>
                  </#if>
                  </#if>
                  </div>
                  <input id="${el}_title_${site.shortName}" type="hidden" value="${site.title?html!""}" />
               </li>
            </#list>
            </ul>
            <#else>
            <p>${msg("label.noSiteMemberships")?html}</p>
            </#if>
         </div>
      </div>
   </@>
</@>


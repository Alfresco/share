<#assign mode = args.mode!"">
<#macro renderActivity activity inline=false>
   <#if activity.userProfile??>
      <#assign userLink><a href="${activity.userProfile?html?replace("@", "%40")}" class="theme-color-1">${activity.fullName?html}</a></#assign>
   <#else>
      <#assign userLink>&quot;<em>${activity.fullName?html}</em>&quot;</#assign>
   </#if>
   <#if activity.secondUserProfile??>
      <#assign secondUserLink><a href="${activity.secondUserProfile?html?replace("@", "%40")}" class="theme-color-1">${(activity.secondFullName!"")?html}</a></#assign>
   <#else>
      <#assign secondUserLink>&quot;<em>${(activity.secondFullName!"")?html}</em>&quot;</#assign>
   </#if>
   <#if activity.itemPage??>
      <#assign itemLink><a href="${activity.itemPage?html}" class="${(cssClasses[activity.type])!""} item-link theme-color-1">${activity.title?html}</a></#assign>
   <#else>
      <#assign itemLink>&quot;<em>${activity.title?html}</em>&quot;</#assign>
   </#if>
   <#assign siteLink><a href="${activity.sitePage?html}" class="site-link theme-color-1">${(siteTitles[activity.siteId]!activity.siteId)?html}</a></#assign>
   <#assign lastDay = "">
   <#assign thisDay = activity.date.isoDate>
   <#if thisDay != lastDay>
      <#assign lastDay = thisDay>
      <div class="new-day"><span class="relativeDate">${thisDay}</span></div>
   </#if>
   <#assign detail = msg(activity.type, itemLink, userLink, activity.custom0?html, activity.custom1?html, siteLink, secondUserLink)>
   <#if mode = "user" && !activity.suppressSite><#assign detail = msg("in.site", detail, siteLink)></#if>
   <#if !inline>
   <div class="activity">
      <#if activity.userAvatar != "avatar">
      <div class="avatar"><img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${activity.userAvatar?string?replace('://','/')}/thumbnail/avatar" alt="${activity.fullName?html}" width="48" /></div>
      <#else>
      <div class="avatar"><img src="${url.context}/proxy/alfresco/slingshot/profile/avatar/${activity.userName?url}/thumbnail/avatar" alt="${activity.fullName?html}" width="48" /></div>
      </#if>
      <div class="content">
         <span class="detail">${detail}</span><br/>
         <span class="time relativeTime" title="${activity.date.isoDate}">${activity.date.isoDate}</span>
      <#if activity.group??>
         <div class="more"><a href="#" onclick='Alfresco.util.toggleClass("_act${activity.id?c}", "hidden");return false'>${msg("more.activities", activity.group?size)} &#x25BC;</a></div>
         <div class="hidden" id="_act${activity.id?c}">
         <#list activity.group as a>
            <@renderActivity a true />
         </#list>
         </div>
      </#if>
      </div>
   </div>
   <#else>
   <div class="activity-inline">
      <div class="content">
         <span class="detail">${detail}</span>
      </div>
   </div>
   </#if>
</#macro>

<#if activities?? && activities?size != 0>
   <#list activities as activity>
      <#if !activity.grouped>
      <@renderActivity activity />
      </#if>
   </#list>
</#if>
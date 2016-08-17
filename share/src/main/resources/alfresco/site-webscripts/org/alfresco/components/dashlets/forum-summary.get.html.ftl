<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/discussions/topiclist.css" group="dashlets"  />
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/dashlets/forum-summary.css" group="dashlets"  />
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script type="text/javascript" src="${url.context}/res/components/dashlets/forum-summary.js" group="dashlets"/>
</@>

<@markup id="widgets">
   <@createWidgets group="dashlets"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign id = args.htmlid?html>
      <#assign siteId = page.url.templateArgs.site!"">
      <div class="dashlet forumsummary">
         <div class="title">${msg("header.title")}</div>
         <div class="toolbar flat-button">
            <div class="hidden">
            
               <#list filters as filter>
               <#if (filter.scope == "") || (filter.scope == "dashboard" && siteId == "") || (filter.scope == "site" && siteId != "")>
               <span class="align-left yui-button yui-menu-button" id="${id}-${filter.name?html}">
                  <span class="first-child">
                     <button type="button" tabindex="0">${msg("filter." + filter.name + "." + filter.options[0].label)}</button>
                  </span>
               </span>
               <select id="${id}-${filter.name?html}-menu">
               <#list filter.options as option>
                  <option value="${option.value?html}">${msg("filter." + filter.name + "." + option.label)}</option>
               </#list>
               </select>
               </#if>
               </#list>

               <#if siteId != "" && (userMembership.isMember && userMembership.role != "SiteConsumer" || user.isAdmin)>
               <span class="align-right yui-button yui-push-button yui-button-align new-topic">
                  <span class="first-child">
                     <a href="${url.context}/page/site/${siteId}/discussions-createtopic" class="theme-color-1">${msg("newTopic.button")}</a>
                  </span>
               </span>
               </#if>
               
               <div class="clear"></div>
            </div>
         </div>
         <div id="${id}-list" class="body scrollableList" <#if args.height??>style="height: ${args.height?html}px;"</#if>>
            <div id="${id}-filtered-topics"></div>
         </div>
      </div>
   </@>
</@>
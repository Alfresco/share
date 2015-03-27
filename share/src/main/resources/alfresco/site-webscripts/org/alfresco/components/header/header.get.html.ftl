<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/header/header.css" group="header"/>
   <@link href="${url.context}/res/modules/about-share.css" group="header"/>
   <#if context.properties["editionInfo"].edition != "UNKNOWN">
      <#-- License usage -->
      <@link href="${url.context}/res/components/console/license.css" group="header"/>
   </#if>
   <#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.css??>
      <#list config.global.header.dependencies.css as cssFile>
         <@link href="${url.context}/res${cssFile}" group="header"/>
      </#list>
   </#if>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/header/header.js" group="header"/>
   <@script src="${url.context}/res/modules/about-share.js" group="header"/>
   <#if config.global.header?? && config.global.header.dependencies?? && config.global.header.dependencies.js??>
      <#list config.global.header.dependencies.js as jsFile>
         <@script src="${url.context}/res${jsFile}" group="header"/>
      </#list>
   </#if>
</@>

<@markup id="widgets">
   <@createWidgets group="header"/>
   <@inlineScript group="header">
      Alfresco.util.createTwister.collapsed = "${collapsedTwisters?js_string}";
   </@>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#include "../../include/alfresco-macros.lib.ftl" />
      <#import "header.inc.ftl" as header>
      <#assign siteActive = page.url.templateArgs.site??>
      <#assign id = args.htmlid?html>
      <#assign jsid = id?replace("-", "_")?js_string>
      <#assign defaultlogo=msg("header.logo")><#if defaultlogo="header.logo"><#assign defaultlogo="app-logo.png"></#if>
      <#if !user.isGuest>
      <div class="header">
         <span class="header-left">
            <span class="logo">
               <a href="${url.context}"><img src="${url.context}<#if logo?? && logo?length!=0>/proxy/alfresco/api/node/${logo?replace('://','/')}/content<#else>/res/themes/${theme}/images/${defaultlogo}</#if>" alt="" /></a>
            </span>
            <span class="logo-spacer">&nbsp;</span>
            <span id="${id}-appItems" class="app-items hidden"><@header.renderItems config.global.header.appItems id "app" /></span>
         </span>
         <#-- Ideally this should be done via the inlineScript directive - but it doesn't work due to ordering at the moment -->
         <#-- <@inlineScript group="header"> -->
         <script type="text/javascript">//<![CDATA[
            ${jsid}.setAppItems([${header.js}]);
         //]]></script>
         <#-- </@> -->
         <span id="${id}-userItems" class="user-items">
            <div class="user-items-wrapper">
               <@header.renderItems config.global.header.userItems id "user" />
            </div>
            <div class="search-box">
               <span id="${id}-search_more" class="yui-button yui-menu-button">
                  <span class="first-child" style="background-image: url(${url.context}/res/components/images/header/search-menu.png)">
                     <button type="button" title="${msg("header.search.description")}" tabindex="0"></button>
                  </span>
               </span>
               <input id="${id}-searchText" type="text" maxlength="1024" />
            </div>
            <div id="${id}-searchmenu_more" class="yuimenu yui-overlay yui-overlay-hidden">
               <div class="bd">
                  <ul class="first-of-type">
                     <li><a style="background-image: url(${url.context}/res/components/images/header/advanced-search.png)" title="${msg("header.advanced-search.description")}" href="${siteURL("advsearch")}">${msg("header.advanced-search.label")}</a></li>
                  </ul>
               </div>
            </div>
         </span>
         <#-- Ideally this should be done via the inlineScript directive - but it doesn't work due to ordering at the moment -->
         <#-- <@inlineScript group="header"> -->
         <script type="text/javascript">//<![CDATA[
            ${jsid}.setUserItems([${header.js}]);
         //]]></script>
         <#-- </@> -->
      </div>
      <#else>
      <div class="header">
         <span class="header-left">
            <span class="logo">
               <a href="#" onclick="${jsid}.showAboutShare(); return false;"><img src="${url.context}/res/themes/${theme}/images/${logo!"app-logo.png"}" alt="" /></a>
            </span>
            <span class="logo-spacer">&nbsp;</span>
         </span>
      </div>
      </#if>
      <div class="clear"></div>
      <#if usage??>
      <div class="license">
         <div class="license-header">
            <div class="warnings">
               <div class="info">
                  <#list usage.warnings as w>
                     <div class="level${usage.level}">${w?html}</div>
                  </#list>
                  <#list usage.errors as e>
                     <div class="level${usage.level}">${e?html}</div>
                  </#list>
               </div>
            </div>
         </div>
      </div>
      </#if>
   </@>
</@>
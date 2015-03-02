<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/modules/edit-site.css" group="title"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/title/collaboration-title.js" group="title"/>
   <@script src="${url.context}/res/modules/edit-site.js" group="title"/>
</@>

<@markup id="widgets">
   <@inlineScript group="title">
      Alfresco.constants.DASHLET_RESIZE = ${userIsSiteManager?string} && YAHOO.env.ua.mobile === null;
   </@>
   <@createWidgets group="title"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
   <#assign el=args.htmlid?html>
   <div class="page-title theme-bg-color-1 theme-border-1">

      <#-- TITLE -->
      <@markup id="title">
      <div class="title">
         <h1 class="theme-color-3">${msg("header.site", "<span>${siteTitle?html}</span>")}</h1>
      </div>
      </@markup>

      <#-- ACTIONS -->
      <@markup id="actions">
      <div class="links title-button hidden">

         <#-- LINKS -->
         <@markup id="links">
            <#list links![] as link>
            <span class="yui-button yui-link-button">
               <span class="first-child">
                  <a id="${el}-${link.id}" href="${link.href!"#"}" class="${link.cssClass!""}">${msg(link.label)}</a>
               </span>
            </span>
            </#list>
         </@markup>

         <#-- MORE MENU -->
         <@markup id="moreMenu">
            <#if moreMenu??>
               <input type="button" id="${el}-more" name="${el}-more" value="${msg(moreMenu.label)}"/>
               <select id="${el}-more-menu">
                  <#list moreMenu.options![] as option>
                     <option value="${option.value}">${msg(option.label)}</option>
                  </#list>
               </select>
            </#if>
         </@markup>

      </div>
      </@markup>

      <div style="clear:both"></div>

   </div>
   </@>
</@>

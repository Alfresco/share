<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/invite/members-bar.css" group="invite"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="invite"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?html/>
      <div id="${el}-body" class="members-bar share-toolbar theme-bg-2">

         <#-- LINKS -->
         <@markup id="links">
         <div class="members-bar-links">
            <#list links as link>
               <a id="${el}-${link.id}" href="${link.href}" class="${link.cssClass!""}">${link.label?html}</a>
               <#if link_has_next>
                  <span class="separator">&nbsp;</span>
               </#if>
            </#list>
         </div>
         </@markup>

      </div>
   </@>
</@>


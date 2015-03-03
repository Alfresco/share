<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/profile/toolbar.css" group="profile"/>
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="profile"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign el=args.htmlid?js_string/>
      <div id="${el}-body" class="toolbar userprofile">

         <#-- LINKS -->
         <@markup id="links">
         <div class="members-bar-links">
            <#list links as link>
               <div class="link">
                  <a id="${el}-${link.id}" href="${link.href}" class="${link.cssClass!""}">${link.label?html}</a>
               </div>
               <#if link_has_next>
                  <div class="separator">&nbsp;</div>
               </#if>
            </#list>
         </div>
         </@markup>

      </div>
   </@>
</@>
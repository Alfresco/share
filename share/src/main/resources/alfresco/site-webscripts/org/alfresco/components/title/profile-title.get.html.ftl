<@markup id="css" >
   <#-- No CSS Dependencies -->
</@>

<@markup id="js">
   <#-- No JavaScript Dependencies -->
</@>

<@markup id="widgets">
   <@createWidgets group="title"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <#assign userName>${profile.properties["firstName"]?html} <#if profile.properties["lastName"]??>${profile.properties["lastName"]?html}</#if></#assign>
      <div class="page-title theme-bg-color-1 theme-border-1">
         <h1 class="theme-color-3">${msg("header.userprofile", "<span>${userName}</span>")}</h1>
      </div>
   </@>
</@>

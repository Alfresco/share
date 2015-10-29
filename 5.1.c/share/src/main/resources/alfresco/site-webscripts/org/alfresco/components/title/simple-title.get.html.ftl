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
      <div class="page-title theme-bg-color-1 theme-border-1">
         <h1 class="theme-color-3"><span>${msg(args.title)}</span> <#if args.subtitle?? && msg(args.subtitle) != args.subtitle>${msg(args.subtitle)}</#if></h1>
      </div>
   </@>
</@>



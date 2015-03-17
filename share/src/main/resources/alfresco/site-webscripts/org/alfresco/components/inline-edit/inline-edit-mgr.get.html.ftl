<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/inline-edit/inline-edit-mgr.css" group="inline-edit"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/inline-edit/inline-edit-mgr.js" group="inline-edit"/>
   <@script src="${url.context}/res/modules/documentlibrary/doclib-actions.js" group="inline-edit"/>
</@>

<@markup id="widgets">
   <@createWidgets group="inline-edit"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <div class="inline-edit-mgr">
         <div class="heading">${msg("inline-edit-mgr.heading")}</div>
      </div>
   </@>
</@>
<div class="header">
   <@markup id="headerImage">
      <#if showImage>
         <img class="headerImage" src="${imageURL}"/>
      </#if>
   </@markup>
   <@markup id="headerTitle">
      <span class="title">${msg("title.label")}</span>
   </@markup>
</div>


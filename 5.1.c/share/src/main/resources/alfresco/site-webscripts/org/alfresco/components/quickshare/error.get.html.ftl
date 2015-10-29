<@standalone>
   <@markup id="css" >
      <#-- CSS Dependencies -->
      <@link rel="stylesheet" type="text/css" href="${url.context}/res/components/quickshare/error.css" />
   </@>

   <@markup id="js"/>

   <@markup id="widgets"/>

   <@markup id="html">
      <@uniqueIdDiv>
         <div class="quickshare-error">
            <@markup id="header">
               <h1 class="title">${msg("header")}</h1>
            </@markup>
            <@markup id="message">
               <hr/>
               <p class="message broken-file-icon">${msg("message")}</p>
            </@markup>
         </div>
      </@>
   </@>
</@>

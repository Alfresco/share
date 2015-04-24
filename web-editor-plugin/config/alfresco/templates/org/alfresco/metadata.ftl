<#include "include/awe.ftl" />
<@templateHeader>
</@>

<@templateBody>
   <div class="js-disabled-form">
      <#if url.args.title??><h2>${url.args.title?html}</h2></#if>
      <@region id="metadata" scope="template" />
      <#if url.args.redirect??><div class="cancel-link"><a href="${url.args.redirect?html}">${msg("button.cancel")}</a></div></#if>
   </div>
</@>
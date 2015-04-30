<#include "/common/page.ftl"/>

<#assign title="${msg('title.404')}"/>

<@templateBody>  
  <div class="error">
    <h2>${msg('title.404')}</h2>
    <br/>
    <p>${msg('error.404.text')}</p>
  </div>
</@>

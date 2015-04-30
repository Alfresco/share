<#include "/common/page.ftl"/>

<@templateBody>
  <div class="error">
    <h2>${errorAsset.title}</h2>
    <@streamasset asset=errorAsset/>
  </div>
</@>
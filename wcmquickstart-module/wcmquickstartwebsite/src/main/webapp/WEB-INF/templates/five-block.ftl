<#include "/common/page.ftl"/>

<@templateBody>
  <@region id="bellyband" scope="page"/>
  
  <div id="left">
    <@region id="left1" scope="page"/>    
    <@region id="left2" scope="page"/>    
    <@region id="left3" scope="page"/>
    
    <div class="h-box-1">
        <@region id="bottom-left" scope="page"/>    
    </div>

    <div class="h-box-2">
        <@region id="bottom-right" scope="page"/>    
    </div>
        
  </div>
  
  <div id="right">
    <@region id="right1" scope="page"/>   
    <@region id="right2" scope="page"/>   
    <@region id="right3" scope="page"/>   
  </div>
</@>

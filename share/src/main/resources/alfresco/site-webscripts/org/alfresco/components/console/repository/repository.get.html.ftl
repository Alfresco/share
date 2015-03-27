<@markup id="css" >
   <#-- CSS Dependencies -->
   <@link href="${url.context}/res/components/console/repository.css" group="console"/>
</@>

<@markup id="js">
   <#-- JavaScript Dependencies -->
   <@script src="${url.context}/res/components/console/consoletool.js" group="console"/>
</@>

<@markup id="widgets">
   <@createWidgets group="console"/>
</@>

<@markup id="html">
   <@uniqueIdDiv>
      <!--[if IE]>
      <iframe id="yui-history-iframe" src="${url.context}/res/yui/history/assets/blank.html"></iframe> 
      <![endif]-->
      <input id="yui-history-field" type="hidden" />
      
      <div class="repository">
         
         <!-- Tools panel -->
         <div>
            <div class="title">${msg("label.tools")}</div>
            
            <div class="row">
               <span class="label">${msg("label.jmxdump")}:</span>
               <a href="${url.context}/proxy/alfresco/api/admin/jmxdump">${msg("link.jmxdump")}</a>
            </div>
         </div>
      
      </div>
   </@>
</@>


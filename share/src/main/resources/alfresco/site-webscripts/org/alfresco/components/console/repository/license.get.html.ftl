<#import "../generic-form-tool.lib.ftl" as gft>
<@gft.renderPanel config.script.config "license">
   <div class="form-generic-tool license">
      <h1 class="thin dark">${msg("tool.license.usageinfo.label")}</h1>
      <div class="share-form">
         <div class="form-container">
            <div class="form-fields">
               <div class="set">
                  <div class="yui-g">
                     <div class="yui-u first">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("license.field.Users")}:</span>
                           <span class="viewmode-value">${usage.users!"0"}</span>
                        </div>
                     </div>
                     <div class="yui-u">
                        <div class="viewmode-field">
                           <span class="viewmode-label">${msg("license.field.Documents")}:</span>
                           <span class="viewmode-value">${usage.documents!"0"}</span>
                        </div>
                     </div>
                  </div>
               </div>
               <div class="yui-g">
                  <div class="viewmode-field">
                     ${msg("license.field.Info")}
                  </div>
               </div>
            </div>
         </div>
      </div>
   </div>
   
   <#if usage.level != 0>
   <div class="form-generic-tool license">
      <div class="share-form">
         <div class="form-container">
            <div class="warnings">
               <div class="info">
                  <#list usage.warnings as w>
                     <div class="level${usage.level}">${w?html}</div>
                  </#list>
                  <#list usage.errors as e>
                     <div class="level${usage.level}">${e?html}</div>
                  </#list>
               </div>
            </div>
         </div>
      </div>
   </div>
   </#if>
</@>

<@link rel="stylesheet" type="text/css" href="${page.url.context}/res/components/console/generic-form-tool.css" />


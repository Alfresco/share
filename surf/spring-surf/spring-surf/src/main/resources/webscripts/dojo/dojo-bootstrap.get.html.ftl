<#assign webframeworkConfig = config.scoped["WebFramework"]["web-framework"]!>
<#if webframeworkConfig??>
   <#if webframeworkConfig.dojoEnabled>
      <@markup id="setDojoConfig">
         <script type="text/javascript">
            var appContext = "${url.context?js_string}";
         
            var dojoConfig = {
               baseUrl: "${url.context?js_string}${webframeworkConfig.dojoBaseUrl}",
               tlmSiblingOfDojo: false,
               locale: (navigator.languages ? navigator.languages[0] : (navigator.language || navigator.userLanguage)).toLowerCase(),
               async: true,
               parseOnLoad: false,
               packages: [
               <#assign packages = webframeworkConfig.dojoPackages>
               <#list packages?keys as name>
                  { name: "${name}", location: "${packages[name]}"<#if webframeworkConfig.dojoPackagesMain[name]??>, main: "${webframeworkConfig.dojoPackagesMain[name]}"</#if>}<#if name_has_next>,</#if>
               </#list>
               ]
            };
         </script>
      </@>
      <@markup id="loadDojo">
         <script type="text/javascript" src="${url.context}${webframeworkConfig.dojoBootstrapFile}"></script>
      </@>
   </#if>
</#if>

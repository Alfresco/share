<#assign webframeworkConfig = config.scoped["WebFramework"]["web-framework"]!>
<#if webframeworkConfig??>
   <#if webframeworkConfig.dojoEnabled>
      var dojoConfig = {
         baseUrl: "${url.context}${webframeworkConfig.dojoBaseUrl}",
         tlmSiblingOfDojo: false,
         async: true,
         parseOnLoad: false,
         packages: [
         <#assign packages = webframeworkConfig.dojoPackages>
         <#list packages?keys as name>
            { name: "${name}", location: "${packages[name]}"<#if webframeworkConfig.dojoPackagesMain[name]??>, main: "${webframeworkConfig.dojoPackagesMain[name]}"</#if>}<#if name_has_next>,</#if>
         </#list>
         ]
      };
   </#if>
</#if>
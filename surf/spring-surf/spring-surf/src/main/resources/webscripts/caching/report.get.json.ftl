<#escape x as jsonUtils.encodeJSONString(x)>
{
   "data":
   {
      "cache":
      [
         <#list reports?keys as cache>
         {
            "bean": "${cache}",
            "reports":
            [
               <#list reports[cache] as report>
               {
                  "name": "${report.name}",
                  "count": ${report.count?c},
                  "size": ${report.size?c}
               }<#if report_has_next>,</#if>
               </#list>
            ]
         }<#if cache_has_next>,</#if>
         </#list>
      ]
   }
}
</#escape>
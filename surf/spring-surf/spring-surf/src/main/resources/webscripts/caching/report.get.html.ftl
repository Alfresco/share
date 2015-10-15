<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>Cache Report</@wsLib.head>
   <body>
      <div>
         <table>
            <tr>
               <td><img src="${resourceurl('/images/logo/AlfrescoLogo32.png', true)}" alt="Alfresco" /></td>
               <td><span class="title">Cache Report</span></td>
            </tr>
         </table>
         <table>
            <tr align="left">
               <th><b>Cache Bean</b></th>
               <th><b>Entries</b></th>
               <th><b>Size (bytes, approx)</b></th>
            </tr>
         <#list reports?keys as cache>
            <tr><td colspan="3"><b>${cache?html}</b></td></tr>
            <#list reports[cache] as report>
            <tr>
               <td>&nbsp;&nbsp;&nbsp;${report.name?html}</td>
               <td>${report.count}</td>
               <td>${report.size}</td>
            </tr>
            </#list>
            <tr><td colspan="3"></td></tr>
         </#list>
         </table>
      <div>
      <br/>
      <div>
         <input type="button" name="report" value="Refresh" onclick="window.location.href='${url.serviceContext}/caches/report'"/>
         &nbsp;&nbsp;
         <input type="button" name="clear" value="Clear" onclick="window.location.href='${url.serviceContext}/caches/report?clear=true'"/>
         <br/><br/>
         <#if args.autorefresh?? && args.autorefresh="true"><#assign auto=true><#else><#assign auto=false></#if>
         <script>
            <#if auto>var timer = setTimeout(function() {window.location.href='${url.serviceContext}/caches/report?autorefresh=true';}, 1000);</#if>
            var auto = ${auto?string};
            function toggle()
            {
               if (auto) {
                  clearTimeout(timer);
                  auto = false;
               }
               else {
                  window.location.href='${url.serviceContext}/caches/report?autorefresh=true';
               }
            }
         </script>
         <input type="button" name="autorefresh" value="Toggle Automatic Refresh" onclick="toggle()"/>
      </div>
   </body>
</html>
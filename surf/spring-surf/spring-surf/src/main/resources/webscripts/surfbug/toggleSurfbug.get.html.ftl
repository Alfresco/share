<#import "/org/springframework/extensions/webscripts/webscripts.lib.html.ftl" as wsLib/>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
   <@wsLib.head>SurfBug Status</@wsLib.head>
   <body>
       <div>
          <table>
             <tr>
                <td><img src="${resourceurl('/images/logo/AlfrescoLogo32.png', true)}" alt="Alfresco" /></td>
                <td><span class="title">SurfBug Status</span></td>
             </tr>
          </table>
          <table>
            <tr align="left"><td><b>Current Status:</b></td></tr>
            <tr><td>
                 <#if surfbugEnabled>
                     Enabled
                 <#else>
                     Disabled
                 </#if>
            </td></tr>
         </table>
          <form action="${url.serviceContext}${url.match}" method="post">
             <#if surfbugEnabled>
                 <input type="hidden" name="statusUpdate" value="disabled"/>
                 <input type="submit" name="submit" value="Disable SurfBug"/>
             <#else>
                 <input type="hidden" name="statusUpdate" value="enabled"/>
                 <input type="submit" name="submit" value="Enable SurfBug"/>
             </#if>
          </form>
      </div>
   </body>
</html>
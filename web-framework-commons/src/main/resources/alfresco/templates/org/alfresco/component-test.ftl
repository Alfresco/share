<#include "include/alfresco-template.ftl" />
<@templateHeader />

<@templateBody>
   <div id="alf-hd">
      <@region id="header" scope="global" protected=true/>
      <@region id="title" scope="template" protected=true />
   </div>
   <div id="bd">
      <h2 style="padding-top: 1em;">Component Test Page</h2>
         <#assign component>${url.args.component!""}</#assign>
         <form method="get">
            <fieldset style="border: 1px solid #aaa; margin-top: 10px; padding: 8px;">
               <legend style="color: #515d6b;">Component</legend>
               <label for="component">URL:</label>
               <input id="component" type="text" name="component" style="width: 50em; margin: 5px 5px 5px 0px;" value="${component}" />
            </fieldset>
            <div style="padding: 1em">
               <input type="submit" value="Load Component" />
               <input type="button" value="Clear" onclick="javascript:document.getElementById('componentUrl').value='';" />
            </div>
         </form>

         <div style="margin: 1em;">
            <@region id="component" scope="template" />
         </div>
      </div>
   </div>
</@>

<@templateFooter>
   <div id="alf-ft">
      <@region id="footer" scope="global" protected=true />
   </div>
</@>
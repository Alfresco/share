<#include "include/alfresco-template.ftl" />
<@templateHeader>
   <@link rel="stylesheet" type="text/css" href="${url.context}/res/templates/invite/invite.css" />
</@>

<@templateBody>
   <@markup id="alf-hd">
   <div id="alf-hd">
      <@region scope="global" id="share-header" chromeless="true"/>
   </div>
   </@>
   <@markup id="bd">
   <div id="bd">
      <@region id="membersbar" scope="template" />
      <div class="yui-g grid">
         <div class="yui-u first column1">
            <div class="yui-b">
               <@region id="people-finder" scope="template" />
            </div>
            <div class="yui-b">
               <@region id="addemail" scope="template" />
            </div>
         </div>
         <div class="yui-u column2">
            <div class="yui-b">
               <@region id="invitationlist" scope="template" />
            </div>
         </div>
      </div>
   </div>
   <br/>
   </@>
</@>

<@templateFooter>
   <@markup id="alf-ft">
   <div id="alf-ft">
      <@region id="footer" scope="global" />
   </div>
   </@>
</@>
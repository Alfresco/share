<#assign el=args.htmlid?html>
<div class="editNodeForm">
   <form id="${el}-form" method="post" action="">
      <div>
         <input type="hidden" id="${el}-site" name="site" value="" />
         <input type="hidden" id="${el}-container" name="container" value="" />
         <input type="hidden" id="${el}-page" name="page" value="discussions-topicview" />
         
         <label for="${el}-title">${msg("label.title")}:</label>
         <input type="text" id="${el}-title" name="title" size="80" value=""/>
                
         <label for="${el}-content">${msg("topicText")}:</label>
         <textarea rows="8" cols="80" id="${el}-content" name="content" class="yuieditor"></textarea> 
         
         <label for="${el}-tag-input-field">${msg("label.tags")}:</label>
         <#import "/org/alfresco/modules/taglibrary/taglibrary.lib.ftl" as taglibraryLib/>
         <@taglibraryLib.renderTagLibraryHTML htmlid=el />

      </div>
      <div class="nodeFormAction">
         <input type="submit" id="${el}-submit" value="${msg('action.save')}" />
         <input type="reset" id="${el}-cancel" value="${msg('action.cancel')}" />
      </div>
   </form>
</div>
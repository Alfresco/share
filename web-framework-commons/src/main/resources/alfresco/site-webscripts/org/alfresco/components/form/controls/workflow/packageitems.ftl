<#include "/org/alfresco/components/form/controls/association.ftl" />

<#macro setPackageItemOptions field>

   <#local documentLinkResolver>
function(item)
{
   var path;
   if (item.isContainer)
   {
      path = "folder-details?nodeRef=" + item.nodeRef;
   }
   else if (item.container)
   {
      switch(item.container){
        case 'blog': path = "blog-postview?postId=" + item.name; item.displayName = item.title; break;
        case 'discussions': path = "discussions-topicview?topicId=" + item.name; item.displayName = item.title; break;
        case 'calendar': path = item.container + (item.fromDate?"?date=" + Alfresco.util.formatDate(item.fromDate, "yyyy-mm-dd"):""); item.displayName = item.title; break;
        case 'wiki': path = "wiki-page?title=" + encodeURIComponent(item.title); item.displayName = item.title.replace(/_/g, " "); break;
        case 'links': path = "links-view?linkId=" + item.name; item.displayName = item.title; break;
        case 'dataLists': path = "data-lists?list=" + item.displayPath.split('/').pop(); break;
        default: path = "document-details?nodeRef=" + item.nodeRef;
      }
   }
   else
   {
      path = "document-details?nodeRef=" + item.nodeRef;
   }

   return Alfresco.util.siteURL(path, { site: item.site });
}
   </#local>
   <#local allowAddAction = false>
   <#local allowRemoveAllAction = false>
   <#local allowRemoveAction = false>
   <#local actions = []>

   <#if form.data['prop_bpm_packageActionGroup']?? && form.data['prop_bpm_packageActionGroup']?is_string && form.data['prop_bpm_packageActionGroup']?length &gt; 0>
      <#local allowAddAction = true>
   </#if>

   <#if form.data['prop_bpm_packageItemActionGroup']?? && form.data['prop_bpm_packageItemActionGroup']?is_string && form.data['prop_bpm_packageItemActionGroup']?length &gt; 0>
      <#local packageItemActionGroup = form.data['prop_bpm_packageItemActionGroup']>
      <#local viewMoreAction = { "name": "view_more_actions", "label": "form.control.object-picker.workflow.view_more_actions", "link": documentLinkResolver }>
      <#if packageItemActionGroup == "read_package_item_actions" || packageItemActionGroup == "edit_package_item_actions">
         <#local actions = actions + [viewMoreAction]>
      <#elseif packageItemActionGroup == "remove_package_item_actions" || packageItemActionGroup == "start_package_item_actions" || packageItemActionGroup == "edit_and_remove_package_item_actions">
         <#local actions = actions + [viewMoreAction]>
         <#local allowRemoveAllAction = true>
         <#local allowRemoveAction = true>
      <#elseif packageItemActionGroup >
      <#else>
         <#local actions = actions + [viewMoreAction]>      
      </#if>
   </#if>


   <#-- Additional item actions -->

   <script type="text/javascript">//<![CDATA[
   (function()
   {
      <#-- Modify the properties on the object finder created by association control-->
      var picker = Alfresco.util.ComponentManager.get("${controlId}");
      picker.setOptions(
      {
         showLinkToTarget: true,
         targetLinkTemplate: ${documentLinkResolver},         
      <#if form.mode == "create" && form.destination?? && form.destination?length &gt; 0>
         startLocation: "${form.destination?js_string}",
      <#elseif field.control.params.startLocation??>
         startLocation: "${field.control.params.startLocation?js_string}",
      </#if>
         itemType: "cm:content",
         displayMode: "${field.control.params.displayMode!"list"}",
         listItemActions: [
         <#list actions as action>
         {
            name: "${action.name}",
            <#if action.link??>
            link: ${action.link},
            <#elseif action.event>
            event: "${action.event}", 
            </#if>
            label: "${action.label}"
         }<#if action_has_next>,</#if>
         </#list>],
         allowRemoveAction: ${allowRemoveAction?string},
         allowRemoveAllAction: ${allowRemoveAllAction?string},
         allowSelectAction: ${allowAddAction?string},
         selectActionLabel: "${field.control.params.selectActionLabel!msg("button.add")}"
      });
   })();
   //]]></script>

</#macro>

<@setPackageItemOptions field />
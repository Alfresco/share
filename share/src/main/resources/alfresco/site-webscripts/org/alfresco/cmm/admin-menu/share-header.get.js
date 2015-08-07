<import resource="classpath:/alfresco/site-webscripts/org/alfresco/share/imports/share-header-tools.lib.js">

/**
 * If user is not Admin, and they belong to group GROUP_ALFRESCO_MODEL_ADMINISTRATORS, add model manager 
 * link to the share header.
 */
if (!user.isAdmin && (user.properties["alfUserGroups"].indexOf("GROUP_ALFRESCO_MODEL_ADMINISTRATORS") != -1))
{
   addNonAdminAdministrativeMenuItem(model.jsonModel, {
      id: "HEADER_CUSTOM_MODEL_MANAGER_CONSOLE",
      label: "tool.custom-model-manager.label",
      targetUrl: "console/custom-model-management-console/custom-model-manager"
   });
}
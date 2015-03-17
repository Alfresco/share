<import resource="classpath:alfresco/templates/org/alfresco/valid-user-site-access.lib.js">

/**
 * Customise Dashboard template controller script
 */

function main()
{
   model.access = isValidUserOrSite(true);
}

main();
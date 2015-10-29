model.jsonModel = {
   rootNodeId: args.htmlid,
   pubSubScope: instance.object.id,
   services: [
      {
         name: "alfresco/services/ReportService"
      },
      {
         name: "alfresco/services/NavigationService"
      }
   ],
   widgets: [
      {
         id: "DASHLET",
         name: "alfresco/dashlets/TopSiteContributorReportDashlet"
      }
   ]
};

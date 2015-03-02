model.jsonModel = {
   rootNodeId: args.htmlid,
   pubSubScope: instance.object.id,
   services: [
      {
         name: "alfresco/services/ReportService"
      }
   ],
   widgets: [
      {
         id: "DASHLET",
         name: "alfresco/dashlets/SiteContentReportDashlet"
      }
   ]
};

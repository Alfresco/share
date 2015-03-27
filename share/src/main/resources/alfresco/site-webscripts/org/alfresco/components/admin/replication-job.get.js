function main()
{
   // Model variables
   var jobDetail = {};
   
   var jobName = page.url.args.jobName;
   if (jobName != null)
   {
      var response = remote.call("/api/replication-definition/" + encodeURIComponent(jobName));
      if (response.status == 200)
      {
         var json = JSON.parse(response);
         jobDetail = json.data;
      }
   }
   
   model.jobDetail = jobDetail;
   
   // Define widget model...
   
   
   var payload = [];
   if (jobDetail && jobDetail.payload)
   {
      for (var i = 0; i < jobDetail.payload.length; i++)
      {
         payload.push(jobDetail.payload[i]["nodeRef"]);
      }
   }
   
   var scheduleStart = "";
   if (jobDetail && jobDetail.schedule && jobDetail.schedule.start && jobDetail.schedule.start.iso8601)
   {
      scheduleStart = jobDetail.schedule.start.iso8601;
   }
   var targetName = "";
   if (jobDetail.targetName)
   {
      targetName = jobDetail.targetName;
   }
   var replicationJob = {
      id: "ReplicationJob",
      name: "Alfresco.component.ReplicationJob",
      options : {
         jobName : jobName || "",
         payload : payload,
         targetName : targetName,
         scheduleStart : scheduleStart
      },
   };
   
   model.widgets = [replicationJob];
}

main();
model.jsonModel = {
   publishOnReady: [
      {
         publishTopic: "ALF_RETRIEVE_SINGLE_DOCUMENT_REQUEST",
         publishPayload: {
            dataSource: "customNodeRef",
            nodeRef: "workspace://SpacesStore/f8394454-0651-48a5-b583-d067c7d03339"
         }
      }
   ],
   services: [
      {
         name: "alfresco/services/LoggingService",
         config: {
            loggingPreferences: {
               enabled: true,
               all: true
            }
         }
      },
      "alfresco/services/DocumentService",
      "alfresco/services/ErrorReporter"
   ],
   widgets:[
      {
         name: "alfresco/documentlibrary/AlfDocument",
         config: {
            widgets: [
               {
                  name: "alfresco/preview/AlfDocumentPreview",
                  config: {
                  }
               }
            ]
         }
      },
      {
         name: "aikauTesting/MockXhr",
         config: {
            
         }
      },
      {
         name: "alfresco/logging/SubscriptionLog"
      },
      {
         name: "aikauTesting/TestCoverageResults"
      }
   ]
};
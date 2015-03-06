model.jsonModel = {
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
      {
         name: "alfresco/services/ActionService"
      },
      {
         name: "alfresco/services/NavigationService"
      },
      {
         name: "alfresco/services/SearchService"
      },
      "alfresco/services/ErrorReporter"
   ],
   widgets: [
      
      {
         id: "FACETS",
         name: "alfresco/layout/VerticalWidgets",
         config: {
            widgets: [
               {
                  name: "alfresco/buttons/AlfButton",
                  config: {
                     id: "BUTTON_1",
                     label: "Publish twister data 1",
                     publishTopic: "ALF_FACET_RESULTS_FACET1QNAME",
                     publishPayload: {
                        activeFilters: null,
                        facetResults: [
                           {
                              label: "result 1",
                              value: "result 1",
                              hits: 10
                           },
                           {
                              label: "result 2",
                              value: "result 2",
                              hits: 9
                           },
                           {
                              label: "result 3",
                              value: "result 3",
                              hits: 8
                           },
                           {
                              label: "result 4",
                              value: "result 4",
                              hits: 7
                           },
                           {
                              label: "result 5",
                              value: "result 5",
                              hits: 18
                           },
                           {
                              label: "result 6",
                              value: "result 6",
                              hits: 2
                           }
                        ]
                     }
                  }
               },
               {
                  id: "TWISTER_HEADING_LEVEL",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "Twister with heading level",
                     facetQName: "FACET1QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5,
                     headingLevel: 3
                  }
               },
               {
                  id: "TWISTER_NO_HEADING_LEVEL",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "Twister with no heading level",
                     facetQName: "FACET3QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5
                  }
               },
               {
                  id: "TWISTER_BAD_HEADING_LEVEL",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "Twister with faulty heading level",
                     facetQName: "FACET2QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5,
                     headingLevel: "a"
                  }
               },
               {
                  id: "TWISTER_BAD_HEADING_LEVEL_TWO",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "Twister with heading level",
                     facetQName: "FACET1QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5,
                     headingLevel: 0
                  }
               },
               {
                  id: "TWISTER_BAD_HEADING_LEVEL_THREE",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "Twister with heading level",
                     facetQName: "FACET1QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5,
                     headingLevel: 7
                  }
               },
               {
                  id: "TWISTER_NULL_LABEL",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: null,
                     facetQName: "FACET2QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5
                  }
               },
               {
                  id: "TWISTER_EMPTY_LABEL",
                  name: "alfresco/search/FacetFilters",
                  config: {
                     label: "",
                     facetQName: "FACET2QNAME",
                     sortBy: "DESCENDING",
                     maxFilters: 5
                  }
               }
            ]
         }
      },
      {
         name: "aikauTesting/ConsoleLog"
      },
      {
         name: "alfresco/logging/SubscriptionLog"
      },
      {
         name: "aikauTesting/TestCoverageResults"
      }
   ]
};
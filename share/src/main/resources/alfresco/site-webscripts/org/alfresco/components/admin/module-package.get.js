model.jsonModel = {
    services: [
        {
            name: "alfresco/services/LoggingService",
            config: {
                loggingPreferences: {
                    enabled: true,
                    all: true,
                    warn: true,
                    error: true
                }
            }
        }
    ],
    widgets: [
        {
            id: "SET_PAGE_TITLE",
            name: "alfresco/header/SetTitle",
            config: {
                title: msg.get("module-package.page.title")
            }
        },
        {
            name: "alfresco/layout/VerticalWidgets",
            config: {
                widgetMarginTop: "15",
                widgets: [
                    {
                        id: "A_LIST",
                        name: "alfresco/lists/views/AlfListView",
                        config: {
                            id: "LIST_WITH_HEADER",
                            noItemsMessage: msg.get("module-package.no-modules"),
                            currentData: {
                                items: modulepackages
                            },
                            widgetsForHeader: [
                                {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                        id: "titleTableHeader",
                                        label: msg.get("module-package.title")
                                    }
                                },
                                {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                        id: "descriptionTableHeader",
                                        label: msg.get("module-package.description")
                                    }
                                },
                                {
                                    name: "alfresco/documentlibrary/views/layouts/HeaderCell",
                                    config: {
                                        id: "versionTableHeader",
                                        label: msg.get("module-package.version"),
                                        sortable: false
                                    }
                                }
                            ],
                            widgets:[
                                {
                                    name: "alfresco/lists/views/layouts/Row",
                                    config: {
                                        widgets: [
                                            {
                                                name: "alfresco/lists/views/layouts/Cell",
                                                config: {
                                                    widgets: [
                                                        {
                                                            name: "alfresco/renderers/Property",
                                                            config: {
                                                                propertyToRender: "title",
                                                                renderAsLink: false
                                                            }
                                                        }
                                                    ]
                                                }
                                            },
                                            {
                                                name: "alfresco/lists/views/layouts/Cell",
                                                config: {
                                                    widgets: [
                                                        {
                                                            name: "alfresco/renderers/Property",
                                                            config: {
                                                                propertyToRender: "description",
                                                                renderAsLink: false
                                                            }
                                                        }
                                                    ]
                                                }
                                            },
                                            {
                                                name: "alfresco/lists/views/layouts/Cell",
                                                config: {
                                                    widgets: [
                                                        {
                                                            name: "alfresco/renderers/Property",
                                                            config: {
                                                                propertyToRender: "version",
                                                                renderAsLink: false
                                                            }
                                                        }
                                                    ]
                                                }
                                            }
                                        ]
                                    }
                                }
                            ]

                        }
                    }
                ]
            }
        }
    ]
};
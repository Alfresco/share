/**
 * Copyright (C) 2005-2015 Alfresco Software Limited.
 *
 * This file is part of Alfresco
 *
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * ConsoleHybridSyncManagement tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleHybridSyncManagement
 */
(function()
{
    /**
     * YUI Library aliases
     */
    var Dom = YAHOO.util.Dom,
        Event = YAHOO.util.Event;
    
    /**
     * Alfresco Slingshot aliases
     */
    var $html = Alfresco.util.encodeHTML,
        $date = function $date(date, format) { return Alfresco.util.formatDate(Alfresco.util.fromISO8601(date), format) };
    
    /**
     * ConsoleHybridSyncManagement tool component constructor.
     *
     * @param {String} htmlId The HTML id of the parent element
     * @return {Alfresco.ConsoleHybridSyncManagement} The new component instance
     * @constructor
     */
    Alfresco.ConsoleHybridSyncManagement = function HybridSyncManagement_constructor(htmlId)
    {
        Alfresco.ConsoleHybridSyncManagement.superclass.constructor.call(this, 
            "Alfresco.ConsoleHybridSyncManagement", 
            htmlId, 
            ["button", "container", "datasource", "datatable", "paginator", "history", "animation"]
        );
        
        return this;
    };
    
    /**
     * Extend from Alfresco.component.Base
     */
    YAHOO.extend(Alfresco.ConsoleHybridSyncManagement, Alfresco.component.Base,
    {
        /**
         * Object container for initialization options
         *
         * @property options
         * @type object
         */
        options:
        {  
            /**
             * Maximum number of ssds to display per page.
             *
             * @property pageSize
             * @type int
             * @default 15
             */
            pageSize: 15,
            
            username: ""
        },
        
        /**
         * Sync id, used for filtering.
         * @instance
         * @type {string}
         * @default ""
        */
        ssdId: "",
          
        /**
         * Sync set def creator username, used for filtering.
         * @instance
         * @type {string}
         * @default ""
         */
        ssdSyncCreator: "",
        
        /**
         * Sync set def that has sync failed aspect, used for filtering.
         * @instance
         * @type {boolean}
         * @default false
         */
        ssdFailed: null,
        
        /**
         * Sync set def that has sync failed aspect and this failed code, used for filtering.
         * @instance
         * @type {string}
         * @default null
         */
        ssdFailedReason: null,
        
        /**
         * Number of search results.
         */
        resultsCount: 0,
        
        /**
         * Current visible page index - counts from 1
         */
        currentPage: 1,
        
        /**
         * Sort direction.
         
         * @instance
         * @type {string}
         * @default asc
         */
        sortDir: "asc",
        
        /**
         * Sort field.
         
         * @instance
         * @type {string}
         * @default syncCreator
         */
        sortField: "syncCreator",
        
        /**
         * Fired by YUI when parent element is available for scripting
         * @method onReady
         */
        onReady: function ConsoleHybridSyncManagement_onReady()
        {
            // Setup DataTable
            this._setupDataTable();
            
            // Setup Button definitions
            this.widgets.searchButton = Alfresco.util.createYUIButton(this, "search-button", this.onSearchClick);
            this.widgets.exportCSVButton = Alfresco.util.createYUIButton(this, "browse-button", this.onExportCSV);
            
            var ssdIdQI = Dom.get(this.id + "-ssd-id");
            this.widgets.enterListener = new YAHOO.util.KeyListener(
                ssdIdQI,
                {
                    keys: YAHOO.util.KeyListener.KEY.ENTER
                },
                {
                    fn: this.onSearchClick,
                    scope: this,
                    correctScope: true
                },
                "keydown"
            ).enable();
            ssdIdQI.value = this.ssdId;
            
            var ssdSyncCreatorQI = Dom.get(this.id + "-ssd-sync-creator");
            this.widgets.enterListener = new YAHOO.util.KeyListener(
                ssdSyncCreatorQI,
                {
                    keys: YAHOO.util.KeyListener.KEY.ENTER
                },
                {
                    fn: this.onSearchClick,
                    scope: this,
                    correctScope: true
                },
                "keydown"
            ).enable();
            ssdSyncCreatorQI.value = this.ssdSyncCreator;
            
            // Show all syncs : perform the initial search
            this._reloadSearchData();
        },
    
        _setDefaultDataTableErrors: function ConsoleHybridSyncManagement__setDefaultDataTableErrors(dataTable)
        {
            dataTable.set("MSG_EMPTY", this.msg("message.no-ssds"));
        },

        /**
         * DataTable definition and setup
         *
         * @method _setupDataTable
         */
        _setupDataTable: function ConsoleHybridSyncManagement_setupDataTable()
        {
            var me = this;
            
            // DataTable Cell Renderers definitions
            
            var renderCellId = function ConsoleHybridSyncManagement_onReady_renderCellId(elCell, oRecord, oColumn, oData)
            {
                var id = oRecord.getData().entry.id;
                var messageDesc = '<span>' + $html(id) + '</span>';
                elCell.innerHTML = messageDesc;
            };
            
            var renderCellSyncError = function ConsoleHybridSyncManagement_onReady_renderCellSyncError(elCell, oRecord, oColumn, oData)
            {
                var syncError = oRecord.getData().entry.syncError;
                var messageDesc = '<span>' + $html(syncError) + '</span>';
                elCell.innerHTML = messageDesc ;
            };
            
            var renderCellPath = function ConsoleHybridSyncManagement_onReady_renderCellPath(elCell, oRecord, oColumn, oData)
            {
                var path = oRecord.getData().entry.path;
                var nodeRef = oRecord.getData().entry.nodeRef;
                var messageDesc = '<span><a class="theme-color-1" href="' + Alfresco.constants.URL_PAGECONTEXT + 'console/admin-console/node-browser#state=panel%3Dview%26nodeRef%3D' + encodeURIComponent(nodeRef) + '">' + $html(path) + '</a></span>';
                elCell.innerHTML = messageDesc ;
            };
            
            var renderCellSyncCreator = function ConsoleHybridSyncManagement_onReady_renderCellSyncCreator(elCell, oRecord, oColumn, oData)
            {
                var syncCreator = oRecord.getData().entry.syncCreator;
                var messageDesc;
                if (oRecord.getData().entry.syncCreatorExists == "true")
                {
                    messageDesc = Alfresco.util.userProfileLink(syncCreator, "", 'class="theme-color-1"');
                }
                else
                {
                    messageDesc = '<span title="' + me.msg("message.ssd-sync-creator-no-link") +'">' + $html(syncCreator) + '</span>';
                }
                elCell.innerHTML = messageDesc;
            };
            
            var renderCellCloudUsername = function ConsoleHybridSyncManagement_onReady_renderCellCloudUsername(elCell, oRecord, oColumn, oData)
            {
               var cloudUsername = oRecord.getData().entry.cloudUsername;
               var messageDesc = '<span>' + $html(cloudUsername) + '</span>';
               elCell.innerHTML = messageDesc ;
            };
            
            var renderCellRemoteTenantId = function ConsoleHybridSyncManagement_onReady_renderCellRemoteTenantId(elCell, oRecord, oColumn, oData)
            {
               var remoteTenantId = oRecord.getData().entry.remoteTenantId;
               var messageDesc = '<span>' + $html(remoteTenantId) + '</span>';
               elCell.innerHTML = messageDesc ;
            };
            
            var renderCellTargetFolderNodeRef = function ConsoleHybridSyncManagement_onReady_renderCellTargetFolderNodeRef(elCell, oRecord, oColumn, oData)
            {
               var targetFolderNodeRef = oRecord.getData().entry.targetFolderNodeRef;
               var messageDesc = '<span>' + $html(targetFolderNodeRef) + '</span>';
               elCell.innerHTML = messageDesc ;
            };
            
            var renderCellActions = function ConsoleHybridSyncManagement_onReady_renderCellActions(elCell, oRecord, oColumn, oData)
            {
                var ssd = oRecord.getData();
                if (me.options.username !== ssd.entry.syncCreator) {
                    var actions = "", reassignLink, index=oRecord._nCount;

                    Dom.addClass(elCell, 'align-center');
                    
                    actions += '<a id="' + me.id + '-reassign-link-'+index+'" href="#" class="reassign-ssd" title="' + me.msg("button.ssd-reassign.label") + '">&nbsp;</a>';
                    
                    elCell.innerHTML = actions;
                    
                    reassignLink=Dom.getChildrenBy(elCell, function(el){
                        return el.id === me.id+"-reassign-link-" + index;
                    })[0];
                    
                    Event.addListener(oRecord.getId(), "mouseover", function()
                    {
                        Dom.addClass(reassignLink, "reassign-ssd-active");
                    });
                    Event.addListener(oRecord.getId(), "mouseout", function()
                    {
                        Dom.removeClass(reassignLink, "reassign-ssd-active");
                    });
                    
                    Event.addListener(reassignLink, 'click', function () {me.onReassignHybridSync(ssd.entry.id, ssd.entry.remoteTenantId);}, reassignLink, false);
                }
            };
            
            // DataSource definition
            var ssdSearchResultsURI = Alfresco.constants.PROXY_URI + "enterprise/sync/syncsetdefinitionsadmin?";
            
            this.widgets.dataSource = new YAHOO.util.DataSource(ssdSearchResultsURI);
            this.widgets.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSON;
            this.widgets.dataSource.connXhrMode = "queueRequests";
            this.widgets.dataSource.responseSchema = {
                resultsList: "list.entries",
                fields: ["entry"],
                metaFields: { totalRecords: "list.pagination.totalItems", count: "list.pagination.count" }
            };
            
            // YUI Paginator definition
            this.widgets.paginator = new YAHOO.widget.Paginator({
                containers: this.id + "-paginator",
                rowsPerPage: this.options.pageSize,
                initialPage: 1,
                template: this.msg("pagination.template"),
                pageReportTemplate: this.msg("pagination.template.page-report"),
                previousPageLinkLabel: this.msg("pagination.previousPageLinkLabel"),
                nextPageLinkLabel: this.msg("pagination.nextPageLinkLabel")
            });
            
            // DataTable column defintions
            var columnDefinitions = [
                { key: "id", label: me.msg("hybridsync.ssd-id-header-label"), sortable: true, formatter: renderCellId },
                { key: "syncError", label: me.msg("hybridsync.ssd-failed-reason-header-label"), sortable: true, formatter: renderCellSyncError },
                { key: "path", label: me.msg("hybridsync.path-desc-header-label"), sortable: false, formatter: renderCellPath },
                { key: "syncCreator", label: me.msg("hybridsync.sync-creator-header-label"), sortable: true, formatter: renderCellSyncCreator },
                { key: "cloudUsername", label: me.msg("hybridsync.cloud-user-header-label"), sortable: false, formatter: renderCellCloudUsername},
                { key: "remoteTenantId", label: me.msg("hybridsync.remote-tenant-id-header-label"), sortable: true, formatter: renderCellRemoteTenantId},
                { key: "targetFolderNodeRef", label: me.msg("hybridsync.target-folder-node-ref-header-label"), sortable: false, formatter: renderCellTargetFolderNodeRef },
                { key: "action", label: me.msg("hybridsync.actions-header-label"), sortable: false, formatter: renderCellActions, width: 45 }
            ];
            
            var generateRequest = function(oState, oSelf) { 
                // Get states or use defaults 
                oState = oState || { pagination: null, sortedBy: null }; 
                var sort = (oState.sortedBy) ? oState.sortedBy.key : "syncCreator"; 
                var dir = (oState.sortedBy && oState.sortedBy.dir === YAHOO.widget.DataTable.CLASS_DESC) ? "desc" : "asc"; 
                
                me.sortDir = dir;
                me.sortField = sort;
                
                var recordOffset = (oState.pagination) ? oState.pagination.recordOffset : 0; 
                var rowsPerPage = (oState.pagination) ? oState.pagination.rowsPerPage : 15; 
                
                // Build custom request 
                var partialUrl = "dir=" + me.sortDir + "&sort=" + me.sortField + "&skipCount=" + recordOffset + "&maxItems=" + rowsPerPage;
                partialUrl = me.addQueryParam(partialUrl, "ssdId", me.ssdId);
                partialUrl = me.addQueryParam(partialUrl, "ssdSyncCreator", me.ssdSyncCreator);
                partialUrl = me.addQueryParam(partialUrl, "ssdFailed", me.ssdFailed);
                partialUrl = me.addQueryParam(partialUrl, "ssdFailedReason", me.ssdFailedReason);
                
                return partialUrl; 
            }; 

            // DataTable definition
            this.widgets.dataTable = new YAHOO.widget.DataTable(this.id + "-ssds", columnDefinitions, this.widgets.dataSource,{
                generateRequest: generateRequest,
                renderLoopSize: Alfresco.util.RENDERLOOPSIZE,
                initialLoad: false,
                paginator: this.widgets.paginator,
                MSG_LOADING: this.msg("message.ssds-loading.data.message"),
                MSG_EMPTY: this.msg("message.no-ssds"),
                dynamicData: true, // Enables dynamic server-driven data 
                sortedBy : { key:"syncCreator", dir:YAHOO.widget.DataTable.CLASS_ASC} // Sets UI initial sort arrow
            });
            
            // Override abstract function within DataTable to set custom error message
            this.widgets.dataTable.doBeforeLoadData = function Search_doBeforeLoadData(sRequest, oResponse, oPayload)
            {
                if (oResponse.error)
                {
                    try
                    {
                        var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                        me.widgets.dataTable.set("MSG_ERROR", response.message);
                    }
                    catch(e)
                    {
                        me._setDefaultDataTableErrors(me.widgets.dataTable);
                        me.widgets.dataTable.render();
                    }
                }
                else if (oResponse.results)
                {
                    // clear the empty error message
                    me.widgets.dataTable.set("MSG_EMPTY", "");
                    
                    // update the results count, update hasMoreResults.
                    me.hasMoreResults = (oResponse.results.length > me.options.maxSearchResults);
                    if (me.hasMoreResults)
                    {
                        oResponse.results = oResponse.results.slice(0, me.options.maxSearchResults);
                    }
                    me.resultsCount = oResponse.results.length;
                    
                    if (me.resultsCount > 0)
                    {
                        Dom.removeClass(me.id + "-paginator", "hidden");
                        Dom.removeClass(me.id + "-ssds-list-bar-bottom", "hidden");
                        Dom.removeClass(me.id + "-ssds", "hidden");
                    
                        // Remove comment to show search result count
                        // Dom.get(this.id + '-ssds-list-info').innerHTML = = this.msg("ssds-found", '<b>' + me.resultsCount + '</b>');
                        Dom.addClass(me.id + "-ssds-list-info", "hidden");
                    }
                    else
                    {
                        Dom.addClass(me.id + "-paginator", "hidden");
                        Dom.addClass(me.id + "-ssds-list-bar-bottom", "hidden");
                        Dom.addClass(me.id + "-ssds", "hidden");
                        
                        // set the text
                        Dom.get(me.id + '-ssds-list-info').innerHTML = me.msg("message.no-ssds");
                        Dom.removeClass(me.id + "-ssds-list-info", "hidden");
                    }
                }
                
                // Must return true to have the "Loading..." message replaced by the error message
                return true;
            };
            
            // Update totalRecords on the fly with value from server
            this.widgets.dataTable.handleDataReturnPayload = function Search_handleDataReturnPayload (oRequest, oResponse, oPayload) {
                oPayload = oPayload || {};
                oPayload.totalRecords = oResponse.meta.totalRecords;
                
                me.resultsCount = totalRecords = oResponse.meta.totalRecords;
                
                me.currentPage = oPayload.page;
                me.widgets.paginator.setState(oPayload);
                
                return oPayload;
            };
            
            // Rendering complete event handler
            this.widgets.dataTable.subscribe("renderEvent", function(){
                // Update the paginator
                me.widgets.paginator.setState(
                {
                    page: me.currentPage,
                    totalRecords: me.resultsCount
                });
                me.widgets.paginator.render();
            });
        },
        
        /**
         * Search button click event handler
         *
         * @method onSearchClick
         * @param e {object} DomEvent
         * @param args {array} Event parameters (depends on event type)
         */
        onSearchClick: function ConsoleHybridSyncManagement_onSearchClick(e, args)
        {
            var ssdIdElem = Dom.get(this.id + "-ssd-id");
            var searchSsdSyncCreatorElem = Dom.get(this.id + "-ssd-sync-creator");
            var searchSsdFailedElem = Dom.get(this.id + "-ssd-failed");
            var searchSsdFailedReasonElem = Dom.get(this.id + "-ssd-failed-reason");
            
            this._performSearch({
                ssdId : ssdIdElem.value,
                ssdSyncCreator : searchSsdSyncCreatorElem.value, 
                ssdFailed : searchSsdFailedElem.checked, 
                ssdFailedReason : searchSsdFailedReasonElem.value
            });
        },
        
        // Failure handler
        onRequestFailure: function failureHandler(sRequest, oResponse)
        {
            if (oResponse.status == 401)
            {
                // Our session has likely timed-out, so refresh to offer the login page
                window.location.reload();
            }
            else
            {
                try
                {
                    var response = YAHOO.lang.JSON.parse(oResponse.responseText);
                    this.widgets.dataTable.set("MSG_ERROR", response.message);
                    this.widgets.dataTable.showTableMessage(response.message, YAHOO.widget.DataTable.CLASS_ERROR);
                }
                catch(e)
                {
                    this._setDefaultDataTableErrors(this.widgets.dataTable);
                    this.widgets.dataTable.render();
                }
            }
        },
        
        /**
         * Updates search results list by calling data webscript with current query term
         *
         * @method _performSearch
         * @param args {object} search args
         */
        _performSearch: function ConsoleHybridSyncManagement__performSearch(args)
        {
            if (args.ssdId != undefined) {
                this.ssdId = YAHOO.lang.trim(args.ssdId);
            }
            
            if (args.ssdSyncCreator != undefined) {
                this.ssdSyncCreator = YAHOO.lang.trim(args.ssdSyncCreator);
            }
            
            if (args.ssdFailed != undefined && args.ssdFailed) {
                this.ssdFailed = true;
            } else {
                this.ssdFailed = false;
            }
            
            if (args.ssdFailedReason != undefined) {
                this.ssdFailedReason = YAHOO.lang.trim(args.ssdFailedReason);
            }
            
            // Update the ui to show that a search is on-going
            this.widgets.dataTable.set("MSG_EMPTY", "");
            this.widgets.dataTable.render();
            
            // Empty results table
            this.widgets.dataTable.deleteRows(0, this.widgets.dataTable.getRecordSet().getLength());
            
            // Reset sort 
            var state =  this.widgets.dataTable.getState(); 
            state.sortedBy = {key:'syncCreator', dir:YAHOO.widget.DataTable.CLASS_ASC}; 
            
            // Success handler
            function successHandler(sRequest, oResponse, oPayload)
            {
                this.widgets.dataTable.onDataReturnInitializeTable.call(this.widgets.dataTable, sRequest, oResponse, oPayload);
                this.resultsCount = oResponse.meta.totalRecords;
                this.currentPage = 1;
            
                // set focus to search input textbox
                Dom.get(this.id + "-ssd-id").focus();
            }
            
            var partialUrl = "dir=asc&sort=syncCreator&skipCount=0&maxItems=" + this.options.pageSize;
            partialUrl = this.addQueryParam(partialUrl, "ssdId", this.ssdId);
            partialUrl = this.addQueryParam(partialUrl, "ssdSyncCreator", this.ssdSyncCreator);
            partialUrl = this.addQueryParam(partialUrl, "ssdFailed", this.ssdFailed);
            partialUrl = this.addQueryParam(partialUrl, "ssdFailedReason", this.ssdFailedReason);
            
            // Reload data sending new request for dataSource
            this.widgets.dataSource.sendRequest(partialUrl,
            {
                success: successHandler,
                failure: this.onRequestFailure,
                scope: this
            });
        },
        
        /**
         * Updates search results list by calling data webscript with current query cached terms
         *
         * @method _reloadSearchData
         */
        _reloadSearchData: function ConsoleHybridSyncManagement__reloadSearchData() {
            this._performSearch(
            {
                ssdId : this.ssdId,
                ssdSyncCreator : this.ssdSyncCreator,
                ssdFailed : this.ssdFailed, 
                ssdFailedReason : this.ssdFailedReason
            });
        },

        /**
         * The method appends the url with the given param name and value, if they are not null.
         * 
         * @param {String} url The url to append to 
         * @param {String} paramName The param name to append to the url
         * @param {object} paramVal The param val to append to the url
         */
        addQueryParam: function ConsoleHybridSyncManagement_addQueryParam(url, paramName, paramVal) {
            // TODO: method should be moved to an utility script
            if (url !== null && paramName !== null && paramVal !== null && typeof(paramVal) !== "undefined") {
                url = url + "&" + paramName + "=" + encodeURIComponent(paramVal);
            }
            return url;
        },
        
        /**
         * The method appends the url with the default sync filter parameters.
         * 
         * @param {String} url The url to append to 
         */
        _appendSearchParams: function ConsoleHybridSyncManagement__appendSearchParams(url) {
            var ssdIdElem = Dom.get(this.id + "-ssd-id");
            var searchSsdSyncCreatorElem = Dom.get(this.id + "-ssd-sync-creator");
            var searchSsdFailedElem = Dom.get(this.id + "-ssd-failed");
            var searchSsdFailedReasonElem = Dom.get(this.id + "-ssd-failed-reason");
            
            url = this.addQueryParam(url, "ssdId", ssdIdElem.value);
            url = this.addQueryParam(url, "ssdSyncCreator", searchSsdSyncCreatorElem.value);
            url = this.addQueryParam(url, "ssdFailed", searchSsdFailedElem.checked);
            url = this.addQueryParam(url, "ssdFailedReason", searchSsdFailedReasonElem.value);
            
            return url;
        },
        
        /**
         * Action onReassignHybridSync
         * 
         * @param ssdName {string}
         */
        onReassignHybridSync: function ConsoleHybridSyncManagement_onReassignHybridSync(paramSsdId, paramRemoteTenantId)
        {
            var me = this;
            Alfresco.util.PopupManager.displayPrompt(
            {
                title: this.msg("message.ssd-reassign", paramSsdId),
                text: this.msg("message.ssd-reassign-prompt", paramSsdId),
                buttons: [
                    {
                        text: this.msg("button.ssd-reassign.confirm-label"),
                        handler: function dlA_onReassignHybridSync_reassign()
                        {
                            this.destroy();
                            me._onReassignHybridSyncConfirm(paramSsdId, paramRemoteTenantId);
                        }
                    },
                    {
                        text: this.msg("button.ssd-reassign.cancel-label"),
                        handler: function dlA_onReassignHybridSync_cancel()
                        {
                            this.destroy();
                        },
                        isDefault: true
                    }
                ]
            });
        },
        
        /**
         * Action onReassignHybridSyncConfirm
         * 
         * @param ssdName {string}
         */
        _onReassignHybridSyncConfirm: function ConsoleHybridSyncManagement_onReassignHybridSyncConfirm(paramSsdId, paramRemoteTenantId)
        {
            Alfresco.util.Ajax.request(
            {
                method: Alfresco.util.Ajax.POST,
                url: Alfresco.constants.PROXY_URI+ "enterprise/sync/syncsetdefreassignment",
                requestContentType : "application/json",
                responseContentType : "application/json",
                dataObj:
                {
                    ssdId: paramSsdId,
                    remoteTenantId: paramRemoteTenantId
                },
                successCallback:
                {
                    fn: function onDelete_success(response)
                    {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                            text: this.msg("message.ssd-reassign.success")
                        });
                        this._reloadSearchData();
                    },
                    scope: this
                },
                failureMessage: this.msg("message.ssd-reassign.failure")
            });
        },
        
        /**
         * Action onExportCSV
         */
        onExportCSV: function ConsoleHybridSyncManagement_onExportCSV()
        {
            var ssdIdElem = Dom.get(this.id + "-ssd-id");
            var searchSsdSyncCreatorElem = Dom.get(this.id + "-ssd-sync-creator");
            var searchSsdFailedElem = Dom.get(this.id + "-ssd-failed");
            var searchSsdFailedReasonElem = Dom.get(this.id + "-ssd-failed-reason");
            
            var url = Alfresco.constants.PROXY_URI + "enterprise/sync/syncsetdefinitionsadmin?format=csv&dir=asc&sort=syncCreator";
            
            url = this.addQueryParam(url, "ssdId", ssdIdElem.value);
            url = this.addQueryParam(url, "ssdSyncCreator", searchSsdSyncCreatorElem.value);
            url = this.addQueryParam(url, "ssdFailed", searchSsdFailedElem.checked);
            url = this.addQueryParam(url, "ssdFailedReason", searchSsdFailedReasonElem.value);

            window.open(url);
        },
    });
})();

/**
 * Copyright (C) 2005-2010 Alfresco Software Limited.
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
 * WorkflowList component.
 *
 * @namespace Alfresco
 * @class Alfresco.component.WorkflowList
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
      $siteURL = Alfresco.util.siteURL;

   /**
    * DocumentList constructor.
    *
    * @param htmlId {String} The HTML id of the parent element
    * @return {Alfresco.component.WorkflowList} The new DocumentList instance
    * @constructor
    */
   Alfresco.component.WorkflowList = function(htmlId)
   {
      Alfresco.component.WorkflowList.superclass.constructor.call(this, "Alfresco.component.WorkflowList", htmlId, ["button", "menu", "container", "datasource", "datatable", "paginator", "json", "history"]);

      /**
       * Decoupled event listeners
       */
      YAHOO.Bubbling.on("filterChanged", this.onFilterChanged, this);
      YAHOO.Bubbling.on("workflowCancelled", this.onWorkflowCancelled, this);

      return this;
   };

   /**
    * Extend from Alfresco.component.Base
    */
   YAHOO.extend(Alfresco.component.WorkflowList, Alfresco.component.Base);

   /**
    * Augment prototype with Common Workflow actions to reuse createFilterURLParameters
    */
   YAHOO.lang.augmentProto(Alfresco.component.WorkflowList, Alfresco.action.WorkflowActions);

   /**
    * Augment prototype with main class implementation, ensuring overwrite is enabled
    */
   YAHOO.lang.augmentObject(Alfresco.component.WorkflowList.prototype,
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
          * Workflow names not to display
          *
          * @property hiddenWorkflowNames
          * @type Array
          * @default []
          */
         hiddenWorkflowNames: [],

         /**
          * Instruction show to resolve filter id & data to url parameters
          *
          * @property filterParameters
          * @type Array
          * @default []
          */
         filterParameters: [],

         /**
          * Workflow definitions containing the titles dto display in the filter title
          *
          * @property workflowDefinitions
          * @type Array
          * @default []
          */
         workflowDefinitions: [],

         /**
          * Number of workflows to display at the same time
          *
          * @property maxItems
          * @type int
          * @default 50
          */
         maxItems: 50
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       * Initial History Manager event registration
       *
       * @method onReady
       */
      onReady: function DL_onReady()
      {
         var url = Alfresco.constants.PROXY_URI + "api/workflow-instances?initiator=" + encodeURIComponent(Alfresco.constants.USERNAME) +
                     "&exclude=" + this.options.hiddenWorkflowNames.join(",");
         this.widgets.alfrescoDataTable = new Alfresco.util.DataTable(
         {
            dataTable:
            {
               container: this.id + "-workflows",
               columnDefinitions:
               [
                  { key: "id", sortable: false, formatter: this.bind(this.renderCellIcons), width: 40 },
                  { key: "title", sortable: false, formatter: this.bind(this.renderCellWorkflowInfo) },
                  { key: "name", sortable: false, formatter: this.bind(this.renderCellActions), width: 200 }
               ],
               config:
               {
                  MSG_EMPTY: this.msg("message.noWorkflows")
               }
            },
            dataSource:
            {
               url: url,
               defaultFilter:
               {
                  filterId: "workflows.active"
               },
               filterResolver: this.bind(function(filter)
               {
                  // Reuse method from WorkflowActions
                  return this.createFilterURLParameters(filter, this.options.filterParameters);
               })
            },
            paginator:
            {
               config:
               {
                  containers: [this.id + "-paginator"],
                  rowsPerPage: this.options.maxItems
               }
            }
         });
      },

      /**
       * Fired when the currently active filter has changed
       *
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onFilterChanged: function WL_onFilterChanged(layer, args)
      {
         var filter = Alfresco.util.cleanBubblingObject(args[1]);
         if (filter.filterId == "workflowType")
         {
            var workflowTitle;
            for (var i = 0, il = this.options.workflowDefinitions.length; i < il; i++)
            {
               if (this.options.workflowDefinitions[i].name == filter.filterData)
               {
                  Dom.get(this.id + "-filterTitle").innerHTML = $html(this.msg("filter." + filter.filterId, this.options.workflowDefinitions[i].title));
               }
            }
         }
         else
         {
            Dom.get(this.id + "-filterTitle").innerHTML = $html(this.msg("filter." + filter.filterId + (filter.filterData ? "." + filter.filterData : ""), filter.filterData));
         }
      },

      /**
       * Fired when the currently active filter has changed
       *
       * @method onFilterChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onWorkflowCancelled: function WL_onFilterChanged(layer, args)
      {
         // Reload data table so the cancelled workflow is removed
         this.widgets.alfrescoDataTable.loadDataTable();
      },

      /**
       * DataTable Cell Renderers
       */

      /**
       * Priority & pooled icons custom datacell formatter
       *
       * @method renderCellIcons
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellIcons: function WL_renderCellIcons(elCell, oRecord, oColumn, oData)
      {
         var priority = oRecord.getData("priority"),
            priorityMap = { "1": "high", "2": "medium", "3": "low" },
            priorityKey = priorityMap[priority + ""],
            desc = '<img src="' + Alfresco.constants.URL_RESCONTEXT + 'components/images/priority-' + priorityKey + '-16.png" title="' + this.msg("label.priority", this.msg("priority." + priorityKey)) + '"/>';
         elCell.innerHTML = desc;
      },

      /**
       * Workflow info custom datacell formatter
       *
       * @method renderCellWorkflowInfo
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellWorkflowInfo: function WL_renderCellWorkflowInfo(elCell, oRecord, oColumn, oData)
      {
         var workflow = oRecord.getData();
         var message = workflow.message,
            dueDate = workflow.dueDate ? Alfresco.util.fromISO8601(workflow.dueDate) : null,
            startedDate = workflow.startDate ? Alfresco.util.fromISO8601(workflow.startDate) : null;
         if (message === null)
         {
            message = this.msg("workflow.no_message");
         }
         var info = '<h3><a href="' + $siteURL('workflow-details?workflowId=' + workflow.id + '&referrer=workflows&myWorkflowsLinkBack=true') + '" class="theme-color-1" title="' + this.msg("link.viewWorkflow") + '">' + $html(message) + '</a></h3>';
         info += '<div class="due"><label>' + this.msg("label.due") + ':</label><span>' + (dueDate ? Alfresco.util.formatDate(dueDate, "longDate") : this.msg("label.none")) + '</span></div>';
         info += '<div class=started"><label>' + this.msg("label.started") + ':</label><span>' + (startedDate ? Alfresco.util.formatDate(startedDate, "longDate") : this.msg("label.none")) + '</span></div>';
         if (!workflow.isActive)
         {
            var endedDate = workflow.endDate ? Alfresco.util.fromISO8601(workflow.endDate) : null;
            info += '<div class=ended"><label>' + this.msg("label.ended") + ':</label><span>' + (endedDate ? Alfresco.util.formatDate(endedDate, "longDate") : this.msg("label.none")) + '</span></div>';
         }
         info += '<div class="type"><label>' + this.msg("label.type") + ':</label><span>' + $html(workflow.title) + '</span></div>';
         info += '<div class="description"><label>' + this.msg("label.description") + ':</label><span>' + $html(workflow.description) + '</span></div>';
         elCell.innerHTML = info;
      },

      /**
       * Actions custom datacell formatter
       *
       * @method renderCellActions
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellActions: function WL_renderCellActions(elCell, oRecord, oColumn, oData)
      {
         // Create actions using WorkflowAction
         this.createAction(elCell, this.msg("link.viewWorkflow"), "workflow-view-link", $siteURL('workflow-details?workflowId=' + oRecord.getData('id') + '&referrer=workflows&myWorkflowsLinkBack=true'));
         if (oRecord.getData('isActive'))
         {
            this.createAction(elCell, this.msg("link.cancelWorkflow"), "workflow-cancel-link", function(event, oRecord)
            {
               this.cancelWorkflow(oRecord.getData("id"), oRecord.getData("message"));
               Event.preventDefault(event);
            }, oRecord);
         }
         else
         {
            this.createAction(elCell, this.msg("link.deleteWorkflow"), "workflow-delete-link", function(event, oRecord)
            {
               this.deleteWorkflow(oRecord.getData("id"), oRecord.getData("message"));
               Event.preventDefault(event);
            }, oRecord);
         }
      }
      
   }, true);
})();

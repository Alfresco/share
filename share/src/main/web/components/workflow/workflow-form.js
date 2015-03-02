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
 * WorkflowForm component.
 *
 * The workflow details page form is actually a form display of the workflow's start task and data form the workflow itself.
 * To be able to display all this information the following approach is taken:
 *
 * 1. The page loads with a url containing the workflowId as an argument.
 * 2. Since we actually want to display the start task the data-loader component has been bound in to the bottom of the page,
 *    instructed to load detailed workflow data based on the workflowId url argument,
 *    so we can get the startTaskInstanceId needed to request the form.
 * 3. A dynamically/ajax loaded form is brought in using the startTaskInstanceId which gives us a start task form with the
 *    "More Info", "Roles" and "Items" sections.
 * 4. However we shall also display info from the workflow itsel, so once the form is loaded and inserted in to the Dom,
 *    the additional sections "Summary", "General", "Current Tasks" & "Workflow History" are inserted inside the form.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.WorkflowForm
 */
(function()
{
   /**
    * YUI Library aliases
    */
   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
   var $html = Alfresco.util.encodeHTML,
      $siteURL = Alfresco.util.siteURL,
      $userProfileLink = Alfresco.util.userProfileLink;

   /**
    * WorkflowForm constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.WorkflowForm} The new WorkflowForm instance
    * @constructor
    */
   Alfresco.component.WorkflowForm = function WorkflowForm_constructor(htmlId)
   {

      Alfresco.component.WorkflowForm.superclass.constructor.call(this, "Alfresco.component.WorkflowForm", htmlId, ["button", "container", "datasource", "datatable"]);
      this.isReady = false;
      this.workflow = null;
      this.currentTasks = [];
      this.historyTasks = [];

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("workflowDetailedData", this.onWorkflowDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.WorkflowForm, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Adds referrer to the url if present
          *
          * @property referrer
          * @type String
          */
         referrer: null,

         /**
          * Adds nodeRef to the url if present
          *
          * @property nodeRef
          * @type String
          */
         nodeRef: null
      },

      /**
       * Flag set after component is instantiated.
       *
       * @property isReady
       * @type {boolean}
       */
      isReady: false,

      /**
       * The workflow to display 
       *
       * @property workflow
       * @type {Object}
       */
      workflow: null,

      /**
       * Sorted list of current tasks
       *
       * @property currentTasks
       * @type {Array}
       */
      currentTasks: null,

      /**
       * Sorted list of workflow history
       *
       * @property historyTasks
       * @type {Array}
       */
      historyTasks: null,

      /**
       * Fired by YUI when parent element is available for scripting
       *
       * @method onReady
       */
      onReady: function WorkflowHistory_onReady()
      {
         // Display workflow history if data has been received
         this.isReady = true;
         this._loadWorkflowForm();
      },

      /**
       * Event handler called when the "onWorkflowDetailedData" event is received
       *
       * @method: onWorkflowDetailedData
       */
      onWorkflowDetailedData: function TDH_onWorkflowDetailedData(layer, args)
      {
         // Save workflow info
         this.workflow = args[1];
         this._loadWorkflowForm();
      },


      /**
       * Returns a task url
       *
       * @method _getTaskUrl
       * @private
       */
      _getTaskUrl: function WF___getReferrer(page, taskId)
      {
         var url = page + "?taskId=" +encodeURIComponent(taskId);
         if (this.options.referrer)
         {
            url += "&referrer=" + encodeURIComponent(this.options.referrer);
         }
         else if (this.options.nodeRef)
         {
            url += "&nodeRef=" + encodeURIComponent(this.options.nodeRef);
         }
         return $siteURL(url);
      },

      /**
       * @method _displayWorkflowForm
       * @private
       */
      _loadWorkflowForm: function WF__loadWorkflowForm()
      {
         if (this.isReady && this.workflow)
         {
            // Display the view diagrambutton if diagram is available
            if (this.workflow.diagramUrl)
            {
               Dom.removeClass(this.id + "-viewWorkflowDiagram");
               Alfresco.util.createYUIButton(this, "viewWorkflowDiagram", this.viewWorkflowDiagram);
            }

            // Split the task list in current and history tasks and save the most recent one
            var tasks = this.workflow.tasks, recentTask;
            for (var i = 0, il = tasks.length; i < il; i++)
            {
               if (tasks[i].state == "COMPLETED")
               {
                  this.historyTasks.push(tasks[i]);
               }
               else
               {
                  this.currentTasks.push(tasks[i]);
               }
            }

            var sortByDate = function(dateStr1, dateStr2)
            {
               var date1 = Alfresco.util.fromISO8601(dateStr1),
                  date2 = Alfresco.util.fromISO8601(dateStr2);
               if (date1 && date2)
               {
                  return date1 < date2 ? 1 : -1;
               }
               else
               {
                  return !date1 ? 1 : -1;
               }
            };

            // Sort tasks by completion date
            this.currentTasks.sort(function(task1, task2)
            {
               return sortByDate(task1.properties.bpm_dueDate, task2.properties.bpm_dueDate);
            });

            // Sort tasks by completion date
            this.historyTasks.sort(function(task1, task2)
            {
               return sortByDate(task1.properties.bpm_completionDate, task2.properties.bpm_completionDate);
            });
            // Save the most recent task
            var findLastCompleted = function(allHistoryTasks)
            {
                if (allHistoryTasks.length > 0)
                {
                    for (var i = 0, il = allHistoryTasks.length; i < il; i++)
                    {
                        var status = allHistoryTasks[i].properties.bpm_status;
                        if (status == "Completed")
                        {
                            return allHistoryTasks[i];
                        }
                    }
                }
                return { properties: {} };
            }
            recentTask = findLastCompleted(this.historyTasks);

            // Set values in the "Summary" & "General" form sections
            Dom.get(this.id + "-recentTaskTitle").innerHTML = $html(recentTask.title || "");
            Dom.get(this.id + "-recentTaskTitle").setAttribute("href", this._getTaskUrl("task-details", recentTask.id));

            Dom.get(this.id + "-title").innerHTML = $html(this.workflow.title);
            Dom.get(this.id + "-description").innerHTML = $html(this.workflow.description);
            
            var message = this.workflow.message;
            if (message === null)
            {
               message = this.msg("workflow.no_message");
            }
            Dom.get(this.id + "-message").innerHTML = $html(message);
            
            Dom.get(this.id + "-recentTaskOwnersComment").innerHTML = $html(recentTask.properties.bpm_comment || this.msg("label.noComment"));

            var taskOwner = recentTask.creator;
            if (taskOwner == null)
            {
               taskOwner = recentTask.owner || {};
            }
            var taskOwnerAvatar = taskOwner.avatar,
               taskOwnerLink = Alfresco.util.userProfileLink(taskOwner.userName, taskOwner.firstName + " " + taskOwner.lastName, null, !taskOwner.firstName);
            Dom.get(this.id + "-recentTaskOwnersAvatar").setAttribute("src", taskOwnerAvatar ? Alfresco.constants.PROXY_URI + taskOwnerAvatar  + "?c=force" : Alfresco.constants.URL_RESCONTEXT + "components/images/no-user-photo-64.png");
            Dom.get(this.id + "-recentTaskOwnersCommentLink").innerHTML = this.msg("label.recentTaskOwnersCommentLink", taskOwnerLink);

            var initiator = this.workflow.initiator || {};
            Dom.get(this.id + "-startedBy").innerHTML = Alfresco.util.userProfileLink(
                  initiator.userName || this.msg("label.usernameDeleted"), initiator.firstName + " " + initiator.lastName, null, !initiator.firstName);

            var dueDate = Alfresco.util.fromISO8601(this.workflow.dueDate);
            if (dueDate)
            {
               Dom.get(this.id + "-dueSummary").innerHTML = this.msg("label.dueOn", Alfresco.util.formatDate(dueDate, "defaultDateOnly"));
               Dom.get(this.id + "-due").innerHTML = Alfresco.util.formatDate(dueDate, "defaultDateOnly");
            }
            else
            {
               Dom.get(this.id + "-dueSummary").innerHTML = this.msg("label.noDueDate");
               Dom.get(this.id + "-due").innerHTML = this.msg("label.none");
            }

            var taskCompletionDate = Alfresco.util.fromISO8601(recentTask.properties.bpm_completionDate);
            Dom.get(this.id + "-recentTaskCompletedOn").innerHTML = $html(taskCompletionDate ? Alfresco.util.formatDate(taskCompletionDate, "mediumDate") : this.msg("label.notCompleted"));

            Dom.get(this.id + "-recentTaskCompletedBy").innerHTML = taskOwner.userName ? taskOwnerLink : this.msg("label.notCompleted");

            Dom.get(this.id + "-recentTaskOutcome").innerHTML = $html(recentTask.outcome || "");

            var workflowCompletedDate = Alfresco.util.fromISO8601(this.workflow.endDate);
            Dom.get(this.id + "-completed").innerHTML = $html(workflowCompletedDate ? Alfresco.util.formatDate(workflowCompletedDate) : this.msg("label.notCompleted"));

            var startDate = Alfresco.util.fromISO8601(this.workflow.startDate);
            if (startDate)
            {
               Dom.get(this.id + "-started").innerHTML = Alfresco.util.formatDate(startDate);
            }

            var priorityMap = { "1": "high", "2": "medium", "3": "low" },
               priorityKey = priorityMap[this.workflow.priority + ""],
               priority = this.msg("priority." + priorityKey),
               priorityLabel = this.msg("label.priorityLevel", priority);
            var prioritySummaryEl = Dom.get(this.id + "-prioritySummary");
            Dom.addClass(prioritySummaryEl, priorityKey);
            prioritySummaryEl.innerHTML = priorityLabel;
            Dom.get(this.id + "-priority").innerHTML = priority;

            var status = this.workflow.isActive ? this.msg("label.workflowIsInProgress") : this.msg("label.workflowIsComplete");
            Dom.get(this.id + "-statusSummary").innerHTML = $html(status);
            Dom.get(this.id + "-status").innerHTML = $html(status);

            // Load workflow's start task which "represents" the workflow
            // (if present)
            if(this.workflow.startTaskInstanceId) {
            Alfresco.util.Ajax.request(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/form",
               dataObj:
               {
                  htmlid: this.id + "-WorkflowForm-" + Alfresco.util.generateDomId(),
                  itemKind: "task",
                  itemId: this.workflow.startTaskInstanceId,
                  mode: "view",
                  formId: "workflow-details",
                  formUI: false
               },
               successCallback:
               {
                  fn: this.onWorkflowFormLoaded,
                  scope: this
               },
               failureMessage: this.msg("message.failure"),
               scope: this,
               execScripts: true
            });
            } else {
               this.onWorkflowFormLoaded({ serverResponse: { responseText: "<div class='form-container'><div class='form-fields'></div></div>" }});
            }
         }
      },

      /**
       * Called when a workflow form has been loaded.
       * Will insert the form in the Dom.
       *
       * @method onWorkflowFormLoaded
       * @param response {Object}
       */
      onWorkflowFormLoaded: function WorkflowForm_onWorkflowFormLoaded(response)
      {
         // Insert the form html
         var formEl = Dom.get(this.id + "-body");
         formEl.innerHTML = response.serverResponse.responseText;

         // Insert the summary & general sections in the top of the form
         var formFieldsEl = Selector.query(".form-fields", this.id, true),
            workflowSummaryEl = Dom.get(this.id + "-summary-form-section"),
            generalSummaryEl = Dom.get(this.id + "-general-form-section");

         formFieldsEl.insertBefore(generalSummaryEl, Dom.getFirstChild(formFieldsEl));
         formFieldsEl.insertBefore(workflowSummaryEl, generalSummaryEl);

         // Create header and data table elements
         var currentTasksContainerEl = Dom.get(this.id + "-currentTasks-form-section"),
            currentTasksTasksEl = Selector.query("div", currentTasksContainerEl, true);

         // DataTable column definitions for current tasks
         var currentTasksColumnDefinitions =
         [
            { key: "name", label: this.msg("column.type"), formatter: this.bind(this.renderCellType) },
            { key: "owner", label: this.msg("column.assignedTo"), formatter: this.bind(this.renderCellOwner) },
            { key: "id", label: this.msg("column.dueDate"), formatter: this.bind(this.renderCellDueDate) },
            { key: "state", label: this.msg("column.status"), formatter: this.bind(this.renderCellStatus) },
            { key: "properties", label: this.msg("column.actions"), formatter: this.bind(this.renderCellCurrentTasksActions) }
         ];

         // Create current tasks data table filled with current tasks
         var currentTasksDS = new YAHOO.util.DataSource(this.currentTasks,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });
         this.widgets.currentTasksDataTable = new YAHOO.widget.DataTable(currentTasksTasksEl, currentTasksColumnDefinitions, currentTasksDS,
         {
            MSG_EMPTY: this.msg("label.noTasks")
         });

         // DataTable column definitions workflow history
         var historyColumnDefinitions =
         [
            { key: "name", label: this.msg("column.type"), formatter: this.bind(this.renderCellType) },
            { key: "owner", label: this.msg("column.completedBy"), formatter: this.bind(this.renderCellCompletedBy) },
            { key: "id", label: this.msg("column.dateCompleted"), formatter: this.bind(this.renderCellDateCompleted) },
            { key: "state", label: this.msg("column.outcome"), formatter: this.bind(this.renderCellOutcome) },
            { key: "properties", label: this.msg("column.comment"), formatter: this.bind(this.renderCellComment) }
         ];

         // Create header and data table elements
         var historyContainerEl = Dom.get(this.id + "-workflowHistory-form-section"),
            historyTasksEl = Selector.query("div", historyContainerEl, true);

         // Create workflow history data table filled with history tasks
         var workflowHistoryDS = new YAHOO.util.DataSource(this.historyTasks,
         {
            responseType: YAHOO.util.DataSource.TYPE_JSARRAY
         });
         this.widgets.historyTasksDataTable = new YAHOO.widget.DataTable(historyTasksEl, historyColumnDefinitions, workflowHistoryDS,
         {
            MSG_EMPTY: this.msg("label.noTasks")
         });

         // Display tables
         Selector.query(".form-fields", this.id, true).appendChild(currentTasksContainerEl);
         Selector.query(".form-fields", this.id, true).appendChild(historyContainerEl);

         // Fire event so other components knows the form finally has been loaded
         YAHOO.Bubbling.fire("workflowFormReady", this);         
      },

      /**
       * Called when view workflow diagram button is clicked.
       * WIll display the workflow's diagram.
       */
      viewWorkflowDiagram: function()
      {
         if (this.workflow.diagramUrl)
         {
            Alfresco.Lightbox.show({ src: Alfresco.constants.PROXY_URI + this.workflow.diagramUrl });
         }
      },

      /**
       * Render task type as link
       *
       * @method renderCellType
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellType: function WorkflowForm_renderCellType(elCell, oRecord, oColumn, oData)
      {
         var task = oRecord.getData();
         if (task.isEditable)
         {
            elCell.innerHTML = '<a href="' + this._getTaskUrl("task-edit", oRecord.getData("id")) + '" title="' + this.msg("link.title.task-edit") + '">' + $html(oRecord.getData("title")) + '</a>';
         }
         else
         {
            elCell.innerHTML = '<a href="' + this._getTaskUrl("task-details", oRecord.getData("id")) + '" title="' + this.msg("link.title.task-details") + '">' + $html(oRecord.getData("title")) + '</a>';
         }
      },

      /**
       * Render task owner as link
       *
       * @method renderCellOwner
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellOwner: function WorkflowForm_renderCellOwner(elCell, oRecord, oColumn, oData)
      {
         var owner = oRecord.getData("owner");
         if (owner != null && owner.userName)
         {
            var displayName = $html(this.msg("field.owner", owner.firstName, owner.lastName));
            elCell.innerHTML = $userProfileLink(owner.userName, owner.firstName && owner.lastName ? displayName : null, null, !owner.firstName);
         }
         else {
            elCell.innerHTML = this.msg("label.none");
         }
      },
      
      /**
       * Render task completer (= owner) as link when the task actually has been Completed (eg. not the case when
       * parallel tasks aren't actually completed due to loop-stop was condition met). 
       *
       * @method renderCellOwner
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellCompletedBy: function WorkflowForm_renderCellCompletedBy(elCell, oRecord, oColumn, oData)
      {
    	  var status = oRecord.getData("properties").bpm_status;
    	 
    	  // Value based on list 'bpm:allowedStatus' in bpmModel.xml 
    	  if(status != null && status != "Completed") 
    	  {
             elCell.innerHTML = this.msg("label.none");
    	  }
    	  else
    	  {
             var creator = oRecord.getData("creator");
             if (creator == null)
             {
                creator = oRecord.getData("owner");
             }
             if (creator != null && creator.userName)
             {
                var displayName = $html(this.msg("field.owner", creator.firstName, creator.lastName));
                elCell.innerHTML = $userProfileLink(creator.userName, creator.firstName && creator.lastName ? displayName : null, null, !creator.firstName);
             }
             else
             {
                elCell.innerHTML = this.msg("label.none");
             }
          }
      },

      /**
       * Render task completed date
       *
       * @method TL_renderCellSelected
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellDateCompleted: function WorkflowForm_renderCellDateCompleted(elCell, oRecord, oColumn, oData)
      {
         var completionDate = Alfresco.util.fromISO8601(oRecord.getData("properties").bpm_completionDate);
         elCell.innerHTML = Alfresco.util.formatDate(completionDate);
      },

      /**
       * Render task due date
       *
       * @method renderCellDueDate
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellDueDate: function WorkflowForm_renderCellDueDate(elCell, oRecord, oColumn, oData)
      {
         var dueISODate = oRecord.getData("properties").bpm_dueDate;
         if (dueISODate !== null)
         {
            var dueDate = Alfresco.util.fromISO8601(dueISODate);
            elCell.innerHTML = Alfresco.util.formatDate(dueDate, "defaultDateOnly");
         }
         else
         {
            elCell.innerHTML = this.msg("label.none");
         }
      },

      /**
       * Render task status
       *
       * @method TL_renderCellSelected
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellStatus: function WorkflowForm_renderCellStatus(elCell, oRecord, oColumn, oData)
      {
         var status = oRecord.getData("properties").bpm_status;
         if (oRecord.getData("propertyLabels") && Alfresco.util.isValueSet(oRecord.getData("propertyLabels").bpm_status, false))
         {
            status = oRecord.getData("propertyLabels").bpm_status;
         }
         
         elCell.innerHTML = $html(status);
      },

      /**
       * Render task outcome
       *
       * @method renderCellOutcome
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellOutcome: function WorkflowForm_renderCellOutcome(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = $html(oRecord.getData("outcome"));
      },

      /**
       *  Render task comment
       *
       * @method renderCellComment
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellComment: function WorkflowForm_renderCellComment(elCell, oRecord, oColumn, oData)
      {
         elCell.innerHTML = $html(oRecord.getData("properties").bpm_comment);
      },

      /**
       * Render actions available for current tasks
       *
       * @method renderCellCurrentTasksActions
       * @param elCell {object}
       * @param oRecord {object}
       * @param oColumn {object}
       * @param oData {object|string}
       */
      renderCellCurrentTasksActions: function WorkflowForm_renderCellCurrentTasksActions(elCell, oRecord, oColumn, oData)
      {
         var task = oRecord.getData();
         elCell.innerHTML += '<a href="' + this._getTaskUrl("task-details", task.id) + '" class="task-details" title="' + this.msg("link.title.task-details") + '">&nbsp;</a>';
         if (task.isEditable)
         {
            elCell.innerHTML += '<a href="' + this._getTaskUrl("task-edit", task.id) + '" class="task-edit" title="' + this.msg("link.title.task-edit") + '">&nbsp;</a>';
         }
      }

   });

})();

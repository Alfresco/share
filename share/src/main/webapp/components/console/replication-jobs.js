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
 * ConsoleReplicationJobs tool component.
 *
 * @namespace Alfresco
 * @class Alfresco.ConsoleReplicationJobs
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
      $siteURL = Alfresco.util.siteURL;

   /**
    * Preferences
    */
   var PREFERENCES_REPLICATIONJOBS = "org.alfresco.share.admin.replicationJobs",
       PREF_SORTBY = PREFERENCES_REPLICATIONJOBS + ".sortBy";
   
   /**
    * Status Constants
    */
   var STATUS_CANCELLED = "Cancelled",
      STATUS_CANCELREQUESTED = "CancelRequested",
      STATUS_COMPLETED = "Completed",
      STATUS_FAILED = "Failed",
      STATUS_NEW = "New",
      STATUS_PENDING = "Pending",
      STATUS_RUNNING = "Running";

   /**
    * ConsoleReplicationJobs constructor.
    *
    * @param {String} htmlId The HTML id üof the parent element
    * @return {Alfresco.ConsoleReplicationJobs} The new ConsoleReplicationJobs instance
    * @constructor
    */
   Alfresco.ConsoleReplicationJobs = function(htmlId)
   {
      this.name = "Alfresco.ConsoleReplicationJobs";
      Alfresco.ConsoleReplicationJobs.superclass.constructor.call(this, htmlId);

      /* Register this component */
      Alfresco.util.ComponentManager.register(this);

      /* Load YUI Components */
      Alfresco.util.YUILoaderHelper.require(["button", "container", "json", "history"], this.onComponentsLoaded, this);

      /* Define panel handlers */
      var parent = this;

      /* Initialise prototype properties */
      this.jobList = [];
      this.jobListIdToName = {};
      this.jobListNameToId = {};
      this.selectedJob = null;
      this.summaryStatusOrder = [STATUS_FAILED, STATUS_COMPLETED, STATUS_RUNNING, STATUS_CANCELLED, STATUS_NEW];
      this.summaryStatusMap = {};
      this.summaryStatusMap[STATUS_FAILED] = STATUS_FAILED;
      this.summaryStatusMap[STATUS_COMPLETED] = STATUS_COMPLETED;
      this.summaryStatusMap[STATUS_RUNNING] = STATUS_RUNNING;
      this.summaryStatusMap[STATUS_PENDING] = STATUS_RUNNING;
      this.summaryStatusMap[STATUS_CANCELREQUESTED] = STATUS_RUNNING;
      this.summaryStatusMap[STATUS_CANCELLED] = STATUS_CANCELLED;
      this.summaryStatusMap[STATUS_NEW] = STATUS_NEW;

      // NOTE: the panel registered first is considered the "default" view and is displayed first

      /* Replication Panel Handler */
      ReplicationPanelHandler = function ReplicationPanelHandler_constructor()
      {
         ReplicationPanelHandler.superclass.constructor.call(this, "replication");
      };

      YAHOO.extend(ReplicationPanelHandler, Alfresco.ConsolePanelHandler,
      {
         /**
          * Called by the ConsolePanelHandler when this panel shall be loaded
          *
          * @method onLoad
          */
         onLoad: function ReplicationPanelHandler_onLoad()
         {
            // Buttons
            parent.widgets.createJob = Alfresco.util.createYUIButton(parent, "create", null,
            {
               type: "link"
            });
            parent.widgets.sortBy = Alfresco.util.createYUIButton(parent, "sortBy", parent.onSortJobs,
            {
               type: "menu",
               menu: "sortBy-menu",
               lazyloadmenu: false
            });
            parent.widgets.sortBy.getMenu().subscribe("click", function (p_sType, p_aArgs)
            {
               var menuItem = p_aArgs[1];
               if (menuItem)
               {
                  parent.widgets.sortBy.set("label", parent.msg("button.sort-by", menuItem.cfg.getProperty("text")));
                  parent.onSortByChanged.call(parent, p_aArgs[1]);
               }
            });
            parent.widgets.sortBy.value = "status";

            parent.widgets.runJob = Alfresco.util.createYUIButton(parent, "run", parent.onRunJob,
            {
               disabled: true
            });
            parent.widgets.cancelJob = Alfresco.util.createYUIButton(parent, "cancel", parent.onCancelJob,
            {
               disabled: true
            });
            parent.widgets.editJob = Alfresco.util.createYUIButton(parent, "edit", parent.onEditJob,
            {
               disabled: true
            });
            parent.widgets.deleteJob = Alfresco.util.createYUIButton(parent, "delete", parent.onDeleteJob,
            {
               disabled: true
            });
         }
      });
      new ReplicationPanelHandler();

      return this;
   };

   YAHOO.extend(Alfresco.ConsoleReplicationJobs, Alfresco.ConsoleTool,
   {
      /**
       * Job List
       * @property jobList
       * @type {Array}
       */
      jobList: null,

      /**
       * Job List Lookup from DOM ID of Job List <li> tag to Job Name
       * @property jobListIdToName
       * @type {Object}
       */
      jobListIdToName: null,

      /**
       * Job List Lookup from Job Name to DOM ID of Job List <li> tag
       * @property jobListNameToId
       * @type {Object}
       */
      jobListNameToId: null,

      /**
       * Currently selected job
       * @property selectedJob
       */
      selectedJob: null,

      /**
       * Display order for summary panel
       * @property summaryStatusOrder
       */
      summaryStatusOrder: null,

      /**
       * Mapping of status for display in summary panel
       * @property summaryStatusMap
       */
      summaryStatusMap: null,

      /**
       * Object container for initialization options
       *
       * @property options
       * @type object
       */
      options:
      {
         /**
          * Job Name - selects and highlights selected job upon initial page load
          *
          * @property jobName
          * @type string
          */
         jobName: "",

         /**
          * Target Group path - used to generate link on Transfer Target name
          *
          * @property targetGroupPath
          * @type string
          */
         targetGroupPath: ""
      },

      /**
       * Fired by YUI when parent element is available for scripting.
       *
       * @method onReady
       */
      onReady: function ConsoleReplicationJobs_onReady()
      {
         // Call super-class onReady() method
         Alfresco.ConsoleReplicationJobs.superclass.onReady.call(this);

         // Preferences service
         this.services.preferences = new Alfresco.service.Preferences();

         var prefs = this.services.preferences.get();
         var sortByPreference = Alfresco.util.findValueByDotNotation(prefs, PREF_SORTBY, null);
         if (sortByPreference != null && this.widgets.sortBy != null)
         {
            this.widgets.sortBy.value = sortByPreference;
            // set the correct menu label
            var menuItems = this.widgets.sortBy.getMenu().getItems();
            for (var index in menuItems)
            {
               if (menuItems.hasOwnProperty(index))
               {
                  if (menuItems[index].value === sortByPreference)
                  {
                     this.widgets.sortBy.set("label", this.msg("button.sort-by", menuItems[index].cfg.getProperty("text")));
                     break;
                  }
               }
            }
         }
         this.populateJobsList(true);
      },

      /**
       * Call remote API to retrieve list of Job Definitions
       *
       * @method populateJobsList
       * @param selectFirstJob {boolean} True to select first job in jobList
       */
      populateJobsList: function ConsoleReplicationJobs_populateJobsList(selectFirstJob)
      {
         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + "api/replication-definitions",
            dataObj:
            {
               sort: this.widgets.sortBy != null ? this.widgets.sortBy.value : null
            },
            successCallback:
            {
               fn: function ConsoleReplicationJobs_populateJobsList_successCallback(response)
               {
                  if (response && response.json && response.json.data)
                  {
                     this.jobList = response.json.data;
                     if (selectFirstJob && this.options.jobName === "" && this.jobList.length > 0)
                     {
                        this.options.jobName = this.jobList[0].name;
                     }
                     this.renderJobsList();
                     this.updateSummaryPanel();
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        text: this._msg("message.invalid-data.failure", response.config.url)
                     });
                  }
               },
               scope: this
            },
            failureMessage: this._msg("message.get-definitions.failure")
         });
      },

      /**
       * Render the list of jobs
       *
       * @method renderJobsList
       */
      renderJobsList: function ConsoleReplicationJobs_renderJobsList()
      {
         if (!YAHOO.lang.isArray(this.jobList))
         {
            return;
         }

         var me = this,
            jobs = this.jobList,
            jobsContainer = Dom.get(this.id + "-jobsList"),
            selectedClass = "selected";

         jobsContainer.innerHTML = "";
         this.jobListIdToName = {};
         this.jobListNameToId = {};

         /**
          * Click handler for selecting list item
          * @method fnOnClick
          */
         var fnOnClick = function ConsoleReplicationJobs_renderJobsList_fnOnClick()
         {
            return function ConsoleReplicationJobs_renderJobsList_onClick()
            {
               var lis = Selector.query("li", jobsContainer);
               Dom.removeClass(lis, selectedClass);
               Dom.addClass(this, selectedClass);
               if (me.jobListIdToName.hasOwnProperty(this.id))
               {
                  me.onJobSelected(me.jobListIdToName[this.id]);
               }
               return false;
            };
         };

         try
         {
            var elHighlight = null,
               job, container, el, elLink, elSpan, elText, id;

            if (jobs.length === 0)
            {
               jobsContainer.innerHTML = '<div class="no-jobs">' + this.msg("label.no-jobs") + '</div>';
            }
            else
            {
               container = document.createElement("ul");
               jobsContainer.appendChild(container);

               // Create the DOM structure: <li onclick class='{selected}'><a class='{enabled/disabled}' title href><span class='{status}'>"Job Name"</span></a></li>
               for (var i = 0, ii = jobs.length; i < ii; i++)
               {
                  job = jobs[i];

                  // Build the DOM elements
                  el = document.createElement("li");
                  el.className = job.enabled ? "enabled" : "disabled";
                  el.title = this._msg("job." + el.className);
                  if (this.selectedJob !== null && this.selectedJob.name == job.name)
                  {
                     el.className += " selected";
                  }
                  el.onclick = fnOnClick();
                  id = Alfresco.util.generateDomId(el);
                  this.jobListIdToName[id] = job.name;
                  this.jobListNameToId[job.name] = id;

                  elLink = document.createElement("a");
                  elLink.className = (job.status || "none").toLowerCase();
                  elLink.href = "#";

                  elSpan = document.createElement("span");

                  elText = document.createTextNode(job.name);

                  // Build the DOM structure with the new elements
                  elSpan.appendChild(elText);
                  elLink.appendChild(elSpan);
                  el.appendChild(elLink);
                  container.appendChild(el);

                  if (job.name == this.options.jobName)
                  {
                     // Fake a user selection for this job
                     Dom.addClass(el, "selected");
                     this.options.jobName = null;
                     YAHOO.lang.later(100, this, this.onJobSelected, [job.name, false]);
                  }
                  else if (this.selectedJob && this.selectedJob.name == job.name)
                  {
                     // Mark current list as selected
                     Dom.addClass(el, "selected");
                  }
               }
            }
         }
         catch(e)
         {
            jobsContainer.innerHTML = '<span class="error">' + this.msg("message.error-unknown") + '</span>';
         }
      },
      
      /**
       * Find the index of a job in the current jobList given its name
       *
       * @method findJobIndexByName
       * @param jobName {String} Job name
       * @return {Number|null} Found job's index in this.jobList array or null
       */
      findJobIndexByName: function ConsoleReplicationJobs_findJobIndexByName(jobName)
      {
         for (var i = 0, ii = this.jobList.length; i < ii; i++)
         {
            if (this.jobList[i].name == jobName)
            {
               return i;
            }
         }
         return null;
      },

      /**
       * Job selected event handler
       *
       * @method onJobSelected
       * @param jobName {String} Job definition name
       * @param p_fadeIn {Boolean} If set to true, then fade the status panel in
       */
      onJobSelected: function ConsoleReplicationJobs_onJobSelected(jobName, p_fadeIn)
      {
         var jobIndex = this.findJobIndexByName(jobName);

         if (jobIndex === null)
         {
            return;
         }

         Alfresco.util.Ajax.jsonGet(
         {
            url: Alfresco.constants.PROXY_URI + "api/replication-definition/" + encodeURIComponent(jobName),
            successCallback:
            {
               fn: function ConsoleReplicationJobs_onJobSelected_successCallback(response)
               {
                  if (response && response.json && response.json.data)
                  {
                     this.jobList[jobIndex] = response.json.data;
                     this.selectedJob = this.jobList[jobIndex];
                     this.renderJobDetail();
                     if (p_fadeIn == true)
                     {
                        Alfresco.util.Anim.fadeIn(this.id + "-jobStatus",
                        {
                           period: 0.2
                        });
                     }
                     else
                     {
                        Dom.setStyle(this.id + "-jobStatus", "opacity", 1);
                     }
                  }
                  else
                  {
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        text: this._msg("message.invalid-data.failure", response.config.url)
                     });
                  }
               },
               scope: this
            },
            failureMessage: this._msg("message.get-job-details.failure", jobName)
         });
      },

      /**
       * Render the currently selected job's detail
       *
       * @method renderJobDetail
       */
      renderJobDetail: function ConsoleReplicationJobs_renderJobDetail()
      {
         var elJob = Dom.get(this.id + "-jobDetail");

         if (this.selectedJob === null)
         {
            elJob.innerHTML = '<div class="message">' + this._msg("label.no-job-selected") + '</div>';
         }
         else
         {
            var job = Alfresco.util.deepCopy(this.selectedJob),
               elTemplate = Dom.get(this.id + "-jobTemplate"),
               startedAt = "", endedAt = "",
               status = "", statusText = "",
               transferTargetUrl = "",
               jobHTML = "";

            // Name / description
            job.name = $html(job.name);
            job.description = $html(job.description);

            // Enabled/disabled
            job.enabledClass = job.enabled ? "enabled" : "disabled";
            job.enabledText = this._msg("job." + job.enabledClass);

            // Start & end time
            if (job.startedAt && job.startedAt.iso8601)
            {
               startedAt = this._msg("label.started-at", Alfresco.util.formatDate(Alfresco.util.fromISO8601(job.startedAt.iso8601)));
            }
            if (job.endedAt && job.endedAt.iso8601)
            {
               endedAt = this._msg("label.ended-at", Alfresco.util.formatDate(Alfresco.util.fromISO8601(job.endedAt.iso8601)));
            }

            // Status
            status = (job.status || "none").toLowerCase();
            statusText = '<div class="' + $html(status) + '">' + this._msg("label.status." + status, startedAt, endedAt) + '</div>';
            statusText += job.failureMessage ? '<div class="warning">' + $html(job.failureMessage) + '</div>' : "";
            job.statusText = statusText;
            
            // Update status within Job List panel
            var aTag = Selector.query("a", this.jobListNameToId[job.name])[0];
            if (aTag !== null)
            {
               aTag.className = $html(status);
            }

            // View Report links
            job.viewReportLocalLink = "#";
            job.viewReportRemoteLink = "#";
            if (job.transferLocalReport !== null)
            {
               job.viewReportLocalLink = $siteURL("document-details?nodeRef=" + $html(job.transferLocalReport));
            }
            if (job.transferRemoteReport !== null)
            {
               job.viewReportRemoteLink = $siteURL("document-details?nodeRef=" + $html(job.transferRemoteReport));
            }

            // Schedule
            var schedule = job.schedule;
            if (schedule)
            {
               var startDate = Alfresco.util.formatDate(Alfresco.util.fromISO8601(schedule.start.iso8601)),
                  dateValue = schedule.intervalCount,
                  dateUnit = this._msg("date-unit." + (dateValue === 1 ? "single." : "plural.") + schedule.intervalPeriod.toLowerCase());

               job.scheduleHTML = this._msg("label.schedule.details", startDate, (dateValue === 1 ? "" : dateValue + " "), dateUnit);
            }
            else
            {
               job.scheduleHTML = this._msg("label.schedule.none");
            }

            // Transfer Target
            if (job.targetName === null)
            {
               job.targetNameClass = "warning";
               job.targetHTML = this._msg("label.transfer-target.none");
            }
            else
            {
               transferTargetUrl = $siteURL("repository?file={file}&filter=path&filterData={path}",
               {
                  file: encodeURIComponent(job.targetName),
                  path: encodeURIComponent(this.options.targetGroupPath)
               });
               
               job.targetNameClass = job.targetExists ? "server" : "warning ";

               job.targetHTML = '<a href="' + transferTargetUrl + '">' + $html(job.targetName) + '</a>';
            }

            // Payload
            var payloadHTML = "", payload,
               fnDetailsPageURL = Alfresco.util.bind(function(payload)
               {
                  return Alfresco.util.siteURL((payload.isFolder ? "folder" : "document") + "-details?nodeRef=" + $html(payload.nodeRef));
               }, this);

            if (job.payload && job.payload.length > 0)
            {
               for (var i = 0, ii = job.payload.length; i < ii; i++)
               {
                  payload = job.payload[i];
                  payloadHTML += '<div class="' + (payload.isFolder ? "folder" : "document") + '"><a href="' + fnDetailsPageURL(payload) + '" title="' + $html(payload.path) + '">' + $html(payload.name) + '</a></div>';
               }
            }
            else
            {
               payloadHTML = '<div class="warning">' + this.msg("label.payload.none") + '</div>';
            }
            job.payloadHTML = payloadHTML;

            // Render new HTML
            jobHTML = YAHOO.lang.substitute(window.unescape(elTemplate.innerHTML), job);

            // Destroy existing buttons if required
            if (this.widgets.refreshJob instanceof YAHOO.widget.Button)
            {
               this.widgets.refreshJob.destroy();
               this.widgets.refreshJob = null;
            }
            if (this.widgets.viewReportLocal instanceof YAHOO.widget.Button)
            {
               this.widgets.viewReportLocal.destroy();
               this.widgets.viewReportLocal = null;
            }
            if (this.widgets.viewReportRemote instanceof YAHOO.widget.Button)
            {
               this.widgets.viewReportRemote.destroy();
               this.widgets.viewReportRemote = null;
            }

            // Inject new HTML & attach new button instances
            elJob.innerHTML = jobHTML;
            this.widgets.refreshJob = Alfresco.util.createYUIButton(this, "refresh", this.onRefreshJob);
            this.widgets.viewReportLocal = Alfresco.util.createYUIButton(this, "viewReportLocal", this.onViewReportLocal,
            {
               type: "link"
            });
            if (job.transferLocalReport === null)
            {
               Dom.addClass(this.id + "-viewReportLocal", "yui-button-disabled");
            }
            else
            {
               Dom.removeClass(this.id + "-viewReportLocal", "yui-button-disabled");
            }
            this.widgets.viewReportRemote = Alfresco.util.createYUIButton(this, "viewReportRemote", this.onViewReportRemote,
            {
               type: "link"
            });
            if (job.transferRemoteReport === null)
            {
               Dom.addClass(this.id + "-viewReportRemote", "yui-button-disabled");
            }
            else
            {
               Dom.removeClass(this.id + "-viewReportRemote", "yui-button-disabled");
            }
         }

         // Update button status
         this.updateButtonStatus();
         
         // Update Summary panel
         this.updateSummaryPanel();
      },

      /**
       * Updates button status depending on selected job status
       *
       * @method updateButtonStatus
       */
      updateButtonStatus: function ConsoleReplicationJobs_updateButtonStatus()
      {
         if (this.selectedJob === null)
         {
            this.widgets.runJob.set("disabled", true);
            this.widgets.cancelJob.set("disabled", true);
            this.widgets.editJob.set("disabled", true);
            this.widgets.deleteJob.set("disabled", true);
            return;
         }

         var status = this.selectedJob.status,
            enabled = this.selectedJob.enabled;

         this.widgets.runJob.set("disabled", status === STATUS_RUNNING || status === STATUS_CANCELREQUESTED || status === STATUS_PENDING || !enabled);
         this.widgets.cancelJob.set("disabled", status !== STATUS_RUNNING || this.selectedJob.executionDetails === null);
         this.widgets.editJob.set("disabled", false);
         this.widgets.deleteJob.set("disabled", false);
      },

      /**
       * Updates summary panel based on client-side copy of Job List
       *
       * @method updateSummaryPanel
       */
      updateSummaryPanel: function ConsoleReplicationJobs_updateSummaryPanel()
      {
         var panel = Dom.get(this.id + "-jobSummary"),
            jobCount = this.jobList.length,
            html = "";

         if (panel === null)
         {
            return;
         }

         if (jobCount === 0)
         {
            html = '<div class="job-count">' + this._msg("label.no-jobs") + '</div>';
         }
         else
         {
            var objStatus = {},
               job, i, status, statusLC
               fnSumStatus = function(p_oStatus, status)
               {
                  p_oStatus[status] = (p_oStatus.hasOwnProperty(status) ? ++p_oStatus[status] : 1);
               };

            html = '<div class="job-count">' + this._msg("label.summary.job-count", jobCount) + '</div>';
            
            // Add up the unique status values
            for (i = 0; i < jobCount; i++)
            {
               job = this.jobList[i];
               fnSumStatus(objStatus, this.summaryStatusMap[job.status]);
            }
            
            // Desired display order: Failed, Completed, Running, Cancelled, New
            html += '<ul>';
            for (i = 0; i < this.summaryStatusOrder.length; i++)
            {
               status = this.summaryStatusOrder[i];
               if (objStatus.hasOwnProperty(status))
               {
                  statusLC = status.toLowerCase();
                  html += '<li class="' + $html(statusLC) + '">' + this._msg("label.summary." + statusLC, objStatus[status]) + '</li>';
               }
            }
         }
         
         panel.innerHTML = html;
      },

      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

       /**
        * Sort jobs menu-button click handler
        *
        * @method onSortByChanged
        * @param p_oMenuItem {object} Selected menu item
        */
       onSortByChanged: function ConsoleReplicationJobs_onSortByChanged(p_oMenuItem)
       {
          this.widgets.sortBy.value = p_oMenuItem.value;
          this.populateJobsList();
          this.services.preferences.set(PREF_SORTBY, this.widgets.sortBy.value);
       },

      /**
       * Run job button click handler
       *
       * @method onRunJob
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onRunJob: function ConsoleReplicationJobs_onRunJob(e, p_obj)
      {
         if (this.selectedJob === null)
         {
            this.updateButtonStatus();
            return;
         }

         Alfresco.util.Ajax.jsonPost(
         {
            url: Alfresco.constants.PROXY_URI + "api/running-replication-actions?name=" + encodeURIComponent(this.selectedJob.name),
            successCallback:
            {
               fn: function ConsoleReplicationJobs_onRunJob_successCallback()
               {
                  this.onRefreshJob();
               },
               scope: this
            },
            failureMessage: this._msg("message.run.failure", this.selectedJob.name)
         });
      },

      /**
       * Cancel job button click handler
       *
       * @method onCancelJob
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancelJob: function ConsoleReplicationJobs_onCancelJob(e, p_obj)
      {
         if (this.selectedJob === null || this.selectedJob.executionDetails === null)
         {
            this.updateButtonStatus();
            return;
         }

         Alfresco.util.Ajax.jsonDelete(
         {
            url: Alfresco.constants.PROXY_URI + encodeURI(this.selectedJob.executionDetails),
            successCallback:
            {
               fn: function ConsoleReplicationJobs_onCancelJob_successCallback()
               {
                  this.onRefreshJob();
               },
               scope: this
            },
            failureCallback:
            {
               fn: function ConsoleReplicationJobs_onCancelJob_failureCallback(response)
               {
                  var failureMessage = this._msg("message.unknown-error");
                  if (response && response.json && response.json.message)
                  {
                     failureMessage = response.json.message;
                  }

                  Alfresco.util.PopupManager.displayPrompt(
                  {
                     title: this._msg("message.cancel.failure", this.selectedJob.name),
                     text: failureMessage
                  });

                  this.onRefreshJob();
               },
               scope: this
            }
         });
      },

      /**
       * Edit job button click handler
       *
       * @method onEditJob
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onEditJob: function ConsoleReplicationJobs_onEditJob(e, p_obj)
      {
         if (this.selectedJob === null)
         {
            this.updateButtonStatus();
            return;
         }

         var url = Alfresco.util.uriTemplate("consolepage",
         {
            pageid: "replication-job"
         });
         url += "?jobName=" + encodeURIComponent(this.selectedJob.name);

         window.location.href = url;
      },

      /**
       * Delete job button click handler
       *
       * @method onDeleteJob
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onDeleteJob: function ConsoleReplicationJobs_onDeleteJob(e, p_obj)
      {
         if (this.selectedJob === null)
         {
            this.updateButtonStatus();
            return;
         }

         var me = this;

         Alfresco.util.PopupManager.displayPrompt(
         {
            title: this.msg("message.confirm.delete.title"),
            text: this.msg("message.confirm.delete", this.selectedJob.name),
            buttons: [
            {
               text: this.msg("button.delete"),
               handler: function ConsoleReplicationJobs_onDeleteJob_onDelete()
               {
                  this.destroy();
                  me._onDeleteJobConfirm.call(me);
               }
            },
            {
               text: this.msg("button.cancel"),
               handler: function ConsoleReplicationJobs_onDeleteJob_onCancel()
               {
                  this.destroy();
               },
               isDefault: true
            }]
         });
      },

      /**
       * Delete job confirmed.
       *
       * @method _onDeleteJobConfirm
       * @protected
       */
      _onDeleteJobConfirm: function ConsoleReplicationJobs_onDeleteJob__onDeleteJobConfirm()
      {
         if (this.selectedJob === null)
         {
            return;
         }

         Alfresco.util.Ajax.request(
         {
            method: Alfresco.util.Ajax.DELETE,
            url: Alfresco.constants.PROXY_URI + "api/replication-definition/" + encodeURIComponent(this.selectedJob.name),
            successCallback:
            {
               fn: function ConsoleReplicationJobs_onDeleteJob__onDeleteJobConfirm_successCallback()
               {
                  this.selectedJob = null;
                  this.renderJobDetail();
                  this.populateJobsList();
               },
               scope: this
            },
            failureMessage: this._msg("message.delete.failure", this.selectedJob.name)
         });
      },

      /**
       * Refresh job button click handler
       *
       * @method onRefreshJob
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onRefreshJob: function ConsoleReplicationJobs_onRefreshJob(e, p_obj)
      {
         if (this.selectedJob === null)
         {
            this.widgets.refreshJob.set("disabled", true);
            return;
         }

         Alfresco.util.Anim.fadeOut(this.id + "-jobStatus",
         {
            adjustDisplay: false,
            callback: function ConsoleReplicationJobs_onRefreshJob_callback()
            {
               this.onJobSelected(this.selectedJob.name, true);
            },
            scope: this,
            period: 0.2
         });
      },

      /**
       * View Report button click handler
       *
       * @method onViewReport
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onViewReport: function ConsoleReplicationJobs_onViewReport(e, p_obj)
      {
         if (this.selectedJob === null || this.selectedJob.transferLocalReport === null)
         {
            this.updateButtonStatus();
            Event.preventDefault(e);
         }
      },

      /**
       * Gets a custom message
       *
       * @method _msg
       * @param messageId {string} The messageId to retrieve
       * @return {string} The custom message
       * @private
       */
      _msg: function ConsoleReplicationJobs__msg(messageId)
      {
         return Alfresco.util.message.call(this, messageId, "Alfresco.ConsoleReplicationJobs", Array.prototype.slice.call(arguments).slice(1));
      }
   });
})();
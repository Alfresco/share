/**
 * Copyright (C) 2005-2013 Alfresco Software Limited.
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
 * TaskEditHeader component.
 *
 * @namespace Alfresco.component
 * @class Alfresco.component.TaskEditHeader
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
       $hasEventInterest = Alfresco.util.hasEventInterest,
       $siteURL = Alfresco.util.siteURL;

   /**
    * TaskEditHeader constructor.
    *
    * @param {String} htmlId The HTML id of the parent element
    * @return {Alfresco.component.TaskEditHeader} The new TaskEditHeader instance
    * @constructor
    */
   Alfresco.component.TaskEditHeader = function TaskEditHeader_constructor(htmlId)
   {
      Alfresco.component.TaskEditHeader.superclass.constructor.call(this, htmlId, ["button"]);

      // Re-register with our own name
      this.name = "Alfresco.component.TaskEditHeader";

      // Instance variables
      this.options = YAHOO.lang.merge(this.options, Alfresco.component.TaskEditHeader.superclass.options);
      Alfresco.util.ComponentManager.reregister(this);
      this.isRunning = false;
      this.taskId = null;

      /* Decoupled event listeners */
      YAHOO.Bubbling.on("taskDetailedData", this.onTaskDetailedData, this);

      return this;
   };

   YAHOO.extend(Alfresco.component.TaskEditHeader, Alfresco.component.ShareFormManager,
   {

      /**
       * Keeps track if this component is running an action or not
       *
       * @property isRunning
       * @type Boolean
       */
      isRunning: false,

      /**
       * The task instance id
       *
       * @property taskId
       * @type String
       */
      taskId: null,

      /**
       * The referrer value query string parameter 
       *
       * @property referrerValue
       * @type String
       */
      referrerValue: null,

      /**
       * Fired by YUI when parent element is available for scripting.
       * Template initialisation, including instantiation of YUI widgets and event listener binding.
       *
       * @method onReady
       */
      onReady: function TEH_onReady()
      {
         // Load in the People Finder component from the server
         Alfresco.util.Ajax.request(
         {
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/people-finder/people-finder",
            dataObj:
            {
               htmlid: this.id + "-peopleFinder"
            },
            successCallback:
            {
               fn: this.onPeopleFinderLoaded,
               scope: this
            },
            failureMessage: "Could not load People Finder component",
            execScripts: true
         });

         // Format dates (datatype could be 'date' or 'datetime')
         var dateEls = document.querySelectorAll('[data-datatype^="date"]');
         for (var i = 0; i < dateEls.length; i++) {
            var formatString = 'date-format.default'; // Default to date time format
            if (dateEls[i].getAttribute('data-datatype') === 'date') {
               formatString = 'date-format.mediumDate';
            }
            dateEls[i].innerHTML = Alfresco.util.formatDate(dateEls[i].innerHTML, Alfresco.util.message(formatString));
         };

      },      

      /**
       * Called when the people finder template has been loaded.
       * Creates a dialog and inserts the people finder for choosing assignees.
       *
       * @method onPeopleFinderLoaded
       * @param response The server response
       */
      onPeopleFinderLoaded: function TEH_onPeopleFinderLoaded(response)
      {
         // Inject the component from the XHR request into it's placeholder DIV element
         var finderDiv = Dom.get(this.id + "-peopleFinder");
         finderDiv.innerHTML = response.serverResponse.responseText;

         // Create the Assignee dialog
         this.widgets.reassignPanel = Alfresco.util.createYUIPanel(this.id + "-reassignPanel");

         // Find the People Finder by container ID
         this.widgets.peopleFinder = Alfresco.util.ComponentManager.get(this.id + "-peopleFinder");

         // Set the correct options for our use
         this.widgets.peopleFinder.setOptions(
         {
            singleSelectMode: true,
            addButtonLabel: this.msg("button.select")
         });

         // Make sure we listen for events when the user selects a person
         YAHOO.Bubbling.on("personSelected", this.onPersonSelected, this);
      },

      /**
       * Called when the user has selected an assigne from the people finder.
       *
       * @method onPersonSelected
       * @param e DomEvent
       * @param args Event parameters (depends on event type)
       */
      onPersonSelected: function TEH_onPersonSelected(e, args)
      {
         // This is a "global" event so we ensure the event is for the current panel by checking panel visibility.
         if ($hasEventInterest(this.widgets.peopleFinder, args))
         {
            this.widgets.reassignPanel.hide();
            this._updateTaskProperties(
            {
               "cm_owner": args[1].userName
            }, "reassign");
         }
      },

      /**
       * Event handler called when the "taskDetailedData" event is received
       *
       * @method: onTaskDetailedData
       */
      onTaskDetailedData: function TEH_onTaskDetailedData(layer, args)
      {
         var task = args[1];

         // Save task id so we can use it when invoking actions later
         this.taskId = task.id;

         // Save the referrer value
         this.referrerValue = Alfresco.util.getQueryStringParameter('referrer');

         // Display actions and create yui buttons
         Selector.query("h1 span", this.id, true).innerHTML = $html(task.title);

         // ALF-13115 fix, inform user that this task has been completed
         if (!task.isEditable)
         {
            Alfresco.util.PopupManager.displayMessage(
            {
               text: this.msg("message.task.completed"),
               displayTime: 2
            });

            YAHOO.lang.later(2000, this, function()
            {
               // Check referrer and fall back to user dashboard if unavailable.
               if(this.referrerValue)
               {
                  if(this.referrerValue == 'tasks')
                  {
                     document.location.href = $siteURL("my-tasks");
                  }
                  else if(this.referrerValue='workflows')
                  {
                     document.location.href = $siteURL("my-workflows");
                  }
                }
                else
                {
                   document.location.href = this.getSiteDefaultUrl() || Alfresco.constants.URL_CONTEXT;
                }
            }, []);
          }

         if (task.isReassignable)
         {
            // Task is reassignable
            this.widgets.reassignButton = Alfresco.util.createYUIButton(this, "reassign", this.onReassignButtonClick);
            Dom.removeClass(Selector.query(".actions .reassign", this.id), "hidden");
         }
         
         if (task.isClaimable)
         {
            // Task is claimable
            this.widgets.claimButton = Alfresco.util.createYUIButton(this, "claim", this.onClaimButtonClick);
            Dom.removeClass(Selector.query(".actions .claim", this.id), "hidden");
            Dom.removeClass(Selector.query(".unassigned-message", this.id), "hidden");
         }
         
         if (task.isReleasable)
         {
            // Task is releasable
            this.widgets.releaseButton = Alfresco.util.createYUIButton(this, "release", this.onReleaseButtonClick);
            Dom.removeClass(Selector.query(".actions .release", this.id), "hidden");
         }
      },

      /**
       * Event handler called when the "release" button is clicked
       *
       * @method: onReleaseButtonClick
       */
      onReleaseButtonClick: function TEH_onReleaseButtonClick(layer, args)
      {
         this._updateTaskProperties(
         {
            "cm_owner": null
         }, "release");
      },

      /**
       * Event handler called when the "claim" button is clicked
       *
       * @method: onClaimButtonClick
       */
      onClaimButtonClick: function TEH_onClaimButtonClick(layer, args)
      {
         this._updateTaskProperties(
         {
            "cm_owner": Alfresco.constants.USERNAME
         }, "claim");
      },

      /**
       * Event handler called when the "reassign" button is clicked
       *
       * @method: onReassignButtonClick
       */
      onReassignButtonClick: function TEH_onReassignButtonClick(layer, args)
      {
         this.widgets.peopleFinder.clearResults();
         this.widgets.reassignPanel.show();
      },

      /**
       * Updates a task property
       *
       * @method: _updateTaskProperties
       * @private
       */
      _updateTaskProperties: function TEH__updateTaskProperties(properties, action)
      {
         this._disableActionButtons(true);
         YAHOO.lang.later(2000, this, function()
         {
            if (this.isRunning)
            {
               if (!this.widgets.feedbackMessage)
               {
                  this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
                  {
                     text: this.msg("message." + action),
                     spanClass: "wait",
                     displayTime: 0
                  });
               }
               else if (!this.widgets.feedbackMessage.cfg.getProperty("visible"))
               {
                  this.widgets.feedbackMessage.show();
               }
            }
         }, []);

         // Run rules for folder (and sub folders)
         if (!this.isRunning)
         {
            this.isRunning = true;

            // Start/stop inherit rules from parent folder
            Alfresco.util.Ajax.jsonPut(
            {
               url: Alfresco.constants.PROXY_URI_RELATIVE + "api/task-instances/" + this.taskId,
               dataObj: properties,
               successCallback:
               {
                  fn: function(response, action)
                  {
                     this.isRunning = false;
                     var data = response.json.data;
                     if (data)
                     {
                        Alfresco.util.PopupManager.displayMessage(
                        {
                           text: this.msg("message." + action + ".success")
                        });

                        YAHOO.lang.later(3000, this, function(data)
                        {
                           if (data.owner && data.owner.userName == Alfresco.constants.USERNAME)
                           {
                              // MNT-11418: Save previous URL
                              if (YAHOO.env.ua.chrome > 0)
                              {
                                 var prevUrl = (history.state && history.state.previous_url) ? history.state.previous_url : document.referrer;
                                 var currUrl = document.location.href;
                                 if (prevUrl && prevUrl != "")
                                 {
                                    history.pushState({'previous_url': prevUrl, 'current_url': currUrl}, document.title, "");
                                 }
                              }
                              // Let the user keep working on the task since he claimed it
                              document.location.reload();
                           }
                           else
                           {
                              // Check referrer and fall back to user dashboard if unavailable.
                              if(this.referrerValue) 
                              {
                                 // MNT-10182. The user may not have rights to see task details any more.
                                 if (this.referrerValue == 'tasks')
                                 {
                                    document.location.href = $siteURL("my-tasks");
                                 }
                                 else
                                 {
                                    // Take the user to the most suitable place
                                    this.navigateForward(true);
                                 }
                              } else {
                                 // ALF-20001. If referrer isn't available, either because there was no previous page 
                                 // (because the user navigated directly to the page via an emailed link)
                                 // or because the referrer header has been blocked, fall back to user dashboard.
                                 document.location.href = this.getSiteDefaultUrl() || Alfresco.constants.URL_CONTEXT;
                              }
                           }
                        }, data);

                     }
                  },
                  obj: action,
                  scope: this
               },
               failureCallback:
               {
                  fn: function(response)
                  {
                     this.isRunning = false;
                     this._disableActionButtons(false);
                     Alfresco.util.PopupManager.displayPrompt(
                     {
                        title: this.msg("message.failure"),
                        text: this.msg("message." + action + ".failure")
                     });
                  },
                  scope: this
               }
            });
         }
      },

      _disableActionButtons: function(disabled)
      {
         if (this.widgets.reassignButton)
         {
            this.widgets.reassignButton.set("disabled", disabled)
         }
         if (this.widgets.releaseButton)
         {
            this.widgets.releaseButton.set("disabled", disabled)
         }
         if (this.widgets.claimButton)
         {
            this.widgets.claimButton.set("disabled", disabled)
         }
      }

   });
})();

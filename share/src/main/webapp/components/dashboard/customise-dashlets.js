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
 * CustomiseDashlets component.
 *
 * @namespace Alfresco
 * @class Alfresco.CustomiseDashlets
 */
(function()
{

   var Dom = YAHOO.util.Dom,
      Event = YAHOO.util.Event,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco.CustomiseDashlets constructor.
    *
    * @param {string} htmlId The HTML id of the parent element
    * @return {Alfresco.CustomiseDashlets} The new CustomiseDashlets instance
    * @constructor
    */
   Alfresco.CustomiseDashlets = function(htmlId)
   {
      Alfresco.CustomiseDashlets.superclass.constructor.call(this, "Alfresco.CustomiseDashlets", htmlId, ["button", "container", "datasource", "dragdrop"]);
      return this;
   };

   YAHOO.extend(Alfresco.CustomiseDashlets, Alfresco.component.Base,
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
          * The current layout
          *
          * @property currentLayout
          * @type {object}
          */
         currentLayout: null,

         /**
          * The url to the dashboard that is configured
          *
          * @property dashboardUrl
          * @type {string}
          */
         dashboardUrl: null,
         
         /**
          * The ID to the dashboard that is configured
          *
          * @property dashboardId
          * @type {string}
          */
         dashboardId: null
      },

      /**
       * @method onReady
       */
      onReady: function CD_onReady()
      {
         // Save reference to buttons so we can change label and such later
         this.widgets.addDashletsButton = Alfresco.util.createYUIButton(this, "addDashlets-button", this.onAddDashletsButtonClick);
         this.widgets.saveButton = Alfresco.util.createYUIButton(this, "save-button", this.onSaveButtonClick, {additionalClass: "alf-primary-button"});
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelButtonClick);

         // Save a reference to the dashlet list and garbage can
         this.widgets.dashletListEl = Dom.get(this.id + "-column-ul-0");
         this.widgets.trashcanListEl = Dom.get(this.id + "-trashcan-img");
         this.widgets.shadowEl = Dom.get(this.id + "-dashlet-li-shadow");

         /**
          * Base version of the drag and drop config making:
          * - the "available dashlet list" deleting dashlets when dropped upon it
          * - the "available dashlet list" protecting its elements by making a copy of the when dragged (rather than moved form the list)
          * - the "trashcan" delete dashlets when dropped upon it
          *
          * Additional draggables and targets will be added below depending on the number of columns on the dashboard.
          */         
         var dndConfig =
         {
            shadow: this.widgets.shadowEl,
            draggables: [
               {
                  container: this.widgets.dashletListEl,
                  groups: [Alfresco.util.DragAndDrop.GROUP_MOVE],
                  cssClass: "availableDashlet",
                  protect: true,
                  duplicatesOnEnterKey: true
               }
            ],
            targets: [
               {
                  container: this.widgets.dashletListEl,
                  group: Alfresco.util.DragAndDrop.GROUP_DELETE
               },
               {
                  container: this.widgets.trashcanListEl,
                  group: Alfresco.util.DragAndDrop.GROUP_DELETE
               }
            ]
         };

         var ul;
         for (var i = 1; true; i++)
         {
            ul = Dom.get(this.id + "-column-ul-" + i);
            if (ul)
            {
               // Make only column 1-n lists drop targets for add since 0 is available dashlets list
               dndConfig.draggables.push(
               {
                  container: ul,
                  groups: [Alfresco.util.DragAndDrop.GROUP_MOVE, Alfresco.util.DragAndDrop.GROUP_DELETE],
                  cssClass: "usedDashlet"
               });
               dndConfig.targets.push(
               {
                  container: ul,
                  group: Alfresco.util.DragAndDrop.GROUP_MOVE,
                  maximum: 5
               });
            }
            else
            {
               break;
            }
         }
		 
         dndConfig.keyboardInstruction1 = Dom.get(this.id + "-keyboard-instruction1");
         dndConfig.keyboardInstruction2 = Dom.get(this.id + "-keyboard-instruction2");

         var dnd = new Alfresco.util.DragAndDrop(dndConfig);
         
         YAHOO.Bubbling.on("onDashboardLayoutChanged", this.onDashboardLayoutChanged, this);
         YAHOO.Bubbling.on("onDashboardLayoutsDisplayed", this.onDashboardLayoutsDisplayed, this);
         YAHOO.Bubbling.on("onDashboardLayoutsHidden", this.onDashboardLayoutsHidden, this);

         Event.addListener(this.id + "-closeAddDashlets-link", "click", this.onCloseAddDashletsLinkClick, this, true);

         // Save references so available dashlet can be shown/hidden later
         this.widgets.availableDiv = Dom.get(this.id + "-available-div");
         this.widgets.toggleDashletsButtonWrapperDiv = Dom.get(this.id + "-toggleDashletsButtonWrapper-div");

      },

      /**
       * Fired when the number of columns has changed has changed
       * @method onDashboardLayoutChanged
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onDashboardLayoutChanged: function CD_onDashboardLayoutChanged(layer, args)
      {
         var newLayout = args[1].dashboardLayout;
         this.options.currentLayout = newLayout;
         var wrapper = Dom.get(this.id +"-wrapper-div");
         if (newLayout)
         {
            for (var i = 1; true; i++)
            {
               var ul = Dom.get(this.id + "-column-div-" + i);
               if (ul)
               {
                  if (i <= newLayout.noOfColumns)
                  {
                     Dom.setStyle(ul, "display", "");
                  }
                  else
                  {
                     Dom.setStyle(ul, "display", "none");
                  }
                  Dom.removeClass(wrapper, "noOfColumns" + i);
               }
               else
               {
                  break;
               }
            }
            Dom.addClass(wrapper, "noOfColumns" + newLayout.noOfColumns);
         }
         else
         {
            throw new Error("The argument for event 'onDashboardLayoutChanged' has changed.");
         }
      },

      /**
       * Fired when the CusomiseLayout component displays the available layouts
       *
       * @method onDashboardLayoutsDisplayed
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onDashboardLayoutsDisplayed: function CD_onDashboardLayoutsDisplayed(layer, args)
      {
         // Hide this component
         Dom.setStyle(this.id, "display", "none");
      },

      /**
       * Fired when the CusomiseLayout component hides the available layouts
       *
       * @method onDashboardLayoutsHidden
       * @param layer {string} the event source
       * @param args {object} arguments object
       */
      onDashboardLayoutsHidden: function CD_onDashboardLayoutsHidden(layer, args)
      {
         // Show this component
         Dom.setStyle(this.id, "display", "");
      },

      /**
       * Fired when the user clicks the Add dashlet button.
       * Hides or shows the dashlet list.
       *
       * @method onAddDashletsButtonClick
       * @param event {object} an "click" event
       */
      onAddDashletsButtonClick: function CD_onAddDashletsButtonClick(event)
      {
         // Hide add dashlets button and fade in available dashlets
         Dom.setStyle(this.widgets.toggleDashletsButtonWrapperDiv, "display", "none");
         Alfresco.util.Anim.fadeIn(this.widgets.availableDiv);
      },

      onCloseAddDashletsLinkClick: function CD_onCloseAddDashletsLinkClick(event)
      {
         // Show add dashlets button and hide available dashlets
         Dom.setStyle(this.widgets.toggleDashletsButtonWrapperDiv, "display", "");
         Dom.setStyle(this.widgets.availableDiv, "display", "none");
         Event.stopEvent(event);
      },

      /**
       * Fired when the user clicks the Save/Done button.
       * Saves the dashboard config and takes the user back to the dashboard page.
       *
       * @method onSaveButtonClick
       * @param event {object} a "click" event
       */
      onSaveButtonClick: function CD_onSaveButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.saveButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);

         // Loop through the columns to get the dashlets to save
         var dashlets = [];
         for (var i = 1; i <= this.options.currentLayout.noOfColumns; i++)
         {
            var ul = Dom.get(this.id + "-column-ul-" + i);
            var lis = Dom.getElementsByClassName("usedDashlet", "li", ul);
            for (var j = 0; j < lis.length; j++)
            {
               var li = lis[j];
               if(!Dom.hasClass(li, "dnd-shadow"))
               {
                  var dashlet =
                  {
                     url: Selector.query("input[type=hidden][name=dashleturl]", li, true).value,
                     regionId: "component-" + i + "-" + (j + 1)
                  };
                  var originalRegionId = Selector.query("input[type=hidden][name=originalregionid]", li, true);
                  if (originalRegionId)
                  {
                     dashlet.originalRegionId = originalRegionId.value;
                  }
                  dashlets[dashlets.length] = dashlet;
               }
            }
         }

         // Prepare save request config
         var templateId = this.options.currentLayout.templateId;
         var dataObj = {dashboardPage: this.options.dashboardId, templateId: templateId, dashlets: dashlets};

         // Do the request and send the user to the dashboard after wards
         Alfresco.util.Ajax.jsonRequest(
         {
            method: Alfresco.util.Ajax.POST,
            url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashboard/customise-dashboard",
            dataObj: dataObj,
            successCallback:
            {
               fn: this.saveWelcomePanelPreference,
               scope: this
            },
            failureMessage: Alfresco.util.message("message.saveFailure", this.name),
            failureCallback:
            {
               fn: function()
               {
                  // Hide spinner
                  this.widgets.feedbackMessage.destroy();

                  // Enable the buttons again
                  this.widgets.saveButton.set("disabled", false);
                  this.widgets.cancelButton.set("disabled", false);
               },
               scope: this
            }
         });

         // Display a spinning save message to the user
         this.widgets.feedbackMessage = Alfresco.util.PopupManager.displayMessage(
         {
            text: Alfresco.util.message("message.saving", this.name),
            spanClass: "wait",
            displayTime: 0
         });
         
      },

      /**
       * Fired when the user clicks the Cancel button.
       * Takes the user back to the dashboard page without saving anything.
       *
       * @method onCancelButtonClick
       * @param event {object} a "click" event
       */
      onCancelButtonClick: function CD_onCancelButtonClick(event)
      {
         // Disable buttons to avoid double submits or cancel during post
         this.widgets.saveButton.set("disabled", true);
         this.widgets.cancelButton.set("disabled", true);
         
         // Send the user to this page again without saveing changes
         document.location.href = Alfresco.constants.URL_PAGECONTEXT + "" + this.options.dashboardUrl;
      },
      
      saveWelcomePanelPreference: function CD_saveWelcomePanelPreference()
      {
         var welcomePanelEnabledElement = Dom.get(this.id + "-welcomePanelEnabled");
         if (welcomePanelEnabledElement && this.options.welcomePanelEnabled != welcomePanelEnabledElement.checked)
         {
            Alfresco.util.Ajax.jsonPost(
            {
               url: Alfresco.constants.URL_SERVICECONTEXT + "components/dashboard/welcome-preference",
               dataObj:
               {
                  welcomePanelEnabled: welcomePanelEnabledElement.checked
               },
               successCallback:
               {
                  fn: function()
                  {
                     // Send the user to the newly configured dashboard
                     document.location.href = Alfresco.constants.URL_PAGECONTEXT + "" + this.options.dashboardUrl;
                  },
                  scope: this
               },
               failureMessage: this.msg("message.saveFailure")
            });
         }
         else
         {
            document.location.href = Alfresco.constants.URL_PAGECONTEXT + "" + this.options.dashboardUrl;
         }
      }

   });

})();

/* Dummy instance to load optional YUI components early */
new Alfresco.CustomiseDashlets(null);

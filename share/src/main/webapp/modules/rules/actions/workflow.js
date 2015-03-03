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
 * Rules "Workflow" Action module.
 *
 * @namespace Alfresco.module
 * @class Alfresco.module.RulesWorkflowAction
 */
(function()
{
   /**
   * YUI Library aliases
   */
   var Dom = YAHOO.util.Dom,
      KeyListener = YAHOO.util.KeyListener,
      Selector = YAHOO.util.Selector;

   /**
    * Alfresco Slingshot aliases
    */
    var $html = Alfresco.util.encodeHTML,
       $combine = Alfresco.util.combinePaths,
       $hasEventInterest = Alfresco.util.hasEventInterest;

   Alfresco.module.RulesWorkflowAction = function(htmlId)
   {
      Alfresco.module.RulesWorkflowAction.superclass.constructor.call(this, "Alfresco.module.RulesWorkflowAction", htmlId, ["button", "container", "connection"]);

      // Decoupled event listeners
      if (htmlId != "null")
      {
         YAHOO.Bubbling.on("folderSelected", this.onDestinationSelected, this);
      }

      return this;
   };

   /**
   * Alias to self
   */
   var RWA = Alfresco.module.RulesWorkflowAction;

   /**
   * View Mode Constants
   */
   YAHOO.lang.augmentObject(RWA,
   {
      /**
       * "Approval step" view mode constant.
       *
       * @property VIEW_MODE_APPROVAL_STEP
       * @type string
       * @final
       */
      VIEW_MODE_APPROVAL_STEP: "approval-step",

      /**
       * "Rejection steo" view mode constant.
       *
       * @property VIEW_MODE_REJECTION_STEP
       * @type string
       * @final
       */
      VIEW_MODE_REJECTION_STEP: "rejection-step"
   });

   YAHOO.extend(Alfresco.module.RulesWorkflowAction, Alfresco.component.Base,
   {
      /**
       * Object container for initialization options
       */
      options:
      {
         /**
          * Current siteId for site view mode.
          *
          * @property siteId
          * @type string
          */
         siteId: "",

         /**
          * Repository's rootNode
          *
          * @property rootNode
          * @type Alfresco.util.NodeRef
          */
         rootNode: null,

         /**
          * Whether the Repo Browser is in use or not
          *
          * @property repositoryBrowsing
          * @type boolean
          */
         repositoryBrowsing: true,

         /**
          * ContainerId representing root container in site view mode
          *
          * @property containerId
          * @type string
          * @default "documentLibrary"
          */
         containerId: "documentLibrary",

         /**
          * Template URL
          *
          * @property templateUrl
          * @type string
          * @default Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/actions/workflow"
          */
         templateUrl: Alfresco.constants.URL_SERVICECONTEXT + "modules/rules/actions/workflow",

         /**
          * Dialog view mode: approval step or rejection steo
          *
          * @property viewMode
          * @type string
          * @default Alfresco.modules.RulesWorkflowAction.VIEW_MODE_APPROVAL_STEP
          */
         viewMode: RWA.VIEW_MODE_APPROVAL_STEP,

         /**
          * Allowed dialog view modes
          *
          * @property allowedViewModes
          * @type array
          * @default [VIEW_MODE_APPROVAL_STEP, VIEW_MODE_REJECTION_STEP]
          */
         allowedViewModes:
         [
            RWA.VIEW_MODE_APPROVAL_STEP,
            RWA.VIEW_MODE_REJECTION_STEP
         ]
      },


      /**
       * Container element for template in DOM.
       *
       * @property containerDiv
       * @type HTMLElement
       */
      containerDiv: null,

      /**
       * Main entry point
       * @method showDialog
       * @param workflowConfig {object} Data to fill the form with
       */
      showDialog: function RWA_showDialog(workflowConfig)
      {
         if (!this.containerDiv)
         {
            // Load the UI template from the server
            Alfresco.util.Ajax.request(
            {
               url: this.options.templateUrl,
               dataObj:
               {
                  htmlid: this.id
               },
               successCallback:
               {
                  fn: this.onTemplateLoaded,
                  obj: workflowConfig,
                  scope: this
               },
               failureMessage: "Could not load template:" + this.options.templateUrl,
               execScripts: true
            });
         }
         else
         {
            // Show the dialog
            this._showDialog(workflowConfig);
         }
      },

      /**
       * Event callback when dialog template has been loaded
       *
       * @method onTemplateLoaded
       * @param response {object} Server response from load template XHR request
       * @param workflowConfig {object} Data to fill the form with
       */
      onTemplateLoaded: function RWA_onTemplateLoaded(response, workflowConfig)
      {
         // Inject the template from the XHR request into a new DIV element
         this.containerDiv = document.createElement("div");
         this.containerDiv.setAttribute("style", "display:none");
         this.containerDiv.innerHTML = response.serverResponse.responseText;

         // The panel is created from the HTML returned in the XHR request, not the container
         var dialogDiv = Dom.getFirstChild(this.containerDiv);

         // Create and render the YUI dialog
         this.widgets.dialog = Alfresco.util.createYUIPanel(dialogDiv);

         this.widgets.destinationLocation = new Alfresco.Location(this.id + "-destinationLabel");
         this.widgets.destinationLocation.setOptions(
         {
            siteId: this.options.siteId,
            rootNode: this.options.rootNode
         });

         // Buttons (note: ok button's click will be handled in forms onBeforeAjaxSubmit)
         this.widgets.selectDestinationButton = Alfresco.util.createYUIButton(this, "selectDestination-button", this.onSelectDestinationClick);
         this.widgets.okButton = Alfresco.util.createYUIButton(this, "ok-button", null,
         {
            type: "submit"
         });
         this.widgets.cancelButton = Alfresco.util.createYUIButton(this, "cancel-button", this.onCancelClick);

         // Configure the forms runtime
         var form = new Alfresco.forms.Form(this.id + "-form");
         this.widgets.form = form;

         // Comment is mandatory
         form.addValidation(this.id + "-action", Alfresco.forms.validation.mandatory, null, "keyup");
         form.addValidation(this.id + "-nodeRef", Alfresco.forms.validation.mandatory, null, "keyup");

         // The ok button is the submit button, and it should be enabled when the form is ready
         form.setSubmitElements(this.widgets.okButton);

         // Stop the form from being submitted and fire and event from the collected information
         form.doBeforeAjaxRequest =
         {
            fn: function(p_config, p_obj)
            {
               // Fire event so other component know
               YAHOO.Bubbling.fire("workflowOptionsSelected",
               {
                  options:
                  {
                     label: p_config.dataObj.label,
                     action: p_config.dataObj.action,
                     nodeRef: p_config.dataObj.nodeRef,
                     path: p_config.dataObj.path,
                     viewMode: this.options.viewMode
                  },
                  eventGroup: this
               });

               this.widgets.dialog.hide();

               // Return fals so the form isn't submitted
               return false;
            },
            obj: null,
            scope: this
         };

         // We're in a popup, so need the tabbing fix
         form.applyTabFix();
         form.init();

         // Register the ESC key to close the dialog
         var escapeListener = new KeyListener(document,
         {
            keys: KeyListener.KEY.ESCAPE
         },
         {
            fn: function(id, keyEvent)
            {
               this.onCancelClick();
            },
            scope: this,
            correctScope: true
         });
         escapeListener.enable();

         // Show the dialog
         this._showDialog(workflowConfig);
      },

      /**
       * Internal show dialog function
       * @method _showDialog
       * @param workflowConfig {object} Data to fill the form with
       */
      _showDialog: function RWA__showDialog(workflowConfig)
      {
         // Dialog title
         Dom.get(this.id + "-title").innerHTML = this.msg(this.options.viewMode + ".header");

         // Display form data
         workflowConfig = workflowConfig ? workflowConfig : {};

         // Label textfield
         Dom.get(this.id + "-label").value = workflowConfig.label ? $html(workflowConfig.label) : this.msg(this.options.viewMode + ".label.default");

         // Action drop down
         Alfresco.util.setSelectedIndex(Dom.get(this.id + "-action"), workflowConfig.action);

         // Destination picker
         Dom.get(this.id + "-nodeRef").value = workflowConfig.nodeRef ? workflowConfig.nodeRef.toString() : "";
         this.widgets.destinationLocation.displayByNodeRef(workflowConfig.nodeRef);

         // Update submit elements & show the dialog
         this.widgets.form.validate();
         this.widgets.form._toggleSubmitElements(true);
         this.widgets.dialog.show();
      },


      /**
       * YUI WIDGET EVENT HANDLERS
       * Handlers for standard events fired from YUI widgets, e.g. "click"
       */

      /**
       * Dialog select destination button event handler
       *
       * @method onSelectDestinationClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onSelectDestinationClick: function RCIA_onSelectDestinationClick(e, p_obj)
      {
         // Set up select destination dialog
         if (!this.widgets.destinationDialog)
         {
            this.widgets.destinationDialog = new Alfresco.module.DoclibGlobalFolder(this.id + "-selectDestination");

            var allowedViewModes =
            [
               Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SITE
            ];
            if (this.options.repositoryBrowsing === true)
            {
               allowedViewModes.push(Alfresco.module.DoclibGlobalFolder.VIEW_MODE_REPOSITORY,
                                     Alfresco.module.DoclibGlobalFolder.VIEW_MODE_USERHOME, 
                                     Alfresco.module.DoclibGlobalFolder.VIEW_MODE_SHARED);
            }

            this.widgets.destinationDialog.setOptions(
            {
               allowedViewModes: allowedViewModes,
               siteId: this.options.siteId,
               containerId: this.options.containerId,
               title: this.msg("title.destinationDialog"),
               nodeRef: this.options.rootNode
            });
         }

         // Make sure correct path is expanded
         var pathNodeRef = Dom.get(this.id + "-nodeRef").value;
         this.widgets.destinationDialog.setOptions(
         {
            pathNodeRef: pathNodeRef ? new Alfresco.util.NodeRef(pathNodeRef) : null
         });

         // Show dialog
         this.widgets.destinationDialog.showDialog();
      },


      /**
       * Folder selected in destination dialog
       *
       * @method onDestinationSelected
       * @param layer {object} Event fired
       * @param args {array} Event parameters (depends on event type)
       */
      onDestinationSelected: function RWA_onDestinationSelected(layer, args)
      {
         // Check the event is directed towards this instance
         if ($hasEventInterest(this.widgets.destinationDialog, args))
         {
            // Save values from dialog and update submit elements
            var obj = args[1];
            if (obj !== null)
            {
               Dom.get(this.id + "-nodeRef").value = obj.selectedFolder.nodeRef;
               this.widgets.destinationLocation.displayByNodeRef(obj.selectedFolder.nodeRef);
            }
            this.widgets.form.validate();
         }
      },

      /**
       * Dialog Cancel button event handler
       *
       * @method onCancelClick
       * @param e {object} DomEvent
       * @param p_obj {object} Object passed back from addListener method
       */
      onCancelClick: function RWA_onCancelClick(e, p_obj)
      {
         this.widgets.dialog.hide();
      }
   });

   /* Dummy instance to load optional YUI components early */
   var dummyInstance = new Alfresco.module.RulesWorkflowAction("null");
})();
